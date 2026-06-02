# 2.4 — Using ONNX Runtime Mobile in Android

> **Module 2 · Lesson 4**  
> 📱 Code · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Add ONNX Runtime Mobile to an Android project
- Load and run `.onnx` models in Kotlin
- Use NNAPI execution provider for NPU acceleration
- Know when to choose ONNX Runtime over TFLite

---

## 1. When to Choose ONNX Runtime over TFLite

```
Choose ONNX Runtime when:
  ✅ Your model was trained in PyTorch (no conversion to TFLite needed)
  ✅ You need the same model on Android + iOS + Windows + Linux
  ✅ Your model uses ops that TFLite doesn't support (e.g. some transformers)
  ✅ You're deploying NLP models (BERT, DistilBERT, multilingual models)

Choose TFLite when:
  ✅ You need the smallest binary size
  ✅ You want the most Android-specific optimizations
  ✅ Your model is in TensorFlow/Keras originally
  ✅ You want GPU delegate (TFLite GPU is more mature than ORT GPU on Android)
```

---

## 2. Add ONNX Runtime to Gradle

```kotlin
// app/build.gradle

dependencies {
    // Full ONNX Runtime Android (~30 MB AAR)
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.17.0")

    // OR — Mobile-optimized smaller build (~7 MB)
    // implementation("com.microsoft.onnxruntime:onnxruntime-mobile:1.17.0")
    // Note: onnxruntime-mobile supports fewer ops — check your model first
}
```

> 💡 **Which to use?** Start with `onnxruntime-android` (full). If APK size matters, switch to `onnxruntime-mobile` and verify your model's ops are all supported.

Also add to `androidResources`:

```kotlin
androidResources {
    noCompress += listOf("tflite", "onnx", "bin", "task")
}
```

---

## 3. Add Your ONNX Model to Assets

```
app/src/main/assets/
├── mobilenetv2.onnx         ← your ONNX model
└── imagenet_labels.txt
```

---

## 4. Core ONNX Runtime Concepts

| Concept | Description | TFLite equivalent |
|---|---|---|
| `OrtEnvironment` | Global runtime — create once per app | N/A (implicit) |
| `OrtSession` | Loaded model instance | `Interpreter` |
| `OrtSession.SessionOptions` | Configure threads, execution providers | `Interpreter.Options` |
| `OnnxTensor` | Input/output data wrapper | `TensorBuffer` / `ByteBuffer` |
| Execution Provider | Hardware backend (CPU, NNAPI, CUDA) | Delegate (GPU, NNAPI) |

---

## 5. Basic ONNX Runtime Inference in Kotlin

```kotlin
import ai.onnxruntime.*
import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import java.nio.FloatBuffer

class OnnxClassifier(context: Context) {

    companion object {
        private const val TAG = "OnnxClassifier"
        private const val MODEL_FILE = "mobilenetv2.onnx"
        private const val INPUT_SIZE = 224
    }

    // OrtEnvironment is expensive to create — create once and reuse
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val inputName: String
    private val outputName: String

    init {
        // Read model bytes from assets
        val modelBytes = context.assets.open(MODEL_FILE).readBytes()

        // Session options — configure hardware
        val sessionOptions = OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(4)     // CPU threads

            // Enable NNAPI (routes to NPU on supported devices)
            try {
                addNnapi()
                Log.d(TAG, "NNAPI execution provider enabled")
            } catch (e: OrtException) {
                Log.w(TAG, "NNAPI not available: ${e.message}")
            }
        }

        session = ortEnv.createSession(modelBytes, sessionOptions)

        // Get input/output names from model
        inputName  = session.inputNames.first()
        outputName = session.outputNames.first()

        Log.d(TAG, "ONNX model loaded")
        Log.d(TAG, "Input  name: $inputName")
        Log.d(TAG, "Output name: $outputName")
        logModelInfo()
    }

    fun classify(bitmap: Bitmap): FloatArray {
        // Step 1: Preprocess bitmap → float array (NCHW for ONNX)
        val inputArray = preprocessBitmap(bitmap)

        // Step 2: Create OnnxTensor from float array
        val shape = longArrayOf(1, 3, INPUT_SIZE.toLong(), INPUT_SIZE.toLong()) // NCHW
        val inputTensor = OnnxTensor.createTensor(
            ortEnv,
            FloatBuffer.wrap(inputArray),
            shape
        )

        // Step 3: Run inference
        val startMs = SystemClock.uptimeMillis()
        val results = session.run(mapOf(inputName to inputTensor))
        val latencyMs = SystemClock.uptimeMillis() - startMs
        Log.d(TAG, "Inference: ${latencyMs}ms")

        // Step 4: Extract output
        val outputTensor = results[outputName]?.value as Array<FloatArray>
        val scores = outputTensor[0]   // shape [1000]

        // Clean up tensors
        inputTensor.close()
        results.close()

        return scores
    }

    private fun preprocessBitmap(bitmap: Bitmap): FloatArray {
        val resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)

        // ImageNet normalization values
        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std  = floatArrayOf(0.229f, 0.224f, 0.225f)

        // ONNX models typically expect NCHW: [1, 3, H, W]
        // Build as 3 separate channel arrays then interleave
        val floatArray = FloatArray(3 * INPUT_SIZE * INPUT_SIZE)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8)  and 0xFF) / 255.0f
            val b = (pixel          and 0xFF) / 255.0f

            // NCHW layout: channel R at offset 0, G at INPUT_SIZE*INPUT_SIZE, etc.
            floatArray[i]                           = (r - mean[0]) / std[0]
            floatArray[INPUT_SIZE * INPUT_SIZE + i] = (g - mean[1]) / std[1]
            floatArray[2 * INPUT_SIZE * INPUT_SIZE + i] = (b - mean[2]) / std[2]
        }

        return floatArray
    }

    private fun logModelInfo() {
        session.inputInfo.forEach { (name, info) ->
            val tensorInfo = info.info as TensorInfo
            Log.d(TAG, "Input  '$name': shape=${tensorInfo.shape.toList()}, type=${tensorInfo.type}")
        }
        session.outputInfo.forEach { (name, info) ->
            val tensorInfo = info.info as TensorInfo
            Log.d(TAG, "Output '$name': shape=${tensorInfo.shape.toList()}, type=${tensorInfo.type}")
        }
    }

    fun close() {
        session.close()
        ortEnv.close()
    }
}
```

---

## 6. Execution Providers — Hardware Acceleration

ONNX Runtime uses **Execution Providers (EPs)** instead of delegates:

```kotlin
val sessionOptions = OrtSession.SessionOptions().apply {
    setIntraOpNumThreads(4)

    // NNAPI — routes to NPU/GPU on Android 8.1+ (recommended first choice)
    addNnapi()

    // CPU only — always available, no extra setup
    // (CPU EP is always the fallback — you don't need to add it explicitly)
}
```

**Available EPs for Android:**

| Execution Provider | Hardware | API |
|---|---|---|
| CPU EP | ARM CPU (default) | Always available |
| NNAPI EP | NPU / GPU via Android NNAPI | `addNnapi()` |
| XNNPACK EP | Optimized CPU | `addXnnpack(mapOf())` |

> 💡 Unlike TFLite which has separate GPU Delegate and NNAPI Delegate, ONNX Runtime on Android primarily uses NNAPI to access NPU/GPU. Direct GPU delegate for ORT Mobile is experimental.

---

## 7. Inspecting an ONNX Model

Useful for debugging shape and type mismatches:

```kotlin
fun inspectOnnxModel(context: Context, modelFileName: String) {
    val ortEnv = OrtEnvironment.getEnvironment()
    val modelBytes = context.assets.open(modelFileName).readBytes()
    val session = ortEnv.createSession(modelBytes)

    println("=== ONNX MODEL INFO: $modelFileName ===")
    println("\nInputs:")
    session.inputInfo.forEach { (name, nodeInfo) ->
        val info = nodeInfo.info as? TensorInfo
        println("  $name: shape=${info?.shape?.toList()}, type=${info?.type}")
    }

    println("\nOutputs:")
    session.outputInfo.forEach { (name, nodeInfo) ->
        val info = nodeInfo.info as? TensorInfo
        println("  $name: shape=${info?.shape?.toList()}, type=${info?.type}")
    }

    session.close()
    ortEnv.close()
}
```

---

## 8. Running an NLP Model (BERT/DistilBERT)

ONNX Runtime shines for NLP models. Here's how to run a text classifier:

```kotlin
class OnnxTextClassifier(context: Context) {

    private val ortEnv = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    // BERT-style models need a tokenizer — use HuggingFace tokenizers library
    // or BertTokenizer from tensorflow-text

    init {
        val modelBytes = context.assets.open("distilbert_sentiment.onnx").readBytes()
        session = ortEnv.createSession(modelBytes, OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(4)
        })
    }

    fun classify(inputIds: LongArray, attentionMask: LongArray): FloatArray {
        val seqLen = inputIds.size.toLong()

        // BERT inputs: input_ids and attention_mask (both shape [1, seqLen])
        val inputIdsTensor = OnnxTensor.createTensor(
            ortEnv, inputIds.map { it }.toLongArray().let {
                java.nio.LongBuffer.wrap(it)
            }, longArrayOf(1, seqLen)
        )

        val attentionTensor = OnnxTensor.createTensor(
            ortEnv, attentionMask.map { it }.toLongArray().let {
                java.nio.LongBuffer.wrap(it)
            }, longArrayOf(1, seqLen)
        )

        val inputs = mapOf(
            "input_ids"      to inputIdsTensor,
            "attention_mask" to attentionTensor
        )

        val output = session.run(inputs)
        val logits = (output["logits"]?.value as Array<FloatArray>)[0]

        inputIdsTensor.close()
        attentionTensor.close()
        output.close()

        return softmax(logits)
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.max()
        val exps = logits.map { Math.exp((it - maxLogit).toDouble()).toFloat() }
        val sum = exps.sum()
        return exps.map { it / sum }.toFloatArray()
    }

    fun close() { session.close(); ortEnv.close() }
}
```

---

## 9. TFLite vs ONNX Runtime — Quick Reference

| | TFLite | ONNX Runtime |
|---|---|---|
| **Input format** | NHWC | NCHW (PyTorch default) |
| **GPU acceleration** | ✅ GpuDelegate (mature) | ⚠️ Experimental on Android |
| **NPU via NNAPI** | ✅ NnApiDelegate | ✅ `addNnapi()` |
| **NLP models** | ⚠️ Limited op support | ✅ Strong transformer support |
| **Binary size** | ~1.2 MB | ~7–30 MB |
| **Cross-platform** | Android/iOS/Pi | All platforms |
| **PyTorch export** | Needs conversion | ✅ Direct |

---

## 10. Summary

- ONNX Runtime Mobile lets you skip the PyTorch → TFLite conversion
- Use `OrtEnvironment` once per app, `OrtSession` per model
- `addNnapi()` enables hardware acceleration on Android
- NCHW layout for ONNX vs NHWC for TFLite — always check `inputInfo`
- ONNX Runtime is the better choice for BERT/transformer NLP models
- Always `.close()` tensors and sessions to avoid memory leaks

---

## ➡️ Next Lesson

**[2.5 — Input/Output Tensor Handling in Kotlin →](./05_tensor_handling.md)**

---

*Module 2 · On-Device AI for Android Development*