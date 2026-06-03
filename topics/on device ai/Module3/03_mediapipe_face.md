# 3.3 — Face Detection & Landmark with MediaPipe Face

> **Module 3 · Lesson 3**  
> 📱 Code · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Add MediaPipe Face Detector and Face Landmarker to Android
- Detect faces and get bounding boxes in real-time via CameraX
- Extract 478 facial landmarks for face mesh, emotion, and AR applications
- Handle front camera mirroring correctly

---

## 1. MediaPipe Face Solutions

MediaPipe offers two face AI solutions:

```
Face Detector
  ├─ Output: bounding boxes + 6 keypoints per face
  ├─ Speed: very fast (~5ms)
  ├─ Use for: face presence check, counting faces, face cropping
  └─ Model size: ~0.8 MB

Face Landmarker
  ├─ Output: 478 3D landmarks per face + face blendshapes
  ├─ Speed: moderate (~15ms on GPU)
  ├─ Use for: face mesh, AR filters, emotion analysis, eye tracking
  └─ Model size: ~9 MB bundled
```

---

## 2. Gradle Dependencies

```kotlin
// app/build.gradle
dependencies {
    // MediaPipe Tasks Vision (includes Face Detector + Landmarker)
    implementation("com.google.mediapipe:tasks-vision:0.10.14")

    // CameraX (from lesson 3.1)
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
}
```

---

## 3. Download MediaPipe Models

MediaPipe models are downloaded once and bundled as assets:

```kotlin
// Download models — add to build.gradle or download manually:
// Face Detector: https://storage.googleapis.com/mediapipe-models/face_detector/blaze_face_short_range/float16/latest/blaze_face_short_range.tflite
// Face Landmarker: https://storage.googleapis.com/mediapipe-models/face_landmarker/face_landmarker/float16/latest/face_landmarker.task

// Place in: app/src/main/assets/
// face_detector.tflite
// face_landmarker.task
```

Or use the MediaPipe model downloader in your `build.gradle`:
```kotlin
// In app/build.gradle — auto-downloads during build
apply plugin: "de.undercouch.download"
task downloadFaceModels(type: Download) {
    src "https://storage.googleapis.com/mediapipe-models/face_detector/blaze_face_short_range/float16/latest/blaze_face_short_range.tflite"
    dest "$projectDir/src/main/assets/face_detector.tflite"
    overwrite false
}
```

---

## 4. Part A — Face Detector (Fast, Bounding Boxes Only)

### 4.1 FaceDetectorHelper.kt

```kotlin
// FaceDetectorHelper.kt
package com.yourname.vision

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult

class FaceDetectorHelper(
    private val context: Context,
    private val minDetectionConfidence: Float = 0.5f,
    private val useGpu: Boolean = true,
    private val onResults: (FaceDetectorResult, Long) -> Unit,
    private val onError: (String) -> Unit
) {
    companion object {
        private const val TAG = "FaceDetectorHelper"
        private const val MODEL_FILE = "face_detector.tflite"
    }

    private var faceDetector: FaceDetector? = null

    init {
        setupDetector()
    }

    private fun setupDetector() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(MODEL_FILE)
            .setDelegate(if (useGpu) Delegate.GPU else Delegate.CPU)
            .build()

        val options = FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(baseOptions)
            .setMinDetectionConfidence(minDetectionConfidence)
            .setRunningMode(RunningMode.LIVE_STREAM)        // for CameraX
            .setResultListener { result, _ ->
                val inferenceMs = SystemClock.uptimeMillis()
                onResults(result, inferenceMs)
            }
            .setErrorListener { error ->
                onError(error.message ?: "Unknown error")
            }
            .build()

        try {
            faceDetector = FaceDetector.createFromOptions(context, options)
            Log.d(TAG, "Face Detector initialized")
        } catch (e: Exception) {
            onError("Face detector init failed: ${e.message}")
        }
    }

    fun detectAsync(bitmap: Bitmap, frameTimeMs: Long) {
        val mpImage = BitmapImageBuilder(bitmap).build()
        faceDetector?.detectAsync(mpImage, frameTimeMs)
    }

    fun close() {
        faceDetector?.close()
        faceDetector = null
    }
}
```

### 4.2 CameraX Analyzer for Face Detection

```kotlin
// FaceDetectorAnalyzer.kt
class FaceDetectorAnalyzer(
    private val helper: FaceDetectorHelper
) : ImageAnalysis.Analyzer {

    private var frameTimestamp = 0L

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxy.toBitmap()
            // Pass frame timestamp for LIVE_STREAM mode
            helper.detectAsync(bitmap, frameTimestamp++)
        } finally {
            imageProxy.close()
        }
    }
}
```

### 4.3 Decode Face Detector Results

```kotlin
// In your UI — handle FaceDetectorResult
fun handleFaceResults(result: FaceDetectorResult, imageWidth: Int, imageHeight: Int) {
    val detections = result.detections()

    detections.forEach { detection ->
        // Bounding box (normalized [0,1])
        val box = detection.boundingBox()
        val left   = box.left   * imageWidth
        val top    = box.top    * imageHeight
        val right  = box.right  * imageWidth
        val bottom = box.bottom * imageHeight

        Log.d("Face", "Face detected at [$left, $top, $right, $bottom]")
        Log.d("Face", "Confidence: ${detection.categories().first().score()}")

        // 6 keypoints: left eye, right eye, nose tip,
        //              mouth center, left ear tragion, right ear tragion
        detection.keypoints()?.forEachIndexed { idx, kp ->
            val x = kp.x() * imageWidth
            val y = kp.y() * imageHeight
            Log.d("Face", "Keypoint[$idx]: ($x, $y)")
        }
    }

    Log.d("Face", "Total faces: ${detections.size}")
}
```

---

## 5. Part B — Face Landmarker (478 Points, AR-Ready)

### 5.1 FaceLandmarkerHelper.kt

```kotlin
// FaceLandmarkerHelper.kt
package com.yourname.vision

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceLandmarkerHelper(
    private val context: Context,
    private val numFaces: Int = 1,             // max faces to track simultaneously
    private val minFaceDetectionConfidence: Float = 0.5f,
    private val minFacePresenceConfidence: Float = 0.5f,
    private val minTrackingConfidence: Float = 0.5f,
    private val useGpu: Boolean = true,
    private val onResults: (FaceLandmarkerResult, Int, Int) -> Unit,
    private val onError: (String) -> Unit
) {
    private var faceLandmarker: FaceLandmarker? = null
    private var imageWidth = 0
    private var imageHeight = 0

    init { setup() }

    private fun setup() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .setDelegate(if (useGpu) Delegate.GPU else Delegate.CPU)
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumFaces(numFaces)
            .setMinFaceDetectionConfidence(minFaceDetectionConfidence)
            .setMinFacePresenceConfidence(minFacePresenceConfidence)
            .setMinTrackingConfidence(minTrackingConfidence)
            .setOutputFaceBlendshapes(true)    // for emotion/expression analysis
            .setOutputFacialTransformationMatrixes(true)  // for 3D head pose
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                onResults(result, imageWidth, imageHeight)
            }
            .setErrorListener { error -> onError(error.message ?: "Error") }
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(context, options)
    }

    fun detectAsync(bitmap: Bitmap, frameTimeMs: Long) {
        imageWidth = bitmap.width
        imageHeight = bitmap.height
        val mpImage = BitmapImageBuilder(bitmap).build()
        faceLandmarker?.detectAsync(mpImage, frameTimeMs)
    }

    fun close() { faceLandmarker?.close() }
}
```

### 5.2 Decode Face Landmarks

```kotlin
// Handle FaceLandmarkerResult
fun handleLandmarkResults(
    result: FaceLandmarkerResult,
    imageWidth: Int,
    imageHeight: Int
) {
    if (result.faceLandmarks().isEmpty()) return

    val landmarks = result.faceLandmarks()[0]   // first face

    // 478 landmarks — each has x, y, z (normalized to image dimensions)
    // Key landmark indices:
    val NOSE_TIP        = 4
    val LEFT_EYE_INNER  = 133
    val RIGHT_EYE_INNER = 362
    val MOUTH_LEFT      = 61
    val MOUTH_RIGHT     = 291
    val CHIN            = 152

    val noseTip = landmarks[NOSE_TIP]
    val noseX = noseTip.x() * imageWidth
    val noseY = noseTip.y() * imageHeight
    Log.d("Landmark", "Nose tip: ($noseX, $noseY)")

    // Face blendshapes (if enabled) — 52 expression coefficients
    result.faceBlendshapes().firstOrNull()?.let { blendshapes ->
        // Emotion-relevant blendshapes
        val smileLeft  = blendshapes.find { it.categoryName() == "mouthSmileLeft" }?.score() ?: 0f
        val smileRight = blendshapes.find { it.categoryName() == "mouthSmileRight" }?.score() ?: 0f
        val eyeBlinkL  = blendshapes.find { it.categoryName() == "eyeBlinkLeft" }?.score() ?: 0f

        if (smileLeft > 0.5f && smileRight > 0.5f) Log.d("Emotion", "Smiling!")
        if (eyeBlinkL > 0.7f) Log.d("Emotion", "Left eye blink!")
    }
}
```

---

## 6. Handle Front Camera Mirroring

The front camera delivers mirrored images. Landmark X coordinates must be flipped:

```kotlin
fun mirrorLandmarkX(normalizedX: Float): Float {
    return 1.0f - normalizedX
}

// When using front camera, mirror ALL x coordinates for display
val displayX = if (isFrontCamera) {
    (1.0f - landmark.x()) * viewWidth
} else {
    landmark.x() * viewWidth
}
val displayY = landmark.y() * viewHeight
```

---

## 7. Key Landmark Indices Reference

```
MediaPipe 478-point face mesh — important indices:

FACE OVAL:  10, 338, 297, 332, 284, 251, 389, 356, 454, 323, 361, 288,
            397, 365, 379, 378, 400, 377, 152, 148, 176, 149, 150, 136,
            172, 58, 132, 93, 234, 127, 162, 21, 54, 103, 67, 109

LEFT EYE:   33, 7, 163, 144, 145, 153, 154, 155, 133
RIGHT EYE:  362, 382, 381, 380, 374, 373, 390, 249, 263

LIPS OUTER: 61, 146, 91, 181, 84, 17, 314, 405, 321, 375, 291
LIPS INNER: 78, 191, 80, 81, 82, 13, 312, 311, 310, 415, 308

NOSE:       1 (bridge), 4 (tip), 5, 6

EYEBROWS:   Left: 46, 53, 52, 65, 55 | Right: 276, 283, 282, 295, 285
```

---

## 8. Performance Reference

| Solution | Latency (GPU) | Latency (CPU) | Use Case |
|---|---|---|---|
| Face Detector | ~5ms | ~15ms | Presence check, counting |
| Face Landmarker (1 face) | ~15ms | ~50ms | AR, expressions, mesh |
| Face Landmarker (3 faces) | ~35ms | ~130ms | Multi-person scenes |

---

## 9. Summary

- `FaceDetector` — fast, gives boxes + 6 keypoints. Best for presence detection.
- `FaceLandmarker` — slower, gives 478 3D landmarks + 52 blendshapes. Best for AR and expressions.
- Use `RunningMode.LIVE_STREAM` + `detectAsync()` for CameraX pipelines.
- Always mirror X coordinates when using the front camera.
- Close helpers in `onDestroy()` — MediaPipe holds GPU resources.

---

## ➡️ Next Lesson

**[3.4 — On-Device OCR with ML Kit Text Recognition v2 →](./04_mlkit_ocr.md)**

---

*Module 3 · On-Device AI for Android Development*