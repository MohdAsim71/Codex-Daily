# 1.1 — Android AI Stack: From App to NPU

> **Module 1 · Lesson 1**  
> 📄 Theory · ⏱️ ~25 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Draw and explain the full Android AI software stack
- Understand how a Kotlin function call reaches the NPU chip
- Identify what happens at each layer during model inference
- Know why Android's layered architecture matters for performance

---

## 1. The Big Picture

When you call `interpreter.run(input, output)` in your Android app, that single line of Kotlin triggers a chain of events across 5 distinct layers — from your app code down to silicon.

```
┌─────────────────────────────────────────────────────────┐
│                   YOUR ANDROID APP                      │
│              (Kotlin / Java code)                       │
├─────────────────────────────────────────────────────────┤
│              INFERENCE LIBRARY LAYER                    │
│     TFLite │ ONNX Runtime │ MediaPipe │ ML Kit          │
├─────────────────────────────────────────────────────────┤
│           ANDROID HARDWARE ABSTRACTION                  │
│        NNAPI (Neural Networks API)                      │
├─────────────────────────────────────────────────────────┤
│              HARDWARE DRIVERS LAYER                     │
│   GPU Driver │ NPU Driver │ DSP Driver │ CPU Runtime    │
├─────────────────────────────────────────────────────────┤
│                   SILICON LAYER                         │
│     CPU cores │ GPU │ NPU │ DSP │ Unified Memory        │
└─────────────────────────────────────────────────────────┘
```

Each layer has a specific job. Let's go from top to bottom.

---

## 2. Layer 1 — Your Android App (Kotlin/Java)

This is the code you write. Your app is responsible for:

- Capturing input (camera frame, microphone audio, text)
- Preprocessing it into the format the model expects
- Calling the inference library
- Taking the model output and showing results to the user

```kotlin
// This is all YOUR code — Layer 1
val bitmap = captureFromCamera()                  // get input
val tensorImage = TensorImage.fromBitmap(bitmap)  // preprocess
val result = model.process(tensorImage)           // ← goes DOWN to Layer 2
showResultOnScreen(result)                        // show output
```

Your app code never talks to hardware directly. It always goes through Layer 2.

---

## 3. Layer 2 — Inference Library

This is where TFLite, ONNX Runtime, MediaPipe, or ML Kit lives. Its job:

- Load the model file (`.tflite`, `.onnx`, `.task`)
- Parse the computation graph
- Break the model into operations (Conv2D, MatMul, ReLU, etc.)
- Decide which hardware to send each operation to
- Call Layer 3 (NNAPI) or talk to hardware drivers directly

```
Model File (.tflite)
        │
        ▼
TFLite Interpreter reads the graph:
  ┌─ Conv2D op       → send to GPU via delegate
  ├─ BatchNorm op    → send to GPU via delegate
  ├─ Custom op       → fallback to CPU
  └─ Softmax op      → send to GPU via delegate
```

> 💡 **Key insight:** TFLite doesn't run everything on one chip. It dynamically partitions the model across available hardware.

---

## 4. Layer 3 — Android NNAPI (Neural Networks API)

NNAPI is Android's **hardware abstraction layer for AI**. Added in Android 8.1 (API 27).

Its job:
- Provide a **single unified API** so inference libraries don't need separate code for every chip vendor
- Route operations to the best available accelerator on the device
- Handle fallback — if NPU can't do an operation, fall back to GPU, then CPU

```
TFLite says: "Run this Conv2D operation"
        │
        ▼
      NNAPI
        │
        ├─ "Does NPU driver support Conv2D?"  YES → NPU
        ├─ "Does GPU driver support Conv2D?"  YES → GPU
        └─ Fallback:                               CPU
```

**Why NNAPI matters:**

```
Without NNAPI:
TFLite → Qualcomm SDK → Qualcomm NPU
TFLite → MediaTek SDK → MediaTek APU
TFLite → Google SDK   → Google TPU
(3 different codepaths!)

With NNAPI:
TFLite → NNAPI → [Qualcomm | MediaTek | Google] driver
(1 codebase, vendors handle the rest)
```

---

## 5. Layer 4 — Hardware Drivers

Each chip vendor ships kernel drivers that translate NNAPI calls into chip-specific instructions. You never write driver code, but understanding this layer helps debug performance issues.

| Chip Vendor | Chip Brand | NPU Driver |
|---|---|---|
| Qualcomm | Snapdragon | Hexagon NN Driver |
| MediaTek | Dimensity | NeuroPilot Driver |
| Google | Tensor G4 | Edge TPU Driver |
| Samsung | Exynos | Eden Driver |

---

## 6. Layer 5 — Silicon (The Hardware)

Modern Android SoCs pack CPU, GPU, NPU, and DSP onto one die with unified memory:

```
Snapdragon 8 Gen 3 (example)
┌──────────────────────────────────────────────┐
│  CPU Cluster          GPU (Adreno 750)       │
│  1× Prime core        77 TFLOPS             │
│  5× Perf cores                              │
│  2× Efficiency cores                        │
│                                              │
│  Hexagon NPU          Hexagon DSP           │
│  98 TOPS (INT8/4)     Audio/Sensor          │
│                                              │
│  Unified LPDDR5X Memory (up to 16 GB)       │
└──────────────────────────────────────────────┘
```

**Memory is unified** — CPU, GPU, NPU, and DSP share the same RAM pool. No expensive memory copy between CPU and GPU like on desktop machines.

---

## 7. The Full Journey: One Inference Call

Tracing `model.process(image)` all the way down:

```
1. YOUR APP        bitmap → TensorImage → model.process()
        │
        ▼
2. TFLITE          Parse graph, apply GPU delegate, partition ops
        │
        ▼
3. GPU DELEGATE    Convert tensors, upload input to GPU memory
        │
        ▼
4. ADRENO DRIVER   Compile shaders, schedule work on GPU cores
        │
        ▼
5. SILICON         Thousands of shader cores run Conv2D in parallel
                   ~8ms for YOLOv8n on Adreno 750
        │
        ▼ (back up the stack)
6. YOUR APP        DetectionResult[] → draw overlays on screen
```

Typical total time: ~8–20ms on flagship · ~30–80ms on mid-range · ~100–200ms on budget.

---

## 8. Why This Matters for You

| Problem | Root Cause | Solution |
|---|---|---|
| Inference is slow | Running on CPU only (Layer 2) | Enable GPU delegate |
| App crashes on some devices | NNAPI op not supported (Layer 3) | Add CPU fallback |
| Model won't load | Wrong format (Layer 2) | Reconvert to TFLite |
| Battery drains fast | GPU at full power (Layer 5) | Use NPU via NNAPI |
| OOM crash | Model too large (Layer 5) | Use INT8 quantized model |

---

## 9. Summary

- Android AI has **5 layers** between your Kotlin code and the chip
- **NNAPI** is the key abstraction — one API works across all Android chips
- The **inference library** (Layer 2) decides what hardware to use
- Modern SoCs have **unified memory** — no VRAM copy penalty
- Every inference call travels all 5 layers in milliseconds

---

## ➡️ Next Lesson

**[1.2 — TFLite vs ONNX Runtime vs MediaPipe vs ML Kit →](./02_tflite_vs_onnx_vs_mediapipe.md)**

---

*Module 1 · On-Device AI for Android Development*