# 2.5 — Input/Output Tensor Handling in Kotlin

> **Module 2 · Lesson 5**  
> 📱 Code · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Convert between Bitmaps, ByteBuffers, FloatArrays and TensorImages
- Handle all common tensor data types (FLOAT32, UINT8, INT32, INT64)
- Apply correct preprocessing for different model types
- Decode and postprocess model outputs (classification, detection, segmentation)
- Avoid the most common tensor mistakes in Android AI

---

## 1. The Tensor Data Flow

Every Android AI inference follows this data flow:

```
Real-world Input
(Bitmap / audio bytes / text)
        │
        ▼  PREPROCESSING
ByteBuffer / FloatArray
(model's expected format)
        │
        ▼  INFERENCE
interpreter.run(input, output)
        │
        ▼  POSTPROCESSING
Decoded Results
(labels, boxes, text, probabilities)
```

Understanding tensors means mastering each of these conversion steps.

---

## 2. Data Types You'll Encounter

| TFLite DataType | Kotlin Type | Bytes/Value | Used For |
|---|---|---|---|
| `FLOAT32` | `Float` | 4 | FP32 models (default) |
| `UINT8` | `Byte` (0–255) | 1 | INT8 quantized models |
| `INT32` | `Int` | 4 | Index outputs, token IDs |
| `INT64` | `Long` | 8 | NLP token IDs (BERT) |
| `BOOL` | `Boolean` | 1 | Mask inputs |

Check your model's type:
```kotlin
val dtype = interpreter.getInputTensor(0).dataType()
// Returns: DataType.FLOAT32, DataType.UINT8, etc.
```

---

## 3. Input: Bitmap → ByteBuffer

The most common conversion — camera frame or gallery image to model input.

### 3.1 FLOAT32 model input (most common)

```kotlin
import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Convert a Bitmap to a FLOAT32 ByteBuffer for TFLite.
 * Applies ImageNet normalization.
 *
 * Output shape: [1, H, W, 3] NHWC — TFLite default
 */
fun bitmapToFloat32Buffer(
    bitmap: Bitmap,
    inputH: Int = 224,
    inputW: Int = 224
): ByteBuffer {
    // Resize bitmap to model's expected input size
    val resized = Bitmap.createScaledBitmap(bitmap, inputW, inputH, true)

    // Allocate: 1 batch × H × W × 3 channels × 4 bytes per float
    val buffer = ByteBuffer
        .allocateDirect(1 * inputH * inputW * 3 * 4)
        .order(ByteOrder.nativeOrder())

    val pixels = IntArray(inputH * inputW)
    resized.getPixels(pixels, 0, inputW, 0, 0, inputW, inputH)

    // ImageNet normalization
    val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
    val std  = floatArrayOf(0.229f, 0.224f, 0.225f)

    for (pixel in pixels) {
        val r = ((pixel shr 16) and 0xFF) / 255.0f
        val g = ((pixel shr 8)  and 0xFF) / 255.0f
        val b = (pixel          and 0xFF) / 255.0f

        buffer.putFloat((r - mean[0]) / std[0])
        buffer.putFloat((g - mean[1]) / std[1])
        buffer.putFloat((b - mean[2]) / std[2])
    }

    buffer.rewind()
    return buffer
}
```

### 3.2 UINT8 model input (INT8 quantized)

```kotlin
/**
 * Convert Bitmap to UINT8 ByteBuffer for INT8 quantized models.
 * Pixel values stay in [0, 255] range — no float normalization.
 */
fun bitmapToUInt8Buffer(bitmap: Bitmap, inputH: Int = 224, inputW: Int = 224): ByteBuffer {
    val resized = Bitmap.createScaledBitmap(bitmap, inputW, inputH, true)

    // 1 byte per channel for UINT8
    val buffer = ByteBuffer
        .allocateDirect(1 * inputH * inputW * 3)
        .order(ByteOrder.nativeOrder())

    val pixels = IntArray(inputH * inputW)
    resized.getPixels(pixels, 0, inputW, 0, 0, inputW, inputH)

    for (pixel in pixels) {
        buffer.put(((pixel shr 16) and 0xFF).toByte())  // R
        buffer.put(((pixel shr 8)  and 0xFF).toByte())  // G
        buffer.put((pixel          and 0xFF).toByte())  // B
    }

    buffer.rewind()
    return buffer
}
```

### 3.3 Using TFLite Support Library (easiest — recommended)

```kotlin
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.common.ops.NormalizeOp

// Build the processor once (reuse per inference)
val imageProcessor = ImageProcessor.Builder()
    .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
    .add(NormalizeOp(
        floatArrayOf(0.485f, 0.456f, 0.406f),
        floatArrayOf(0.229f, 0.224f, 0.225f)
    ))
    .build()

// Per-inference usage (just 2 lines)
val tensorImage = TensorImage.fromBitmap(bitmap)
val processed = imageProcessor.process(tensorImage)
// processed.buffer is ready to pass to interpreter.run()
```

---

## 4. Output: ByteBuffer → Results

### 4.1 Classification output — FloatArray of probabilities

```kotlin
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

// Allocate output buffer (do this ONCE before inference loop)
val outputShape = interpreter.getOutputTensor(0).shape()   // e.g. [1, 1000]
val outputType  = interpreter.getOutputTensor(0).dataType()
val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType)

// After interpreter.run():
val scores: FloatArray = outputBuffer.floatArray   // length 1000

// Get top-K
fun topK(scores: FloatArray, k: Int = 5): List<Pair<Int, Float>> {
    return scores
        .mapIndexed { index, score -> index to score }
        .sortedByDescending { it.second }
        .take(k)
}

val top5 = topK(scores)
top5.forEach { (classIdx, prob) ->
    println("Class $classIdx: ${prob * 100:.1f}%")
}
```

### 4.2 Softmax (if model doesn't apply it internally)

```kotlin
fun softmax(logits: FloatArray): FloatArray {
    val maxLogit = logits.max()
    val expScores = logits.map { Math.exp((it - maxLogit).toDouble()).toFloat() }
    val sumExp = expScores.sum()
    return expScores.map { it / sumExp }.toFloatArray()
}

// Check if you need softmax:
// - Model output sums to ~1.0 already → softmax already applied (probabilities)
// - Model output has negative values / doesn't sum to 1 → apply softmax
val sum = scores.sum()
val needsSoftmax = Math.abs(sum - 1.0f) > 0.01f
val probabilities = if (needsSoftmax) softmax(scores) else scores
```

### 4.3 Object detection output — boxes + scores + classes

Detection models have multiple outputs. Shapes vary by model:

```kotlin
// Typical TFLite SSD detection model outputs:
// Output 0: boxes  [1, numDetections, 4]  — [ymin, xmin, ymax, xmax] normalized 0-1
// Output 1: scores [1, numDetections]     — confidence 0-1
// Output 2: classes[1, numDetections]     — class index (float)
// Output 3: count  [1]                    — number of valid detections

data class Detection(
    val classIndex: Int,
    val confidence: Float,
    val boundingBox: RectF   // normalized coordinates [0, 1]
)

fun decodeDetections(
    boxesArray: FloatArray,      // flat [ymin0, xmin0, ymax0, xmax0, ymin1, ...]
    scoresArray: FloatArray,
    classesArray: FloatArray,
    detectionCount: Int,
    confidenceThreshold: Float = 0.5f
): List<Detection> {

    val detections = mutableListOf<Detection>()

    for (i in 0 until detectionCount) {
        val score = scoresArray[i]
        if (score < confidenceThreshold) continue

        val ymin = boxesArray[i * 4 + 0]
        val xmin = boxesArray[i * 4 + 1]
        val ymax = boxesArray[i * 4 + 2]
        val xmax = boxesArray[i * 4 + 3]

        detections.add(Detection(
            classIndex = classesArray[i].toInt(),
            confidence = score,
            boundingBox = RectF(xmin, ymin, xmax, ymax)
        ))
    }

    return detections.sortedByDescending { it.confidence }
}
```

### 4.4 Scale bounding boxes to screen coordinates

```kotlin
fun scaleBoxToScreen(
    normalizedBox: RectF,    // [0, 1] normalized
    imageWidth: Int,
    imageHeight: Int
): RectF {
    return RectF(
        normalizedBox.left   * imageWidth,
        normalizedBox.top    * imageHeight,
        normalizedBox.right  * imageWidth,
        normalizedBox.bottom * imageHeight
    )
}
```

---

## 5. Text Input — Tokenization for NLP Models

NLP models take token IDs as input, not raw strings:

```kotlin
// Simple whitespace tokenizer for demonstration
// In production, use a proper BERT tokenizer library
class SimpleTokenizer(vocabPath: String, context: Context) {

    private val vocab: Map<String, Int>

    init {
        vocab = mutableMapOf<String, Int>().also { map ->
            context.assets.open(vocabPath).bufferedReader().forEachLine { line ->
                val parts = line.trim().split("\t")
                if (parts.size == 2) map[parts[0]] = parts[1].toInt()
            }
        }
    }

    fun tokenize(text: String, maxLength: Int = 128): Pair<LongArray, LongArray> {
        val words = text.lowercase().split(" ")
        val cls = 101L   // [CLS] token
        val sep = 102L   // [SEP] token
        val pad = 0L     // [PAD] token
        val unk = 100L   // [UNK] token

        val tokenIds = mutableListOf(cls)
        for (word in words) {
            tokenIds.add(vocab[word]?.toLong() ?: unk)
            if (tokenIds.size >= maxLength - 1) break
        }
        tokenIds.add(sep)

        // Pad or truncate to maxLength
        while (tokenIds.size < maxLength) tokenIds.add(pad)

        val inputIds      = tokenIds.take(maxLength).toLongArray()
        val attentionMask = LongArray(maxLength) { if (inputIds[it] != pad) 1L else 0L }

        return inputIds to attentionMask
    }
}
```

---

## 6. Audio Input — Float PCM Waveform

For speech models (Whisper, Wav2Vec):

```kotlin
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.AudioFormat
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun recordAudioToFloatBuffer(durationMs: Int = 3000): FloatArray {
    val sampleRate = 16000    // most speech models expect 16kHz
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    val totalSamples = sampleRate * durationMs / 1000

    val recorder = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate, channelConfig, audioFormat, bufferSize
    )

    val pcmBuffer = ShortArray(totalSamples)
    recorder.startRecording()
    recorder.read(pcmBuffer, 0, totalSamples)
    recorder.stop()
    recorder.release()

    // Convert Short PCM [-32768, 32767] to Float [-1.0, 1.0]
    return FloatArray(totalSamples) { i -> pcmBuffer[i] / 32768.0f }
}
```

---

## 7. Common Tensor Mistakes and Fixes

| Mistake | Symptom | Fix |
|---|---|---|
| Forgot `buffer.rewind()` | All-zero output every time | Call `buffer.rewind()` before `interpreter.run()` |
| Wrong channel order (BGR vs RGB) | Wrong predictions | Android `getPixels()` returns ARGB — extract R,G,B correctly |
| Wrong normalization range | Low confidence on all classes | Match normalization to training: [0,1] or ImageNet stats |
| NCHW vs NHWC | Garbage output / shape error | TFLite=NHWC, ONNX=NCHW — transpose accordingly |
| Not allocating output buffer | NPE / shape error | Pre-allocate `TensorBuffer.createFixedSize()` |
| Re-creating buffer every frame | Slow inference (60→10fps) | Allocate once, reuse per inference |
| Not calling `rewind()` on output | Reads stale previous result | `outputBuffer.buffer.rewind()` after `run()` |

---

## 8. Performance Tips for Tensor Operations

```kotlin
// ❌ Slow — creates new objects every frame
fun badInference(bitmap: Bitmap) {
    val inputBuffer = ByteBuffer.allocateDirect(...)  // allocation every frame!
    val outputArray = FloatArray(1000)                // allocation every frame!
    interpreter.run(inputBuffer, outputArray)
}

// ✅ Fast — pre-allocate, reuse
class GoodClassifier {
    // Allocated ONCE in init
    private val inputBuffer  = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
        .order(ByteOrder.nativeOrder())
    private val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1000), FLOAT32)

    fun classify(bitmap: Bitmap): FloatArray {
        fillInputBuffer(bitmap)               // reuses inputBuffer
        interpreter.run(
            inputBuffer,
            outputBuffer.buffer.rewind()      // reuses outputBuffer
        )
        return outputBuffer.floatArray
    }
}
```

---

## 9. Summary Cheat Sheet

```kotlin
// ── Input: Bitmap → TFLite (FLOAT32, NHWC) ───────────
val inputBuffer = ByteBuffer.allocateDirect(1 * H * W * 3 * 4).order(nativeOrder())
bitmap.getPixels(pixels, ...)
pixels.forEach { inputBuffer.putFloat(normalizedR); putFloat(G); putFloat(B) }
inputBuffer.rewind()

// ── Input: Support Library (easier) ──────────────────
val processed = imageProcessor.process(TensorImage.fromBitmap(bitmap))
// use processed.buffer directly

// ── Output: Allocate once ─────────────────────────────
val outputBuffer = TensorBuffer.createFixedSize(outputShape, FLOAT32)

// ── Run inference ─────────────────────────────────────
interpreter.run(inputBuffer, outputBuffer.buffer.rewind())

// ── Decode output ─────────────────────────────────────
val scores = outputBuffer.floatArray
val topIdx  = scores.indices.maxByOrNull { scores[it] }

// ── Always rewind before run ──────────────────────────
// inputBuffer.rewind()            ← for manual ByteBuffer
// outputBuffer.buffer.rewind()    ← for TensorBuffer
```

---

## ➡️ Next Lesson

**[2.6 — 🛠️ Project: Image Classifier Android App →](./06_project_image_classifier.md)**

---

*Module 2 · On-Device AI for Android Development*