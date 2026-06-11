# 6.1 — Android Neural Networks API (NNAPI) Deep-Dive

> **Module 6 · Lesson 1**  
> 📄 Theory · ⏱️ ~25 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Explain NNAPI's role in Android's AI stack
- Understand how NNAPI routes operations to hardware accelerators
- Know which operations NNAPI supports and which fall back to CPU
- Identify when NNAPI helps vs hurts performance
- Debug NNAPI acceleration issues on real devices

---

## 1. What is NNAPI?

```
Android Neural Networks API (NNAPI)
  Introduced: Android 8.1 (API 27), November 2017
  Purpose: Hardware abstraction layer for AI inference

Without NNAPI:
  TFLite → write driver code for each vendor (Qualcomm, MediaTek, Google...)
  Every framework duplicates vendor-specific code

With NNAPI:
  TFLite → NNAPI → [Qualcomm driver | MediaTek driver | Google driver]
  One unified API — vendors implement their own drivers
```

NNAPI sits between inference frameworks (TFLite, PyTorch Mobile) and hardware drivers:

```
┌─────────────────────────────────────────────┐
│            Your Android App                 │
├─────────────────────────────────────────────┤
│        TFLite / ONNX Runtime                │  ← inference framework
├─────────────────────────────────────────────┤
│      Android NNAPI (android.nn.*)           │  ← hardware abstraction
├──────────────┬──────────────┬───────────────┤
│  Qualcomm    │  MediaTek    │  Google       │  ← vendor HAL drivers
│  Hexagon     │  NeuroPilot  │  Edge TPU     │
├──────────────┴──────────────┴───────────────┤
│   CPU │ GPU │ NPU │ DSP │ Custom Silicon    │  ← actual hardware
└─────────────────────────────────────────────┘
```

---

## 2. NNAPI Version History

Each Android version improved NNAPI significantly:

| Android | API | Key Additions |
|---|---|---|
| 8.1 | 27 | Initial release, basic ops (Conv2D, FC, ReLU) |
| 9.0 | 28 | + LSTM, batch norm, more activation functions |
| 10.0 | 29 | + quantized INT8 ops, dynamic shapes |
| 11.0 | 30 | + vendor extensions, better error reporting |
| 12.0 | 31 | + burst execution, control flow ops |
| 13.0 | 33 | + memory domain support, improved efficiency |
| 14.0+ | 34+ | + transformer-friendly ops, extended quantization |

> 💡 **Minimum target:** API 29 (Android 10) for full INT8 quantization support — the most important feature for on-device AI.

---

## 3. How NNAPI Routes Operations

When TFLite submits a model to NNAPI, this happens:

```
Step 1: Model Partitioning
  TFLite sends entire model graph to NNAPI
  NNAPI queries each available accelerator:
    "Can you run Conv2D?"  → Qualcomm: YES
    "Can you run LSTM?"    → Qualcomm: NO (this version)
    "Can you run GeLU?"    → MediaTek: NO

Step 2: Graph Partitioning
  NNAPI splits the model:
    Subgraph A (NPU-supported ops) → NPU
    Subgraph B (unsupported ops)   → CPU fallback

Step 3: Execution
  NPU executes Subgraph A → returns partial outputs
  CPU executes Subgraph B → returns final outputs
  NNAPI assembles the full output

Step 4: Result returned to TFLite
```

The partitioning is automatic — you don't control it directly.

---

## 4. NNAPI Operation Support Matrix

Not all operations are accelerated by all NPUs:

```
Operation           Qualcomm    MediaTek    Google      Always?
──────────────────  ─────────   ─────────   ─────────   ───────
Conv2D              ✅ NPU      ✅ NPU      ✅ TPU      ✅ CPU
DepthwiseConv2D     ✅ NPU      ✅ NPU      ✅ TPU      ✅ CPU
FullyConnected      ✅ NPU      ✅ NPU      ✅ TPU      ✅ CPU
ReLU / ReLU6        ✅ NPU      ✅ NPU      ✅ TPU      ✅ CPU
BatchNorm           ✅ NPU      ✅ NPU      ✅ TPU      ✅ CPU
MaxPool / AvgPool   ✅ NPU      ✅ NPU      ✅ TPU      ✅ CPU
LSTM / RNN          ⚠️ partial  ⚠️ partial  ✅ TPU      ✅ CPU
Softmax             ✅ NPU      ⚠️ partial  ✅ TPU      ✅ CPU
Resize Bilinear     ⚠️ GPU      ✅ NPU      ✅ TPU      ✅ CPU
Custom ops          ❌          ❌          ❌          ✅ CPU
Transformer (SDPA)  ⚠️ API 34+ ❌          ✅ API 34+  ✅ CPU
```

The takeaway: standard CNN ops (Conv, Pool, BN, ReLU) are almost always NPU-accelerated. Transformers and custom ops usually fall back to CPU.

---

## 5. NNAPI Hardware Abstraction Layer (HAL)

Vendors implement the NNAPI HAL to make their hardware accessible:

```
NNAPI HAL versions:
  HAL 1.0 (Android 8.1)  — basic ops
  HAL 1.1 (Android 9.0)  — batch, padding
  HAL 1.2 (Android 10.0) — full quantization, burst
  HAL 1.3 (Android 11.0) — vendor extensions, fenced execution
  AIDL HAL (Android 12+) — replaced HIDl, better stability

Vendor HAL implementations:
  Qualcomm → Hexagon NN Delegate (part of SNPE)
  MediaTek  → NeuroPilot Delegate
  Google    → Edge TPU Driver (Pixel-specific)
  Samsung   → Eden Driver (Exynos-specific)
  ARM       → Ethos-N NPU Driver
```

---

## 6. NNAPI Burst Execution

For sustained inference (e.g. real-time camera AI), use burst execution:

```
Normal NNAPI execution (per-frame):
  Frame 1 → Open connection → Execute → Close → 8ms + 2ms overhead
  Frame 2 → Open connection → Execute → Close → 8ms + 2ms overhead
  Total overhead per 30 frames: 60ms wasted

NNAPI Burst execution:
  Open connection once
  Frame 1 → Execute → 8ms
  Frame 2 → Execute → 8ms
  Frame 30 → Execute → 8ms
  Close connection
  Total overhead: 2ms (just open + close)
```

TFLite uses burst execution automatically when `INFERENCE_PREFERENCE_SUSTAINED_SPEED` is set.

---

## 7. When NNAPI Helps vs Hurts

### NNAPI HELPS (use it):
```
✅ Large CNN models (MobileNet, EfficientNet, YOLO)
✅ Models with standard ops (Conv, BN, Pool, ReLU)
✅ Sustained inference (video, real-time camera)
✅ Quantized INT8 models (on Android 10+)
✅ Flagship/mid-range devices from 2021+
✅ Repeated inference (amortizes connection overhead)
```

### NNAPI HURTS (avoid it):
```
❌ Small models with few ops (overhead > benefit)
❌ Models with many custom or unsupported ops
❌ One-shot inference (photo classification, single text)
❌ Budget devices with poor NNAPI drivers
❌ Models with dynamic input shapes (bad driver support)
❌ Very old devices (API 27–28) — poor implementation
```

---

## 8. NNAPI Caching — Critical for Cold Start

First NNAPI execution compiles the model for the specific NPU. This takes 3–10 seconds. Always use caching:

```kotlin
// Without caching: 8 seconds cold start EVERY TIME
val options = Interpreter.Options().addDelegate(NnApiDelegate())

// With caching: 8 seconds ONCE, then < 100ms
val options = Interpreter.Options().addDelegate(
    NnApiDelegate(
        NnApiDelegate.Options().apply {
            setCacheDir(cacheDir.absolutePath)
            setModelToken("yolov8n_v1")   // unique key per model version
        }
    )
)
```

The compiled model is cached in the app's cache directory. Cache is invalidated if:
- Device firmware is updated
- The model file changes
- You change the `modelToken`

---

## 9. Debugging NNAPI Acceleration

```bash
# Check if NNAPI is accelerating on your device
adb shell setprop debug.nn.vlog all
adb logcat -s NeuralNetworks

# Look for lines like:
# "Partition 0: [Conv2D, BatchNorm, ReLU] → DEVICE: qti-hta"  ← NPU
# "Partition 1: [LSTM]                    → DEVICE: nnapi-cpu" ← CPU fallback

# Check available NNAPI devices
adb shell cmd neural_networks list-devices
# Output:
# nnapi-reference (CPU reference implementation)
# qti-hta (Qualcomm High-performance Tensor Accelerator)
# qti-dsp (Qualcomm DSP)
```

```kotlin
// In code — check NNAPI devices programmatically (Android 10+)
import android.os.Build

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    // Use reflection or NnApiDelegate.getAvailableDevices()
    // to list available accelerators
    val delegate = NnApiDelegate()
    val devices = delegate.supportedDevices
    devices.forEach { device ->
        Log.d("NNAPI", "Device: ${device.name} type=${device.deviceType}")
    }
}
```

---

## 10. NNAPI vs GPU Delegate — When to Use Each

```
NNAPI Delegate:
  Targets: NPU (Neural Processing Unit) primarily
  Power:   Most efficient — NPU is purpose-built, lowest watts/TOPS
  Speed:   Fastest on flagship devices with modern NPUs
  Models:  Best for standard CNN ops and INT8 quantized models
  Risk:    Driver quality varies widely across devices

GPU Delegate:
  Targets: GPU (Adreno, Mali, PowerVR)
  Power:   Less efficient than NPU — GPU uses more power
  Speed:   Very fast, consistent across most devices
  Models:  Best for FP16 models and vision tasks
  Risk:    More predictable behavior — GPU drivers are mature

Rule of thumb:
  Try NNAPI first → benchmark → if slower than GPU, use GPU
  Always have CPU fallback for devices where both fail
```

---

## 11. Summary

- NNAPI is Android's hardware abstraction layer between inference frameworks and chips
- It automatically partitions model graphs — supported ops go to NPU, rest falls to CPU
- Standard CNN ops (Conv2D, BatchNorm, ReLU, Pool) are almost universally NPU-accelerated
- Always enable NNAPI caching — cold start compilation takes 3–10 seconds without it
- Test on real devices — NNAPI driver quality varies enormously across vendors
- For sustained inference (video), use burst execution via `INFERENCE_PREFERENCE_SUSTAINED_SPEED`
- Benchmark NNAPI vs GPU on each target device — don't assume NNAPI is always faster

---

## ➡️ Next Lesson

**[6.2 — Enabling GPU Delegate in TFLite (2–5× Speedup) →](./02_gpu_delegate_tflite.md)**

---

*Module 6 · On-Device AI for Android Development*