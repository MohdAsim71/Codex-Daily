# 1.2 — TFLite vs ONNX Runtime vs MediaPipe vs ML Kit

> **Module 1 · Lesson 2**  
> 📄 Theory · ⏱️ ~25 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Explain the purpose and strengths of each Android AI framework
- Choose the right framework for a given use case
- Understand the tradeoffs between flexibility, ease, and performance
- Know which framework to reach for first on a new project

---

## 1. The Four Frameworks at a Glance

```
┌──────────────────┬─────────────────────────────────────────────────┐
│   Framework      │   Best described as...                          │
├──────────────────┼─────────────────────────────────────────────────┤
│ TFLite           │ The Swiss Army knife — run any model, full control│
│ ONNX Runtime     │ The cross-platform choice — models from anywhere  │
│ MediaPipe        │ The pipeline builder — vision/audio workflows     │
│ ML Kit           │ The fastest start — Google's ready-made AI APIs   │
└──────────────────┴─────────────────────────────────────────────────┘
```

They are **not competitors** — they solve different problems. Many apps use 2 or 3 of them together.

---

## 2. TensorFlow Lite (TFLite)

**Made by:** Google  
**Model format:** `.tflite`  
**Best for:** Custom models, full control, widest device support

### What it is

TFLite is Google's inference runtime for mobile and embedded devices. It takes a `.tflite` model file and runs it on Android (or iOS, Raspberry Pi, microcontrollers).

### Strengths

- ✅ Widest hardware support (every Android device since API 21)
- ✅ GPU Delegate and NNAPI Delegate for acceleration
- ✅ TFLite Model Maker — easy fine-tuning for custom data
- ✅ Large ecosystem — most ML tutorials target TFLite
- ✅ TFLite Support Library — handles preprocessing for you
- ✅ Works offline, no account needed

### Weaknesses

- ❌ Must convert models to `.tflite` format first (from PyTorch or TF)
- ❌ Not all PyTorch ops are supported in TFLite
- ❌ More boilerplate than ML Kit for common tasks

### When to use TFLite

- You have a custom trained model (object detector, classifier, etc.)
- You need fine-grained control over inference
- You want to run models on a wide range of Android devices
- Your model was trained in TensorFlow or can be exported to TFLite

### Code preview

```kotlin
// Load model from assets
val model = FileUtil.loadMappedFile(context, "model.tflite")
val options = Interpreter.Options().apply {
    addDelegate(GpuDelegate())   // hardware acceleration
    setNumThreads(4)
}
val interpreter = Interpreter(model, options)

// Run inference
interpreter.run(inputArray, outputArray)
```

---

## 3. ONNX Runtime Mobile

**Made by:** Microsoft  
**Model format:** `.onnx`  
**Best for:** PyTorch models, cross-platform (Android + iOS + Windows)

### What it is

ONNX Runtime is Microsoft's inference engine that runs `.onnx` models — the format PyTorch exports to natively.

### Strengths

- ✅ PyTorch → ONNX is one line: `torch.onnx.export(model, ...)`
- ✅ Same model file works on Android, iOS, Windows, Linux
- ✅ Supports many PyTorch ops that TFLite doesn't
- ✅ NNAPI Execution Provider for NPU acceleration
- ✅ Strong for NLP models (BERT, GPT-style)

### Weaknesses

- ❌ Larger binary size than TFLite (~10 MB AAR)
- ❌ Fewer Android-specific optimizations than TFLite
- ❌ Less documentation for mobile vs. desktop use
- ❌ ONNX Runtime Mobile is a subset — not all ops available

### When to use ONNX Runtime

- Your model was trained in PyTorch
- You need the same model to run on Android AND iOS AND desktop
- You're using NLP models (Transformers, BERT variants)
- You want to skip the PyTorch → TFLite conversion step

### Code preview

```kotlin
// Add dependency: com.microsoft.onnxruntime:onnxruntime-android

val sessionOptions = OrtSession.SessionOptions().apply {
    addNnapi()   // use NNAPI for NPU/GPU acceleration
}

val env = OrtEnvironment.getEnvironment()
val session = env.createSession(modelBytes, sessionOptions)

// Run inference
val inputTensor = OnnxTensor.createTensor(env, inputArray, shape)
val result = session.run(mapOf("input" to inputTensor))
val output = result[0].value as Array<FloatArray>
```

---

## 4. MediaPipe

**Made by:** Google  
**Model format:** `.task` bundles (TFLite inside)  
**Best for:** Vision and audio pipelines, real-time camera processing

### What it is

MediaPipe is Google's framework for building **perception pipelines** — chains of AI models working together in real-time. It's not just an inference engine; it's a full pipeline system with built-in preprocessing, postprocessing, and multi-model chaining.

### Strengths

- ✅ Pre-built solutions: face detection, hand tracking, pose, object detection, image segmentation
- ✅ Handles camera input → model → overlay rendering as one pipeline
- ✅ Optimized for real-time performance (30–60 fps)
- ✅ MediaPipe LLM Inference API — run Gemma 2B on-device
- ✅ Cross-platform (Android, iOS, Web, Python)
- ✅ GPU-accelerated out of the box

### Weaknesses

- ❌ Less flexible for custom model architectures
- ❌ Larger dependency than bare TFLite
- ❌ `.task` bundle format — harder to inspect than raw `.tflite`
- ❌ Opinionated pipeline structure — harder to customize mid-pipeline

### When to use MediaPipe

- You need real-time camera-based AI (face, hands, pose, objects)
- You want pre-built, production-ready solutions without training
- You're building multimodal pipelines (e.g. detect hands → classify gesture)
- You want to run a small LLM (Gemma 2B) on Android

### Code preview

```kotlin
// Object detection — 5 lines of Kotlin
val options = ObjectDetector.ObjectDetectorOptions.builder()
    .setBaseOptions(BaseOptions.builder().useGpu().build())
    .setMaxResults(5)
    .setScoreThreshold(0.5f)
    .build()

val detector = ObjectDetector.createFromFileAndOptions(context, "model.task", options)
val result = detector.detect(mpImage)   // returns List<Detection>
```

---

## 5. ML Kit

**Made by:** Google  
**Model format:** Managed internally (you don't see the model file)  
**Best for:** Common tasks with zero model management

### What it is

ML Kit is Google's **ready-made AI API** for Android (and iOS). Google ships the models; you just call the API. You don't download, convert, or manage any model files.

### Strengths

- ✅ Fastest to get started — literally 5 lines of code
- ✅ No model file management (Google handles it)
- ✅ Works fully offline (models bundled or downloaded once)
- ✅ Covers the most common AI tasks out of the box
- ✅ Automatic updates — Google ships better models, your app improves
- ✅ Free, no API key, no usage limits

### Weaknesses

- ❌ No custom model support — only what Google provides
- ❌ Can't fine-tune or replace the model
- ❌ Black box — you can't see the model weights
- ❌ Limited to specific task categories

### What ML Kit can do (built-in)

| Feature | Offline | Notes |
|---|---|---|
| Text recognition (OCR) | ✅ | 100+ scripts/languages |
| Face detection | ✅ | Landmarks, contours |
| Barcode scanning | ✅ | All major formats |
| Object detection | ✅ | Coarse classification |
| Language identification | ✅ | 100+ languages |
| Translation | ✅ (after download) | 50+ language pairs |
| Smart reply | ✅ | Short reply suggestions |
| Image labeling | ✅ | 400+ labels |
| Pose detection | ✅ | 33 body landmarks |

### Code preview

```kotlin
// OCR — doesn't get simpler than this
val recognizer = TextRecognition.getClient(
    TextRecognizerOptions.DEFAULT_OPTIONS
)

recognizer.process(InputImage.fromBitmap(bitmap, 0))
    .addOnSuccessListener { visionText ->
        textView.text = visionText.text   // done!
    }
```

---

## 6. Side-by-Side Comparison

| Feature | TFLite | ONNX Runtime | MediaPipe | ML Kit |
|---|---|---|---|---|
| **Custom models** | ✅ Full | ✅ Full | ⚠️ Limited | ❌ No |
| **PyTorch models** | ⚠️ Convert needed | ✅ Native | ❌ No | ❌ No |
| **Ease of start** | Medium | Medium | Easy | Easiest |
| **Real-time video** | ⚠️ Manual setup | ⚠️ Manual | ✅ Built-in | ✅ Built-in |
| **LLMs on Android** | ❌ | ❌ | ✅ (Gemma) | ❌ |
| **GPU acceleration** | ✅ | ✅ | ✅ | ✅ |
| **NPU / NNAPI** | ✅ | ✅ | ✅ | ✅ |
| **Cross-platform** | Android/iOS/Pi | All platforms | Android/iOS/Web | Android/iOS |
| **Model size control** | ✅ Full | ✅ Full | ⚠️ Partial | ❌ |
| **Offline** | ✅ | ✅ | ✅ | ✅ |
| **Binary overhead** | ~1 MB | ~10 MB | ~5–20 MB | ~5 MB |

---

## 7. Decision Framework

```
START: What is your task?
       │
       ├─ Common task (OCR, face, barcode, translation, labels)
       │     → ML Kit first (done in 5 lines, zero management)
       │
       ├─ Real-time camera pipeline (detect, track, pose, gesture)
       │     → MediaPipe (built for this exact use case)
       │
       ├─ Run Gemma / small LLM on Android
       │     → MediaPipe LLM Inference API
       │
       ├─ Custom model trained in PyTorch
       │     └─ Need cross-platform (Android + iOS)?
       │           YES → ONNX Runtime Mobile
       │           NO  → Convert to TFLite → TFLite
       │
       └─ Custom model trained in TensorFlow / Keras
             → TFLite (direct conversion, best support)
```

---

## 8. Real Project Examples

| App Type | Framework Choice | Why |
|---|---|---|
| Document scanner | ML Kit (OCR) | Built-in, zero setup, best quality |
| Custom brand logo detector | TFLite | Need custom training |
| Fitness pose tracker | MediaPipe | Real-time, 33-landmark pose built-in |
| Offline translator | ML Kit Translate | 50 languages, downloads once |
| Medical image classifier | TFLite | Custom model, full control |
| PyTorch NLP model deploy | ONNX Runtime | No conversion step needed |
| Local LLM chatbot | MediaPipe LLM | Gemma 2B on GPU |
| Barcode inventory app | ML Kit | 1 line of code |

---

## 9. Summary

- **ML Kit** — start here for common tasks. No model files, fastest to ship.
- **MediaPipe** — for real-time camera AI and pipelines. Also for on-device LLMs.
- **TFLite** — for custom models trained in TF/Keras. Maximum flexibility.
- **ONNX Runtime** — for PyTorch models. Best cross-platform story.

They complement each other. Production apps often combine ML Kit for OCR + TFLite for a custom classifier + MediaPipe for camera processing.

---

## ➡️ Next Lesson

**[1.3 — How Android Accesses Hardware: NNAPI, GPU Delegate, Hexagon DSP →](./03_nnapi_hardware.md)**

---

*Module 1 · On-Device AI for Android Development*