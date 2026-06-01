# 04 — Use Cases: Vision, Speech & NLP on the Edge

> **Module 1 · Lesson 4**  
> 📓 Notebook · ⏱️ ~30 min

---

## 🎯 Learning Objectives

By the end of this lesson you will:
- See working examples of on-device Vision, Speech, and NLP inference
- Run your first local model with ONNX Runtime
- Understand which Python libraries power each use case
- Map real-world applications to the on-device AI stack

---

## 0. Install Required Libraries

```bash
pip install onnxruntime numpy pillow requests transformers --quiet
```

---

## 1. The Three Pillars of On-Device AI

Most real-world on-device AI falls into three domains:

```
┌────────────────────┬────────────────────┬────────────────────┐
│   VISION AI        │   SPEECH/AUDIO AI  │    NLP / TEXT AI   │
│                    │                    │                    │
│ • Object detection │ • Speech-to-text   │ • On-device LLMs   │
│ • Face recognition │ • Wake word        │ • Text classif.    │
│ • OCR              │ • Speaker ID       │ • Summarization    │
│ • Pose estimation  │ • Noise cancel     │ • Sentiment        │
│ • Segmentation     │ • Text-to-speech   │ • Named entities   │
│                    │                    │                    │
│ Models: YOLO,      │ Models: Whisper,   │ Models: Phi-3,     │
│ ResNet, SAM        │ Wav2Vec, SpeechT5  │ Gemma, TinyBERT    │
└────────────────────┴────────────────────┴────────────────────┘
```

---

## 2. Vision AI — Image Classification with ONNX

Image classification is the "Hello World" of computer vision. We'll use **MobileNetV2** — a model specifically designed for on-device inference.

**Why MobileNetV2?**
- Only 14 MB in size (vs ResNet-50 at 98 MB)
- Runs in <10ms on a modern CPU
- Trained on ImageNet — recognizes 1000 object categories
- Designed for mobile and embedded devices

### Step 1 — Download the Model

```python
# ── Download MobileNetV2 ONNX model ───────────────────
import urllib.request
import os

model_url = "https://github.com/onnx/models/raw/main/validated/vision/classification/mobilenet/model/mobilenetv2-12.onnx"
model_path = "mobilenetv2.onnx"

if not os.path.exists(model_path):
    print("Downloading MobileNetV2 ONNX model (~14 MB)...")
    urllib.request.urlretrieve(model_url, model_path)
    print(f"✅ Downloaded: {model_path}")
else:
    print(f"✅ Model already exists: {model_path}")

size_mb = os.path.getsize(model_path) / 1e6
print(f"   Model size : {size_mb:.1f} MB")
```

### Step 2 — Run On-Device Inference

```python
# ── Run On-Device Image Classification ────────────────
import onnxruntime as ort
import numpy as np
from PIL import Image
import urllib.request
import time

# Download a test image
img_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Cute_dog.jpg/320px-Cute_dog.jpg"
img_path = "test_dog.jpg"
if not os.path.exists(img_path):
    urllib.request.urlretrieve(img_url, img_path)

# Load and preprocess image
def preprocess_image(image_path, input_size=224):
    img = Image.open(image_path).convert("RGB")
    img = img.resize((input_size, input_size))
    arr = np.array(img).astype(np.float32) / 255.0
    # ImageNet normalization
    mean = np.array([0.485, 0.456, 0.406])
    std  = np.array([0.229, 0.224, 0.225])
    arr = (arr - mean) / std
    # NCHW format
    arr = arr.transpose(2, 0, 1)[np.newaxis, :, :, :]
    return arr.astype(np.float32)

# Load model — ONNX Runtime auto-selects best hardware
session = ort.InferenceSession(model_path)
print(f"Execution provider: {session.get_providers()[0]}")

# Run inference
input_data = preprocess_image(img_path)
input_name = session.get_inputs()[0].name

start = time.perf_counter()
outputs = session.run(None, {input_name: input_data})
latency = (time.perf_counter() - start) * 1000

# Get top-5 predictions
logits = outputs[0][0]
probs = np.exp(logits) / np.sum(np.exp(logits))
top5_idx = np.argsort(probs)[::-1][:5]

# ImageNet labels (abbreviated)
imagenet_labels = {
    207: "golden retriever", 208: "Labrador retriever",
    209: "Chesapeake Bay retriever", 263: "Pembroke, Pembroke Welsh corgi",
    265: "toy poodle", 266: "miniature poodle"
}

print(f"\n{'='*45}")
print("ON-DEVICE CLASSIFICATION RESULTS")
print(f"{'='*45}")
print(f"Inference time : {latency:.1f} ms  ← on your CPU!")
print(f"\nTop-5 predictions:")
for i, idx in enumerate(top5_idx):
    label = imagenet_labels.get(idx, f"class_{idx}")
    print(f"  #{i+1}  {label:<30} {probs[idx]*100:.2f}%")
```

**Expected Output:**
```
Execution provider: CPUExecutionProvider
=============================================
ON-DEVICE CLASSIFICATION RESULTS
=============================================
Inference time : 8.3 ms  ← on your CPU!

Top-5 predictions:
  #1  golden retriever               82.14%
  #2  Labrador retriever             11.23%
  #3  Chesapeake Bay retriever        4.10%
  ...
```

---

## 3. NLP AI — Sentiment Analysis On-Device

NLP (Natural Language Processing) on the edge means running text understanding models locally — no API calls, no data leaving your device.

We'll use **DistilBERT** — a compressed version of BERT that's 40% smaller and 60% faster, designed for on-device inference.

```python
# ── On-Device Sentiment Analysis ──────────────────────
from transformers import pipeline
import time

print("Loading DistilBERT sentiment model (local)...")
print("(First run downloads ~68 MB — cached after that)\n")

# This runs ENTIRELY locally — no API call
classifier = pipeline(
    "sentiment-analysis",
    model="distilbert-base-uncased-finetuned-sst-2-english",
    device=-1  # -1 = CPU, 0 = first GPU
)

test_texts = [
    "The on-device AI revolution is amazing and empowering!",
    "I hate when my data is sent to the cloud without my permission.",
    "Running AI locally is surprisingly fast on modern hardware.",
    "The battery drains too fast when running large models.",
    "Privacy-first AI is the future of personal computing.",
]

print(f"{'='*60}")
print("ON-DEVICE SENTIMENT ANALYSIS")
print(f"{'='*60}")

for text in test_texts:
    start = time.perf_counter()
    result = classifier(text)[0]
    latency = (time.perf_counter() - start) * 1000

    emoji = "😊" if result["label"] == "POSITIVE" else "😞"
    print(f"\n{emoji} {result['label']} ({result['score']*100:.1f}%) — {latency:.0f}ms")
    print(f"   Text: {text[:60]}{'...' if len(text) > 60 else ''}")
```

**Expected Output:**
```
😊 POSITIVE (99.2%) — 43ms
   Text: The on-device AI revolution is amazing and empowering!

😞 NEGATIVE (97.8%) — 38ms
   Text: I hate when my data is sent to the cloud without my permissio...

😊 POSITIVE (98.5%) — 37ms
   Text: Running AI locally is surprisingly fast on modern hardware.
```

> 💡 **Key point:** No API key, no internet, no data sent anywhere. DistilBERT runs 100% locally on your CPU.

---

## 4. Speech AI — Transcription with Whisper

OpenAI's Whisper model runs fully locally with the `openai-whisper` library.  
No API key. No internet. Audio never leaves your device.

```python
# ── On-Device Speech Transcription ────────────────────
import whisper
import time

# Load model locally — runs on your CPU/GPU
# Sizes: tiny (39M), base (74M), small (244M), medium (769M), large (1.5B)
model = whisper.load_model("base")

# Transcribe — everything happens on your machine
start = time.perf_counter()
result = model.transcribe("audio.mp3")
latency = time.perf_counter() - start

print(f"Transcript : {result['text']}")
print(f"Language   : {result['language']}")
print(f"Time taken : {latency:.1f}s")
print("✅ Zero bytes sent to any server")
```

> 📌 **Note:** Full hands-on implementation with microphone input and real audio files is covered in **Module 6 — On-Device Speech & Audio AI**.

**Install Whisper:**
```bash
pip install openai-whisper
# Also requires ffmpeg:
# macOS:  brew install ffmpeg
# Ubuntu: sudo apt install ffmpeg
# Windows: winget install Gyan.FFmpeg
```

**Whisper model size guide:**

| Model | Size | Speed | Best For |
|---|---|---|---|
| `tiny` | 39 MB | Fastest | Quick drafts, low-resource devices |
| `base` | 74 MB | Fast | Good balance for most uses |
| `small` | 244 MB | Medium | Better accuracy |
| `medium` | 769 MB | Slow | High accuracy |
| `large` | 1.5 GB | Slowest | Best quality, needs good hardware |

---

## 5. Real-World Application Map

Here's how on-device AI maps to real applications you can build:

| Application | Domain | Model | Library | Device |
|---|---|---|---|---|
| Offline document scanner | Vision | TrOCR | ONNX Runtime | Laptop / Phone |
| Local voice assistant | Speech + NLP | Whisper + Phi-3 | whisper.cpp + llama.cpp | Laptop / Pi |
| Privacy-first medical imaging | Vision | ResNet / ViT | ONNX Runtime | Hospital PC |
| Smart factory defect detection | Vision | YOLOv8 | Ultralytics + ONNX | Jetson Orin |
| Offline translation app | NLP | Helsinki-NLP | Transformers | Phone / Laptop |
| Agricultural crop monitoring | Vision | EfficientNet | TFLite | Pi + Camera |
| Meeting summarizer (private) | NLP / Speech | Whisper + Llama | Local pipeline | Laptop |
| Wake word detector | Speech | openWakeWord | ONNX Runtime | Any device |

---

## 6. The On-Device AI Pipeline

Every on-device AI application follows this 5-step pipeline:

```
📥  1. INPUT
     └─ Raw data: image / audio / text
          │
          ▼
🔧  2. PREPROCESS
     └─ Resize, normalize, tokenize
          │
          ▼
🧠  3. MODEL
     └─ ONNX / TFLite / llama.cpp runs locally
          │
          ▼
🔍  4. POSTPROCESS
     └─ Decode output: label, text, boxes
          │
          ▼
📤  5. OUTPUT
     └─ Show result to user / trigger action
```

```python
# ── Visualize the On-Device Pipeline ──────────────────

pipeline_steps = [
    ("1. INPUT",       "Raw data: image / audio / text",          "📥"),
    ("2. PREPROCESS",  "Resize, normalize, tokenize",             "🔧"),
    ("3. MODEL",       "ONNX / TFLite / llama.cpp runs locally",  "🧠"),
    ("4. POSTPROCESS", "Decode output: label, text, boxes",       "🔍"),
    ("5. OUTPUT",      "Show result to user / trigger action",    "📤"),
]

print("=" * 55)
print("THE ON-DEVICE AI INFERENCE PIPELINE")
print("=" * 55)
for step, desc, icon in pipeline_steps:
    print(f"\n  {icon}  {step}")
    print(f"     └─ {desc}")
    if step != pipeline_steps[-1][0]:
        print("          │")
        print("          ▼")

print("\n" + "=" * 55)
print("All steps run on YOUR hardware. No cloud needed.")
print("=" * 55)
```

> 💡 **Key insight:** All 5 steps happen on your device. No data is ever transmitted. This is the fundamental difference from cloud AI.

---

## ✅ Summary

- On-device AI covers **Vision**, **Speech**, and **NLP** workloads
- You ran your first local inference with **ONNX Runtime + MobileNetV2**
- You ran **on-device sentiment analysis** with DistilBERT — no API key needed
- **Whisper** can do full speech transcription locally on CPU
- Every on-device AI app follows the same 5-step pipeline:

```
Input → Preprocess → Model → Postprocess → Output
```

---

## ➡️ Next Lesson

**[05 — Setting Up Your Development Environment →](./05_setup.md)**

---

*Module 1 · On-Device AI with Python*