# 3.1 — CameraX Setup for Real-Time AI Inference

> **Module 3 · Lesson 1**  
> 📱 Code · ⏱️ ~35 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Add CameraX to an Android project and configure all three use cases
- Display a live camera preview in your app
- Connect `ImageAnalysis` to an AI model for per-frame inference
- Manage camera lifecycle correctly to avoid leaks and crashes
- Control frame rate and resolution for optimal AI performance

---

## 1. Why CameraX for AI?

Android has three camera APIs. CameraX is the right choice for AI apps:

```
Camera1 API (legacy)
  ❌ No lifecycle awareness — must manage manually
  ❌ Inconsistent behavior across devices
  ❌ No built-in ImageAnalysis pipeline

Camera2 API (raw)
  ✅ Full control over every camera parameter
  ❌ 2000+ lines of boilerplate for basic preview
  ❌ Not worth the complexity unless you need RAW capture

CameraX (Jetpack) ← USE THIS
  ✅ Lifecycle-aware — no memory leaks
  ✅ Works consistently across 98%+ of Android devices
  ✅ Built-in ImageAnalysis — designed for AI per-frame processing
  ✅ 90% less code than Camera2
  ✅ Works on API 21+ (Android 5.0)
```

---

## 2. CameraX Use Cases

CameraX has three independent use cases you bind together:

```
┌─────────────────────────────────────────────────┐
│                  CameraX                        │
│                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐  │
│  │ Preview  │  │ Image    │  │ Image        │  │
│  │          │  │ Capture  │  │ Analysis     │  │
│  │ Show live│  │ Take a   │  │ Per-frame AI │  │
│  │ feed on  │  │ photo    │  │ inference    │  │
│  │ screen   │  │          │  │              │  │
│  └──────────┘  └──────────┘  └──────────────┘  │
│                                                 │
│  All three bound to the same CameraX lifecycle  │
└─────────────────────────────────────────────────┘
```

For AI apps you typically use **Preview + ImageAnalysis** together.

---

## 3. Add CameraX to Gradle

```kotlin
// app/build.gradle
dependencies {
    val cameraxVersion = "1.3.1"

    // Core CameraX
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")   // Camera2 backend
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion") // Lifecycle binding

    // CameraX View — PreviewView widget
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Optional: CameraX Extensions (bokeh, night mode, etc.)
    // implementation("androidx.camera:camera-extensions:$cameraxVersion")
}
```

---

## 4. AndroidManifest.xml

```xml
<manifest>
    <!-- Camera permission -->
    <uses-permission android:name="android.permission.CAMERA" />

    <!-- Declare camera as a feature (required = false means app works without it) -->
    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

    <application
        android:hardwareAccelerated="true"  ← needed for GPU inference
        ...>
```

---

## 5. Layout — PreviewView

`PreviewView` is CameraX's live camera display widget:

```xml
<!-- activity_camera.xml -->
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Camera preview — fills the screen -->
    <androidx.camera.view.PreviewView
        android:id="@+id/previewView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:scaleType="fillCenter" />

    <!-- AI overlay drawn ON TOP of camera preview -->
    <com.yourname.vision.OverlayView
        android:id="@+id/overlayView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!-- Stats: fps, inference time -->
    <TextView
        android:id="@+id/tvStats"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|start"
        android:padding="8dp"
        android:background="#80000000"
        android:textColor="#FFFFFF"
        android:textSize="12sp"
        android:fontFamily="monospace" />

</FrameLayout>
```

---

## 6. Request Camera Permission

```kotlin
// In your Activity — always request permission before starting camera
private val cameraPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        startCamera()
    } else {
        Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
        finish()
    }
}

private fun checkAndRequestCameraPermission() {
    when {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            startCamera()  // already granted
        }
        else -> {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
```

---

## 7. Core CameraX Setup — startCamera()

```kotlin
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var tvStats: TextView

    // Dedicated background thread for camera and AI work
    // Never run AI inference on the main thread!
    private lateinit var cameraExecutor: ExecutorService

    // Your AI analyzer — swap this for YOLO, Face, Pose, etc.
    private lateinit var analyzer: ImageAnalysis.Analyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        tvStats = findViewById(R.id.tvStats)

        // One thread for camera image delivery
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Initialize your AI model here
        analyzer = MyAiAnalyzer { results ->
            // Called on background thread — post to main for UI
            runOnUiThread { updateUI(results) }
        }

        checkAndRequestCameraPermission()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // ── Use Case 1: Preview ───────────────────────
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // ── Use Case 2: ImageAnalysis ─────────────────
            val imageAnalysis = ImageAnalysis.Builder()
                // Resolution for AI — lower = faster inference
                .setTargetResolution(Size(640, 480))

                // IMPORTANT: drop frames we can't process in time
                // Don't queue frames — always process the LATEST frame
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

                // Output format — most AI models work with YUV_420_888
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)

                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, analyzer)
                }

            // ── Select camera ─────────────────────────────
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind all before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to lifecycle — CameraX handles start/stop
                cameraProvider.bindToLifecycle(
                    this,            // LifecycleOwner
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed: ${e.message}")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()  // ← always shut down the executor
    }
}
```

---

## 8. Implementing ImageAnalysis.Analyzer

The `Analyzer` is where your AI model connects to the camera:

```kotlin
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.graphics.Bitmap

/**
 * Template for any AI image analyzer.
 * Swap out the inference logic for YOLO, face detection, OCR, etc.
 */
class MyAiAnalyzer(
    private val onResults: (List<Any>) -> Unit
) : ImageAnalysis.Analyzer {

    // Track inference performance
    private var frameCount = 0
    private var lastFpsTime = System.currentTimeMillis()
    private var fps = 0f

    override fun analyze(imageProxy: ImageProxy) {
        // CRITICAL: always call close() when done
        // If you forget, the camera freezes — no more frames delivered
        try {
            val bitmap = imageProxy.toBitmap()  // CameraX 1.3+ built-in

            // ── Run your AI model here ────────────────────
            val results = runAiInference(bitmap)

            // ── Update FPS counter ────────────────────────
            frameCount++
            val now = System.currentTimeMillis()
            if (now - lastFpsTime >= 1000) {
                fps = frameCount * 1000f / (now - lastFpsTime)
                frameCount = 0
                lastFpsTime = now
            }

            onResults(results)

        } finally {
            imageProxy.close()   // ← MUST call this — or camera freezes!
        }
    }

    private fun runAiInference(bitmap: Bitmap): List<Any> {
        // Replace with your actual model call
        // e.g. yoloDetector.detect(bitmap)
        //      faceDetector.detect(bitmap)
        //      poseEstimator.estimate(bitmap)
        return emptyList()
    }
}
```

> ⚠️ **The most common CameraX mistake:** Forgetting `imageProxy.close()`. The camera delivers exactly one new frame only after you close the current one. If you forget, your app freezes on the first frame.

---

## 9. Frame Rate Control

Real-time AI needs careful frame rate management:

```kotlin
class ThrottledAnalyzer(
    private val maxFps: Int = 15,            // limit to 15 fps for heavy models
    private val onResults: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastAnalyzedTimestamp = 0L

    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        val minInterval = 1000L / maxFps    // e.g. 67ms for 15fps

        if (now - lastAnalyzedTimestamp >= minInterval) {
            lastAnalyzedTimestamp = now
            val bitmap = imageProxy.toBitmap()
            onResults(bitmap)
        }
        // Always close — even when skipping a frame
        imageProxy.close()
    }
}

// Usage: 30fps for fast models (YOLO nano), 10fps for heavy models
val analyzer = ThrottledAnalyzer(maxFps = 30) { bitmap ->
    val results = yoloDetector.detect(bitmap)
    runOnUiThread { overlayView.setResults(results) }
}
```

---

## 10. Resolution vs Inference Speed Tradeoff

```
Resolution      │ Inference Speed  │ Detection Quality
────────────────┼──────────────────┼───────────────────
1920×1080       │ Slowest          │ Best (small objects)
1280×720        │ Slow             │ Good
640×480         │ Fast ✅          │ Good for most tasks
320×240         │ Fastest          │ OK for large objects
```

Set resolution in `ImageAnalysis.Builder`:

```kotlin
ImageAnalysis.Builder()
    .setTargetResolution(Size(640, 480))   // balance of speed and quality
    // OR target a specific aspect ratio
    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
    .build()
```

---

## 11. Switch Between Front and Back Camera

```kotlin
private var cameraFacing = CameraSelector.LENS_FACING_BACK

fun flipCamera() {
    cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
        CameraSelector.LENS_FACING_FRONT
    } else {
        CameraSelector.LENS_FACING_BACK
    }
    startCamera()  // restart with new selector
}

// In startCamera():
val cameraSelector = CameraSelector.Builder()
    .requireLensFacing(cameraFacing)
    .build()
```

> 💡 **Note for face AI:** Front camera images are mirrored. When drawing overlays, flip bounding box X coordinates: `mirroredX = imageWidth - originalX`.

---

## 12. CameraX Lifecycle Summary

```
Activity.onCreate()
    └─ cameraExecutor = Executors.newSingleThreadExecutor()
    └─ checkAndRequestCameraPermission()
           └─ startCamera()
                  └─ bindToLifecycle(this, ...) ← CameraX owns lifecycle

Activity becomes visible → CameraX starts camera automatically
Activity goes to background → CameraX pauses camera automatically
Activity comes back → CameraX resumes camera automatically

Activity.onDestroy()
    └─ cameraExecutor.shutdown()  ← you manage the executor
    └─ CameraX handles camera release automatically
```

---

## 13. Summary

- Use `STRATEGY_KEEP_ONLY_LATEST` — always process the freshest frame
- Always call `imageProxy.close()` — forgetting freezes the camera
- Use `cameraExecutor` (single background thread) for all AI work
- Use `runOnUiThread {}` to push results back to the UI
- Target `640×480` as a good starting resolution for most AI models
- CameraX's lifecycle binding means zero camera leak bugs

---

## ➡️ Next Lesson

**[3.2 — Object Detection with YOLOv8 TFLite on Android →](./02_yolov8_tflite_android.md)**

---

*Module 3 · On-Device AI for Android Development*