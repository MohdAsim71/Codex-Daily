# 2.3 — Loading and Running a .tflite Model in Kotlin

> **Module 2 · Lesson 3**  
> 📱 Code · ⏱️ ~35 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Load a `.tflite` model from assets using both raw and Support Library approaches
- Understand interpreter options (threads, delegates, XNNPACK)
- Run inference manually with `ByteBuffer` and `FloatArray`
- Run inference using the TFLite Support Library (easier)
- Handle GPU delegate initialization and fallback gracefully

---

## 1. Two Ways to Run TFLite in Kotlin

```
Method A — Raw Interpreter API
  ├─ More control
  ├─ Works with any model
  └─ More boilerplate (you handle tensors manually)

Method B — TFLite Support Library
  ├─ Less code
  ├─ TensorImage, ImageProcessor handle preprocessing
  └─ Best for vision models (image input)
```

We'll cover both. In production, use Method B for image models and Method A for everything else (NLP, audio, custom models).

---

## 2. Method A — Raw Interpreter API

### 2.1 Load the Model

```kotlin
import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.MappedByteBuffer

/**
 * Load a TFLite model from assets using memory mapping.
 * Memory mapping is faster and uses less RAM than loading into a ByteArray.
 */
fun loadModel(context: Context, modelFileName: String): MappedByteBuffer {
    return FileUtil.loadMappedFile(context, modelFileName)
}
```

### 2.2 Configure Interpreter Options

```kotlin
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate

fun buildInterpreterOptions(
    useGpu: Boolean = false,
    useNnapi: Boolean = false,
    numThreads: Int = 4
): Interpreter.Options {

    val options = Interpreter.Options()

    when {
        useNnapi -> {
            val nnApiDelegate = NnApiDelegate(
                NnApiDelegate.Options().apply {
                    setAllowFp16(true)
                    setExecutionPreference(
                        NnApiDelegate.Options.EXECUTION_PREFERENCE_FAST_SINGLE_ANSWER
                    )
                }
            )
            options.addDelegate(nnApiDelegate)
        }

        useGpu -> {
            val gpuDelegate = GpuDelegate(
                GpuDelegate.Options().apply {
                    setPrecisionLossAllowed(true)  // FP16 on GPU = faster
                    setQuantizedModelsAllowed(true)
                }
            )
            options.addDelegate(gpuDelegate)
        }

        else -> {
            // CPU with optimizations
            options.setNumThreads(numThreads)
            options.setUseXNNPACK(true)        // Google's optimized CPU kernels
        }
    }

    return options
}
```

### 2.3 Inspect Input and Output Tensors

Always inspect tensors first — this tells you exactly what shape and type your arrays must be:

```kotlin
fun inspectModel(interpreter: Interpreter) {
    println("=== MODEL TENSOR INFO ===")

    for (i in 0 until interpreter.inputTensorCount) {
        val t = interpreter.getInputTensor(i)
        println("Input[$i]:")
        println("  name  : ${t.name()}")
        println("  shape : ${t.shape().toList()}")
        println("  dtype : ${t.dataType()}")
        println("  bytes : ${t.numBytes()}")
    }

    for (i in 0 until interpreter.outputTensorCount) {
        val t = interpreter.getOutputTensor(i)
        println("Output[$i]:")
        println("  name  : ${t.name()}")
        println("  shape : ${t.shape().toList()}")
        println("  dtype : ${t.dataType()}")
        println("  bytes : ${t.numBytes()}")
    }
}
```

### 2.4 Run Inference with FloatArrays

```kotlin
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Run inference on a pre-processed float array input.
 * This is the lowest-level approach — works with any model.
 */
fun runInferenceRaw(
    interpreter: Interpreter,
    inputData: FloatArray,        // pre-processed input, flattened
    outputSize: Int               // number of output values
): FloatArray {

    // Input ByteBuffer: 4 bytes per float
    val inputBuffer = ByteBuffer
        .allocateDirect(inputData.size * 4)
        .order(ByteOrder.nativeOrder())

    inputData.forEach { inputBuffer.putFloat(it) }
    inputBuffer.rewind()

    // Output ByteBuffer
    val outputBuffer = ByteBuffer
        .allocateDirect(outputSize * 4)
        .order(ByteOrder.nativeOrder())

    // Run inference
    interpreter.run(inputBuffer, outputBuffer)

    // Read output
    outputBuffer.rewind()
    val results = FloatArray(outputSize)
    outputBuffer.asFloatBuffer().get(results)
    return results
}
```

### 2.5 Multi-input / Multi-output Models

Some models (detection, segmentation) have multiple inputs or outputs. Use `runForMultipleInputsOutputs`:

```kotlin
fun runMultiOutputInference(interpreter: Interpreter, input: ByteBuffer) {

    // Output 0: bounding boxes [1, 10, 4]
    val boxesBuffer = ByteBuffer.allocateDirect(1 * 10 * 4 * 4).order(ByteOrder.nativeOrder())

    // Output 1: class scores [1, 10]
    val scoresBuffer = ByteBuffer.allocateDirect(1 * 10 * 4).order(ByteOrder.nativeOrder())

    // Output 2: detection count [1]
    val countBuffer = ByteBuffer.allocateDirect(1 * 4).order(ByteOrder.nativeOrder())

    val outputs = mapOf(
        0 to boxesBuffer,
        1 to scoresBuffer,
        2 to countBuffer
    )

    interpreter.runForMultipleInputsOutputs(arrayOf(input), outputs)

    // Read results
    scoresBuffer.rewind()
    val scores = FloatArray(10)
    scoresBuffer.asFloatBuffer().get(scores)
}
```

---

## 3. Method B — TFLite Support Library (Recommended for Vision)

The Support Library eliminates boilerplate for image preprocessing:

```kotlin
import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class TFLiteVisionClassifier(context: Context) {

    private val interpreter: Interpreter
    private val imageProcessor: ImageProcessor
    private val outputBuffer: TensorBuffer

    init {
        // Load model
        val model = FileUtil.loadMappedFile(context, "mobilenetv2.tflite")
        interpreter = Interpreter(model, Interpreter.Options().apply {
            setNumThreads(4)
            setUseXNNPACK(true)
        })

        // Build image preprocessing pipeline
        // This handles resize + normalize in one shot
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(
                floatArrayOf(0.485f, 0.456f, 0.406f),   // ImageNet mean
                floatArrayOf(0.229f, 0.224f, 0.225f)    // ImageNet std
            ))
            .build()

        // Pre-allocate output buffer (do this once, not every inference)
        val outputShape = interpreter.getOutputTensor(0).shape()       // [1, 1000]
        val outputType  = interpreter.getOutputTensor(0).dataType()
        outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType)
    }

    fun classify(bitmap: Bitmap): FloatArray {
        // Step 1: Wrap bitmap in TensorImage
        val tensorImage = TensorImage.fromBitmap(bitmap)

        // Step 2: Apply preprocessing pipeline (resize + normalize)
        val processedImage = imageProcessor.process(tensorImage)

        // Step 3: Run inference
        val startMs = SystemClock.uptimeMillis()
        interpreter.run(processedImage.buffer, outputBuffer.buffer.rewind())
        val latencyMs = SystemClock.uptimeMillis() - startMs

        android.util.Log.d("TFLite", "Inference: ${latencyMs}ms")

        // Step 4: Return raw scores (apply softmax separately if needed)
        return outputBuffer.floatArray
    }

    fun close() = interpreter.close()
}
```

---

## 4. Image Format Rules — NCHW vs NHWC

This trips up many developers. PyTorch and TFLite use **different channel orderings**:

```
PyTorch (training):   NCHW  →  [Batch, Channels, Height, Width]  e.g. [1, 3, 224, 224]
TFLite  (inference):  NHWC  →  [Batch, Height, Width, Channels]  e.g. [1, 224, 224, 3]
```

The TFLite converter handles this automatically during export. But if you're building input tensors manually, always check your model's expected input shape:

```kotlin
val inputShape = interpreter.getInputTensor(0).shape()
// [1, 224, 224, 3]  ← NHWC (TFLite default)
// [1, 3, 224, 224]  ← NCHW (unusual in TFLite)

// TFLite Support Library handles this for you — just pass a Bitmap
val tensorImage = TensorImage.fromBitmap(bitmap)  // always correct NHWC
```

---

## 5. Handling INT8 Quantized Models

INT8 models use `uint8` tensors (0–255) instead of `float32`. The Support Library handles the conversion:

```kotlin
// Check if model is quantized
val inputDtype = interpreter.getInputTensor(0).dataType()
// DataType.FLOAT32 = FP32 model
// DataType.UINT8   = INT8 quantized model

// For quantized models, NormalizeOp uses [0, 255] range instead of ImageNet stats
val imageProcessor = if (inputDtype == DataType.UINT8) {
    // Quantized: scale pixel values to [0, 255] uint8
    ImageProcessor.Builder()
        .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
        .add(CastOp(DataType.UINT8))
        .build()
} else {
    // Float: normalize to ImageNet distribution
    ImageProcessor.Builder()
        .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp(
            floatArrayOf(0.485f, 0.456f, 0.406f),
            floatArrayOf(0.229f, 0.224f, 0.225f)
        ))
        .build()
}
```

---

## 6. GPU Delegate — Initialization and Lifecycle

GPU delegate must be created and closed carefully to avoid memory leaks:

```kotlin
class TFLiteClassifierWithGpu(context: Context) {

    private var gpuDelegate: GpuDelegate? = null
    private val interpreter: Interpreter

    init {
        val model = FileUtil.loadMappedFile(context, "mobilenetv2.tflite")
        val options = Interpreter.Options()

        // Try GPU, fall back to CPU
        try {
            gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate!!)
            Log.d("TFLite", "Using GPU delegate")
        } catch (e: Exception) {
            Log.w("TFLite", "GPU not available, using CPU: ${e.message}")
            options.setNumThreads(4)
        }

        interpreter = Interpreter(model, options)
    }

    // Call this in ViewModel.onCleared() or Activity.onDestroy()
    fun close() {
        interpreter.close()
        gpuDelegate?.close()   // release GPU memory — don't forget!
    }
}
```

> ⚠️ **Memory leak warning:** If you forget `gpuDelegate?.close()`, the GPU memory stays allocated until the process dies. On Android, this causes OOM crashes on other apps sharing the GPU.

---

## 7. Thread Safety

`Interpreter` is **not thread-safe**. Never call `interpreter.run()` from multiple threads simultaneously:

```kotlin
// ❌ WRONG — race condition
Thread { interpreter.run(input1, output1) }.start()
Thread { interpreter.run(input2, output2) }.start()

// ✅ CORRECT — serialize inference calls
private val inferenceMutex = Mutex()

suspend fun classify(bitmap: Bitmap): FloatArray = withContext(Dispatchers.IO) {
    inferenceMutex.withLock {
        // Only one thread runs inference at a time
        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.floatArray
    }
}

// OR — create a separate interpreter per thread
// Each thread owns its own Interpreter instance
```

---

## 8. Complete Reference Card

```kotlin
// Load model
val model = FileUtil.loadMappedFile(context, "model.tflite")

// Create interpreter
val interpreter = Interpreter(model, Interpreter.Options().apply {
    setNumThreads(4)
    setUseXNNPACK(true)
    // addDelegate(GpuDelegate())    // for GPU
    // addDelegate(NnApiDelegate())  // for NPU
})

// Inspect tensors
val inputShape  = interpreter.getInputTensor(0).shape()   // e.g. [1, 224, 224, 3]
val outputShape = interpreter.getOutputTensor(0).shape()  // e.g. [1, 1000]
val inputType   = interpreter.getInputTensor(0).dataType() // FLOAT32 or UINT8

// Prepare output buffer
val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType)

// Run (Support Library — for images)
val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
interpreter.run(tensorImage.buffer, outputBuffer.buffer.rewind())

// Run (Raw — for any input)
interpreter.run(inputByteBuffer, outputByteBuffer)

// Get results
val scores: FloatArray = outputBuffer.floatArray

// Clean up
interpreter.close()
gpuDelegate?.close()
```

---

## 9. Summary

- Use **Method B** (Support Library) for image input — less code, same performance
- Use **Method A** (raw) for NLP, audio, or custom input shapes
- Always `inspectModel()` to confirm input/output shapes before writing inference code
- Pre-allocate `TensorBuffer` once — not inside the inference loop
- GPU delegate needs explicit `.close()` — not just the interpreter
- Never call `interpreter.run()` from multiple threads simultaneously

---

## ➡️ Next Lesson

**[2.4 — Using ONNX Runtime Mobile in Android →](./04_onnx_runtime_mobile.md)**

---

*Module 2 · On-Device AI for Android Development*