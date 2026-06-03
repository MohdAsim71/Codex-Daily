# 3.5 — Pose Estimation with MediaPipe Pose

> **Module 3 · Lesson 5**  
> 📱 Code · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Set up MediaPipe Pose Landmarker in Android
- Get 33 3D body landmarks in real-time via CameraX
- Calculate joint angles for exercise recognition
- Draw a pose skeleton overlay on the camera preview

---

## 1. MediaPipe Pose Landmarker

```
Output: 33 3D landmarks per person
  Each landmark has: x, y, z, visibility, presence

Landmark positions:
  ┌──────────────────────────────────────────┐
  │  0  nose                                 │
  │  1  left eye inner    2  left eye        │
  │  3  left eye outer    4  right eye inner │
  │  5  right eye         6  right eye outer │
  │  7  left ear          8  right ear       │
  │  9  mouth left        10 mouth right     │
  │  11 left shoulder     12 right shoulder  │
  │  13 left elbow        14 right elbow     │
  │  15 left wrist        16 right wrist     │
  │  17 left pinky        18 right pinky     │
  │  19 left index        20 right index     │
  │  21 left thumb        22 right thumb     │
  │  23 left hip          24 right hip       │
  │  25 left knee         26 right knee      │
  │  27 left ankle        28 right ankle     │
  │  29 left heel         30 right heel      │
  │  31 left foot index   32 right foot idx  │
  └──────────────────────────────────────────┘

Model lite: ~3 MB, ~25ms on GPU — best for mobile
Model full: ~7 MB, ~40ms on GPU — more accurate
Model heavy: ~27 MB, ~80ms on GPU — maximum accuracy
```

---

## 2. Gradle Dependency

```kotlin
// app/build.gradle
dependencies {
    implementation("com.google.mediapipe:tasks-vision:0.10.14")
}
```

Download pose model and add to `app/src/main/assets/`:
```
pose_landmarker_lite.task    ← recommended for mobile
```

---

## 3. PoseLandmarkerHelper.kt

```kotlin
// PoseLandmarkerHelper.kt
package com.yourname.vision

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseLandmarkerHelper(
    private val context: Context,
    private val modelFileName: String = "pose_landmarker_lite.task",
    private val numPoses: Int = 1,
    private val minPoseDetectionConfidence: Float = 0.5f,
    private val minPosePresenceConfidence: Float = 0.5f,
    private val minTrackingConfidence: Float = 0.5f,
    private val useGpu: Boolean = true,
    private val onResults: (PoseLandmarkerResult, Int, Int) -> Unit,
    private val onError: (String) -> Unit
) {
    companion object {
        private const val TAG = "PoseLandmarkerHelper"

        // Skeleton connection pairs [from, to] — which landmarks to connect with lines
        val POSE_CONNECTIONS = listOf(
            // Face
            0 to 1, 1 to 2, 2 to 3, 3 to 7,
            0 to 4, 4 to 5, 5 to 6, 6 to 8,
            // Torso
            11 to 12, 11 to 23, 12 to 24, 23 to 24,
            // Left arm
            11 to 13, 13 to 15, 15 to 17, 15 to 19, 15 to 21, 17 to 19,
            // Right arm
            12 to 14, 14 to 16, 16 to 18, 16 to 20, 16 to 22, 18 to 20,
            // Left leg
            23 to 25, 25 to 27, 27 to 29, 29 to 31, 27 to 31,
            // Right leg
            24 to 26, 26 to 28, 28 to 30, 30 to 32, 28 to 32
        )
    }

    private var poseLandmarker: PoseLandmarker? = null
    private var imageWidth  = 0
    private var imageHeight = 0

    init { setup() }

    private fun setup() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelFileName)
            .setDelegate(if (useGpu) Delegate.GPU else Delegate.CPU)
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumPoses(numPoses)
            .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
            .setMinPosePresenceConfidence(minPosePresenceConfidence)
            .setMinTrackingConfidence(minTrackingConfidence)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                onResults(result, imageWidth, imageHeight)
            }
            .setErrorListener { error ->
                onError(error.message ?: "Pose error")
            }
            .build()

        try {
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
            Log.d(TAG, "Pose Landmarker initialized")
        } catch (e: Exception) {
            onError("Init failed: ${e.message}")
        }
    }

    fun detectAsync(bitmap: Bitmap, frameTimeMs: Long) {
        imageWidth  = bitmap.width
        imageHeight = bitmap.height
        val mpImage = BitmapImageBuilder(bitmap).build()
        poseLandmarker?.detectAsync(mpImage, frameTimeMs)
    }

    fun close() { poseLandmarker?.close() }
}
```

---

## 4. CameraX Analyzer for Pose

```kotlin
// PoseAnalyzer.kt
class PoseAnalyzer(
    private val helper: PoseLandmarkerHelper
) : ImageAnalysis.Analyzer {

    private var frameTimestamp = 0L

    override fun analyze(imageProxy: ImageProxy) {
        try {
            helper.detectAsync(imageProxy.toBitmap(), frameTimestamp++)
        } finally {
            imageProxy.close()
        }
    }
}
```

---

## 5. Draw Pose Skeleton on Overlay

```kotlin
// PoseOverlayView.kt
package com.yourname.vision

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var imageWidth  = 1
    private var imageHeight = 1

    private val jointPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val bonePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }
    private val visibilityThreshold = 0.5f

    fun setResults(result: PoseLandmarkerResult, imgWidth: Int, imgHeight: Int) {
        results = result
        imageWidth  = imgWidth
        imageHeight = imgHeight
        invalidate()   // triggers onDraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val result = results ?: return
        if (result.landmarks().isEmpty()) return

        val landmarks = result.landmarks()[0]   // first person

        val scaleX = width.toFloat()  / imageWidth
        val scaleY = height.toFloat() / imageHeight

        // Helper: get screen coordinates for a landmark
        fun landmarkToScreen(idx: Int): PointF? {
            val lm = landmarks.getOrNull(idx) ?: return null
            if ((lm.visibility() ?: 0f) < visibilityThreshold) return null
            return PointF(lm.x() * imageWidth * scaleX, lm.y() * imageHeight * scaleY)
        }

        // Draw bones (skeleton connections)
        bonePaint.alpha = 200
        for ((from, to) in PoseLandmarkerHelper.POSE_CONNECTIONS) {
            val start = landmarkToScreen(from) ?: continue
            val end   = landmarkToScreen(to)   ?: continue
            canvas.drawLine(start.x, start.y, end.x, end.y, bonePaint)
        }

        // Draw joints (circles at each landmark)
        for (i in landmarks.indices) {
            val point = landmarkToScreen(i) ?: continue

            // Color joints by body region
            jointPaint.color = when (i) {
                in 0..10  -> Color.YELLOW       // face
                in 11..22 -> Color.CYAN         // upper body
                in 23..32 -> Color.GREEN        // lower body
                else      -> Color.WHITE
            }
            canvas.drawCircle(point.x, point.y, 10f, jointPaint)
        }
    }
}
```

---

## 6. Calculate Joint Angles — Exercise Counter

Joint angles let you build exercise recognition (squat counter, push-up counter, rep tracker):

```kotlin
// JointAngleCalculator.kt
package com.yourname.vision

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.*

object JointAngleCalculator {

    /**
     * Calculate the angle at the MIDDLE point of three landmarks.
     * Returns angle in degrees [0, 180].
     *
     * Example: elbow angle = angle(shoulder, elbow, wrist)
     */
    fun calculateAngle(
        a: NormalizedLandmark,    // first point
        b: NormalizedLandmark,    // vertex (the joint we're measuring)
        c: NormalizedLandmark     // third point
    ): Double {
        // Vectors from b to a and b to c
        val ba = doubleArrayOf(
            a.x().toDouble() - b.x().toDouble(),
            a.y().toDouble() - b.y().toDouble()
        )
        val bc = doubleArrayOf(
            c.x().toDouble() - b.x().toDouble(),
            c.y().toDouble() - b.y().toDouble()
        )

        val dotProduct = ba[0] * bc[0] + ba[1] * bc[1]
        val magnitudeA = sqrt(ba[0].pow(2) + ba[1].pow(2))
        val magnitudeB = sqrt(bc[0].pow(2) + bc[1].pow(2))

        if (magnitudeA == 0.0 || magnitudeB == 0.0) return 0.0

        val cosAngle = (dotProduct / (magnitudeA * magnitudeB)).coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(cosAngle))
    }
}

// Squat counter using knee angle
class SquatCounter {
    private var repCount = 0
    private var isSquatting = false

    companion object {
        const val SQUAT_DOWN_ANGLE = 90.0   // knee bent < 90° = squatting
        const val SQUAT_UP_ANGLE   = 160.0  // knee straight > 160° = standing
    }

    fun update(landmarks: List<NormalizedLandmark>): Int {
        // Left knee angle: hip (23) → knee (25) → ankle (27)
        val hipL   = landmarks.getOrNull(23) ?: return repCount
        val kneeL  = landmarks.getOrNull(25) ?: return repCount
        val ankleL = landmarks.getOrNull(27) ?: return repCount

        val kneeAngle = JointAngleCalculator.calculateAngle(hipL, kneeL, ankleL)

        if (!isSquatting && kneeAngle < SQUAT_DOWN_ANGLE) {
            isSquatting = true   // went down
        } else if (isSquatting && kneeAngle > SQUAT_UP_ANGLE) {
            isSquatting = false
            repCount++           // completed one rep!
        }

        return repCount
    }
}
```

---

## 7. Usage in Activity

```kotlin
// In your Activity
private lateinit var poseHelper: PoseLandmarkerHelper
private lateinit var poseOverlay: PoseOverlayView
private val squatCounter = SquatCounter()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pose)

    poseOverlay = findViewById(R.id.poseOverlay)

    poseHelper = PoseLandmarkerHelper(
        context = this,
        useGpu = true,
        onResults = { result, imgW, imgH ->
            runOnUiThread {
                poseOverlay.setResults(result, imgW, imgH)

                // Count squats
                result.landmarks().firstOrNull()?.let { lms ->
                    val reps = squatCounter.update(lms)
                    tvRepCount.text = "Squats: $reps"
                }
            }
        },
        onError = { Log.e("Pose", it) }
    )
}
```

---

## 8. Performance Reference

| Model | GPU Latency | CPU Latency | Accuracy |
|---|---|---|---|
| `pose_landmarker_lite` | ~25ms | ~80ms | Good |
| `pose_landmarker_full` | ~40ms | ~130ms | Very good |
| `pose_landmarker_heavy` | ~80ms | ~250ms | Best |

> 💡 Use `lite` for real-time (30fps target). Use `full` for fitness apps where accuracy matters more than speed.

---

## 9. Summary

- MediaPipe Pose gives 33 3D landmarks with visibility scores
- `POSE_CONNECTIONS` list defines which landmark pairs to connect as a skeleton
- Use `visibility < 0.5f` check to skip landmarks the model isn't confident about
- Joint angles enable exercise counting, rep tracking, form feedback
- `PoseOverlayView` draws skeleton and joints as a transparent overlay over the camera

---

## ➡️ Next Lesson

**[3.6 — Drawing Bounding Boxes & Overlays on CameraX Preview →](./06_bounding_box_overlay.md)**

---

*Module 3 · On-Device AI for Android Development*