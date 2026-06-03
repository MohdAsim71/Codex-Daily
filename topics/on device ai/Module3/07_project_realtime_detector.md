# 3.7 — 🛠️ Project: Real-Time Object Detector Android App

> **Module 3 · Project**  
> 🛠️ Build · ⏱️ ~90 min

---

## 🎯 Project Goal

Build a **production-quality real-time object detection Android app** that:

- Streams live camera via CameraX
- Detects 80 COCO objects with YOLOv8n TFLite in real-time
- Draws polished bounding boxes with labels via `OverlayView`
- Shows live FPS, inference time, and object count
- Supports GPU toggle and front/back camera flip
- Handles permissions cleanly with a permission rationale screen
- Uses MVVM architecture with `ViewModel` + `StateFlow`

---

## 📱 App Preview

```
┌───────────────────────────────┐
│  [flip cam]   🔍 Detector  [⚙]│
├───────────────────────────────┤
│                               │
│   ╔══════════════╗            │
│   ║  person      ║            │
│   ║  94%         ║            │
│   ╚══════════════╝            │
│                               │
│       ╔═════════╗             │
│       ║  phone  ║             │
│       ║  71%    ║             │
│       ╚═════════╝             │
│                               │
├───────────────────────────────┤
│ ⏱ 18ms │ 30fps │ 2 objects   │
│ [CPU]         [GPU ●]         │
└───────────────────────────────┘
```

---

## 1. Project Structure

```
app/src/main/
├── java/com/yourname/detector/
│   ├── MainActivity.kt                  ← entry, permission check
│   ├── DetectorActivity.kt              ← camera + UI
│   ├── DetectorViewModel.kt             ← business logic, state
│   ├── ml/
│   │   ├── YoloV8Detector.kt            ← from lesson 3.2
│   │   └── Detection.kt                 ← data class
│   ├── ui/
│   │   ├── OverlayView.kt               ← from lesson 3.6
│   │   └── StatsView.kt                 ← custom stats bar
│   └── utils/
│       ├── PermissionHelper.kt
│       └── FpsCounter.kt
├── assets/
│   └── yolov8n.tflite
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── activity_detector.xml
    │   └── view_stats.xml
    └── values/
        ├── strings.xml
        └── colors.xml
```

---

## 2. FpsCounter.kt

```kotlin
// utils/FpsCounter.kt
package com.yourname.detector.utils

class FpsCounter {
    private var frameCount = 0
    private var lastTime = System.currentTimeMillis()
    private var currentFps = 0f

    /** Call once per frame. Returns current FPS (updates every second). */
    fun tick(): Float {
        frameCount++
        val now = System.currentTimeMillis()
        val elapsed = now - lastTime
        if (elapsed >= 1000L) {
            currentFps = frameCount * 1000f / elapsed
            frameCount = 0
            lastTime   = now
        }
        return currentFps
    }

    fun reset() {
        frameCount = 0
        lastTime   = System.currentTimeMillis()
        currentFps = 0f
    }
}
```

---

## 3. DetectorState — ViewModel State

```kotlin
// DetectorViewModel.kt
package com.yourname.detector

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.detector.ml.Detection
import com.yourname.detector.ml.YoloV8Detector
import com.yourname.detector.utils.FpsCounter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DetectorUiState(
    val detections: List<Detection>  = emptyList(),
    val inferenceMs: Long            = 0L,
    val fps: Float                   = 0f,
    val accelerator: String          = "CPU",
    val isGpuEnabled: Boolean        = false,
    val isFrontCamera: Boolean       = false,
    val imageWidth: Int              = 1,
    val imageHeight: Int             = 1
)

class DetectorViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DetectorUiState())
    val uiState: StateFlow<DetectorUiState> = _uiState.asStateFlow()

    private var detector: YoloV8Detector? = null
    private val fpsCounter = FpsCounter()

    init {
        initDetector(useGpu = false)
    }

    fun setGpuEnabled(enabled: Boolean) {
        if (enabled == _uiState.value.isGpuEnabled) return
        _uiState.value = _uiState.value.copy(isGpuEnabled = enabled)
        initDetector(useGpu = enabled)
    }

    fun flipCamera() {
        _uiState.value = _uiState.value.copy(
            isFrontCamera = !_uiState.value.isFrontCamera
        )
    }

    fun processFrame(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            val det = detector ?: return@launch
            val detections = det.detect(bitmap)
            val fps = fpsCounter.tick()

            _uiState.value = _uiState.value.copy(
                detections    = detections,
                inferenceMs   = detections.firstOrNull()?.inferenceTimeMs ?: 0L,
                fps           = fps,
                accelerator   = if (_uiState.value.isGpuEnabled) "GPU" else "CPU",
                imageWidth    = bitmap.width,
                imageHeight   = bitmap.height
            )
        }
    }

    private fun initDetector(useGpu: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            detector?.close()
            detector = YoloV8Detector(
                context = getApplication(),
                modelFileName = "yolov8n.tflite",
                useGpu = useGpu,
                confidenceThreshold = 0.45f,
                iouThreshold = 0.45f
            )
            Log.d("Detector", "Initialized on ${if (useGpu) "GPU" else "CPU"}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        detector?.close()
    }
}
```

---

## 4. Layout — activity_detector.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#000000">

    <!-- Camera preview fills the screen -->
    <androidx.camera.view.PreviewView
        android:id="@+id/previewView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/statsBar"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:scaleType="fillCenter" />

    <!-- Detection overlay on top of camera -->
    <com.yourname.detector.ui.OverlayView
        android:id="@+id/overlayView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="@android:color/transparent"
        app:layout_constraintTop_toTopOf="@id/previewView"
        app:layout_constraintBottom_toBottomOf="@id/previewView"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Top bar: flip + title -->
    <LinearLayout
        android:id="@+id/topBar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:padding="12dp"
        android:background="#60000000"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <ImageButton
            android:id="@+id/btnFlipCamera"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:src="@drawable/ic_flip_camera"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:tint="#FFFFFF" />

        <TextView
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="🔍 Object Detector"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:textStyle="bold"
            android:gravity="center" />

        <!-- placeholder to balance the row -->
        <View android:layout_width="40dp" android:layout_height="40dp" />
    </LinearLayout>

    <!-- Bottom stats bar -->
    <LinearLayout
        android:id="@+id/statsBar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="#CC000000"
        android:padding="12dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <!-- Stats row -->
        <TextView
            android:id="@+id/tvStats"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="#FFFFFF"
            android:fontFamily="monospace"
            android:textSize="13sp"
            android:layout_marginBottom="8dp" />

        <!-- GPU/CPU toggle row -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="CPU"
                android:textColor="#AAAAAA"
                android:textSize="13sp" />

            <Switch
                android:id="@+id/switchGpu"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginHorizontal="8dp" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="GPU"
                android:textColor="#AAAAAA"
                android:textSize="13sp" />

            <View android:layout_width="0dp" android:layout_height="1dp"
                android:layout_weight="1" />

            <TextView
                android:id="@+id/tvObjectCount"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#FFFFFF"
                android:textSize="13sp" />
        </LinearLayout>
    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## 5. DetectorActivity.kt — Full Implementation

```kotlin
// DetectorActivity.kt
package com.yourname.detector

import android.os.Bundle
import android.util.Size
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.yourname.detector.ui.OverlayView
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetectorActivity : AppCompatActivity() {

    private val viewModel: DetectorViewModel by viewModels()

    private lateinit var previewView: PreviewView
    private lateinit var overlayView: OverlayView
    private lateinit var tvStats: TextView
    private lateinit var tvObjectCount: TextView
    private lateinit var switchGpu: Switch
    private lateinit var btnFlipCamera: ImageButton
    private lateinit var cameraExecutor: ExecutorService

    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detector)

        previewView    = findViewById(R.id.previewView)
        overlayView    = findViewById(R.id.overlayView)
        tvStats        = findViewById(R.id.tvStats)
        tvObjectCount  = findViewById(R.id.tvObjectCount)
        switchGpu      = findViewById(R.id.switchGpu)
        btnFlipCamera  = findViewById(R.id.btnFlipCamera)

        cameraExecutor = Executors.newSingleThreadExecutor()

        setupListeners()
        observeState()
        startCamera(frontCamera = false)
    }

    private fun setupListeners() {
        switchGpu.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setGpuEnabled(isChecked)
        }

        btnFlipCamera.setOnClickListener {
            viewModel.flipCamera()
            startCamera(frontCamera = viewModel.uiState.value.isFrontCamera)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Update overlay
                overlayView.setDetections(
                    detections  = state.detections,
                    imageWidth  = state.imageWidth,
                    imageHeight = state.imageHeight
                )

                // Update stats
                tvStats.text = "⏱ ${state.inferenceMs}ms  " +
                    "| ${"%.0f".format(state.fps)}fps  " +
                    "| ${state.accelerator}"

                tvObjectCount.text = "${state.detections.size} objects"
            }
        }
    }

    private fun startCamera(frontCamera: Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        try {
                            val bitmap = imageProxy.toBitmap()
                            viewModel.processFrame(bitmap)
                        } finally {
                            imageProxy.close()
                        }
                    }
                }

            val selector = if (frontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, selector, preview, imageAnalysis)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
```

---

## 6. Build and Run

```
1. Copy yolov8n.tflite to app/src/main/assets/
2. Sync Gradle
3. Connect Android device via USB
4. Run ▶
5. Grant camera permission
6. Point camera at objects — see boxes appear in real-time
7. Toggle GPU switch — observe inference time drop 3–6×
8. Tap flip camera — switch to front camera
```

---

## 7. Performance Tuning Checklist

```
□ Using GPU delegate?          → 3–6× faster on flagship
□ Image resolution at 640×480? → good balance speed vs accuracy
□ STRATEGY_KEEP_ONLY_LATEST?   → prevents frame queue buildup
□ Output buffer pre-allocated? → no GC pressure during inference
□ Single cameraExecutor thread?→ no thread contention
□ State updates via StateFlow? → no runOnUiThread race conditions
□ iouThreshold = 0.45?         → good NMS default
□ confidenceThreshold = 0.45?  → filter low-quality detections
```

---

## 8. Challenge Tasks

```
🔹 Easy
  □ Add a filter chip to show only specific classes (e.g. "only people")
  □ Make the bounding box stroke width proportional to confidence
  □ Show a toast when a specific object is detected ("⚠️ Person detected!")

🔸 Medium
  □ Add screenshot button — capture frame + overlay as one image
  □ Log detection history — show what was detected in the last 30 seconds
  □ Add NNAPI toggle alongside GPU toggle, compare all three modes

🔴 Hard
  □ Replace YOLOv8n with a custom-trained model (fine-tune on 5 custom classes)
  □ Add object tracking — assign IDs to objects across frames (SORT algorithm)
  □ Count how many times a specific object enters the camera frame per minute
```

---

## ✅ Module 3 Complete!

You have finished **Module 3 — Vision AI on Android**.

### What you built and learned:
- CameraX setup with `ImageAnalysis` for per-frame AI
- YOLOv8n real-time object detection with NMS decoding in Kotlin
- Face detection and 478-point face landmarking with MediaPipe
- Offline OCR with ML Kit Text Recognition v2
- 33-point pose estimation with joint angle calculation
- A production-quality `OverlayView` that scales coordinates correctly
- A complete real-time detector app with GPU toggle, camera flip, and FPS counter

---

## ➡️ Next Module

**[Module 04 — Speech & NLP AI on Android →](../module_04_speech_nlp/)**

---

*Module 3 · On-Device AI for Android Development*