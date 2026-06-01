# 02 — Cloud vs. Edge: Latency, Privacy & Cost

> **Module 1 · Lesson 2**  
> 📄 Theory · ⏱️ ~25 min read

---

## 🎯 Learning Objectives

By the end of this lesson you will be able to:

- Compare cloud AI and on-device AI across key dimensions
- Explain the latency, privacy, cost, and reliability tradeoffs
- Decide which approach fits a given use case
- Understand hybrid architectures that combine both

---

## 1. The Big Picture

When you build an AI-powered application, the most fundamental decision is:

> **Where does the model run?**

```
Option A — Cloud AI                Option B — On-Device AI
─────────────────────              ───────────────────────
Data leaves your device            Data never leaves device
Model runs on remote server        Model runs on local hardware
Needs internet connection          Works fully offline
Pay per API call                   One-time model cost
Unlimited model size               Constrained by device memory
Easy to update model               Model update requires app update
```

Neither is always better. The right choice depends on your use case.

---

## 2. Latency

**Latency** = time from sending input → receiving output.

### Cloud AI Latency Breakdown

```
User Input
    │
    ▼  ~1–5ms     Serialize & send request
Network ──────────────────────────────────▶ Cloud Server
                  ~20–300ms network RTT          │
                                                 ▼ ~10–500ms
                                           Model inference
Network ◀─────────────────────────────────       │
                  ~20–300ms return trip    Send response
    │
    ▼
Total: 50ms – 1000ms+ (depends on region, load, model size)
```

### On-Device AI Latency Breakdown

```
User Input
    │
    ▼ <1ms    Load input into memory
Local Model ──────────────────────────────▶ NPU/CPU/GPU
                  ~5–100ms inference             │
                                           Return output
    │
    ▼
Total: 5ms – 150ms (depends on model size and hardware)
```

### Comparison Table

| Scenario | Cloud Latency | On-Device Latency | Winner |
|---|---|---|---|
| Simple classification | 100–400ms | 10–50ms | 🏆 On-Device |
| Real-time video (30fps) | ❌ Not feasible | ✅ 33ms budget | 🏆 On-Device |
| Large LLM (70B params) | 500ms–2s | ❌ Not feasible | 🏆 Cloud |
| Speech transcription | 200–800ms | 100–300ms | 🏆 On-Device |
| Offline environment | ❌ Fails | ✅ Works | 🏆 On-Device |

---

## 3. Privacy

This is often the most important factor for users.

### Cloud AI — Privacy Risks

```
Your Data                    Cloud Server
─────────                    ────────────
Medical image  ──────────▶   Third-party servers
Voice recording ─────────▶   Could be logged
Private document ────────▶   Subject to data laws
Face photo ──────────────▶   Stored, possibly used for training
```

**Problems:**
- Data crosses international borders
- Subject to government subpoenas
- Breach risk on cloud provider
- Provider may use your data to train future models
- GDPR, HIPAA compliance becomes complex

### On-Device AI — Privacy Guarantee

```
Your Data                    Stays Here
─────────                    ──────────
Medical image  ──────────▶   Your phone / laptop
Voice recording ─────────▶   Local NPU
Private document ────────▶   Never leaves device
Face photo ──────────────▶   Processed, discarded locally
```

**Benefits:**
- Zero data transmission = zero breach risk from network
- Inherently GDPR / HIPAA friendly
- User controls their own data entirely
- No need for privacy policy on AI processing

### Privacy-Critical Use Cases That Demand On-Device

- 🏥 Medical imaging and diagnostics
- 💬 Personal assistant / journaling apps
- 🔐 Biometric authentication (Face ID, fingerprint)
- 💼 Legal document analysis
- 🧒 Apps for children

---

## 4. Cost

### Cloud AI Cost Model

```
Cost = (Number of API calls) × (Price per call or per token)

Example — OpenAI GPT-4o:
  Input:  $2.50 per 1M tokens
  Output: $10.00 per 1M tokens

10,000 users × 500 tokens/day = 5B tokens/month
Cost: ~$12,500–50,000/month
```

Cloud costs scale **linearly** with usage — the more users, the more you pay.

### On-Device AI Cost Model

```
Cost = (One-time model development) + (Distribution via app update)
     = Fixed cost, regardless of number of users

100 users or 10,000,000 users → same infrastructure cost
```

On-device costs scale **near-zero** after initial development.

### Cost Comparison

| Scale | Cloud AI Monthly Cost | On-Device Monthly Cost |
|---|---|---|
| 1,000 users | ~$50–500 | ~$0 |
| 100,000 users | ~$5,000–50,000 | ~$0 |
| 10,000,000 users | ~$500,000+ | ~$0 |

> 💡 **Key insight:** On-device AI has high upfront development cost but near-zero marginal cost per user.

---

## 5. Reliability & Offline Support

| Condition | Cloud AI | On-Device AI |
|---|---|---|
| No internet connection | ❌ Fails completely | ✅ Works perfectly |
| Slow/unstable connection | ⚠️ Slow, may timeout | ✅ Unaffected |
| Cloud provider outage | ❌ App broken | ✅ Unaffected |
| Remote / rural area | ❌ No service | ✅ Works fine |
| Airplane mode | ❌ Fails | ✅ Works |

On-device AI is critical for:
- 🚜 Agricultural apps in rural areas
- ✈️ In-flight applications
- 🏔️ Field work, disaster response
- 🏭 Industrial environments with no WiFi
- 🚗 Automotive (tunnels, remote roads)

---

## 6. Model Size & Capability

The biggest limitation of on-device AI:

```
Cloud AI                          On-Device AI
────────                          ────────────
GPT-4:     ~1.7 Trillion params   Phi-3 Mini:   3.8B params
Claude:    Unknown (very large)   Gemma-2 2B:   2B params
Gemini:    Unknown (very large)   Llama 3.2 1B: 1B params

Unlimited RAM (distributed)       2–16 GB RAM typical
Powerful A100/H100 GPUs           CPU, integrated GPU, NPU
```

**Rule of thumb:**
- Tasks needing deep reasoning, broad knowledge → Cloud
- Tasks needing speed, privacy, offline → On-Device

---

## 7. The Hybrid Architecture

Many production apps use **both** — the best of both worlds:

```
                    ┌─────────────────┐
                    │   Your App      │
                    └────────┬────────┘
                             │
              ┌──────────────▼──────────────┐
              │        Decision Layer        │
              │  (route based on task type)  │
              └──────┬───────────────┬───────┘
                     │               │
          ┌──────────▼──────┐  ┌─────▼────────────┐
          │  On-Device Model │  │   Cloud Model     │
          │                 │  │                   │
          │ • Wake words     │  │ • Complex queries │
          │ • Face unlock    │  │ • Large LLM tasks │
          │ • Draft replies  │  │ • Rare edge cases │
          │ • Image preview  │  │ • Heavy reasoning │
          └─────────────────┘  └───────────────────┘
```

**Example:** Apple's Siri
- Wake word detection → fully on-device (NPU)
- Simple commands → on-device (Apple Intelligence)
- Complex web queries → routed to cloud servers

---

## 8. Decision Framework

Use this checklist to decide for your project:

```
START
  │
  ├─ Does data contain PII / sensitive info?
  │     YES → On-Device (privacy requirement)
  │
  ├─ Does the app need to work offline?
  │     YES → On-Device (reliability requirement)
  │
  ├─ Is real-time response (<100ms) required?
  │     YES → On-Device (latency requirement)
  │
  ├─ Will you have millions of users?
  │     YES → On-Device saves massive cost
  │
  ├─ Does the task need a very large model (>7B params)?
  │     YES → Cloud (capability requirement)
  │
  └─ None of the above?
        → Start with Cloud, optimize later
```

---

## 9. Summary

| Dimension | Cloud AI | On-Device AI |
|---|---|---|
| **Latency** | 50ms–1s+ | 5–150ms |
| **Privacy** | Data leaves device | Data stays local |
| **Cost at scale** | Grows linearly | Near zero |
| **Offline** | ❌ | ✅ |
| **Model size** | Unlimited | ~1–13B params typical |
| **Updates** | Instant | Requires app update |
| **Best for** | Complex reasoning, large models | Speed, privacy, offline |

---

## ➡️ Next Lesson

**[03 — Hardware Overview: CPU, GPU, NPU, DSP →](./03_hardware_overview.ipynb)**

---

*Module 1 · On-Device AI with Python*