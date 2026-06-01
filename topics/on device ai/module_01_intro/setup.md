# 05 — Setting Up Your Development Environment

> **Module 1 · Lesson 5**  
> ⚙️ Setup Guide · ⏱️ ~20 min

---

## 🎯 What You'll Set Up

By the end of this lesson your machine will have:

- ✅ Python 3.10+ with a dedicated virtual environment
- ✅ Core on-device AI libraries (PyTorch, ONNX Runtime, Transformers)
- ✅ Jupyter Lab for running course notebooks
- ✅ Hardware verification — know exactly what your machine can run
- ✅ A working first inference to confirm everything works

---

## 1. Python Version

This course requires **Python 3.10 or higher**.

```bash
# Check your Python version
python --version
# or
python3 --version
```

Expected output: `Python 3.10.x` or higher.

**If you need to install Python:**

| OS | Method |
|---|---|
| Windows | [python.org/downloads](https://www.python.org/downloads/) or `winget install Python.Python.3.12` |
| macOS | `brew install python@3.12` |
| Ubuntu/Debian | `sudo apt install python3.12 python3.12-venv` |
| Raspberry Pi | `sudo apt install python3 python3-pip python3-venv` |

---

## 2. Create a Virtual Environment

Always use a virtual environment to keep this course isolated from your system Python.

```bash
# Create the environment
python -m venv ondevice-ai-env

# Activate it
# macOS / Linux:
source ondevice-ai-env/bin/activate

# Windows (Command Prompt):
ondevice-ai-env\Scripts\activate.bat

# Windows (PowerShell):
ondevice-ai-env\Scripts\Activate.ps1
```

You should see `(ondevice-ai-env)` at the start of your terminal prompt.

---

## 3. Install Core Dependencies

### 3.1 Upgrade pip first

```bash
pip install --upgrade pip
```

### 3.2 Install PyTorch

Choose the right command for your hardware:

**CPU only (works everywhere):**
```bash
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
```

**NVIDIA GPU (CUDA 12.1):**
```bash
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121
```

**Apple Silicon (M1/M2/M3/M4 — MPS backend):**
```bash
pip install torch torchvision torchaudio
```

### 3.3 Install Course Libraries

```bash
pip install \
  onnxruntime \
  transformers \
  accelerate \
  huggingface_hub \
  numpy \
  pillow \
  opencv-python \
  psutil \
  jupyterlab \
  ipywidgets \
  matplotlib \
  tqdm \
  requests
```

### 3.4 Install Speech Libraries (Module 6 preview)

```bash
pip install openai-whisper
```

> 💡 **Raspberry Pi / ARM users:** Replace `onnxruntime` with `onnxruntime` (same name, different build is auto-selected). For TFLite: `pip install tflite-runtime`

---

## 4. Platform-Specific Notes

### 🍎 macOS (Apple Silicon)

```bash
# Verify MPS (Metal Performance Shaders) is available
python -c "import torch; print(torch.backends.mps.is_available())"
# Expected: True

# For Core ML support (Module 2)
pip install coremltools
```

### 🐧 Linux / Ubuntu

```bash
# System dependencies for OpenCV and audio
sudo apt-get install -y libglib2.0-0 libsm6 libxext6 libxrender-dev
sudo apt-get install -y ffmpeg libavcodec-dev  # for Whisper audio
```

### 🪟 Windows

```bash
# Visual C++ Redistributable required for some packages
# Download from: https://aka.ms/vs/17/release/vc_redist.x64.exe

# FFmpeg for Whisper (install via winget)
winget install Gyan.FFmpeg
```

### 🍓 Raspberry Pi 5

```bash
# System packages
sudo apt-get update
sudo apt-get install -y python3-pip python3-venv python3-numpy
sudo apt-get install -y libatlas-base-dev libopenblas-dev

# Lightweight ONNX Runtime for ARM
pip install onnxruntime  # ARM64 build auto-selected

# TFLite Runtime
pip install tflite-runtime
```

---

## 5. Verify Your Installation

Run this script to confirm everything is working:

```python
# save as: verify_setup.py
import sys
import importlib

print("=" * 55)
print("ON-DEVICE AI — ENVIRONMENT VERIFICATION")
print("=" * 55)

# Python version
print(f"\n✅ Python {sys.version.split()[0]}")

# Check packages
packages = {
    "torch":           "PyTorch",
    "onnxruntime":     "ONNX Runtime",
    "transformers":    "HuggingFace Transformers",
    "numpy":           "NumPy",
    "PIL":             "Pillow",
    "cv2":             "OpenCV",
    "psutil":          "psutil",
}

all_ok = True
for module, name in packages.items():
    try:
        pkg = importlib.import_module(module)
        version = getattr(pkg, "__version__", "installed")
        print(f"✅ {name:<30} v{version}")
    except ImportError:
        print(f"❌ {name:<30} NOT INSTALLED")
        all_ok = False

# Hardware
print("\n" + "-" * 55)
print("HARDWARE")
print("-" * 55)

import torch
print(f"CUDA available  : {torch.cuda.is_available()}")
print(f"MPS available   : {torch.backends.mps.is_available()}")

import onnxruntime as ort
print(f"ONNX providers  : {ort.get_available_providers()}")

import psutil
vm = psutil.virtual_memory()
print(f"RAM available   : {vm.available / 1e9:.1f} GB / {vm.total / 1e9:.1f} GB")

print("\n" + "=" * 55)
if all_ok:
    print("🎉 All packages installed! You are ready to go.")
else:
    print("⚠️  Some packages are missing. Install them above.")
print("=" * 55)
```

```bash
# Run the verification
python verify_setup.py
```

---

## 6. Quick First Inference Test

If the verification above passes, run this to confirm end-to-end inference works:

```python
# save as: first_inference.py
import onnxruntime as ort
import numpy as np

print("Running first on-device inference...")

# Create a tiny dummy model input
dummy_input = np.random.randn(1, 3, 224, 224).astype(np.float32)

# List available execution providers
providers = ort.get_available_providers()
print(f"Using: {providers[0]}")

# The real model test comes in Lesson 4
print(f"\n✅ ONNX Runtime working on: {providers[0]}")
print("You're ready for on-device AI!")
```

---

## 7. Launch Jupyter Lab

All notebooks in this course run in Jupyter Lab:

```bash
# Make sure your virtual environment is active, then:
jupyter lab
```

This will open `http://localhost:8888` in your browser. Navigate to the `module_01_intro/` folder and open any `.ipynb` file.

---

## 8. Folder Structure for This Course

Set up your local folder like this:

```bash
mkdir On-Device-AI-with-Python
cd On-Device-AI-with-Python

mkdir -p module_01_intro
mkdir -p module_02_formats_runtimes
mkdir -p module_03_quantization
mkdir -p module_04_llms_ondevice
mkdir -p module_05_vision_ai
mkdir -p module_06_speech_audio
mkdir -p module_07_edge_deployment
mkdir -p module_08_performance_privacy
mkdir -p datasets models
```

---

## 9. Troubleshooting

| Problem | Solution |
|---|---|
| `pip install` fails with permissions | Use `pip install --user` or activate venv |
| `torch` not found after install | Make sure venv is activated |
| CUDA not detected | Install NVIDIA drivers + CUDA Toolkit 12.x |
| MPS not working | Update to macOS 12.3+ and PyTorch 2.0+ |
| Jupyter can't find kernel | `pip install ipykernel` then `python -m ipykernel install --user` |
| `cv2` import error on Linux | `sudo apt-get install libgl1-mesa-glx` |
| Whisper `ffmpeg` error | Install FFmpeg: `sudo apt install ffmpeg` |

---

## ✅ Setup Complete!

You now have everything needed for this course. Here's your environment summary:

```
✅ Python 3.10+         → Language
✅ PyTorch              → Training + model export
✅ ONNX Runtime         → Cross-platform local inference
✅ Transformers         → HuggingFace model hub
✅ OpenCV + Pillow      → Image processing
✅ Whisper              → Speech recognition
✅ Jupyter Lab          → Interactive notebooks
```

---

## 🎉 Module 1 Complete!

You have completed **Module 1 — Introduction to On-Device AI**.

### What you learned:
- What on-device AI is and why it matters
- How cloud AI compares to edge AI across latency, privacy, cost, and reliability
- The four hardware types: CPU, GPU, NPU, DSP
- Real-world use cases for vision, speech, and NLP on the edge
- How to set up a complete on-device AI development environment

---

## ➡️ Next Module

**[Module 02 — Model Formats & Runtimes →](../module_02_formats_runtimes/)**

---

*Module 1 · On-Device AI with Python*