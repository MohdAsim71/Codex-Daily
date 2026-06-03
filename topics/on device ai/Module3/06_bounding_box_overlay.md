# 3.6 — Drawing Bounding Boxes & Overlays on CameraX Preview

> **Module 3 · Lesson 6**  
> 📱 Code · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Build a reusable `OverlayView` for drawing any AI result on screen
- Correctly scale AI coordinates from model space to screen space
- Draw bounding boxes, labels, landmarks, and skeleton connections
- Handle device rotation and preview aspect ratio differences
- Animate overlays smoothly without causing UI jank

---

## 1. The Coordinate Space Problem

This is the #1 source of wrong overlay positioning. You have three different coordinate spaces:

```
Model input space        Camera image space       Screen space
──────────────           ──────────────────       ─────────────
e.g. 320×320             e.g. 640×480             e.g. 1080×2400
│                        │                        │
│ [0,0] ────────────►    │ [0,0] ──────────────►  │ [0,0] ─────────────►
│                        │                        │
│ Model outputs           Camera delivers          View.draw()
│ coords in              coords in its            needs coords
│ its input size          captured resolution      in pixels
```

You must convert: Model space → Camera space → Screen space

```
Detection box from YOLO (model space):
  x1=0.2, y1=0.1, x2=0.5, y2=0.4  (normalized 0-1)

Step 1: To camera image pixels (640×480):
  x1 = 0.2 × 640 = 128px
  y1 = 0.1 × 480 = 48px

Step 2: To screen pixels (OverlayView is 1080×2400):
  scaleX = 1080 / 640 = 1.6875
  scaleY = 2400 / 480 = 5.0
  x1 = 128 × 1.6875 = 216px  ← final screen X
  y1 = 48  × 5.0    = 240px  ← final screen Y
```

---

## 2. Universal OverlayView — Full Implementation

A single custom View that handles YOLO boxes, face landmarks, pose skeletons, and text overlays:

```kotlin
// OverlayView.kt
package com.yourname.vision

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.yourname.vision.Detection

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // ── State ─────────────────────────────────────────────
    private var detections: List<Detection> = emptyList()
    private var landmarks: List<PointF> = emptyList()
    private var connections: List<Pair<Int, Int>> = emptyList()

    // Image dimensions that produced the AI results
    private var imageWidth  = 1
    private var imageHeight = 1

    // Scale factors: image coords → view coords
    private val scaleX get() = width.toFloat()  / imageWidth
    private val scaleY get() = height.toFloat() / imageHeight

    // ── Paints ───────────────────────────────────────────
    private val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val labelBgPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val labelTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    private val landmarkPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    private val bonePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }

    // Class-based colors for YOLO (cycles through these for different classes)
    private val classColors = listOf(
        Color.rgb(255, 89, 94),    // red
        Color.rgb(255, 202, 58),   // yellow
        Color.rgb(138, 201, 38),   // green
        Color.rgb(25, 130, 196),   // blue
        Color.rgb(106, 76, 147),   // purple
        Color.rgb(255, 146, 76),   // orange
        Color.rgb(73, 213, 203),   // teal
        Color.rgb(255, 107, 129),  // pink
    )

    // ── Public API ────────────────────────────────────────

    /** Set YOLO/object detection results */
    fun setDetections(
        detections: List<Detection>,
        imageWidth: Int,
        imageHeight: Int
    ) {
        this.detections  = detections
        this.imageWidth  = imageWidth
        this.imageHeight = imageHeight
        invalidate()
    }

    /** Set landmark points (face, pose) */
    fun setLandmarks(
        points: List<PointF>,            // in image pixel coords
        connections: List<Pair<Int,Int>>,
        imageWidth: Int,
        imageHeight: Int
    ) {
        this.landmarks   = points
        this.connections = connections
        this.imageWidth  = imageWidth
        this.imageHeight = imageHeight
        invalidate()
    }

    /** Clear everything */
    fun clear() {
        detections  = emptyList()
        landmarks   = emptyList()
        connections = emptyList()
        invalidate()
    }

    // ── Drawing ───────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDetections(canvas)
        drawLandmarks(canvas)
    }

    private fun drawDetections(canvas: Canvas) {
        for (detection in detections) {
            val box = detection.boundingBox
            val color = classColors[detection.classId % classColors.size]

            // Scale box from image coords to view coords
            val left   = box.left   * scaleX
            val top    = box.top    * scaleY
            val right  = box.right  * scaleX
            val bottom = box.bottom * scaleY
            val scaledBox = RectF(left, top, right, bottom)

            // Draw semi-transparent fill
            fillPaint.color = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
            canvas.drawRoundRect(scaledBox, 8f, 8f, fillPaint)

            // Draw box border
            boxPaint.color = color
            canvas.drawRoundRect(scaledBox, 8f, 8f, boxPaint)

            // Draw corner accents (makes it look more polished)
            drawCornerAccents(canvas, scaledBox, color)

            // Draw label background + text
            val label = "${detection.label}  ${(detection.confidence * 100).toInt()}%"
            val textWidth = labelTextPaint.measureText(label)
            val textHeight = labelTextPaint.textSize
            val labelTop  = (top - textHeight - 8f).coerceAtLeast(0f)
            val labelRect = RectF(left, labelTop, left + textWidth + 16f, labelTop + textHeight + 8f)

            labelBgPaint.color = color
            canvas.drawRoundRect(labelRect, 4f, 4f, labelBgPaint)
            canvas.drawText(label, left + 8f, labelTop + textHeight, labelTextPaint)
        }
    }

    private fun drawCornerAccents(canvas: Canvas, box: RectF, color: Int) {
        val cornerLen = (box.width() * 0.15f).coerceAtMost(30f)
        val paint = Paint(boxPaint).apply {
            this.color = color
            strokeWidth = 8f
        }

        // Top-left
        canvas.drawLine(box.left, box.top, box.left + cornerLen, box.top, paint)
        canvas.drawLine(box.left, box.top, box.left, box.top + cornerLen, paint)
        // Top-right
        canvas.drawLine(box.right, box.top, box.right - cornerLen, box.top, paint)
        canvas.drawLine(box.right, box.top, box.right, box.top + cornerLen, paint)
        // Bottom-left
        canvas.drawLine(box.left, box.bottom, box.left + cornerLen, box.bottom, paint)
        canvas.drawLine(box.left, box.bottom, box.left, box.bottom - cornerLen, paint)
        // Bottom-right
        canvas.drawLine(box.right, box.bottom, box.right - cornerLen, box.bottom, paint)
        canvas.drawLine(box.right, box.bottom, box.right, box.bottom - cornerLen, paint)
    }

    private fun drawLandmarks(canvas: Canvas) {
        if (landmarks.isEmpty()) return

        // Draw connections (skeleton bones)
        for ((from, to) in connections) {
            val p1 = landmarks.getOrNull(from) ?: continue
            val p2 = landmarks.getOrNull(to)   ?: continue
            canvas.drawLine(
                p1.x * scaleX, p1.y * scaleY,
                p2.x * scaleX, p2.y * scaleY,
                bonePaint
            )
        }

        // Draw landmark points
        for (point in landmarks) {
            canvas.drawCircle(point.x * scaleX, point.y * scaleY, 8f, landmarkPaint)
        }
    }
}
```

---

## 3. Drawing a Confidence Bar

For a polished UX, add a confidence bar below each label:

```kotlin
private fun drawConfidenceBar(canvas: Canvas, box: RectF, confidence: Float, color: Int) {
    val barWidth = box.width()
    val barHeight = 6f
    val barTop = box.bottom + 4f

    // Background
    val bgPaint = Paint().apply {
        this.color = Color.argb(100, 0, 0, 0)
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(
        RectF(box.left, barTop, box.right, barTop + barHeight),
        3f, 3f, bgPaint
    )

    // Fill
    val fillPaint = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(
        RectF(box.left, barTop, box.left + barWidth * confidence, barTop + barHeight),
        3f, 3f, fillPaint
    )
}
```

---

## 4. Handling Rotation

Camera images can have a rotation offset relative to the screen. Always account for it:

```kotlin
// ImageProxy delivers rotation — pass it through to the overlay
override fun analyze(imageProxy: ImageProxy) {
    val rotation = imageProxy.imageInfo.rotationDegrees
    val bitmap = imageProxy.toBitmap()

    // If rotation is 90 or 270, image width/height are swapped
    val (effectiveW, effectiveH) = if (rotation == 90 || rotation == 270) {
        bitmap.height to bitmap.width
    } else {
        bitmap.width to bitmap.height
    }

    val detections = detector.detect(bitmap)
    runOnUiThread {
        overlayView.setDetections(detections, effectiveW, effectiveH)
    }
    imageProxy.close()
}
```

---

## 5. Smooth Overlay Updates — Avoid UI Jank

Calling `invalidate()` every frame on the main thread can cause dropped UI frames. Use these patterns:

```kotlin
// Pattern 1: Throttle overlay updates to 30fps max
private var lastOverlayUpdate = 0L
fun updateOverlaySafely(detections: List<Detection>) {
    val now = System.currentTimeMillis()
    if (now - lastOverlayUpdate < 33) return   // max 30fps
    lastOverlayUpdate = now
    runOnUiThread {
        overlayView.setDetections(detections, imageWidth, imageHeight)
    }
}

// Pattern 2: Use postOnAnimation for smooth rendering
overlayView.postOnAnimation {
    overlayView.setDetections(detections, imageWidth, imageHeight)
}
```

---

## 6. Overlay Layout in XML

```xml
<!-- Always use FrameLayout so overlay sits on top of camera preview -->
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.camera.view.PreviewView
        android:id="@+id/previewView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!-- OverlayView on top — transparent background -->
    <com.yourname.vision.OverlayView
        android:id="@+id/overlayView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/transparent" />

</FrameLayout>
```

---

## 7. Complete Usage Example

```kotlin
// In your Activity — everything wired together
class DetectorActivity : AppCompatActivity() {

    private lateinit var overlayView: OverlayView
    private lateinit var detector: YoloV8Detector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detector)

        overlayView = findViewById(R.id.overlayView)
        detector = YoloV8Detector(this, useGpu = true)

        startCameraWithAnalyzer { imageProxy ->
            val bitmap = imageProxy.toBitmap()
            val detections = detector.detect(bitmap)

            runOnUiThread {
                // OverlayView handles all scaling internally
                overlayView.setDetections(
                    detections  = detections,
                    imageWidth  = bitmap.width,
                    imageHeight = bitmap.height
                )
            }
            imageProxy.close()
        }
    }
}
```

---

## 8. Summary

- Three coordinate spaces: model → image → screen. Always scale correctly.
- `scaleX = viewWidth / imageWidth`, `scaleY = viewHeight / imageHeight`
- `OverlayView` extends `View`, draws in `onDraw()`, triggered by `invalidate()`
- Use `FrameLayout` with transparent `OverlayView` layered over `PreviewView`
- Corner accents, confidence bars, and color-coded labels make the UX polished
- Throttle overlay updates to 30fps to avoid janky UI

---

## ➡️ Next Lesson

**[3.7 — 🛠️ Project: Real-Time Object Detector Android App →](./07_project_realtime_detector.md)**

---

*Module 3 · On-Device AI for Android Development*