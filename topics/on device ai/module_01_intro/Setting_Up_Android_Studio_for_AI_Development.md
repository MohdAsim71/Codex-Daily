# 1.4 — Setting Up Android Studio for AI Development

> **Module 1 · Lesson 4**  
> ⚙️ Setup Guide · ⏱️ ~20 min

---

## 🎯 What You'll Set Up

By the end of this lesson your machine will have:

- ✅ Android Studio Hedgehog (2023.1.1) or newer
- ✅ Android SDK API 26–35 installed
- ✅ NDK and CMake (required for native AI libraries)
- ✅ An emulator OR a physical device connected via ADB
- ✅ A starter Android project with TFLite dependency ready

---

## 1. Install Android Studio

Download from: **https://developer.android.com/studio**

| OS | Notes |
|---|---|
| Windows | Run the `.exe` installer, default settings are fine |
| macOS | Drag to `/Applications`, open and allow in Security settings |
| Ubuntu/Debian | Extract `.tar.gz`, run `studio.sh` from `bin/` |

**Minimum system requirements for AI development:**

| Requirement | Minimum | Recommended |
|---|---|---|
| RAM | 8 GB | 16 GB+ |
| Disk | 8 GB free | 20 GB+ free |
| OS | Windows 10 / macOS 12 / Ubuntu 20.04 | Latest |
| CPU | Any x86_64 | Apple M-series or AMD Ryzen 7+ |

---

## 2. Install Required SDK Components

Open Android Studio → **SDK Manager** (top right gear icon or `Tools → SDK Manager`)

### SDK Platforms tab — install:
- ✅ Android 14.0 (API 35) — latest
- ✅ Android 13.0 (API 33) — wide target
- ✅ Android 8.0 (API 26) — minimum for this course

### SDK Tools tab — install:
- ✅ Android SDK Build-Tools (latest)
- ✅ Android Emulator
- ✅ Android SDK Platform-Tools
- ✅ **NDK (Side by side)** ← required for TFLite GPU delegate and ONNX Runtime
- ✅ **CMake** ← required for native libraries

```
SDK Manager → SDK Tools → check:
  ☑ NDK (Side by side)   ← version 25.x or newer
  ☑ CMake                ← version 3.22.x or newer
```

---

## 3. Set Up a Physical Device (Recommended)

A real Android phone gives you real NPU/GPU benchmarks. Emulators don't have NPUs.

### Enable Developer Options on your phone:
```
Settings → About Phone → tap "Build Number" 7 times
→ Developer Options now appears in Settings
```

### Enable USB Debugging:
```
Settings → Developer Options → USB Debugging → ON
```

### Connect and verify:
```bash
# Check device is detected
adb devices

# Expected output:
List of devices attached
XXXXXXXXXX    device   ← your phone
```

If `adb` is not found, add Android SDK platform-tools to your PATH:
```bash
# macOS / Linux — add to ~/.bashrc or ~/.zshrc
export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools

# Windows — add to System Environment Variables:
C:\Users\<you>\AppData\Local\Android\Sdk\platform-tools
```

---

## 4. Set Up an Emulator (Optional)

Emulators work for basic testing but **cannot test GPU delegate or NNAPI** (no NPU in emulator).

```
Android Studio → Device Manager → Create Device
→ Category: Phone
→ Model: Pixel 8 (recommended)
→ System Image: API 34, Google APIs x86_64
→ Finish
```

> 💡 For emulator acceleration on your machine:
> - **Intel CPU:** Enable Intel HAXM in SDK Manager
> - **AMD CPU:** Enable Windows Hypervisor Platform
> - **Apple Silicon:** ARM emulators run natively — very fast

---

## 5. Create a New Android Project

```
File → New → New Project
→ Template: Empty Activity
→ Name: OnDeviceAIStarter
→ Package name: com.yourname.ondeviceai
→ Language: Kotlin
→ Minimum SDK: API 26 (Android 8.0)
→ Finish
```

---

## 6. Add AI Dependencies to Gradle

Open `app/build.gradle` and add to the `dependencies` block:

```kotlin
// app/build.gradle

android {
    defaultConfig {
        minSdk = 26
        targetSdk = 35
    }

    // Required for TFLite model assets
    aaptOptions {
        noCompress "tflite"
    }
}

dependencies {
    // ── TensorFlow Lite ──────────────────────────────────
    implementation 'org.tensorflow:tensorflow-lite:2.14.0'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'
    implementation 'org.tensorflow:tensorflow-lite-gpu:2.14.0'
    implementation 'org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.4.4'

    // ── ONNX Runtime Mobile ──────────────────────────────
    implementation 'com.microsoft.onnxruntime:onnxruntime-android:1.17.0'

    // ── MediaPipe ────────────────────────────────────────
    implementation 'com.google.mediapipe:tasks-vision:0.10.14'
    implementation 'com.google.mediapipe:tasks-text:0.10.14'

    // ── ML Kit ───────────────────────────────────────────
    implementation 'com.google.mlkit:text-recognition:16.0.0'
    implementation 'com.google.mlkit:object-detection:17.0.1'
    implementation 'com.google.mlkit:translate:17.0.2'

    // ── CameraX (for real-time AI with camera) ───────────
    def camerax_version = "1.3.1"
    implementation "androidx.camera:camera-core:$camerax_version"
    implementation "androidx.camera:camera-camera2:$camerax_version"
    implementation "androidx.camera:camera-lifecycle:$camerax_version"
    implementation "androidx.camera:camera-view:$camerax_version"
}
```

> 💡 You don't need ALL of these in every project. Add only what you use — keeps APK size small. We'll use specific subsets each module.

Click **Sync Now** in the yellow banner. First sync downloads ~200 MB.

---

## 7. Add a TFLite Model to Assets

Place your `.tflite` model file in the assets folder:

```
app/
└── src/
    └── main/
        └── assets/         ← create this folder if missing
            └── mobilenetv2.tflite
```

**Create the assets folder:**
```
Right-click on main/ → New → Directory → type "assets"
```

**Add model file:**
- Download `mobilenetv2.tflite` from the course `models/` folder
- Drag and drop into the `assets/` folder in Android Studio

---

## 8. Configure AndroidManifest.xml

Open `app/src/main/AndroidManifest.xml` and add required permissions:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Camera permission for real-time AI -->
    <uses-permission android:name="android.permission.CAMERA" />

    <!-- Internet — only if downloading models remotely -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- Audio recording for speech AI -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <!-- Declare camera feature (optional but good practice) -->
    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

    <application
        android:hardwareAccelerated="true"   ← important for GPU delegate
        ... >
```

---

## 9. Verify Everything Works

Add this to your `MainActivity.kt` to verify TFLite loads correctly:

```kotlin
// MainActivity.kt
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyTFLite()
    }

    private fun verifyTFLite() {
        try {
            // Load model from assets
            val model = FileUtil.loadMappedFile(this, "mobilenetv2.tflite")
            val interpreter = Interpreter(model)

            // Check input/output shapes
            val inputShape = interpreter.getInputTensor(0).shape()
            val outputShape = interpreter.getOutputTensor(0).shape()

            Log.d("TFLite", "✅ Model loaded successfully!")
            Log.d("TFLite", "Input shape : ${inputShape.toList()}")
            Log.d("TFLite", "Output shape: ${outputShape.toList()}")

            interpreter.close()
        } catch (e: Exception) {
            Log.e("TFLite", "❌ Failed to load model: ${e.message}")
        }
    }
}
```

Run the app. Check Logcat (filter by "TFLite"):

```
D/TFLite: ✅ Model loaded successfully!
D/TFLite: Input shape : [1, 224, 224, 3]
D/TFLite: Output shape: [1, 1000]
```

If you see this — your setup is complete! ✅

---

## 10. Useful Android Studio Settings for AI Dev

```
Preferences → Editor → General → Auto Import
  ☑ Add unambiguous imports on the fly
  ☑ Optimize imports on the fly

Preferences → Build, Execution, Deployment → Compiler
  ☑ Compile independent modules in parallel

Logcat filter for AI debugging:
  Tag: TFLite OR MediaPipe OR NNAPI
```

**Useful ADB commands during development:**

```bash
# See live logcat from your app only
adb logcat -s TFLite MediaPipe NNAPI

# Push a new model to device for testing
adb push new_model.tflite /data/local/tmp/

# Check available memory on device
adb shell cat /proc/meminfo | grep MemAvailable

# Run TFLite benchmark tool
adb shell /data/local/tmp/benchmark_model \
  --graph=/data/local/tmp/model.tflite \
  --use_gpu=true \
  --num_runs=50
```

---

## ✅ Setup Checklist

```
☑ Android Studio installed (Hedgehog or newer)
☑ SDK API 26, 33, 35 installed
☑ NDK (Side by side) installed
☑ CMake installed
☑ Physical device connected (adb devices shows it) OR emulator set up
☑ New Android project created (min SDK 26, Kotlin)
☑ TFLite + MediaPipe + ML Kit dependencies added to Gradle
☑ assets/ folder created with mobilenetv2.tflite
☑ AndroidManifest.xml updated with permissions
☑ Verification test shows: "✅ Model loaded successfully!"
```

---

## ➡️ Next Lesson

**[1.5 — Your First AI Inference in an Android App (Kotlin) →](./05_first_inference_kotlin.md)**

---

*Module 1 · On-Device AI for Android Development*