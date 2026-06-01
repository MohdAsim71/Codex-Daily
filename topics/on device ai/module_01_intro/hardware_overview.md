# 03 — Hardware Overview: CPU, GPU, NPU, DSP

> **Module 1 · Lesson 3**  
> 📓 Notebook · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this notebook you will be able to:
- Identify and explain the role of CPU, GPU, NPU, and DSP in AI inference
- Detect what hardware is available on your machine using Python
- Benchmark a simple operation across CPU and GPU
- Know which hardware to target for different AI workloads
---
## 1. The Four Processors in Modern Devices

Modern devices contain multiple specialized processors. Each is optimized for different types of work:

```
┌─────────────────────────────────────────────────────┐
│                  System-on-Chip (SoC)               │
│                                                     │
│   ┌───────┐   ┌───────┐   ┌───────┐   ┌───────┐   │
│   │  CPU  │   │  GPU  │   │  NPU  │   │  DSP  │   │
│   │       │   │       │   │       │   │       │   │
│   │General│   │Parallel│  │ AI    │   │Signal │   │
│   │Purpose│   │Graphics│  │ Math  │   │Process│   │
│   └───────┘   └───────┘   └───────┘   └───────┘   │
│                                                     │
│              Shared Memory (RAM)                    │
└─────────────────────────────────────────────────────┘
```

| Processor | Full Name | Strength | AI Use |
|---|---|---|---|
| **CPU** | Central Processing Unit | Sequential logic, control flow | Small models, preprocessing |
| **GPU** | Graphics Processing Unit | Parallel matrix math | Medium-large models, training |
| **NPU** | Neural Processing Unit | Low-power tensor ops | Always-on AI (wake word, camera) |
| **DSP** | Digital Signal Processor | Streaming signal math | Audio, sensor fusion |
---
## 2. CPU — Central Processing Unit

The CPU is the "general manager" of the computer.

**Characteristics:**
- Few cores (4–32 on most devices), but each is very powerful
- Optimized for sequential, branching tasks
- Good at running complex logic, loops, conditionals
- Modern CPUs have SIMD/AVX extensions that speed up matrix math

**For AI inference:**
- Works fine for small models (< 1B parameters)
- llama.cpp is specifically optimized for CPU inference
- Intel CPUs have AVX-512 for faster quantized math
- Apple M-series CPUs have very fast unified memory access
# ── Inspect your CPU ──────────────────────────────────
import platform
import os
import multiprocessing

print("=" * 50)
print("CPU INFORMATION")
print("=" * 50)
print(f"Processor     : {platform.processor()}")
print(f"Architecture  : {platform.machine()}")
print(f"System        : {platform.system()} {platform.release()}")
print(f"Physical cores: {os.cpu_count()}")
print(f"Logical cores : {multiprocessing.cpu_count()}")

# Check for AVX support (Linux)
try:
with open("/proc/cpuinfo") as f:
cpuinfo = f.read()
flags = [line for line in cpuinfo.split("\n") if line.startswith("flags")]
if flags:
flag_list = flags[0].split(":"[0:] if ":" in flags[0] else flags)[1].split()
avx_support = "avx2" in flag_list
avx512_support = "avx512f" in flag_list
print(f"AVX2 support  : {avx_support}")
print(f"AVX-512       : {avx512_support}")
except:
print("(CPU flags: run on Linux for AVX details)")
---
## 3. GPU — Graphics Processing Unit

The GPU was originally designed for rendering graphics, but it turns out matrix multiplication — the core operation of neural networks — is exactly what GPUs are great at.

**Characteristics:**
- Thousands of small cores (NVIDIA RTX 4090 has 16,384 CUDA cores)
- Optimized for massive parallelism — doing thousands of things at once
- Separate VRAM (video memory) — model must fit entirely in VRAM
- High power consumption (100–400W for desktop GPUs)

**Why GPUs dominate AI:**
```
Neural Network Layer = Matrix Multiplication

CPU approach:  Calculate A×B sequentially → slow
GPU approach:  Calculate ALL elements of A×B in parallel → fast

A 1000×1000 matrix multiply:
  CPU (8 cores):  ~50ms
  GPU (4096 cores): ~0.5ms  → 100× faster
```
# ── Inspect your GPU ──────────────────────────────────
import subprocess

# Check NVIDIA GPU
try:
result = subprocess.run(
["nvidia-smi", "--query-gpu=name,memory.total,driver_version,compute_cap",
"--format=csv,noheader"],
capture_output=True, text=True, timeout=5
)
if result.returncode == 0:
print("NVIDIA GPU DETECTED")
for line in result.stdout.strip().split("\n"):
parts = [p.strip() for p in line.split(",")]
print(f"  Name         : {parts[0]}")
print(f"  VRAM         : {parts[1]}")
print(f"  Driver       : {parts[2]}")
print(f"  Compute Cap  : {parts[3]}")
else:
print("No NVIDIA GPU found.")
except FileNotFoundError:
print("nvidia-smi not found — no NVIDIA GPU or drivers not installed.")

# Check PyTorch GPU availability
try:
import torch
print("\nPyTorch GPU Info:")
print(f"  CUDA available : {torch.cuda.is_available()}")
if torch.cuda.is_available():
print(f"  GPU name       : {torch.cuda.get_device_name(0)}")
mem = torch.cuda.get_device_properties(0).total_memory
print(f"  GPU memory     : {mem / 1e9:.1f} GB")
print(f"  MPS (Apple)    : {torch.backends.mps.is_available()}")
except ImportError:
print("PyTorch not installed — run: pip install torch")
---
## 4. NPU — Neural Processing Unit

The NPU is a chip designed **specifically** for AI workloads. It is the key to efficient on-device AI.

**Characteristics:**
- Purpose-built for tensor operations (the math behind neural networks)
- Extremely power-efficient — 10–50× more efficient than GPU per operation
- Always-on capability — can run 24/7 on battery (e.g., wake word)
- Typically supports only quantized models (INT8, INT4)

**Where NPUs live:**

| Device | NPU | TOPS (Trillion Ops/sec) |
|---|---|---|
| Apple M4 | Apple Neural Engine | 38 TOPS |
| Snapdragon X Elite | Hexagon NPU | 45 TOPS |
| Snapdragon 8 Gen 3 (phone) | Hexagon NPU | 98 TOPS |
| Intel Core Ultra | Intel NPU | 11 TOPS |
| Google Pixel (Tensor G4) | Google TPU | ~50 TOPS |
| NVIDIA Jetson Orin | Deep Learning Accelerator | 275 TOPS |

> 💡 **TOPS** = Trillion Operations Per Second — the standard measure of NPU throughput
# ── Check NPU / Accelerator Availability ─────────────
import platform

print("=" * 50)
print("ACCELERATOR AVAILABILITY CHECK")
print("=" * 50)

# Apple Neural Engine via MPS (Metal Performance Shaders)
try:
import torch
mps = torch.backends.mps.is_available()
print(f"Apple MPS (Neural Engine proxy) : {'✅ Available' if mps else '❌ Not available'}")
except ImportError:
pass

# ONNX Runtime execution providers (shows what accelerators ONNX can use)
try:
import onnxruntime as ort
providers = ort.get_available_providers()
print(f"\nONNX Runtime Execution Providers:")
provider_notes = {
"CUDAExecutionProvider":     "NVIDIA GPU",
"CoreMLExecutionProvider":   "Apple Neural Engine / GPU",
"DnnlExecutionProvider":     "Intel oneDNN (CPU optimized)",
"OpenVINOExecutionProvider": "Intel NPU / GPU via OpenVINO",
"TensorrtExecutionProvider": "NVIDIA TensorRT",
"QNNExecutionProvider":      "Qualcomm NPU",
"CPUExecutionProvider":      "CPU (always available)",
}
for p in providers:
note = provider_notes.get(p, "")
print(f"  ✅ {p:<40} {note}")
except ImportError:
print("onnxruntime not installed — run: pip install onnxruntime")
---
## 5. DSP — Digital Signal Processor

DSPs are specialized for processing continuous data streams — audio, video, sensor data.

**Characteristics:**
- Very low power, always-on
- Optimized for streaming math (FFT, filtering, convolution)
- Used for audio processing, sensor fusion, always-on detection

**Common DSP use cases in AI:**
- 🎤 Pre-processing audio before sending to speech model
- 🏃 Accelerometer + gyroscope fusion for activity recognition
- 📷 Image signal processing before vision model
- 🔊 Noise cancellation in AirPods / earbuds

> On phones, the Qualcomm Hexagon is both a DSP and NPU — it handles both signal processing and AI inference in one chip.
---
## 6. Benchmarking: CPU vs GPU Matrix Multiply

Let's directly measure the performance difference between CPU and GPU for the same operation that powers neural networks — matrix multiplication.
# ── CPU vs GPU Benchmark ──────────────────────────────
import torch
import time

def benchmark_matmul(device, size=2048, warmup=3, runs=10):
"""Benchmark matrix multiplication on a given device."""
a = torch.randn(size, size, device=device)
b = torch.randn(size, size, device=device)

    # Warmup
    for _ in range(warmup):
        _ = torch.matmul(a, b)
    if device != "cpu":
        torch.cuda.synchronize()

    # Timed runs
    start = time.perf_counter()
    for _ in range(runs):
        _ = torch.matmul(a, b)
    if device != "cpu":
        torch.cuda.synchronize()
    elapsed = (time.perf_counter() - start) / runs * 1000  # ms

    return elapsed

print(f"Matrix size: 2048 × 2048")
print(f"Operation : torch.matmul (FP32)")
print("-" * 40)

# CPU
cpu_time = benchmark_matmul("cpu")
print(f"CPU time  : {cpu_time:.2f} ms")

# GPU (if available)
if torch.cuda.is_available():
gpu_time = benchmark_matmul("cuda")
print(f"GPU time  : {gpu_time:.2f} ms")
print(f"Speedup   : {cpu_time / gpu_time:.1f}×")
elif torch.backends.mps.is_available():
mps_time = benchmark_matmul("mps")
print(f"MPS time  : {mps_time:.2f} ms")
print(f"Speedup   : {cpu_time / mps_time:.1f}×")
else:
print("GPU/MPS   : Not available on this machine")
print("(This is normal — on-device AI often uses CPU or NPU)")
---
## 7. Memory — The Hidden Bottleneck

Compute power is only half the story. **Memory** is often the real bottleneck for on-device AI.
# ── Memory Analysis ───────────────────────────────────
import psutil

vm = psutil.virtual_memory()
print("=" * 50)
print("SYSTEM MEMORY")
print("=" * 50)
print(f"Total RAM     : {vm.total / 1e9:.1f} GB")
print(f"Available     : {vm.available / 1e9:.1f} GB")
print(f"Used          : {vm.used / 1e9:.1f} GB")
print(f"Usage         : {vm.percent}%")

# Estimate what models can fit
available_gb = vm.available / 1e9
print("\n" + "=" * 50)
print("MODEL FIT ESTIMATE (FP16 weights)")
print("=" * 50)
models = [
("Phi-3 Mini 3.8B",    3.8, 2.0),
("Gemma-2 2B",         2.0, 1.5),
("Llama 3.2 3B",       3.0, 2.0),
("Mistral 7B",         7.0, 4.5),
("Llama 3.1 8B",       8.0, 5.0),
("Llama 3.1 70B",      70.0, 40.0),
]
for name, params_b, ram_gb in models:
fits = "✅" if ram_gb < available_gb else "❌"
print(f"  {fits} {name:<22} ~{ram_gb:.1f} GB RAM needed")
---
## 8. Choosing the Right Hardware for Your Workload

```
What are you running?
        │
        ├─ Always-on detection (wake word, camera filter)
        │     → NPU  (low power, real-time)
        │
        ├─ Real-time vision (object detection, face ID)
        │     → GPU / NPU
        │
        ├─ Small LLM (1B–7B) for chat / summarization
        │     → CPU (llama.cpp) or GPU (if available)
        │
        ├─ Audio processing (STT, noise cancel)
        │     → DSP → CPU → NPU
        │
        └─ Large LLM (13B+)
              → Cloud or Jetson with large GPU
```

| Workload | Best Hardware | Python Framework |
|---|---|---|
| Small models, CPU inference | CPU | ONNX Runtime, llama.cpp |
| Vision models, fast inference | NVIDIA GPU | PyTorch + CUDA |
| Apple devices | Apple Neural Engine | Core ML, MPS |
| Intel laptops | Intel NPU | OpenVINO |
| Qualcomm phones/PCs | Hexagon NPU | ONNX QNN Provider |
| Raspberry Pi | CPU (ARM) | TFLite, ONNX Runtime |
---
## ✅ Summary

- **CPU** — general purpose, great for small models and control logic
- **GPU** — massively parallel, ideal for medium/large model inference and training
- **NPU** — purpose-built for AI, power-efficient, needed for always-on tasks
- **DSP** — for streaming signal data (audio, sensors) before AI processing
- Memory is often the limiting factor — model must fit in RAM/VRAM
- ONNX Runtime automatically selects the best available hardware via execution providers

---

## ➡️ Next Lesson

**[04 — Use Cases: Vision, Speech, NLP on Edge →](./04_use_cases.ipynb)**

---
*Module 1 · On-Device AI with Python*