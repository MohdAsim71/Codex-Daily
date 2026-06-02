# 2.2 — Adding TFLite to an Android Project (Gradle + AAR)

> **Module 2 · Lesson 2**  
> 📱 Code · ⏱️ ~20 min

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Add TFLite dependencies to a Gradle project correctly
- Understand the difference between core, support, and GPU libraries
- Prevent Gradle from compressing your `.tflite` model file
- Add a model to the assets folder and verify it loads

---

## 1. TFLite Library Structure

TFLite on Android is split into several libraries. You only add what you need:

```
TFLite Android Libraries
│
├── tensorflow-lite              ← core interpreter (always needed)
├── tensorflow-lite-support      ← helper classes (ImageProcessor, TensorImage)
├── tensorflow-lite-gpu          ← GPU Delegate for hardware acceleration
├── tensorflow-lite-gpu-delegate-plugin  ← GPU delegate auto-init
├── tensorflow-lite-task-vision  ← high-level vision tasks (detector, classifier)
├── tensorflow-lite-task-text    ← high-level text tasks (NLP classifiers)
└── tensorflow-lite-task-audio   ← high-level audio tasks
```

For this course we focus on core + support + gpu. The `task-*` libraries are wrappers that hide details — useful for quick prototyping but limit control.

---

## 2. Gradle Setup — Module-Level build.gradle

Open `app/build.gradle` (the **app** level, not project level):

```kotlin
// app/build.gradle

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yourname.ondeviceai"
        minSdk = 26          // Android 8.0 — minimum for NNAPI
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    // ── CRITICAL: Prevent compression of model files ──────
    // TFLite uses memory-mapped file I/O for performance.
    // If Gradle compresses the .tflite file, memory mapping breaks.
    androidResources {
        noCompress += listOf("tflite", "lite", "bin", "task")
    }

    // ── Required for native libraries in TFLite GPU ───────
    packagingOptions {
        pickFirst("**/libOpenCL.so")
        pickFirst("**/libOpenCL-pixel.so")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // ── TFLite Core (required) ────────────────────────────
    implementation("org.tensorflow:tensorflow-lite:2.14.0")

    // ── TFLite Support Library ────────────────────────────
    // Provides: TensorImage, ImageProcessor, TensorBuffer,
    //           FileUtil, NormalizeOp, ResizeOp
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // ── GPU Acceleration ──────────────────────────────────
    implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.4.4")

    // ── Standard Android dependencies ────────────────────
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
```

> 💡 **Groovy vs Kotlin DSL:** The examples above use Kotlin DSL (`.kts`). If your project uses Groovy DSL (`build.gradle` without `.kts`), replace `implementation(...)` with `implementation '...'` and `listOf(...)` with `["..."]`.

---

## 3. ProGuard Rules (for Release Builds)

Add these to `app/proguard-rules.pro` to prevent TFLite from being stripped in release builds:

```
# TFLite
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.gpu.** { *; }
-keep class org.tensorflow.lite.support.** { *; }

# ONNX Runtime (if used)
-keep class ai.onnxruntime.** { *; }

# MediaPipe (if used)
-keep class com.google.mediapipe.** { *; }
```

---

## 4. Add Your Model to Assets

The `assets/` folder is the correct place for TFLite model files in Android.

### Create the assets folder (if missing):

```
app/src/main/
└── assets/              ← create this directory
    ├── mobilenetv2.tflite
    └── imagenet_labels.txt
```

**In Android Studio:**
```
Right-click on: app/src/main/
→ New → Directory
→ Type: assets
→ OK
```

Then drag your `.tflite` file into that folder, or:
```
Right-click assets/ → Show in Explorer/Finder
→ paste your .tflite file
```

### Verify it shows in Android Studio:

```
app/
└── src/
    └── main/
        └── assets/
            └── mobilenetv2.tflite   ← should appear here with a model icon
```

---

## 5. Verify the Model Loads at Runtime

Add this utility function to check your model loads correctly before building the full classifier:

```kotlin
// ModelVerifier.kt
package com.yourname.ondeviceai

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

object ModelVerifier {

    fun verify(context: Context, modelFileName: String): Boolean {
        return try {
            val modelBuffer = FileUtil.loadMappedFile(context, modelFileName)
            val interpreter = Interpreter(modelBuffer)

            val inputCount  = interpreter.inputTensorCount
            val outputCount = interpreter.outputTensorCount

            Log.d("ModelVerifier", "✅ Model loaded: $modelFileName")
            Log.d("ModelVerifier", "   Input tensors  : $inputCount")
            Log.d("ModelVerifier", "   Output tensors : $outputCount")

            for (i in 0 until inputCount) {
                val tensor = interpreter.getInputTensor(i)
                Log.d("ModelVerifier",
                    "   Input[$i]: shape=${tensor.shape().toList()}, " +
                    "dtype=${tensor.dataType()}, name=${tensor.name()}")
            }

            for (i in 0 until outputCount) {
                val tensor = interpreter.getOutputTensor(i)
                Log.d("ModelVerifier",
                    "   Output[$i]: shape=${tensor.shape().toList()}, " +
                    "dtype=${tensor.dataType()}, name=${tensor.name()}")
            }

            interpreter.close()
            true

        } catch (e: Exception) {
            Log.e("ModelVerifier", "❌ Failed to load $modelFileName: ${e.message}")
            false
        }
    }
}
```

Call it from `MainActivity.onCreate()`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Verify model on startup
    ModelVerifier.verify(this, "mobilenetv2.tflite")
}
```

**Expected Logcat output:**
```
D/ModelVerifier: ✅ Model loaded: mobilenetv2.tflite
D/ModelVerifier:    Input tensors  : 1
D/ModelVerifier:    Output tensors : 1
D/ModelVerifier:    Input[0]: shape=[1, 224, 224, 3], dtype=FLOAT32, name=input
D/ModelVerifier:    Output[0]: shape=[1, 1000], dtype=FLOAT32, name=output
```

---

## 6. APK Size Impact

Understanding how TFLite affects your APK size:

| Component | Size Added |
|---|---|
| `tensorflow-lite` (core) | ~1.2 MB |
| `tensorflow-lite-support` | ~0.4 MB |
| `tensorflow-lite-gpu` | ~1.6 MB |
| MobileNetV2 FP32 model | +14.0 MB |
| MobileNetV2 INT8 model | +3.6 MB |
| **Total with INT8 model** | **~6.8 MB** |

> 💡 **Tip:** Use **Play Asset Delivery** to deliver large model files separately from the APK download. Covered in Module 7.

---

## 7. Multiple Models in One App

If your app uses more than one model, put them all in `assets/`:

```
assets/
├── classifier.tflite       ← image classification
├── detector.tflite         ← object detection
├── nlp_model.tflite        ← text classification
└── labels/
    ├── imagenet_labels.txt
    └── custom_labels.txt
```

Load them separately:

```kotlin
val classifier = Interpreter(FileUtil.loadMappedFile(context, "classifier.tflite"))
val detector   = Interpreter(FileUtil.loadMappedFile(context, "detector.tflite"))
val nlpModel   = Interpreter(FileUtil.loadMappedFile(context, "nlp_model.tflite"))

// Close all when done
override fun onDestroy() {
    classifier.close()
    detector.close()
    nlpModel.close()
}
```

---

## 8. Common Gradle Errors and Fixes

| Error | Cause | Fix |
|---|---|---|
| `Failed to open file (Is it a zip file?)` | Model was compressed by Gradle | Add `noCompress += "tflite"` |
| `Duplicate class org.tensorflow...` | Two conflicting TFLite versions | Use `implementation` not `api`; check transitive deps |
| `libOpenCL.so: Multiple files...` | OpenCL conflict with other libs | Add `pickFirst("**/libOpenCL.so")` |
| `Cannot find symbol GpuDelegate` | GPU library not added | Add `tensorflow-lite-gpu` dependency |
| `Unresolved reference: FileUtil` | Support library missing | Add `tensorflow-lite-support` dependency |

---

## 9. Summary

- Always add `noCompress += "tflite"` — without this memory mapping breaks
- The TFLite library is split — only add what you use to keep APK small
- Model files go in `app/src/main/assets/`
- Add ProGuard rules for release builds
- Use `ModelVerifier` to confirm input/output tensor shapes before building inference logic
- INT8 quantized models are ~4× smaller with near-identical accuracy

---

## ➡️ Next Lesson

**[2.3 — Loading and Running a .tflite Model in Kotlin →](./03_run_tflite_kotlin.md)**

---

*Module 2 · On-Device AI for Android Development*