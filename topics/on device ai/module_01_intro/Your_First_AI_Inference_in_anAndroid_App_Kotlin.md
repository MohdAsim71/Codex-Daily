# 1.5 — Your First AI Inference in an Android App (Kotlin)

> **Module 1 · Lesson 5**  
> 📱 Code · ⏱️ ~35 min

---

## 🎯 What You'll Build

A complete Android app that:
- Loads a pre-trained MobileNetV2 image classifier (`.tflite`)
- Accepts an image from the gallery
- Runs inference **fully on-device** — no internet, no API
- Displays the top-3 predicted labels with confidence scores
- Shows inference time in milliseconds

```
┌─────────────────────────┐
│   On-Device Classifier  │
│                         │
│  [  Pick Image  ]       │
│                         │
│  🐕 golden retriever    │
│     82.1%               │
│  🐕 Labrador retriever  │
│     11.3%               │
│  🐕 toy poodle          │
│      4.2%               │
│                         │
│  ⏱️ Inference: 12ms     │
│  🔧 CPU (XNNPack)       │
└─────────────────────────┘
```

---

## 1. Project Setup

Use the Android project from Lesson 1.4. Make sure you have:

```
app/src/main/assets/
└── mobilenetv2.tflite         ← download from course models/ folder
```

**Gradle dependencies (app/build.gradle):**

```kotlin
dependencies {
    implementation 'org.tensorflow:tensorflow-lite:2.14.0'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'
    implementation 'org.tensorflow:tensorflow-lite-gpu:2.14.0'
}
```

---

## 2. Project File Structure

```
app/src/main/
├── java/com/yourname/ondeviceai/
│   ├── MainActivity.kt           ← UI and user interaction
│   ├── ImageClassifier.kt        ← all inference logic here
│   └── ClassificationResult.kt  ← data class for results
├── assets/
│   ├── mobilenetv2.tflite        ← model
│   └── imagenet_labels.txt       ← 1000 class names
└── res/layout/
    └── activity_main.xml         ← UI layout
```

---

## 3. Data Class — ClassificationResult.kt

```kotlin
// ClassificationResult.kt
package com.yourname.ondeviceai

data class ClassificationResult(
    val label: String,
    val confidence: Float,       // 0.0 to 1.0
    val inferenceTimeMs: Long,   // milliseconds
    val accelerator: String      // "CPU", "GPU", "NNAPI"
)
```

---

## 4. The Classifier — ImageClassifier.kt

This is the core of your app. It handles:
- Loading the model from assets
- Preprocessing the image
- Running inference
- Decoding the output

```kotlin
// ImageClassifier.kt
package com.yourname.ondeviceai

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.InputStreamReader

class ImageClassifier(
    private val context: Context,
    private val useGpu: Boolean = false
) {
    companion object {
        private const val TAG = "ImageClassifier"
        private const val MODEL_FILE = "mobilenetv2.tflite"
        private const val LABELS_FILE = "imagenet_labels.txt"
        private const val INPUT_SIZE = 224
        private const val TOP_K = 3
    }

    private var interpreter: Interpreter
    private var gpuDelegate: GpuDelegate? = null
    private val labels: List<String>
    private var acceleratorUsed: String

    // ImageNet normalization values
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
        .add(NormalizeOp(
            floatArrayOf(0.485f, 0.456f, 0.406f),  // mean (RGB)
            floatArrayOf(0.229f, 0.224f, 0.225f)   // std  (RGB)
        ))
        .build()

    init {
        // Load model
        val modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)

        // Configure interpreter options
        val options = Interpreter.Options()

        if (useGpu) {
            try {
                gpuDelegate = GpuDelegate(
                    GpuDelegate.Options().apply {
                        setPrecisionLossAllowed(true)
                    }
                )
                options.addDelegate(gpuDelegate!!)
                acceleratorUsed = "GPU"
                Log.d(TAG, "GPU Delegate enabled")
            } catch (e: Exception) {
                Log.w(TAG, "GPU Delegate failed, falling back to CPU: ${e.message}")
                acceleratorUsed = "CPU"
            }
        } else {
            options.setNumThreads(4)
            options.setUseXNNPACK(true)
            acceleratorUsed = "CPU"
        }

        interpreter = Interpreter(modelBuffer, options)

        // Load labels
        labels = loadLabels()

        Log.d(TAG, "Classifier initialized on: $acceleratorUsed")
        Log.d(TAG, "Input shape : ${interpreter.getInputTensor(0).shape().toList()}")
        Log.d(TAG, "Output shape: ${interpreter.getOutputTensor(0).shape().toList()}")
    }

    fun classify(bitmap: Bitmap): List<ClassificationResult> {
        // Step 1 — Preprocess image
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        // Step 2 — Prepare output buffer
        val outputShape = interpreter.getOutputTensor(0).shape()   // [1, 1000]
        val outputDataType = interpreter.getOutputTensor(0).dataType()
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType)

        // Step 3 — Run inference (timed)
        val startTime = SystemClock.uptimeMillis()
        interpreter.run(processedImage.buffer, outputBuffer.buffer.rewind())
        val inferenceTime = SystemClock.uptimeMillis() - startTime

        // Step 4 — Decode output (softmax + top-K)
        val scores = outputBuffer.floatArray
        val softmaxScores = softmax(scores)

        return getTopK(softmaxScores, TOP_K, inferenceTime)
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.max()
        val expScores = logits.map { Math.exp((it - maxLogit).toDouble()).toFloat() }
        val sumExp = expScores.sum()
        return expScores.map { it / sumExp }.toFloatArray()
    }

    private fun getTopK(
        scores: FloatArray,
        k: Int,
        inferenceTime: Long
    ): List<ClassificationResult> {
        return scores
            .mapIndexed { index, score -> index to score }
            .sortedByDescending { it.second }
            .take(k)
            .map { (index, score) ->
                ClassificationResult(
                    label = labels.getOrElse(index) { "Unknown class $index" },
                    confidence = score,
                    inferenceTimeMs = inferenceTime,
                    accelerator = acceleratorUsed
                )
            }
    }

    private fun loadLabels(): List<String> {
        val labels = mutableListOf<String>()
        context.assets.open(LABELS_FILE).use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                reader.forEachLine { line ->
                    if (line.isNotBlank()) labels.add(line.trim())
                }
            }
        }
        Log.d(TAG, "Loaded ${labels.size} labels")
        return labels
    }

    fun close() {
        interpreter.close()
        gpuDelegate?.close()
    }
}
```

---

## 5. The Layout — activity_main.xml

```xml
<!-- res/layout/activity_main.xml -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center_horizontal">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="On-Device Image Classifier"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="24dp"/>

    <!-- Selected image preview -->
    <ImageView
        android:id="@+id/imageView"
        android:layout_width="280dp"
        android:layout_height="280dp"
        android:scaleType="centerCrop"
        android:background="#F0F0F0"
        android:layout_marginBottom="16dp"/>

    <!-- Pick image button -->
    <Button
        android:id="@+id/btnPickImage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Pick Image from Gallery"
        android:layout_marginBottom="8dp"/>

    <!-- GPU toggle -->
    <CheckBox
        android:id="@+id/checkGpu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Use GPU Acceleration"
        android:layout_marginBottom="16dp"/>

    <!-- Results card -->
    <LinearLayout
        android:id="@+id/resultsContainer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="#F8F8F8"
        android:padding="16dp"
        android:visibility="gone">

        <TextView
            android:id="@+id/tvResult1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="16sp"
            android:textStyle="bold"/>

        <TextView
            android:id="@+id/tvResult2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:layout_marginTop="4dp"/>

        <TextView
            android:id="@+id/tvResult3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:layout_marginTop="4dp"/>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#DDDDDD"
            android:layout_marginTop="12dp"
            android:layout_marginBottom="8dp"/>

        <TextView
            android:id="@+id/tvInferenceInfo"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="12sp"
            android:textColor="#666666"/>
    </LinearLayout>

</LinearLayout>
```

---

## 6. MainActivity.kt — Putting It All Together

```kotlin
// MainActivity.kt
package com.yourname.ondeviceai

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    // Views
    private lateinit var imageView: ImageView
    private lateinit var btnPickImage: Button
    private lateinit var checkGpu: CheckBox
    private lateinit var resultsContainer: LinearLayout
    private lateinit var tvResult1: TextView
    private lateinit var tvResult2: TextView
    private lateinit var tvResult3: TextView
    private lateinit var tvInferenceInfo: TextView

    // Classifier (lazy init — created after user picks GPU option)
    private var classifier: ImageClassifier? = null
    private var currentBitmap: Bitmap? = null

    // Coroutine scope for background inference
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val stream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                currentBitmap = bitmap
                imageView.setImageBitmap(bitmap)
                runInference(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        imageView = findViewById(R.id.imageView)
        btnPickImage = findViewById(R.id.btnPickImage)
        checkGpu = findViewById(R.id.checkGpu)
        resultsContainer = findViewById(R.id.resultsContainer)
        tvResult1 = findViewById(R.id.tvResult1)
        tvResult2 = findViewById(R.id.tvResult2)
        tvResult3 = findViewById(R.id.tvResult3)
        tvInferenceInfo = findViewById(R.id.tvInferenceInfo)

        // Initialize classifier on CPU by default
        initClassifier(useGpu = false)

        // Button: open image picker
        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }

        // Toggle GPU/CPU and re-run if image is loaded
        checkGpu.setOnCheckedChangeListener { _, isChecked ->
            initClassifier(useGpu = isChecked)
            currentBitmap?.let { runInference(it) }
        }
    }

    private fun initClassifier(useGpu: Boolean) {
        // Close existing classifier
        classifier?.close()
        classifier = null

        // Init new one on background thread (loading model takes ~200ms)
        scope.launch(Dispatchers.IO) {
            classifier = ImageClassifier(this@MainActivity, useGpu)
        }
    }

    private fun runInference(bitmap: Bitmap) {
        val cls = classifier ?: run {
            Toast.makeText(this, "Classifier loading, please wait...", Toast.LENGTH_SHORT).show()
            return
        }

        btnPickImage.isEnabled = false
        btnPickImage.text = "Running inference..."

        // Run inference on IO thread (never block the main/UI thread!)
        scope.launch(Dispatchers.IO) {
            val results = cls.classify(bitmap)

            // Update UI on main thread
            withContext(Dispatchers.Main) {
                displayResults(results)
                btnPickImage.isEnabled = true
                btnPickImage.text = "Pick Image from Gallery"
            }
        }
    }

    private fun displayResults(results: List<ClassificationResult>) {
        if (results.isEmpty()) return

        resultsContainer.visibility = View.VISIBLE

        val resultViews = listOf(tvResult1, tvResult2, tvResult3)
        val emojis = listOf("🥇", "🥈", "🥉")

        results.forEachIndexed { index, result ->
            if (index < resultViews.size) {
                val pct = (result.confidence * 100).toInt()
                resultViews[index].text = "${emojis[index]} ${result.label}  —  $pct%"
            }
        }

        // Hide unused result rows
        for (i in results.size until resultViews.size) {
            resultViews[i].text = ""
        }

        // Show inference stats
        val first = results.first()
        tvInferenceInfo.text =
            "⏱️ ${first.inferenceTimeMs}ms  ·  🔧 ${first.accelerator}  ·  📦 MobileNetV2"
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()      // cancel all coroutines
        classifier?.close() // release GPU/CPU memory
    }
}
```

---

## 7. Run It!

1. Connect your Android phone via USB (or use emulator)
2. Click **Run ▶** in Android Studio
3. Tap **Pick Image from Gallery**
4. Select any photo
5. See results appear with inference time!

**Expected Logcat output:**

```
D/ImageClassifier: Classifier initialized on: CPU
D/ImageClassifier: Input shape : [1, 224, 224, 3]
D/ImageClassifier: Output shape: [1, 1000]
D/ImageClassifier: Loaded 1000 labels
```

**Try enabling GPU:**
- Check the GPU checkbox
- Pick the same image
- Compare the inference time — should be 3–6× faster on flagship devices

---

## 8. What You Just Did — Step by Step

```
User taps "Pick Image"
        │
        ▼
Android image picker returns a URI
        │
        ▼
BitmapFactory.decodeStream() → Bitmap
        │
        ▼ (background thread via Coroutines)
ImageProcessor: resize 224×224, normalize RGB values
        │
        ▼
TFLite Interpreter.run() → 1000 float scores
        │
        ▼
softmax() converts raw logits → probabilities (sum = 1.0)
        │
        ▼
top-3 scores mapped to label names
        │
        ▼ (main thread)
UI updated with results + inference time
```

---

## 9. Key Patterns to Remember

```kotlin
// ✅ Always run inference on a background thread
scope.launch(Dispatchers.IO) {
    val result = classifier.classify(bitmap)
    withContext(Dispatchers.Main) {
        updateUI(result)
    }
}

// ✅ Always close interpreter when done
override fun onDestroy() {
    classifier?.close()
}

// ✅ rewind() the output buffer before each inference
interpreter.run(inputBuffer, outputBuffer.buffer.rewind())

// ✅ Use TFLite Support Library for preprocessing
// Don't write your own resize/normalize — use ImageProcessor
val processor = ImageProcessor.Builder()
    .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
    .add(NormalizeOp(mean, std))
    .build()
```

---

## ✅ Module 1 Complete!

You have finished **Module 1 — The Android On-Device AI Ecosystem**.

### What you learned:
- The 5-layer Android AI stack from Kotlin to silicon
- When to choose TFLite, ONNX Runtime, MediaPipe, or ML Kit
- How NNAPI, GPU Delegate, and Hexagon DSP work
- How to set up Android Studio for AI development
- How to build a complete on-device image classifier in Kotlin

---

## ➡️ Next Module

**[Module 02 — Deploying Models to Android →](../module_02_deploying_models/)**

---

*Module 1 · On-Device AI for Android Development*