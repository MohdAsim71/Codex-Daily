# 01 — What is On-Device AI?

> **Module 1 · Lesson 1**  
> 📄 Theory · ⏱️ ~20 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Define On-Device AI and explain how it differs from cloud AI
- Identify where inference happens in a traditional vs. edge AI pipeline
- List real-world products powered by on-device AI
- Understand why on-device AI is growing rapidly

---

## 1. What is AI Inference?

When we train an AI model, we feed it data and it learns patterns. But **training** is only half the story.

The other half is **inference** — giving the trained model new input and getting a prediction back.

```
Input → [Trained Model] → Output (Prediction)

Example:
Photo of a dog → [Image Classifier] → "Golden Retriever, 97% confidence"
```

Most of the time when you "use AI", you are doing inference, not training.

---

## 2. Where Does Inference Happen?

Traditionally, inference happens **in the cloud**:

```
Your Device  ──── Internet ────▶  Cloud Server
(phone/PC)      (sends data)      (runs model)
                                       │
Your Device  ◀─── Internet ────  Returns result
               (receives answer)
```

**On-Device AI** moves this process directly onto your local hardware:

```
Your Device
┌─────────────────────────────┐
│  Input → [Model] → Output   │
│  (everything stays here)    │
└─────────────────────────────┘
```

No internet. No server. No waiting.

---

## 3. Formal Definition

> **On-Device AI** (also called *Edge AI* or *Local AI*) is the practice of running machine learning model inference directly on end-user hardware — such as a smartphone, laptop, microcontroller, or embedded system — without sending data to a remote server.

The "device" can be:

| Device Type | Examples |
|---|---|
| 🖥️ Personal computer | MacBook, Windows laptop, Linux desktop |
| 📱 Mobile | Android phone, iPhone, tablet |
| 🍓 Single-board computer | Raspberry Pi, NVIDIA Jetson |
| 🔌 Microcontroller | Arduino, ESP32, STM32 |
| 🏭 Edge server | Industrial PC at a factory floor |
| 🚗 Embedded system | Car ECU, drone, camera |

---

## 4. A Quick History

```
1950s–2000s   AI runs on mainframes and research servers
     │
2010s         Cloud AI dominates (AWS, Google Cloud, Azure)
     │         Deep learning takes off, needs big GPUs
     │
2017          Apple Neural Engine in iPhone X
              First mainstream on-device AI chip
     │
2019–2022     TFLite, Core ML, ONNX Runtime mature
              Qualcomm, MediaTek add NPUs to every phone chip
     │
2023–2024     LLMs go local — llama.cpp, Ollama, Phi-3, Gemma
              Apple M-series, Snapdragon X Elite push hard
     │
2025+         On-device AI becomes the default for privacy-sensitive apps
              Every laptop and phone ships with an NPU
```

---

## 5. Real Products Using On-Device AI Today

You already use on-device AI every day — you just didn't know it:

| Product | On-Device AI Feature |
|---|---|
| 📱 iPhone / Android | Face ID, portrait mode, voice recognition |
| 🎧 AirPods / Pixel Buds | Noise cancellation, adaptive EQ |
| 💻 Windows Copilot+ PCs | Local LLM, semantic search, live captions |
| 📷 DSLR / mirrorless cameras | Subject tracking, scene detection |
| 🚗 Tesla Autopilot | Real-time object detection, lane keeping |
| 🏠 Alexa / Google Home | Wake word detection ("Hey Alexa") |
| 🖊️ Remarkable / Kindle | Handwriting recognition |

---

## 6. The Three Layers of On-Device AI

On-device AI has three components working together:

```
┌─────────────────────────────────────────┐
│           APPLICATION LAYER             │
│   Your Python app, mobile app, script   │
├─────────────────────────────────────────┤
│             RUNTIME LAYER               │
│  ONNX Runtime / TFLite / Core ML /      │
│  llama.cpp — executes the model         │
├─────────────────────────────────────────┤
│            HARDWARE LAYER               │
│  CPU · GPU · NPU · DSP · Memory         │
└─────────────────────────────────────────┘
```

In this course, you will learn all three layers — from Python code down to hardware.

---

## 7. Key Terms Glossary

| Term | Meaning |
|---|---|
| **Inference** | Running a trained model on new input to get a prediction |
| **Edge AI** | AI that runs at the "edge" of the network, close to the data source |
| **On-Device AI** | Inference that runs locally on user hardware, no cloud needed |
| **NPU** | Neural Processing Unit — a chip designed specifically for AI math |
| **Model** | A file containing the learned weights of a neural network |
| **Runtime** | Software that loads and executes a model (ONNX Runtime, TFLite, etc.) |
| **Latency** | Time from input to output — lower is better |
| **Quantization** | Shrinking a model's size by reducing numerical precision |

---

## 8. Summary

- AI inference is the act of using a trained model to make predictions
- Traditionally inference happens in the cloud — on-device AI brings it local
- On-device AI runs on phones, laptops, Pi, embedded systems, and more
- Every major hardware vendor now ships dedicated AI chips (NPUs)
- In this course you will learn to build, optimize, and deploy models locally using Python

---

## ➡️ Next Lesson

**[02 — Cloud vs. Edge: Latency, Privacy & Cost →](./02_cloud_vs_edge.md)**

---

*Module 1 · On-Device AI with Python*