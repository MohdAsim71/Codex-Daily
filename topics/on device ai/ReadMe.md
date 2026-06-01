# 🤖 On-Device AI for Android Development

> Build privacy-first, offline-capable Android apps powered by local AI — no cloud, no API keys, no data leaks.

![Android](https://img.shields.io/badge/Android-API%2026%2B-green?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-purple?style=flat-square&logo=kotlin)
![TFLite](https://img.shields.io/badge/TFLite-2.14-orange?style=flat-square)
![Level](https://img.shields.io/badge/Level-Intermediate-blue?style=flat-square)
![Modules](https://img.shields.io/badge/Modules-7-red?style=flat-square)
![Lessons](https://img.shields.io/badge/Lessons-38-teal?style=flat-square)

> ⚠️ **Pre-requisite:** Complete [On-Device AI with Python](../On-Device-AI-with-Python/) first.  
> That course gives you the model knowledge (ONNX, TFLite, quantization) you'll deploy here.

---

## 📊 Course Overview

| Detail | Info |
|---|---|
| 📦 Modules | 7 |
| 📝 Lessons | 38 |
| 📱 Android Projects | 6 |
| 🎯 Level | Intermediate |
| 💻 Language | Kotlin (primary) + Python (model prep) |
| 🛠️ IDE | Android Studio Hedgehog+ |
| 📱 Min Android API | 26 (Android 8.0) |

---

## ✅ Prerequisites

### From the Python Course (must complete first):
- ✅ Understanding of model formats: TFLite, ONNX
- ✅ Model quantization (INT8, FP16)
- ✅ How inference pipelines work
- ✅ Basic model export with PyTorch / TFLite

### Android Knowledge Needed:
- ✅ Kotlin basics (functions, classes, coroutines)
- ✅ Android Studio setup
- ✅ Basic Android app structure (Activity, ViewModel, Gradle)
- ✅ CameraX or basic camera API familiarity (helpful, not required)

---

## 🛠️ Tools & Frameworks

| Tool | Purpose | Used In |
|---|---|---|
| **TFLite Android SDK** | Run `.tflite` models in Android apps | Modules 2, 3, 4, 5 |
| **ONNX Runtime Mobile** | Run `.onnx` models on Android | Module 2 |
| **MediaPipe** | Google's on-device ML pipeline | Modules 3, 4 |
| **MediaPipe LLM Inference API** | Run Gemma/Phi on Android | Module 5 |
| **MLC-LLM** | Run quantized LLMs on Android GPU/NPU | Module 5 |
| **ML Kit** | Google's ready-made on-device AI APIs | Module 3 |
| **CameraX** | Modern Android camera API for AI | Modules 3, 4, 6 |
| **NNAPI** | Android's Neural Networks API (NPU access) | Module 6 |
| **QNN SDK** | Qualcomm NPU access | Module 6 |
| **Android Studio** | IDE for building and profiling | All modules |
| **Python + PyTorch** | Preparing & exporting models | Modules 2, 3 |

---

## 🗺️ Learning Path

```
On-Device AI with Python (prerequisite)
              │
              ▼
┌─────────────────────────────────────────────────┐
│         Android On-Device AI Course             │
│                                                 │
│  M1: Android AI Ecosystem                       │
│      │                                          │
│  M2: Deploying Models (TFLite + ONNX)           │
│      │                                          │
│  M3: Vision AI on Android (Camera + ML)         │
│      │                                          │
│  M4: Speech & NLP on Android                    │
│      │                                          │
│  M5: LLMs on Android (Gemma, Phi-3)             │
│      │                                          │
│  M6: NPU & Hardware Acceleration                │
│      │                                          │
│  M7: Production, Performance & Publishing       │
│                                                 │
└─────────────────────────────────────────────────┘
              │
              ▼
        Ship your AI Android app 🚀
```

---

## 📚 Curriculum

---

### Module 01 — The Android On-Device AI Ecosystem

> Android AI Stack · TFLite vs ONNX vs MediaPipe · ML Kit · NNAPI

| # | Lesson | Type |
|---|---|---|
| 1.1 | Android AI stack: from app to NPU | 📄 Theory |
| 1.2 | TFLite vs ONNX Runtime vs MediaPipe vs ML Kit — when to use what | 📄 Theory |
| 1.3 | How Android accesses hardware: NNAPI, GPU Delegate, Hexagon DSP | 📄 Theory |
| 1.4 | Setting up Android Studio for AI development | ⚙️ Setup |
| 1.5 | Your first AI inference in an Android app (Kotlin) | 📱 Code |

**Key concepts:**
```
Android App (Kotlin)
      │
      ▼
TFLite / ONNX Runtime / MediaPipe   ← inference library
      │
      ▼
Android NNAPI                        ← hardware abstraction layer
      │
      ├──▶ CPU (always available)
      ├──▶ GPU (OpenGL ES / Vulkan)
      ├──▶ NPU (Qualcomm Hexagon, MediaTek APU, Google Tensor)
      └──▶ DSP (Qualcomm Hexagon DSP)
```

---

### Module 02 — Deploying Models to Android

> Export · Convert · Integrate · Test

| # | Lesson | Type |
|---|---|---|
| 2.1 | Exporting PyTorch models to TFLite (Python side) | 📓 Notebook |
| 2.2 | Adding TFLite to an Android project (Gradle + AAR) | 📱 Code |
| 2.3 | Loading and running a `.tflite` model in Kotlin | 📱 Code |
| 2.4 | Using ONNX Runtime Mobile in Android | 📱 Code |
| 2.5 | Input/output tensor handling in Kotlin | 📱 Code |
| 2.6 | 🛠️ **Project:** Image classifier Android app (MobileNetV2) | 🛠️ Project |

**Gradle setup (preview):**
```kotlin
// build.gradle (app)
dependencies {
    // TFLite
    implementation 'org.tensorflow:tensorflow-lite:2.14.0'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'
    implementation 'org.tensorflow:tensorflow-lite-gpu:2.14.0'

    // ONNX Runtime Mobile
    implementation 'com.microsoft.onnxruntime:onnxruntime-android:1.17.0'

    // MediaPipe
    implementation 'com.google.mediapipe:tasks-vision:0.10.14'
}
```

**Running inference in Kotlin (preview):**
```kotlin
// Load model from assets
val model = MyModel.newInstance(context)

// Prepare input
val image = TensorImage.fromBitmap(bitmap)

// Run inference
val outputs = model.process(image)
val probability = outputs.probabilityAsTensorBuffer

// Cleanup
model.close()
```

---

### Module 03 — Vision AI on Android

> Object Detection · Face Detection · OCR · Pose Estimation · CameraX

| # | Lesson | Type |
|---|---|---|
| 3.1 | CameraX setup for real-time AI inference | 📱 Code |
| 3.2 | Object detection with YOLOv8 TFLite on Android | 📱 Code |
| 3.3 | Face detection & landmark with MediaPipe Face | 📱 Code |
| 3.4 | On-device OCR with ML Kit Text Recognition v2 | 📱 Code |
| 3.5 | Pose estimation with MediaPipe Pose | 📱 Code |
| 3.6 | Drawing bounding boxes & overlays on CameraX preview | 📱 Code |
| 3.7 | 🛠️ **Project:** Real-time object detector Android app | 🛠️ Project |

**CameraX + TFLite pipeline:**
```
CameraX Preview
      │
      ▼
ImageAnalysis.Analyzer   ← called every frame
      │
      ▼
Bitmap → TensorImage     ← preprocess
      │
      ▼
TFLite Interpreter       ← inference on GPU/NPU
      │
      ▼
DetectionResult[]        ← bounding boxes + labels
      │
      ▼
OverlayView.draw()       ← render on screen
```

**ML Kit OCR (simplest on-device OCR):**
```kotlin
val recognizer = TextRecognition.getClient(
    TextRecognizerOptions.DEFAULT_OPTIONS  // runs 100% on-device
)

recognizer.process(inputImage)
    .addOnSuccessListener { visionText ->
        for (block in visionText.textBlocks) {
            Log.d("OCR", block.text)
        }
    }
```

---

### Module 04 — Speech & NLP AI on Android

> Whisper · On-Device STT · Text Classification · Translation · Summarization

| # | Lesson | Type |
|---|---|---|
| 4.1 | On-device Speech-to-Text: options on Android | 📄 Theory |
| 4.2 | Whisper TFLite on Android — offline transcription | 📱 Code |
| 4.3 | Android SpeechRecognizer API vs on-device models | 📄 Theory |
| 4.4 | Text classification with TFLite NLP models | 📱 Code |
| 4.5 | On-device translation with ML Kit Translate | 📱 Code |
| 4.6 | Sentiment analysis on Android (DistilBERT TFLite) | 📱 Code |
| 4.7 | 🛠️ **Project:** Offline voice notes app with transcription | 🛠️ Project |

**ML Kit offline translation (50+ languages, no internet):**
```kotlin
// Download language model once, then works offline
val translator = Translation.getClient(
    TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.HINDI)
        .build()
)

// Download model if not present
translator.downloadModelIfNeeded()
    .addOnSuccessListener {
        translator.translate("Hello, world!")
            .addOnSuccessListener { translatedText ->
                Log.d("Translation", translatedText)  // "नमस्ते, दुनिया!"
            }
    }
```

**On-device STT options comparison:**

| Option | Offline | Quality | Size | Best For |
|---|---|---|---|---|
| Android SpeechRecognizer | ❌ (needs internet) | High | 0 MB | Quick, quality STT |
| ML Kit (on-device) | ✅ | Medium | ~80 MB | Basic commands |
| Whisper TFLite (tiny) | ✅ | Good | ~40 MB | Short clips |
| Whisper TFLite (base) | ✅ | Very good | ~150 MB | General STT |
| Vosk | ✅ | Good | ~50 MB | Continuous STT |

---

### Module 05 — LLMs on Android

> Gemma · Phi-3 · MediaPipe LLM API · MLC-LLM · RAG on Android

| # | Lesson | Type |
|---|---|---|
| 5.1 | State of LLMs on Android — what's feasible in 2025 | 📄 Theory |
| 5.2 | MediaPipe LLM Inference API — run Gemma 2B locally | 📱 Code |
| 5.3 | MLC-LLM on Android — Phi-3 Mini on your phone | 📱 Code |
| 5.4 | Streaming token output in Android UI (Kotlin Flow) | 📱 Code |
| 5.5 | Building a local RAG pipeline on Android | 📱 Code |
| 5.6 | Memory management for LLMs on mobile | 📄 Theory |
| 5.7 | 🛠️ **Project:** Fully offline AI chat app (Gemma 2B) | 🛠️ Project |

**MediaPipe LLM Inference — Gemma on Android:**
```kotlin
// Add dependency
// implementation("com.google.mediapipe:tasks-genai:0.10.14")

val options = LlmInference.LlmInferenceOptions.builder()
    .setModelPath("/data/local/tmp/gemma-2b-it-gpu-int4.bin")
    .setMaxTokens(512)
    .setPreferredBackend(LlmInference.Backend.GPU)  // uses phone GPU/NPU
    .build()

val llmInference = LlmInference.createFromOptions(context, options)

// Streaming response (shows tokens as they generate)
llmInference.generateResponseAsync(
    prompt = "Explain on-device AI in simple terms.",
    listener = { partialResult, done ->
        runOnUiThread {
            textView.append(partialResult)
        }
    }
)
```

**LLM options for Android (2025):**

| Model | Size on Disk | RAM Needed | Backend | Speed |
|---|---|---|---|---|
| Gemma 2B INT4 | ~1.3 GB | ~2 GB | GPU / NPU | Fast |
| Phi-3 Mini INT4 | ~2.2 GB | ~3 GB | GPU | Medium |
| Gemma 7B INT4 | ~4.7 GB | ~6 GB | GPU | Slow |
| TinyLlama 1.1B | ~0.7 GB | ~1.5 GB | CPU | Fast |

> ⚠️ **Device requirement:** LLMs need 6 GB+ RAM. Works well on flagship phones (Pixel 8 Pro, Samsung S24, OnePlus 12).

---

### Module 06 — NPU & Hardware Acceleration on Android

> NNAPI · GPU Delegate · Qualcomm QNN · Hexagon DSP · Benchmarking

| # | Lesson | Type |
|---|---|---|
| 6.1 | Android Neural Networks API (NNAPI) deep-dive | 📄 Theory |
| 6.2 | Enabling GPU Delegate in TFLite (2–5× speedup) | 📱 Code |
| 6.3 | Qualcomm Hexagon NPU via QNN SDK | 📱 Code |
| 6.4 | Profiling with Android Studio ML Model Binding | 📱 Code |
| 6.5 | Benchmarking with TFLite Benchmark Tool | 📱 Code |
| 6.6 | 🛠️ **Project:** Benchmark the same model on CPU vs GPU vs NPU | 🛠️ Project |

**Enabling GPU Delegate (biggest easy win):**
```kotlin
// Without GPU delegate: CPUExecutionProvider ~120ms
// With GPU delegate: GPUExecutionProvider ~18ms  → 6.7× faster

val gpuDelegate = GpuDelegate(
    GpuDelegate.Options().apply {
        setPrecisionLossAllowed(true)  // FP16 on GPU — faster
        setQuantizedModelsAllowed(true)
    }
)

val options = Interpreter.Options().apply {
    addDelegate(gpuDelegate)
    setNumThreads(4)
}

val interpreter = Interpreter(modelBuffer, options)
```

**Hardware acceleration decision tree:**
```
What hardware does the user's phone have?
        │
        ├─ Qualcomm Snapdragon (most Android flagship)
        │     → Try QNN Delegate → GPU Delegate → NNAPI → CPU
        │
        ├─ MediaTek Dimensity
        │     → Try NNAPI → GPU Delegate → CPU
        │
        ├─ Google Tensor (Pixel phones)
        │     → Try NNAPI (has dedicated TPU) → GPU → CPU
        │
        └─ Unknown / budget chip
              → GPU Delegate → CPU (safe fallback)
```

**Benchmark results (typical flagship, YOLOv8n TFLite):**

| Delegate | Latency | Power | Notes |
|---|---|---|---|
| CPU (4 threads) | ~120ms | Medium | Always works |
| GPU Delegate | ~18ms | Medium-High | Best general choice |
| NNAPI | ~12ms | Low | Needs Android 8.1+ |
| Hexagon (QNN) | ~8ms | Lowest | Qualcomm only |

---

### Module 07 — Production, Performance & Publishing

> Memory · Battery · App Size · Play Store · Model Updates

| # | Lesson | Type |
|---|---|---|
| 7.1 | Managing model lifecycle (load, cache, unload) | 📱 Code |
| 7.2 | Reducing APK size: model compression & dynamic delivery | 📱 Code |
| 7.3 | Battery & thermal management for AI apps | 📄 Theory |
| 7.4 | Updating models without app updates (remote model delivery) | 📱 Code |
| 7.5 | Privacy compliance: GDPR, on-device AI audit checklist | 📄 Theory |
| 7.6 | Publishing an AI Android app to the Play Store | 📱 Code |
| 7.7 | 🏁 **Capstone:** End-to-end AI Android app — build, optimize & ship | 🏁 Capstone |

**Keeping APK size manageable:**
```kotlin
// Option 1: Bundle model inside APK (simple, but increases size)
// Place .tflite in /assets — adds directly to APK

// Option 2: Play Asset Delivery (large models, free CDN delivery)
// build.gradle
android {
    assetPacks = [":objectDetectionModel"]
}
// Delivers model on first launch, not bundled in APK download

// Option 3: Firebase ML (download & update models remotely)
val remoteModel = CustomRemoteModel.Builder("yolov8-detector").build()
FirebaseModelManager.getInstance().download(remoteModel, conditions)
```

**Model lifecycle best practice:**
```kotlin
class VisionViewModel : ViewModel() {

    private var interpreter: Interpreter? = null

    fun loadModel(context: Context) {
        // Load on background thread
        viewModelScope.launch(Dispatchers.IO) {
            interpreter = Interpreter(loadModelFile(context), options)
        }
    }

    override fun onCleared() {
        super.onCleared()
        interpreter?.close()   // ← always release GPU/NPU memory
        interpreter = null
    }
}
```

---

## 📁 Repository Structure

```
On-Device-AI-Android/
│
├── 📁 module_01_ecosystem/
│   ├── 01_android_ai_stack.md
│   ├── 02_tflite_vs_onnx_vs_mediapipe.md
│   ├── 03_nnapi_hardware.md
│   ├── 04_android_studio_setup.md
│   └── 05_first_inference_kotlin/          ← Android Studio project
│       ├── app/
│       └── build.gradle
│
├── 📁 module_02_deploying_models/
│   ├── 01_export_pytorch_to_tflite.ipynb   ← Python notebook
│   ├── 02_add_tflite_to_android.md
│   ├── 03_run_tflite_kotlin/               ← Android Studio project
│   ├── 04_onnx_runtime_mobile/             ← Android Studio project
│   ├── 05_tensor_handling.md
│   └── project_image_classifier/           ← Full Android app
│
├── 📁 module_03_vision_ai/
│   ├── 01_camerax_setup/
│   ├── 02_yolov8_tflite_android/
│   ├── 03_mediapipe_face/
│   ├── 04_mlkit_ocr/
│   ├── 05_mediapipe_pose/
│   ├── 06_bounding_box_overlay/
│   └── project_realtime_detector/          ← Full Android app
│
├── 📁 module_04_speech_nlp/
│   ├── 01_stt_options.md
│   ├── 02_whisper_tflite_android/
│   ├── 03_speechrecognizer_vs_ondevice.md
│   ├── 04_text_classification_tflite/
│   ├── 05_mlkit_translate/
│   ├── 06_distilbert_tflite/
│   └── project_voice_notes/                ← Full Android app
│
├── 📁 module_05_llms_android/
│   ├── 01_llms_on_android_2025.md
│   ├── 02_mediapipe_llm_gemma/
│   ├── 03_mlc_llm_phi3/
│   ├── 04_streaming_kotlin_flow/
│   ├── 05_rag_on_android/
│   ├── 06_memory_management.md
│   └── project_offline_chat_app/           ← Full Android app
│
├── 📁 module_06_npu_acceleration/
│   ├── 01_nnapi_deepdive.md
│   ├── 02_gpu_delegate_tflite/
│   ├── 03_qualcomm_qnn/
│   ├── 04_android_studio_profiling/
│   ├── 05_tflite_benchmark_tool.md
│   └── project_hardware_benchmark/         ← Full Android app
│
├── 📁 module_07_production/
│   ├── 01_model_lifecycle.md
│   ├── 02_apk_size_reduction.md
│   ├── 03_battery_thermal.md
│   ├── 04_remote_model_delivery/
│   ├── 05_privacy_compliance_checklist.md
│   ├── 06_play_store_publishing.md
│   └── capstone_full_ai_app/               ← Complete shippable app
│
├── 📁 models/                              ← Pretrained .tflite files
│   ├── mobilenetv2.tflite
│   ├── yolov8n.tflite
│   ├── whisper_tiny.tflite
│   └── gemma-2b-it-gpu-int4.bin
│
├── 📁 python_model_prep/                   ← Python scripts for model export
│   ├── export_to_tflite.py
│   ├── quantize_model.py
│   └── validate_tflite.py
│
├── requirements_python.txt                 ← Python deps for model prep
└── README.md
```

---

## 🚀 Getting Started

### 1. Set Up Android Studio

```bash
# Download Android Studio: https://developer.android.com/studio
# Install SDK: API 26+ (Android 8.0 minimum)
# Install NDK and CMake (for native model libraries)
```

### 2. Clone This Repo

```bash
git clone https://github.com/your-username/On-Device-AI-Android.git
cd On-Device-AI-Android
```

### 3. Set Up Python Environment (for model preparation)

```bash
python -m venv android-ai-env
source android-ai-env/bin/activate

pip install -r requirements_python.txt
```

### 4. Open First Android Project

```bash
# Open in Android Studio:
File → Open → module_02_deploying_models/project_image_classifier/
```

---

## 📦 requirements_python.txt

```
torch>=2.2.0
torchvision>=0.17.0
tensorflow>=2.14.0
onnx>=1.16.0
onnx-tf>=1.10.0
numpy>=1.26.0
pillow>=10.0.0
```

---

## 🗺️ Two-Course Learning Roadmap

```
PHASE 1 — Foundations (2–3 months)
On-Device AI with Python
├── Understand model formats (TFLite, ONNX)
├── Learn quantization & compression
├── Run models locally on your laptop
└── Build confidence with inference pipelines
          │
          ▼
PHASE 2 — Android Deployment (2–3 months)
On-Device AI for Android
├── Port those same models into Android apps
├── Use CameraX, MediaPipe, ML Kit
├── Run LLMs on actual phones (Gemma 2B)
├── Unlock NPU acceleration
└── Ship a real app to the Play Store
          │
          ▼
OUTCOME — You can build production Android apps
that run powerful AI completely offline 🚀
```

---

## 📱 What You'll Be Able to Build

By the end of this course you can build Android apps that:

- 📷 Detect objects in real-time using the camera — fully offline
- 🗣️ Transcribe speech to text without sending audio anywhere
- 💬 Run a local chatbot (Gemma 2B) on the phone with no internet
- 🔤 Translate text between 50+ languages on-device
- 🧑 Detect faces, poses, and gestures with MediaPipe
- 📄 Read and extract text from documents via OCR — offline
- 🚀 Accelerate all of the above using the phone's NPU/GPU

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/new-lesson`
3. Commit your changes: `git commit -m 'Add lesson: GPU delegate setup'`
4. Push to the branch: `git push origin feature/new-lesson`
5. Open a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

> Made with ❤️ for Android developers who want to ship AI apps without the cloud.  
> Star ⭐ the repo if this helps you. Contributions welcome!