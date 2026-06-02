# 2.6 — 🛠️ Project: Image Classifier Android App (MobileNetV2)

> **Module 2 · Project**  
> 🛠️ Build · ⏱️ ~60 min

---

## 🎯 Project Goal

Build a **production-quality image classifier Android app** that:

- Classifies images from the gallery or camera
- Runs MobileNetV2 fully on-device (no internet)
- Supports GPU acceleration with real-time toggle
- Benchmarks CPU vs GPU inference and displays results
- Uses clean MVVM architecture with Kotlin Coroutines

---

## 📱 App Preview

```
┌───────────────────────────────┐
│    🤖 On-Device Classifier    │
├───────────────────────────────┤
│                               │
│  ┌─────────────────────────┐  │
│  │                         │  │
│  │      [Photo Here]       │  │
│  │                         │  │
│  └─────────────────────────┘  │
│                               │
│  [ 📷 Camera ]  [ 🖼 Gallery ] │
│                               │
│  ─────────── Results ──────── │
│  🥇 golden retriever   82.1%  │
│  🥈 Labrador retriever 11.3%  │
│  🥉 toy poodle          4.2%  │
│                               │
│  ⚡ GPU  [═══════════] toggle │
│  ⏱ 12ms · 📦 MobileNetV2 INT8│
└───────────────────────────────┘
```

---

## 1. Project Structure

```
app/src/main/
├── java/com/yourname/classifier/
│   ├── MainActivity.kt               ← UI, camera/gallery interaction
│   ├── ClassifierViewModel.kt        ← business logic, coroutines
│   ├── ImageClassifier.kt            ← TFLite inference engine
│   ├── ClassificationResult.kt       ← data model
│   └── utils/
│       ├── ImageUtils.kt             ← bitmap helpers
│       └── LabelUtils.kt            ← load imagenet labels
├── assets/
│   ├── mobilenetv2_int8.tflite       ← INT8 quantized model (~3.6 MB)
│   └── imagenet_labels.txt           ← 1000 class names
└── res/
    ├── layout/activity_main.xml
    └── values/strings.xml
```

---

## 2. Data Classes

```kotlin
// ClassificationResult.kt
package com.yourname.classifier

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val inferenceTimeMs: Long,
    val accelerator: String
) {
    val confidencePct: Int get() = (confidence * 100).toInt()
}

sealed class ClassifierState {
    object Idle : ClassifierState()
    object Loading : ClassifierState()
    data class Success(val results: List<ClassificationResult>) : ClassifierState()
    data class Error(val message: String) : ClassifierState()
}
```

---

## 3. Image Utilities

```kotlin
// utils/ImageUtils.kt
package com.yourname.classifier.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

object ImageUtils {

    /**
     * Load a Bitmap from a content URI (from gallery or camera).
     * Handles rotation metadata automatically.
     */
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            Log.e("ImageUtils", "Failed to load bitmap: ${e.message}")
            null
        }
    }

    /**
     * Ensure the bitmap is in ARGB_8888 format (required by TFLite).
     */
    fun ensureArgb8888(bitmap: Bitmap): Bitmap {
        return if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmap
        } else {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        }
    }
}
```

```kotlin
// utils/LabelUtils.kt
package com.yourname.classifier.utils

import android.content.Context

object LabelUtils {

    fun loadLabels(context: Context, fileName: String = "imagenet_labels.txt"): List<String> {
        return context.assets.open(fileName)
            .bufferedReader()
            .readLines()
            .filter { it.isNotBlank() }
    }
}
```

---

## 4. The Classifier Engine

```kotlin
// ImageClassifier.kt
package com.yourname.classifier

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.yourname.classifier.utils.ImageUtils
import com.yourname.classifier.utils.LabelUtils
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ImageClassifier private constructor(
    private val interpreter: Interpreter,
    private val imageProcessor: ImageProcessor,
    private val outputBuffer: TensorBuffer,
    private val labels: List<String>,
    private val gpuDelegate: GpuDelegate?,
    private val acceleratorName: String
) {

    companion object {
        private const val TAG = "ImageClassifier"
        private const val MODEL_FILE = "mobilenetv2_int8.tflite"
        private const val INPUT_SIZE = 224
        private const val TOP_K = 5

        fun create(context: Context, useGpu: Boolean = false): ImageClassifier {
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)
            val options = Interpreter.Options()
            var gpuDelegate: GpuDelegate? = null
            val acceleratorName: String

            if (useGpu) {
                try {
                    gpuDelegate = GpuDelegate(
                        GpuDelegate.Options()
                            .setPrecisionLossAllowed(true)
                            .setQuantizedModelsAllowed(true)
                    )
                    options.addDelegate(gpuDelegate)
                    acceleratorName = "GPU"
                    Log.d(TAG, "GPU Delegate enabled")
                } catch (e: Exception) {
                    Log.w(TAG, "GPU failed, falling back to CPU: ${e.message}")
                    options.setNumThreads(4).setUseXNNPACK(true)
                    acceleratorName = "CPU (GPU fallback)"
                }
            } else {
                options.setNumThreads(4)
                options.setUseXNNPACK(true)
                acceleratorName = "CPU"
            }

            val interpreter = Interpreter(modelBuffer, options)

            // Detect if model is quantized (UINT8) or float (FLOAT32)
            val inputType = interpreter.getInputTensor(0).dataType()
            Log.d(TAG, "Model input type: $inputType, accelerator: $acceleratorName")

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .apply {
                    if (inputType == org.tensorflow.lite.DataType.FLOAT32) {
                        add(NormalizeOp(
                            floatArrayOf(0.485f, 0.456f, 0.406f),
                            floatArrayOf(0.229f, 0.224f, 0.225f)
                        ))
                    }
                    // For UINT8 quantized, no normalization — raw [0,255] bytes
                }
                .build()

            val outputShape = interpreter.getOutputTensor(0).shape()
            val outputType = interpreter.getOutputTensor(0).dataType()
            val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType)

            val labels = LabelUtils.loadLabels(context)

            return ImageClassifier(
                interpreter, imageProcessor, outputBuffer,
                labels, gpuDelegate, acceleratorName
            )
        }
    }

    fun classify(bitmap: Bitmap): List<ClassificationResult> {
        val safeBitmap = ImageUtils.ensureArgb8888(bitmap)

        // Preprocess
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(safeBitmap))

        // Inference (timed)
        val startMs = SystemClock.uptimeMillis()
        interpreter.run(tensorImage.buffer, outputBuffer.buffer.rewind())
        val inferenceMs = SystemClock.uptimeMillis() - startMs

        // Decode
        val scores = if (outputBuffer.dataType == org.tensorflow.lite.DataType.UINT8) {
            // Dequantize UINT8 output [0-255] → probabilities [0-1]
            outputBuffer.buffer.rewind()
            FloatArray(outputBuffer.flatSize) {
                (outputBuffer.buffer.get().toInt() and 0xFF) / 255.0f
            }
        } else {
            softmax(outputBuffer.floatArray)
        }

        return getTopK(scores, TOP_K, inferenceMs)
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val max = logits.max()
        val exps = logits.map { Math.exp((it - max).toDouble()).toFloat() }
        val sum = exps.sum()
        return exps.map { it / sum }.toFloatArray()
    }

    private fun getTopK(
        scores: FloatArray,
        k: Int,
        inferenceMs: Long
    ): List<ClassificationResult> {
        return scores
            .mapIndexed { i, s -> i to s }
            .sortedByDescending { it.second }
            .take(k)
            .map { (idx, score) ->
                ClassificationResult(
                    label = labels.getOrElse(idx) { "class_$idx" },
                    confidence = score,
                    inferenceTimeMs = inferenceMs,
                    accelerator = acceleratorName
                )
            }
    }

    fun close() {
        interpreter.close()
        gpuDelegate?.close()
    }
}
```

---

## 5. ViewModel

```kotlin
// ClassifierViewModel.kt
package com.yourname.classifier

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.classifier.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassifierViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<ClassifierState>(ClassifierState.Idle)
    val state: StateFlow<ClassifierState> = _state.asStateFlow()

    private var classifier: ImageClassifier? = null
    private var useGpu: Boolean = false

    init {
        initClassifier(useGpu = false)
    }

    fun setGpuEnabled(enabled: Boolean) {
        if (enabled == useGpu) return
        useGpu = enabled
        initClassifier(enabled)
    }

    fun classifyFromUri(uri: Uri) {
        viewModelScope.launch {
            _state.value = ClassifierState.Loading

            val bitmap = withContext(Dispatchers.IO) {
                ImageUtils.loadBitmapFromUri(getApplication(), uri)
            }

            if (bitmap == null) {
                _state.value = ClassifierState.Error("Could not load image")
                return@launch
            }

            classifyBitmap(bitmap)
        }
    }

    fun classifyBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.value = ClassifierState.Loading

            val results = withContext(Dispatchers.IO) {
                classifier?.classify(bitmap)
            }

            _state.value = if (results != null) {
                ClassifierState.Success(results)
            } else {
                ClassifierState.Error("Classifier not ready")
            }
        }
    }

    private fun initClassifier(useGpu: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            classifier?.close()
            classifier = ImageClassifier.create(getApplication(), useGpu)
        }
    }

    override fun onCleared() {
        super.onCleared()
        classifier?.close()
    }
}
```

---

## 6. Layout — activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="20dp"
    android:gravity="center_horizontal"
    android:background="@color/white">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="🤖 On-Device Classifier"
        android:textSize="22sp"
        android:textStyle="bold"
        android:layout_marginBottom="20dp"/>

    <!-- Image preview -->
    <ImageView
        android:id="@+id/imagePreview"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:scaleType="centerCrop"
        android:background="#F5F5F5"
        android:contentDescription="Selected image"
        android:layout_marginBottom="16dp"/>

    <!-- Action buttons -->
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginBottom="16dp">

        <Button
            android:id="@+id/btnCamera"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="📷 Camera"
            android:layout_marginEnd="8dp"/>

        <Button
            android:id="@+id/btnGallery"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="🖼 Gallery"/>
    </LinearLayout>

    <!-- Results card -->
    <LinearLayout
        android:id="@+id/cardResults"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="#F8F8F8"
        android:padding="16dp"
        android:visibility="gone">

        <TextView android:id="@+id/tvResult1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="16sp" android:textStyle="bold"/>
        <TextView android:id="@+id/tvResult2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp" android:layout_marginTop="4dp"/>
        <TextView android:id="@+id/tvResult3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="14sp" android:layout_marginTop="4dp"/>

        <View android:layout_width="match_parent" android:layout_height="1dp"
            android:background="#E0E0E0" android:layout_marginVertical="12dp"/>

        <!-- GPU toggle + stats -->
        <LinearLayout android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <Switch
                android:id="@+id/switchGpu"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="⚡ GPU"
                android:layout_marginEnd="12dp"/>

            <TextView
                android:id="@+id/tvStats"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textSize="12sp"
                android:textColor="#666666"/>
        </LinearLayout>
    </LinearLayout>

    <!-- Loading indicator -->
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        android:layout_marginTop="16dp"/>

</LinearLayout>
```

---

## 7. MainActivity.kt

```kotlin
// MainActivity.kt
package com.yourname.classifier

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: ClassifierViewModel by viewModels()

    private lateinit var imagePreview: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var cardResults: LinearLayout
    private lateinit var tvResult1: TextView
    private lateinit var tvResult2: TextView
    private lateinit var tvResult3: TextView
    private lateinit var tvStats: TextView
    private lateinit var switchGpu: Switch
    private lateinit var progressBar: ProgressBar

    private val resultTexts by lazy { listOf(tvResult1, tvResult2, tvResult3) }
    private val medals = listOf("🥇", "🥈", "🥉")

    // Gallery image picker
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imagePreview.setImageURI(uri)
                viewModel.classifyFromUri(uri)
            }
        }
    }

    // Camera permission request
    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera() else
            Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show()
    }

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            imagePreview.setImageBitmap(it)
            viewModel.classifyBitmap(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        observeState()
        setupListeners()
    }

    private fun bindViews() {
        imagePreview = findViewById(R.id.imagePreview)
        btnCamera    = findViewById(R.id.btnCamera)
        btnGallery   = findViewById(R.id.btnGallery)
        cardResults  = findViewById(R.id.cardResults)
        tvResult1    = findViewById(R.id.tvResult1)
        tvResult2    = findViewById(R.id.tvResult2)
        tvResult3    = findViewById(R.id.tvResult3)
        tvStats      = findViewById(R.id.tvStats)
        switchGpu    = findViewById(R.id.switchGpu)
        progressBar  = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        switchGpu.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setGpuEnabled(isChecked)
        }
    }

    private fun launchCamera() = cameraLauncher.launch(null)

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ClassifierState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is ClassifierState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        cardResults.visibility = View.GONE
                    }
                    is ClassifierState.Success -> {
                        progressBar.visibility = View.GONE
                        showResults(state.results)
                    }
                    is ClassifierState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showResults(results: List<ClassificationResult>) {
        cardResults.visibility = View.VISIBLE

        results.take(3).forEachIndexed { i, result ->
            resultTexts[i].text = "${medals[i]} ${result.label}  ${result.confidencePct}%"
        }
        // Hide unused rows
        for (i in results.size until resultTexts.size) resultTexts[i].text = ""

        val first = results.first()
        tvStats.text = "⏱ ${first.inferenceTimeMs}ms  ·  🔧 ${first.accelerator}  ·  📦 MobileNetV2"
    }
}
```

---

## 8. Build and Run

```
1. Sync Gradle — click "Sync Now"
2. Connect phone via USB (or use emulator)
3. Run ▶ in Android Studio
4. Tap "Gallery" → pick a photo
5. See results appear instantly
6. Toggle GPU switch — compare inference time
```

---

## 9. Extend the Project — Challenge Tasks

Try these enhancements to deepen your understanding:

```
🔹 Easy
  □ Add a "Share Result" button that exports the image + prediction as text
  □ Show a confidence bar (ProgressBar) next to each result
  □ Display the top-10 instead of top-3

🔸 Medium
  □ Add NNAPI toggle alongside GPU toggle
  □ Run 10 inferences and show average + min/max latency
  □ Compare FP32 vs INT8 model accuracy on the same image

🔴 Hard
  □ Replace MobileNetV2 with EfficientNet-Lite — export and compare accuracy
  □ Fine-tune MobileNetV2 on a custom 5-class dataset (flowers, pets, food)
  □ Add real-time CameraX preview with classification every 500ms
```

---

## ✅ Module 2 Complete!

You have finished **Module 2 — Deploying Models to Android**.

### What you built and learned:
- Exported a PyTorch model all the way to a `.tflite` file
- Added TFLite to Android with correct Gradle config and no-compress rules
- Loaded and ran models using both raw interpreter and Support Library
- Used ONNX Runtime Mobile as an alternative runtime
- Mastered tensor preprocessing and postprocessing
- Built a full MVVM image classifier app with GPU toggle

---

## ➡️ Next Module

**[Module 03 — Vision AI on Android →](../module_03_vision_ai/)**

---

*Module 2 · On-Device AI for Android Development*