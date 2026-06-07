# 4.1 — On-Device Speech-to-Text: Options on Android

> **Module 4 · Lesson 1**  
> 📄 Theory · ⏱️ ~20 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Compare all major STT options available on Android in 2025
- Understand the tradeoffs between accuracy, latency, size, and privacy
- Choose the right STT approach for your use case
- Know which devices and Android versions each approach supports

---

## 1. The STT Landscape on Android

```
Speech-to-Text on Android — 2025 Options:

┌─────────────────────────────────────────────────────────────────┐
│  ONLINE (needs internet)        │  OFFLINE (fully on-device)    │
├─────────────────────────────────┼───────────────────────────────┤
│  Android SpeechRecognizer API   │  ML Kit On-Device STT         │
│  (Google, Bing, etc.)           │  Whisper TFLite (tiny/base)   │
│  → Best quality, any language   │  Vosk Android                 │
│  → Sends audio to cloud         │  Mozilla DeepSpeech (legacy)  │
│  → Fails offline                │  SherpaOnnx                   │
└─────────────────────────────────┴───────────────────────────────┘
```

For privacy-first and offline apps, we focus on the right column.

---

## 2. Detailed Comparison

| Option | Offline | Languages | Size | Accuracy | Latency | Best For |
|---|---|---|---|---|---|---|
| **Android SpeechRecognizer** | ❌ | 100+ | 0 MB | ⭐⭐⭐⭐⭐ | 200–800ms | Quick dictation, high quality |
| **ML Kit On-Device STT** | ✅ | ~17 | ~80 MB | ⭐⭐⭐ | 100–400ms | Basic commands, always-on |
| **Whisper Tiny (TFLite)** | ✅ | 99 | ~40 MB | ⭐⭐⭐⭐ | 500ms–2s | Short clips, many languages |
| **Whisper Base (TFLite)** | ✅ | 99 | ~150 MB | ⭐⭐⭐⭐⭐ | 1–5s | Documents, notes, meetings |
| **Vosk** | ✅ | 20+ | ~50 MB | ⭐⭐⭐ | 50–200ms | Real-time streaming STT |
| **SherpaOnnx** | ✅ | 30+ | ~30–80 MB | ⭐⭐⭐⭐ | 100–500ms | Streaming, embedded |

---

## 3. Android SpeechRecognizer API

The built-in Android API that routes to Google's cloud servers (or other providers):

```
Your App → Android SpeechRecognizer → Google Cloud → Transcription
```

**How it works:**
- Android captures audio from the microphone
- Streams it to the configured recognition service (Google by default)
- Returns transcript via callback

**When to use:**
- ✅ Quality is the top priority
- ✅ Users always have internet
- ✅ You want zero model management
- ❌ NOT for privacy-sensitive content
- ❌ NOT for offline use

**Key limitation:** Google deprecated the offline recognition mode in Android 13+. True offline via this API is unreliable across devices.

---

## 4. ML Kit On-Device Speech Recognition

Google's on-device STT — no cloud, audio stays on device:

```
Your App → ML Kit RecognitionTask → On-Device Model → Transcription
```

**Characteristics:**
- Model downloaded on first use (~80 MB per language)
- Works offline after download
- Supports streaming (word-by-word) or batch mode
- ~17 languages as of 2025

**When to use:**
- ✅ Simple voice commands (short phrases)
- ✅ You want Google's maintained model without cloud dependency
- ✅ App primarily uses English or major European languages
- ❌ NOT for long-form transcription (documents, meetings)
- ❌ NOT for less common languages

---

## 5. Whisper TFLite — The Best Offline Option

OpenAI's Whisper converted to TFLite is the gold standard for offline STT:

```
Audio File / PCM Buffer → Whisper TFLite → Transcription (any of 99 languages)
```

**Why Whisper wins for offline:**
- Trained on 680,000 hours of diverse audio
- 99 languages with excellent accuracy
- Multilingual: can auto-detect language
- Works on audio files, not just live mic (great for note-taking apps)
- Tiny model = 39M params, ~40 MB — runs on any Android 8.0+ device

**Model size guide:**

```
Whisper Tiny  (39M params)  → ~40 MB  → ~500ms/clip  → Good accuracy
Whisper Base  (74M params)  → ~150 MB → ~1–2s/clip   → Very good accuracy
Whisper Small (244M params) → ~500 MB → ~3–6s/clip   → Excellent accuracy

For Android: start with Tiny, upgrade to Base if accuracy matters
```

**Limitation:** Whisper processes fixed-length audio chunks (30 seconds max per pass). Not designed for real-time streaming word-by-word output.

---

## 6. Vosk — Best for Real-Time Streaming

Vosk is designed specifically for streaming real-time transcription:

```
Live Mic → Vosk → Word-by-word output (low latency)
```

**Characteristics:**
- Streaming architecture — words appear as they're spoken
- Very low latency (~50–200ms per word)
- 20+ languages
- Model sizes from 40 MB (small) to 1.8 GB (large)
- Apache 2.0 license

**When to use:**
- ✅ Live transcription (subtitles, live captions)
- ✅ Voice-controlled UI (commands appear instantly)
- ✅ You need word-level timing
- ❌ Less accurate than Whisper on continuous speech

---

## 7. SherpaOnnx — Emerging Contender

SherpaOnnx (from k2-fsa) is gaining traction in 2025:

```
Your App → SherpaOnnx (ONNX Runtime) → Streaming transcription
```

- Uses ONNX Runtime under the hood
- Excellent for CJK languages (Chinese, Japanese, Korean)
- Models from 30–80 MB
- Real-time streaming support
- Good choice for Asian language apps

---

## 8. Decision Framework

```
START: What is your app's STT requirement?
        │
        ├─ Does it need to work offline?
        │     NO  → Android SpeechRecognizer API (best quality, easiest)
        │
        ├─ Is it short voice commands? (<5 words, single phrase)
        │     YES → ML Kit On-Device STT (fast, simple API)
        │
        ├─ Is it long-form? (notes, dictation, documents, meetings)
        │     YES → Whisper TFLite Base (best accuracy offline)
        │
        ├─ Does it need real-time word-by-word streaming?
        │     YES → Vosk (designed for this)
        │
        ├─ Is the primary language Chinese / Japanese / Korean?
        │     YES → SherpaOnnx (best CJK support)
        │
        └─ Need 99-language support offline?
              YES → Whisper TFLite (only option with this breadth)
```

---

## 9. Audio Requirements Summary

All on-device STT models expect a specific audio format:

| Parameter | Value | Notes |
|---|---|---|
| **Sample rate** | 16,000 Hz | Most models require exactly 16kHz |
| **Channels** | Mono (1) | Stereo must be downmixed |
| **Bit depth** | 16-bit PCM | Standard for Android AudioRecord |
| **Format** | Float32 [-1, 1] or Int16 [-32768, 32767] | Whisper needs Float32 |
| **Chunk size** | Model-dependent | Whisper: 30s max; Vosk: any |

Android's `AudioRecord` delivers 16-bit PCM which you convert to Float32 for Whisper:

```kotlin
// Short (16-bit) → Float32 conversion
val floatArray = shortArray.map { it.toFloat() / 32768.0f }.toFloatArray()
```

---

## 10. Privacy Comparison

```
Option                    Audio leaves device?   Who processes it?
─────────────────────     ────────────────────   ─────────────────
SpeechRecognizer API      ✅ YES                 Google servers
ML Kit On-Device          ❌ NO                  On-device model
Whisper TFLite            ❌ NO                  On-device model
Vosk                      ❌ NO                  On-device model
SherpaOnnx                ❌ NO                  On-device model
```

For medical, legal, financial, or personal audio — on-device is the only acceptable choice.

---

## 11. Summary

| | SpeechRecognizer | ML Kit | Whisper TFLite | Vosk |
|---|---|---|---|---|
| Offline | ❌ | ✅ | ✅ | ✅ |
| Languages | 100+ | ~17 | 99 | 20+ |
| Accuracy | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| Streaming | ✅ | ✅ | ❌ | ✅ |
| Privacy | ❌ | ✅ | ✅ | ✅ |
| Setup effort | Minimal | Low | Medium | Medium |

**Recommendation for this course:** We use **Whisper TFLite** (Lesson 4.2) as the primary on-device STT — best accuracy, 99 languages, and directly relevant to production privacy-first apps.

---

## ➡️ Next Lesson

**[4.2 — Whisper TFLite on Android: Offline Transcription →](./02_whisper_tflite_android.md)**

---

*Module 4 · On-Device AI for Android Development*