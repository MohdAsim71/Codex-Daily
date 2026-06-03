# 3.2 — Object Detection with YOLOv8 TFLite on Android

> **Module 3 · Lesson 2**  
> 📱 Code · ⏱️ ~40 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Export a YOLOv8 model to TFLite format using Python
- Understand the YOLOv8 output tensor structure
- Decode bounding boxes, class scores, and NMS in Kotlin
- Run real-time object detection at 20–30fps on a flagship Android device

---

## 1. Why YOLOv8?

```
YOLO (You Only Look Once) is the industry standard for real-time object detection.

YOLOv8 vs alternatives:
  ┌────────────────┬──────────┬───────────┬────────────────┐
  │ Model          │ Speed    │ Accuracy  │ Android size   │
  ├────────────────┼──────────┼───────────┼────────────────┤
  │ YOLOv8n (nano) │ 20-30fps │ Good      │ ~6 MB          │ ← use this
  │ YOLOv8s (small)│ 10-20fps │ Very good │ ~22 MB         │
  │ YOLOv8m (med)  │ 5-10fps  │ Excellent │ ~52 MB         │
  │ SSD MobileNet  │ 25-35fps │ Fair      │ ~6 MB          │
  │ EfficientDet-0 │ 15-25fps │ Good      │ ~14 MB         │
  └────────────────┴──────────┴───────────┴────────────────┘

YOLOv8n is the best balance for mobile — fast enough for real-time, accurate enough for production.
```

---

## 2. Export YOLOv8 to TFLite (Python)

Run this on your laptop/PC before Android development:

```python
# export_yolov8.py
# pip install ultralytics

from ultralytics import YOLO
import os

# Load YOLOv8 nano pretrained on COCO (80 classes)
model = YOLO("yolov8n.pt")

# Export to TFLite INT8 (best for Android)
# imgsz=320 for speed, 640 for accuracy
model.export(
    format="tflite",
    imgsz=320,           # input resolution — 320 is fast, 640 is accurate
    int8=True,           # INT8 quantization — ~4× smaller
    data="coco8.yaml",   # calibration dataset for INT8
)

# Output: yolov8n_saved_model/yolov8n_integer_quant.tflite
print("Export complete!")
print(f"Model size: {os.path.getsize('yolov8n_saved_model/yolov8n_integer_quant.tflite') / 1e6:.1f} MB")
```

Copy the exported `.tflite` file to `app/src/main/assets/yolov8n.tflite`.

---

## 3. Understanding YOLOv8 Output Tensor

This is critical — you must know what the model outputs to decode it correctly.

```
YOLOv8n TFLite output shape: [1, 84, 2100]

Breakdown:
  1    = batch size
  84   = 4 box coords + 80 COCO class scores
  2100 = number of anchor predictions (varies with input size)

For input 320×320: 2100 predictions
For input 640×640: 8400 predictions

Each column [i] contains:
  [0]     = center_x (normalized 0-1)
  [1]     = center_y (normalized 0-1)
  [2]     = width    (normalized 0-1)
  [3]     = height   (normalized 0-1)
  [4..83] = class scores for 80 COCO classes (NOT softmax — raw logits)
```

COCO 80 classes include: person, bicycle, car, motorcycle, airplane, bus, train, truck, boat, traffic light, fire hydrant, stop sign, parking meter, bench, bird, cat, dog, horse, sheep, cow, elephant, bear, zebra, giraffe, backpack, umbrella, handbag, tie, suitcase, frisbee, skis, snowboard, sports ball, kite, baseball bat, baseball glove, skateboard, surfboard, tennis racket, bottle, wine glass, cup, fork, knife, spoon, bowl, banana, apple, sandwich, orange, broccoli, carrot, hot dog, pizza, donut, cake, chair, couch, potted plant, bed, dining table, toilet, tv, laptop, mouse, remote, keyboard, cell phone, microwave, oven, toaster, sink, refrigerator, book, clock, vase, scissors, teddy bear, hair drier, toothbrush.

---

## 4. Data Classes

```kotlin
// Detection.kt
package com.yourname.vision

import android.graphics.RectF

data class Detection(
    val boundingBox: RectF,      // normalized [0,1] in original image coords
    val classId: Int,
    val label: String,
    val confidence: Float,
    val inferenceTimeMs: Long = 0L
)
```

---

## 5. YOLOv8 Detector — Full Implementation

```kotlin
// YoloV8Detector.kt
package com.yourname.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import java.nio.ByteOrder

class YoloV8Detector(
    context: Context,
    private val modelFileName: String = "yolov8n.tflite",
    private val useGpu: Boolean = false,
    private val confidenceThreshold: Float = 0.45f,
    private val iouThreshold: Float = 0.45f,
    private val maxDetections: Int = 30
) {
    companion object {
        private const val TAG = "YoloV8Detector"
        private val COCO_LABELS = listOf(
            "person","bicycle","car","motorcycle","airplane","bus","train","truck",
            "boat","traffic light","fire hydrant","stop sign","parking meter","bench",
            "bird","cat","dog","horse","sheep","cow","elephant","bear","zebra","giraffe",
            "backpack","umbrella","handbag","tie","suitcase","frisbee","skis",
            "snowboard","sports ball","kite","baseball bat","baseball glove",
            "skateboard","surfboard","tennis racket","bottle","wine glass","cup",
            "fork","knife","spoon","bowl","banana","apple","sandwich","orange",
            "broccoli","carrot","hot dog","pizza","donut","cake","chair","couch",
            "potted plant","bed","dining table","toilet","tv","laptop","mouse",
            "remote","keyboard","cell phone","microwave","oven","toaster","sink",
            "refrigerator","book","clock","vase","scissors","teddy bear",
            "hair drier","toothbrush"
        )
    }

    private val interpreter: Interpreter
    private var gpuDelegate: GpuDelegate? = null

    // Input/output dimensions — read from model
    private val inputWidth: Int
    private val inputHeight: Int
    private val numClasses: Int
    private val numAnchors: Int

    // Pre-allocated output buffer [1, 84, 2100]
    private lateinit var outputBuffer: Array<Array<FloatArray>>

    // Pre-built image processor
    private val imageProcessor: ImageProcessor

    init {
        val modelBuffer = FileUtil.loadMappedFile(context, modelFileName)
        val options = Interpreter.Options()

        if (useGpu) {
            try {
                gpuDelegate = GpuDelegate()
                options.addDelegate(gpuDelegate!!)
                Log.d(TAG, "GPU delegate enabled")
            } catch (e: Exception) {
                Log.w(TAG, "GPU failed: ${e.message}")
                options.setNumThreads(4)
            }
        } else {
            options.setNumThreads(4)
            options.setUseXNNPACK(true)
        }

        interpreter = Interpreter(modelBuffer, options)

        // Read actual input shape from model
        val inputShape = interpreter.getInputTensor(0).shape()   // [1, H, W, 3]
        inputHeight = inputShape[1]
        inputWidth  = inputShape[2]

        // Read output shape [1, 84, numAnchors]
        val outputShape = interpreter.getOutputTensor(0).shape()
        numClasses  = outputShape[1] - 4    // 84 - 4 = 80
        numAnchors  = outputShape[2]        // 2100 for 320px input

        // Pre-allocate output array
        outputBuffer = Array(1) { Array(outputShape[1]) { FloatArray(numAnchors) } }

        // Image preprocessor — normalize to [0, 1]
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputHeight, inputWidth, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))   // [0,255] → [0,1]
            .build()

        Log.d(TAG, "YOLOv8 ready: input=${inputWidth}×${inputHeight}, anchors=$numAnchors, classes=$numClasses")
    }

    fun detect(bitmap: Bitmap): List<Detection> {
        // 1. Preprocess
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        // 2. Inference
        val startMs = SystemClock.uptimeMillis()
        interpreter.run(tensorImage.buffer, outputBuffer)
        val inferenceMs = SystemClock.uptimeMillis() - startMs

        // 3. Decode predictions
        val detections = decodeOutput(outputBuffer[0], bitmap.width, bitmap.height, inferenceMs)

        // 4. Apply Non-Maximum Suppression
        return applyNms(detections)
    }

    /**
     * Decode raw YOLOv8 output tensor into Detection objects.
     * Output shape: [84, numAnchors] — each column is one prediction.
     */
    private fun decodeOutput(
        output: Array<FloatArray>,   // [84, numAnchors]
        origWidth: Int,
        origHeight: Int,
        inferenceMs: Long
    ): List<Detection> {

        val detections = mutableListOf<Detection>()

        // Scale factors to map from model input coords to original image coords
        val scaleX = origWidth.toFloat() / inputWidth
        val scaleY = origHeight.toFloat() / inputHeight

        for (anchorIdx in 0 until numAnchors) {
            // Box coordinates (cx, cy, w, h) — normalized to input size
            val cx = output[0][anchorIdx]
            val cy = output[1][anchorIdx]
            val w  = output[2][anchorIdx]
            val h  = output[3][anchorIdx]

            // Find best class
            var maxScore = 0f
            var bestClass = 0
            for (c in 0 until numClasses) {
                val score = output[4 + c][anchorIdx]
                if (score > maxScore) {
                    maxScore = score
                    bestClass = c
                }
            }

            // Filter low-confidence detections early
            if (maxScore < confidenceThreshold) continue

            // Convert cx,cy,w,h → x1,y1,x2,y2 (in original image pixels)
            val x1 = ((cx - w / 2) * inputWidth  * scaleX).coerceIn(0f, origWidth.toFloat())
            val y1 = ((cy - h / 2) * inputHeight * scaleY).coerceIn(0f, origHeight.toFloat())
            val x2 = ((cx + w / 2) * inputWidth  * scaleX).coerceIn(0f, origWidth.toFloat())
            val y2 = ((cy + h / 2) * inputHeight * scaleY).coerceIn(0f, origHeight.toFloat())

            if (x2 <= x1 || y2 <= y1) continue  // skip degenerate boxes

            detections.add(Detection(
                boundingBox = RectF(x1, y1, x2, y2),
                classId = bestClass,
                label = COCO_LABELS.getOrElse(bestClass) { "class_$bestClass" },
                confidence = maxScore,
                inferenceTimeMs = inferenceMs
            ))
        }

        return detections
    }

    /**
     * Non-Maximum Suppression — remove overlapping duplicate detections.
     * Keeps the highest-confidence box when boxes overlap above iouThreshold.
     */
    private fun applyNms(detections: List<Detection>): List<Detection> {
        // Sort by confidence (highest first)
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()
        val kept = mutableListOf<Detection>()

        while (sorted.isNotEmpty() && kept.size < maxDetections) {
            val best = sorted.removeAt(0)
            kept.add(best)

            // Remove all detections of same class that overlap too much with 'best'
            sorted.removeAll { other ->
                other.classId == best.classId &&
                computeIou(best.boundingBox, other.boundingBox) > iouThreshold
            }
        }

        return kept
    }

    /** Intersection over Union — measures overlap between two boxes. */
    private fun computeIou(a: RectF, b: RectF): Float {
        val intersectLeft   = maxOf(a.left, b.left)
        val intersectTop    = maxOf(a.top, b.top)
        val intersectRight  = minOf(a.right, b.right)
        val intersectBottom = minOf(a.bottom, b.bottom)

        val intersectArea = maxOf(0f, intersectRight - intersectLeft) *
                            maxOf(0f, intersectBottom - intersectTop)
        if (intersectArea == 0f) return 0f

        val aArea = (a.right - a.left) * (a.bottom - a.top)
        val bArea = (b.right - b.left) * (b.bottom - b.top)

        return intersectArea / (aArea + bArea - intersectArea)
    }

    fun close() {
        interpreter.close()
        gpuDelegate?.close()
    }
}
```

---

## 6. CameraX Analyzer for YOLO

```kotlin
// YoloAnalyzer.kt
package com.yourname.vision

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.graphics.Bitmap

class YoloAnalyzer(
    private val detector: YoloV8Detector,
    private val onDetections: (List<Detection>, Long) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxy.toBitmap()
            val detections = detector.detect(bitmap)
            val inferenceMs = detections.firstOrNull()?.inferenceTimeMs ?: 0L
            onDetections(detections, inferenceMs)
        } finally {
            imageProxy.close()   // MUST call — or camera freezes
        }
    }
}
```

---

## 7. Wire It All Together in the Activity

```kotlin
// DetectionActivity.kt
class DetectionActivity : AppCompatActivity() {

    private lateinit var detector: YoloV8Detector
    private lateinit var overlayView: OverlayView
    private lateinit var tvStats: TextView
    private lateinit var cameraExecutor: ExecutorService

    private var frameCount = 0
    private var lastFpsTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection)

        overlayView = findViewById(R.id.overlayView)
        tvStats     = findViewById(R.id.tvStats)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Init YOLOv8 detector
        detector = YoloV8Detector(
            context = this,
            modelFileName = "yolov8n.tflite",
            useGpu = true,                // try GPU first
            confidenceThreshold = 0.45f,
            iouThreshold = 0.45f
        )

        checkAndRequestCameraPermission()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val analyzer = YoloAnalyzer(detector) { detections, inferenceMs ->
                // Count FPS
                frameCount++
                val now = System.currentTimeMillis()
                val fps = if (now - lastFpsTime >= 1000) {
                    val f = frameCount * 1000f / (now - lastFpsTime)
                    frameCount = 0; lastFpsTime = now; f
                } else 0f

                runOnUiThread {
                    overlayView.setDetections(
                        detections = detections,
                        imageWidth = previewView.width,
                        imageHeight = previewView.height
                    )
                    tvStats.text = "⏱ ${inferenceMs}ms  " +
                        "${if (fps > 0) "| %.0ffps".format(fps) else ""}  " +
                        "| ${detections.size} objects"
                }
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, analyzer) }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        detector.close()
    }
}
```

---

## 8. Expected Performance

| Device | GPU Delegate | CPU (4 threads) |
|---|---|---|
| Snapdragon 8 Gen 3 | ~18ms (~55fps) | ~60ms (~16fps) |
| Snapdragon 778G | ~28ms (~35fps) | ~90ms (~11fps) |
| Dimensity 8200 | ~25ms (~40fps) | ~85ms (~12fps) |
| Snapdragon 680 (budget) | ~55ms (~18fps) | ~200ms (~5fps) |

> 💡 YOLOv8n at 320×320 with GPU delegate runs real-time (>20fps) on most mid-range and all flagship devices from 2022+.

---

## 9. Tuning for Your Use Case

```kotlin
// Fast (light objects, large objects):
YoloV8Detector(
    confidenceThreshold = 0.6f,  // higher → fewer false positives
    iouThreshold = 0.5f,
    maxDetections = 10
)

// Thorough (small objects, crowded scenes):
YoloV8Detector(
    confidenceThreshold = 0.3f,  // lower → catches more, more false positives
    iouThreshold = 0.35f,
    maxDetections = 50
)
```

---

## 10. Summary

- YOLOv8n is the best starting point for real-time Android detection
- Output shape `[1, 84, 2100]` — 4 box coords + 80 class scores per anchor
- Always apply NMS after decoding — raw output has many overlapping boxes
- GPU delegate gives 3–4× speedup over CPU for YOLO
- `STRATEGY_KEEP_ONLY_LATEST` is essential — never queue old camera frames

---

## ➡️ Next Lesson

**[3.3 — Face Detection & Landmark with MediaPipe Face →](./03_mediapipe_face.md)**

---

*Module 3 · On-Device AI for Android Development*