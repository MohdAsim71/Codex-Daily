# 1.3 — How Android Accesses Hardware: NNAPI, GPU Delegate, Hexagon DSP

> **Module 1 · Lesson 3**  
> 📄 Theory · ⏱️ ~25 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Explain what NNAPI is and how it routes AI work to hardware
- Distinguish between NNAPI, GPU Delegate, and Hexagon DSP access
- Know which acceleration path to try first on different devices
- Understand fallback behavior when hardware acceleration fails

---

## 1. The Hardware Access Problem

Android runs on thousands of device models — from a $80 budget phone to a $1200 flagship. Each has completely different AI hardware:

```
Pixel 8 Pro      → Google Tensor G3 (custom TPU + Cortex-X3)
Samsung S24      → Snapdragon 8 Gen 3 (Hexagon NPU, 98 TOPS)
OnePlus 12       → Snapdragon 8 Gen 3 (same)
Redmi Note 13    → Snapdragon 685 (no dedicated NPU)
Nokia G42        → Snapdragon 480+ (basic NPU)
Raspberry Pi 5   → ARM Cortex-A76 (CPU only)
```

As an app developer, you can't write separate code for each chip. Android solves this with a hardware abstraction stack.

---

## 2. The Three Ways TFLite Reaches Hardware

```
Your TFLite Interpreter
        │
        ├──── Path A: CPU (always available, slowest)
        │     └─ Multi-threaded ARM NEON/SVE instructions
        │
        ├──── Path B: GPU Delegate (OpenGL ES / Vulkan)
        │     └─ Runs on Adreno / Mali / PowerVR GPU
        │
        └──── Path C: NNAPI Delegate (recommended for NPU)
              └─ Android routes to NPU/DSP via vendor drivers
```

You can mix paths — TFLite will use GPU for supported ops and CPU for everything else.

---

## 3. Path A — CPU Inference (Default)

If you don't configure anything, TFLite runs on CPU using ARM's optimized math libraries.

**How it's optimized:**
- Uses ARM NEON (SIMD) — processes 4 floats in one instruction
- Uses SVE/SVE2 on newer ARM chips — processes 8–16 floats at once
- XNNPack — Google's optimized CPU inference engine (auto-enabled)
- Multi-threading — splits work across CPU cores

```kotlin
// Default — CPU with XNNPack + 4 threads
val options = Interpreter.Options().apply {
    setNumThreads(4)
    setUseXNNPACK(true)   // enabled by default in TFLite 2.x
}
val interpreter = Interpreter(modelBuffer, options)
```

**When CPU is fine:**
- Small models (< 5 MB)
- Non-latency-critical tasks (run once per tap, not per frame)
- Quantized INT8 models (XNNPack makes INT8 very fast on CPU)
- Battery-constrained scenarios (NPU/GPU drain more power for burst tasks)

**CPU performance on typical devices:**

| Device | CPU | YOLOv8n Latency (CPU) |
|---|---|---|
| Pixel 8 Pro | Cortex-X3 | ~45ms |
| Samsung S24 | Cortex-X4 | ~38ms |
| Redmi Note 13 | Kryo 460 | ~180ms |
| Raspberry Pi 5 | Cortex-A76 | ~220ms |

---

## 4. Path B — GPU Delegate

The GPU Delegate lets TFLite run model operations on the phone's GPU (Adreno, Mali, PowerVR) using OpenGL ES compute shaders or Vulkan.

**Typical speedup: 3–8× over CPU for vision models.**

```kotlin
// Enable GPU Delegate
val gpuDelegate = GpuDelegate(
    GpuDelegate.Options().apply {
        setPrecisionLossAllowed(true)   // use FP16 on GPU (faster, tiny accuracy loss)
        setQuantizedModelsAllowed(true) // allow INT8 models on GPU
        setInferencePreference(
            GpuDelegate.Options.INFERENCE_PREFERENCE_FAST_SINGLE_ANSWER
            // or: INFERENCE_PREFERENCE_SUSTAINED_SPEED (for video)
        )
    }
)

val options = Interpreter.Options().apply {
    addDelegate(gpuDelegate)
}
val interpreter = Interpreter(modelBuffer, options)

// IMPORTANT: Always close the delegate when done
override fun onDestroy() {
    super.onDestroy()
    interpreter.close()
    gpuDelegate.close()   // ← releases GPU memory
}
```

**How GPU Delegate works internally:**

```
TFLite Interpreter
        │
        ▼
GPU Delegate
  ├─ Checks each op: "Is this op supported on GPU?"
  │     Supported: Conv2D, DepthwiseConv, MaxPool, etc.
  │     Not supported: Some custom ops, certain LSTM variants
  │
  ├─ Uploads input tensor to GPU memory (one-time cost)
  ├─ Compiles OpenGL ES compute shaders (first run only, cached)
  ├─ Executes all supported ops on GPU in parallel
  └─ Downloads output tensor from GPU memory
```

**GPU Delegate limitations:**
- First inference is slow (~200–500ms) — shader compilation. After that: fast.
- Some ops fall back to CPU (transparent, but slows things down)
- FP32 models run fastest; INT8 support varies by GPU

**Inference preference — important choice:**

```kotlin
// For apps that run inference once (photo classification):
INFERENCE_PREFERENCE_FAST_SINGLE_ANSWER
// GPU boosts to max frequency for single fast result

// For real-time video (30 fps object detection):
INFERENCE_PREFERENCE_SUSTAINED_SPEED
// GPU runs at stable frequency — avoids thermal throttling
```

---

## 5. Path C — NNAPI Delegate (NPU Access)

NNAPI (Neural Networks API) is Android's official path to the NPU. Available since Android 8.1 (API 27).

```kotlin
// Enable NNAPI — routes to NPU/DSP on supported devices
val nnApiDelegate = NnApiDelegate(
    NnApiDelegate.Options().apply {
        setExecutionPreference(
            NnApiDelegate.Options.EXECUTION_PREFERENCE_FAST_SINGLE_ANSWER
            // or: EXECUTION_PREFERENCE_SUSTAINED_SPEED
            // or: EXECUTION_PREFERENCE_LOW_POWER
        )
        setAllowFp16(true)           // allow FP16 precision (faster)
        setUseNnapiCpu(false)        // don't use NNAPI's CPU fallback
        setCacheDir(cacheDir.path)   // cache compiled model (faster restarts)
        setModelToken("my_model_v1") // cache key
    }
)

val options = Interpreter.Options().apply {
    addDelegate(nnApiDelegate)
}
```

**What happens when NNAPI receives a model:**

```
NNAPI receives model graph
        │
        ▼
NNAPI queries each available accelerator driver:
  ┌─ "Qualcomm Hexagon driver, can you run Conv2D?"  YES
  ├─ "Qualcomm Hexagon driver, can you run LSTM?"    NO
  └─ "CPU fallback for unsupported ops"
        │
        ▼
NNAPI partitions the model:
  Supported ops   → NPU (Hexagon)  [fast, power-efficient]
  Unsupported ops → CPU fallback   [slower]
```

**NNAPI performance by device:**

| Device | NPU | NNAPI YOLOv8n Latency |
|---|---|---|
| Pixel 8 Pro | Google TPU | ~10ms |
| Samsung S24 Ultra | Hexagon NPU | ~8ms |
| OnePlus 12 | Hexagon NPU | ~9ms |
| Redmi Note 13 | Basic NPU | ~35ms |
| Samsung A54 | Exynos NPU | ~28ms |

> ⚠️ **NNAPI warning:** On some budget devices, NNAPI is technically available but the NPU driver is poor. Always benchmark — sometimes CPU or GPU is faster than NNAPI on those devices.

---

## 6. Qualcomm Hexagon DSP — Direct Access

Qualcomm devices (most flagship Androids) have a **Hexagon DSP** — a specialized processor for signal processing and AI that is even more power-efficient than the GPU.

You can access it two ways:

**Via NNAPI (easier, recommended):**
```kotlin
// NNAPI automatically routes to Hexagon on Qualcomm devices
// Just enable NNAPI as shown above — Hexagon is used automatically
```

**Via QNN SDK (advanced, maximum performance):**
```kotlin
// Requires Qualcomm Neural Networks SDK
// Only for Qualcomm-specific apps
// Gives direct access to Hexagon without NNAPI overhead
// Used by high-performance camera and video apps
```

**Hexagon vs GPU — when to prefer Hexagon:**
- Always-on tasks (wake word, sensor fusion) — Hexagon is 5–10× more battery-efficient
- Sustained video processing — Hexagon doesn't thermal throttle as fast as GPU
- INT4/INT8 quantized models — Hexagon has dedicated INT8 hardware units

---

## 7. Choosing the Right Acceleration Path

**Use this priority order on most Android apps:**

```
Step 1: Try NNAPI Delegate
        │
        ├─ Works well and is fast?  ✅ USE IT
        └─ Slow or crashes?
              │
              ▼
Step 2: Try GPU Delegate
        │
        ├─ Works well?  ✅ USE IT
        └─ Not supported?
              │
              ▼
Step 3: Fall back to CPU
        └─ Always works ✅
```

**Implement automatic fallback in Kotlin:**

```kotlin
fun createInterpreter(context: Context, modelPath: String): Interpreter {
    val modelBuffer = FileUtil.loadMappedFile(context, modelPath)

    // Try NNAPI first
    try {
        val nnApiDelegate = NnApiDelegate()
        val options = Interpreter.Options().addDelegate(nnApiDelegate)
        val interpreter = Interpreter(modelBuffer, options)
        Log.d("AI", "Using NNAPI acceleration")
        return interpreter
    } catch (e: Exception) {
        Log.w("AI", "NNAPI failed: ${e.message}")
    }

    // Try GPU next
    try {
        val gpuDelegate = GpuDelegate()
        val options = Interpreter.Options().addDelegate(gpuDelegate)
        val interpreter = Interpreter(modelBuffer, options)
        Log.d("AI", "Using GPU acceleration")
        return interpreter
    } catch (e: Exception) {
        Log.w("AI", "GPU delegate failed: ${e.message}")
    }

    // Fallback to CPU
    Log.d("AI", "Using CPU inference")
    return Interpreter(modelBuffer, Interpreter.Options().setNumThreads(4))
}
```

---

## 8. Benchmarking All Three Paths

Always benchmark on real devices. Use TFLite Benchmark Tool:

```bash
# Run on connected Android device via adb
adb push yolov8n.tflite /data/local/tmp/
adb shell

# CPU benchmark
/data/local/tmp/benchmark_model \
  --graph=/data/local/tmp/yolov8n.tflite \
  --num_threads=4

# GPU benchmark
/data/local/tmp/benchmark_model \
  --graph=/data/local/tmp/yolov8n.tflite \
  --use_gpu=true

# NNAPI benchmark
/data/local/tmp/benchmark_model \
  --graph=/data/local/tmp/yolov8n.tflite \
  --use_nnapi=true
```

**Example output (Snapdragon 8 Gen 3):**

```
CPU  (4 threads):  avg=118.2ms  min=112.1ms  max=131.4ms
GPU  (Delegate) :  avg=17.8ms   min=16.2ms   max=21.3ms   ← 6.6× faster
NNAPI (Hexagon) :  avg=9.1ms    min=8.4ms    max=10.2ms   ← 13× faster
```

---

## 9. Summary

| Path | Hardware | Best For | Typical Speedup |
|---|---|---|---|
| **CPU** | ARM cores | Small models, always available | 1× (baseline) |
| **GPU Delegate** | Adreno / Mali | Vision models, real-time video | 3–8× |
| **NNAPI** | NPU / DSP | Sustained AI, best efficiency | 5–15× |
| **Hexagon (QNN)** | Qualcomm NPU | Qualcomm-only, max performance | 10–20× |

- Always implement **fallback**: NNAPI → GPU → CPU
- **Benchmark on real devices** — NNAPI is sometimes slower on budget chips
- Use `SUSTAINED_SPEED` preference for real-time video, `FAST_SINGLE_ANSWER` for one-shot inference
- Cache compiled NNAPI models — first load is slow, cached loads are instant

---

## ➡️ Next Lesson

**[1.4 — Setting Up Android Studio for AI Development →](./04_android_studio_setup.md)**

---

*Module 1 · On-Device AI for Android Development*