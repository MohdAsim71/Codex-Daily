# 3.4 — On-Device OCR with ML Kit Text Recognition v2

> **Module 3 · Lesson 4**  
> 📱 Code · ⏱️ ~25 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Add ML Kit Text Recognition v2 to Android
- Recognize text from still images and live camera frames
- Extract structured data: blocks, lines, words, and symbols
- Build a document scanner that works completely offline
- Handle multiple languages and scripts

---

## 1. ML Kit Text Recognition v2 Overview

```
ML Kit Text Recognition v2 capabilities:

  ✅ 100+ languages and scripts
  ✅ Works fully offline (model bundled or downloaded once)
  ✅ Recognizes: Latin, Chinese, Devanagari, Japanese, Korean
  ✅ Structured output: Block → Line → Word → Symbol
  ✅ Bounding boxes for every element
  ✅ Confidence scores per element
  ✅ No API key, no internet, no data sent anywhere

Model options:
  Latin script   — bundled, ~1 MB, instant
  Chinese/Japanese/Korean — downloaded on first use, ~10 MB each
  Devanagari (Hindi, Sanskrit) — downloaded on first use, ~8 MB
```

---

## 2. Gradle Dependencies

```kotlin
// app/build.gradle
dependencies {
    // Latin script — bundled in AAR, no download needed
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // Chinese Traditional + Simplified
    implementation("com.google.mlkit:text-recognition-chinese:16.0.0")

    // Japanese
    implementation("com.google.mlkit:text-recognition-japanese:16.0.0")

    // Korean
    implementation("com.google.mlkit:text-recognition-korean:16.0.0")

    // Devanagari (Hindi, Sanskrit, Marathi, etc.)
    implementation("com.google.mlkit:text-recognition-devanagari:16.0.0")
}
```

> 💡 Add only the scripts you need. Each script library is ~1–10 MB.

---

## 3. Basic OCR — Still Image

The simplest possible OCR — process a Bitmap from gallery or camera:

```kotlin
// OcrHelper.kt
package com.yourname.vision

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrHelper {

    // Create recognizer once and reuse — expensive to create
    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS   // Latin script, fully offline
    )

    /**
     * Recognize text in a Bitmap. Runs on a background thread automatically.
     * Results arrive on the calling thread via callbacks.
     */
    fun recognizeText(
        bitmap: Bitmap,
        rotationDegrees: Int = 0,
        onSuccess: (Text) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val inputImage = InputImage.fromBitmap(bitmap, rotationDegrees)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                onSuccess(visionText)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun close() = recognizer.close()
}
```

---

## 4. Parse the Structured OCR Output

ML Kit returns a rich hierarchy of text elements:

```kotlin
fun parseOcrResult(visionText: Text) {
    // Full text (all recognized text joined)
    val fullText = visionText.text
    Log.d("OCR", "Full text: $fullText")

    // Structured hierarchy
    for (block in visionText.textBlocks) {
        // Block = paragraph-level chunk
        Log.d("OCR", "\n── Block ──────────────────────")
        Log.d("OCR", "Text   : ${block.text}")
        Log.d("OCR", "Box    : ${block.boundingBox}")
        Log.d("OCR", "Angle  : ${block.recognizedLanguage}")

        for (line in block.lines) {
            // Line = one line of text
            Log.d("OCR", "  Line : ${line.text}")
            Log.d("OCR", "  Conf : ${line.confidence}")

            for (element in line.elements) {
                // Element = one word
                Log.d("OCR", "    Word: ${element.text} | box=${element.boundingBox}")

                // Symbol-level (character by character) — only if needed
                for (symbol in element.symbols) {
                    Log.d("OCR", "      Char: '${symbol.text}' conf=${symbol.confidence}")
                }
            }
        }
    }
}
```

---

## 5. Real-Time OCR with CameraX

For live camera OCR (e.g. scanning receipts, signs, labels):

```kotlin
// LiveOcrAnalyzer.kt
package com.yourname.vision

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class LiveOcrAnalyzer(
    private val onTextDetected: (Text) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    // Flag to avoid overlapping inference calls
    private var isProcessing = false

    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        isProcessing = true
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            isProcessing = false
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                onTextDetected(visionText)
            }
            .addOnCompleteListener {
                imageProxy.close()   // close INSIDE the callback
                isProcessing = false
            }
    }
}
```

---

## 6. Document Scanner Use Case

Extract all text from a document photo, organized by reading order:

```kotlin
// DocumentScanner.kt
package com.yourname.vision

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text

data class DocumentScanResult(
    val fullText: String,
    val lines: List<String>,
    val wordCount: Int,
    val confidence: Float
)

class DocumentScanner {

    fun scan(bitmap: Bitmap, onResult: (DocumentScanResult) -> Unit) {
        OcrHelper.recognizeText(
            bitmap = bitmap,
            onSuccess = { visionText ->
                val lines = mutableListOf<String>()
                var totalConf = 0f
                var lineCount = 0

                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        lines.add(line.text.trim())
                        totalConf += line.confidence ?: 0f
                        lineCount++
                    }
                }

                val wordCount = visionText.text
                    .split("\\s+".toRegex())
                    .filter { it.isNotBlank() }
                    .size

                onResult(DocumentScanResult(
                    fullText  = visionText.text,
                    lines     = lines,
                    wordCount = wordCount,
                    confidence = if (lineCount > 0) totalConf / lineCount else 0f
                ))
            },
            onFailure = { e ->
                android.util.Log.e("DocumentScanner", "OCR failed: ${e.message}")
            }
        )
    }
}
```

---

## 7. Multi-Language OCR

To recognize non-Latin scripts, swap the recognizer options:

```kotlin
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

// Select recognizer based on target language
fun createRecognizer(language: String) = when (language) {
    "zh" -> TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    "hi" -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
    "ja" -> TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    "ko" -> TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    else -> TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)  // Latin
}
```

> 💡 Non-Latin models download on first use (~8–10 MB). After that they work offline. Show a progress indicator on first launch.

---

## 8. Bounding Box Overlay for OCR

Draw detected text regions on screen:

```kotlin
// In your custom OverlayView
fun drawOcrBoxes(canvas: Canvas, visionText: Text, scaleX: Float, scaleY: Float) {
    val boxPaint = Paint().apply {
        color = Color.argb(100, 0, 255, 0)
        style = Paint.Style.FILL
    }
    val strokePaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 28f
        setShadowLayer(4f, 0f, 0f, Color.BLACK)
    }

    for (block in visionText.textBlocks) {
        block.boundingBox?.let { box ->
            val scaledBox = RectF(
                box.left   * scaleX,
                box.top    * scaleY,
                box.right  * scaleX,
                box.bottom * scaleY
            )
            canvas.drawRect(scaledBox, boxPaint)
            canvas.drawRect(scaledBox, strokePaint)
            canvas.drawText(block.text.take(30), scaledBox.left, scaledBox.top - 4f, textPaint)
        }
    }
}
```

---

## 9. Performance Reference

| Scenario | Latency | Notes |
|---|---|---|
| Still image, Latin, phone | 50–200ms | Fast on modern devices |
| Live camera, Latin | 80–300ms | Use `isProcessing` flag |
| Still image, Chinese | 200–500ms | Larger model |
| High-res document photo | 300–800ms | Scale down to 1080p first |

> 💡 **Tip:** For live OCR, scale the camera frame down to 720p before processing. Text is still readable but inference is 2–3× faster.

---

## 10. Summary

- ML Kit Text Recognition v2 is the easiest path to offline OCR on Android
- Structured output: `textBlocks → lines → elements → symbols`
- Use `InputImage.fromMediaImage()` for CameraX live frames
- Use `isProcessing` flag to prevent overlapping inference calls
- Non-Latin scripts (Chinese, Hindi) download ~8–10 MB on first use, then work offline

---

## ➡️ Next Lesson

**[3.5 — Pose Estimation with MediaPipe Pose →](./05_mediapipe_pose.md)**

---

*Module 3 · On-Device AI for Android Development*