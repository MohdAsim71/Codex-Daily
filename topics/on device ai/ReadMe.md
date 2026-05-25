# 🤖 On-Device AI with Python

> Run powerful AI models locally — no cloud required. From model optimization to deployment on mobile, Raspberry Pi, and NPU hardware.

![Python](https://img.shields.io/badge/Python-3.10%2B-blue?style=flat-square&logo=python)
![Edge ML](https://img.shields.io/badge/Edge-ML-green?style=flat-square)
![Level](https://img.shields.io/badge/Level-Intermediate-orange?style=flat-square)
![Modules](https://img.shields.io/badge/Modules-8-purple?style=flat-square)
![Lessons](https://img.shields.io/badge/Lessons-42-teal?style=flat-square)

---

## 📊 Course Overview

| Detail | Info |
|---|---|
| 📦 Modules | 8 |
| 📝 Lessons | 42 |
| 📓 Notebooks | 18 |
| 🛠️ Projects | 6 |
| 🎯 Level | Intermediate |
| 🐍 Language | Python 3.10+ |

---

## ✅ Prerequisites

Before starting this course, you should be comfortable with:

- 🐍 Python basics (functions, classes, file I/O)
- 🧮 Basic ML concepts (training, inference, loss)
- 🔢 NumPy & PyTorch fundamentals
- 💻 Command line / terminal usage

---

## 🛠️ Tools & Frameworks

| Tool | Purpose |
|---|---|
| **ONNX Runtime** | Cross-platform model inference |
| **PyTorch** | Model training & export |
| **TensorFlow Lite** | Mobile & embedded deployment |
| **llama.cpp** | LLMs on CPU/GPU locally |
| **Whisper.cpp** | Offline speech recognition |
| **OpenVINO** | Intel hardware acceleration |
| **Ollama** | Local LLM API server |
| **Core ML** | Apple Silicon deployment |

---

## 📚 Curriculum

---

### Module 01 — Introduction to On-Device AI

> Cloud vs. Edge · Why local matters · Hardware overview

| # | Lesson | Type |
|---|---|---|
| 1.1 | What is On-Device AI? | 📄 Theory |
| 1.2 | Cloud vs. Edge: latency, privacy, cost | 📄 Theory |
| 1.3 | Hardware overview: CPU, GPU, NPU, DSP | 📓 Notebook |
| 1.4 | Use cases: vision, speech, NLP on edge | 📓 Notebook |
| 1.5 | Setting up your dev environment | ⚙️ Setup |

---

### Module 02 — Model Formats & Runtimes

> ONNX · TFLite · Core ML · OpenVINO

| # | Lesson | Type |
|---|---|---|
| 2.1 | ONNX format deep-dive | 📓 Notebook |
| 2.2 | TensorFlow Lite: convert & run | 📓 Notebook |
| 2.3 | Core ML for Apple Silicon | 📓 Notebook |
| 2.4 | OpenVINO for Intel hardware | 📓 Notebook |
| 2.5 | 🛠️ **Project:** Run YOLO locally via ONNX | 🛠️ Project |

---

### Module 03 — Model Quantization & Pruning

> INT8 · Post-Training Quantization · QAT · Distillation

| # | Lesson | Type |
|---|---|---|
| 3.1 | Why quantization? Size vs. accuracy tradeoff | 📄 Theory |
| 3.2 | Post-Training Quantization (PTQ) with PyTorch | 📓 Notebook |
| 3.3 | Quantization-Aware Training (QAT) | 📓 Notebook |
| 3.4 | Structured & unstructured pruning | 📓 Notebook |
| 3.5 | Knowledge distillation basics | 📓 Notebook |
| 3.6 | 🛠️ **Project:** Compress a ResNet to run on Pi | 🛠️ Project |

---

### Module 04 — Running LLMs On-Device

> llama.cpp · Ollama · Phi-3 · Gemma · Qwen

| # | Lesson | Type |
|---|---|---|
| 4.1 | Small language models: Phi-3, Gemma-2, Qwen | 📄 Theory |
| 4.2 | llama.cpp: GGUF format & Python bindings | 📓 Notebook |
| 4.3 | Ollama + Python: local API server | 📓 Notebook |
| 4.4 | LM Studio & HuggingFace transformers local | 📓 Notebook |
| 4.5 | Streaming output & chat loop in Python | 📓 Notebook |
| 4.6 | 🛠️ **Project:** Offline RAG chatbot with Ollama | 🛠️ Project |

---

### Module 05 — On-Device Vision AI

> Object detection · Classification · Segmentation · OCR

| # | Lesson | Type |
|---|---|---|
| 5.1 | Real-time object detection with YOLOv8 local | 📓 Notebook |
| 5.2 | Face detection & recognition offline | 📓 Notebook |
| 5.3 | OCR on device with TrOCR + ONNX | 📓 Notebook |
| 5.4 | Image segmentation with SAM2 locally | 📓 Notebook |
| 5.5 | 🛠️ **Project:** Live camera defect detector on Pi | 🛠️ Project |

---

### Module 06 — On-Device Speech & Audio AI

> Whisper · Wake Word Detection · TTS · Audio Classification

| # | Lesson | Type |
|---|---|---|
| 6.1 | Offline STT with Whisper.cpp + Python | 📓 Notebook |
| 6.2 | Wake word detection (openWakeWord) | 📓 Notebook |
| 6.3 | Text-to-Speech: Piper TTS on CPU | 📓 Notebook |
| 6.4 | Audio classification with YAMNet locally | 📓 Notebook |
| 6.5 | 🛠️ **Project:** Fully offline voice assistant | 🛠️ Project |

---

### Module 07 — Deploying to Edge Devices

> Raspberry Pi · NVIDIA Jetson · Android · Docker

| # | Lesson | Type |
|---|---|---|
| 7.1 | Raspberry Pi 5 setup for AI inference | 📓 Notebook |
| 7.2 | NVIDIA Jetson Nano / Orin deployment | 📓 Notebook |
| 7.3 | Android with TFLite via Python scripting | 📓 Notebook |
| 7.4 | Packaging models with Docker on edge | 📓 Notebook |
| 7.5 | 🛠️ **Project:** End-to-end IoT AI pipeline | 🛠️ Project |

---

### Module 08 — Performance, Benchmarking & Privacy

> Profiling · Optimization · Federated AI · Capstone

| # | Lesson | Type |
|---|---|---|
| 8.1 | Profiling inference: latency, memory, power | 📓 Notebook |
| 8.2 | Benchmarking models across devices | 📓 Notebook |
| 8.3 | Privacy-first AI: federated & on-device | 📄 Theory |
| 8.4 | Optimizing with multi-threading & batching | 📓 Notebook |
| 8.5 | 🏁 **Capstone:** Privacy-preserving AI app | 🏁 Capstone |

---

## 📁 Repository Structure

```
On-Device-AI-with-Python/
│
├── 📁 module_01_intro/
│   ├── 01_what_is_ondevice_ai.md
│   ├── 02_cloud_vs_edge.md
│   ├── 03_hardware_overview.ipynb
│   ├── 04_use_cases.ipynb
│   └── 05_setup.md
│
├── 📁 module_02_formats_runtimes/
│   ├── 01_onnx_deepdive.ipynb
│   ├── 02_tflite_convert_run.ipynb
│   ├── 03_coreml_apple.ipynb
│   ├── 04_openvino_intel.ipynb
│   └── project_yolo_onnx/
│
├── 📁 module_03_quantization/
│   ├── 01_why_quantization.md
│   ├── 02_ptq_pytorch.ipynb
│   ├── 03_qat.ipynb
│   ├── 04_pruning.ipynb
│   ├── 05_distillation.ipynb
│   └── project_resnet_pi/
│
├── 📁 module_04_llms_ondevice/
│   ├── 01_small_language_models.md
│   ├── 02_llamacpp_python.ipynb
│   ├── 03_ollama_api.ipynb
│   ├── 04_huggingface_local.ipynb
│   ├── 05_streaming_chat.ipynb
│   └── project_offline_rag/
│
├── 📁 module_05_vision_ai/
│   ├── 01_yolov8_local.ipynb
│   ├── 02_face_detection.ipynb
│   ├── 03_ocr_trocr_onnx.ipynb
│   ├── 04_sam2_segmentation.ipynb
│   └── project_defect_detector/
│
├── 📁 module_06_speech_audio/
│   ├── 01_whisper_cpp_python.ipynb
│   ├── 02_wake_word.ipynb
│   ├── 03_piper_tts.ipynb
│   ├── 04_yamnet_audio_class.ipynb
│   └── project_voice_assistant/
│
├── 📁 module_07_edge_deployment/
│   ├── 01_raspberry_pi5_setup.ipynb
│   ├── 02_jetson_deployment.ipynb
│   ├── 03_android_tflite.ipynb
│   ├── 04_docker_edge.ipynb
│   └── project_iot_pipeline/
│
├── 📁 module_08_performance_privacy/
│   ├── 01_profiling_inference.ipynb
│   ├── 02_benchmarking.ipynb
│   ├── 03_privacy_first_ai.md
│   ├── 04_multithreading_batching.ipynb
│   └── capstone_privacy_app/
│
├── 📁 datasets/
├── 📁 models/
├── requirements.txt
└── README.md
```

---

## 🚀 Getting Started

```bash
# Clone the repository
git clone https://github.com/your-username/On-Device-AI-with-Python.git
cd On-Device-AI-with-Python

# Create virtual environment
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Launch Jupyter
jupyter notebook
```

---

## 📦 requirements.txt (Core)

```
torch>=2.2.0
onnxruntime>=1.17.0
tflite-runtime>=2.14.0
transformers>=4.40.0
ollama>=0.1.8
openai-whisper>=20231117
numpy>=1.26.0
opencv-python>=4.9.0
pillow>=10.0.0
jupyter>=1.0.0
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/new-lesson`
3. Commit your changes: `git commit -m 'Add lesson on XYZ'`
4. Push to the branch: `git push origin feature/new-lesson`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

> Made with ❤️ for the on-device AI community. Star ⭐ the repo if you find it useful!