# Android Dev → Global SWE: 6-Month Execution Plan

> **Profile:** 5-year Android Developer from India  
> **Target:** High-paying remote roles at US/Europe-based companies  
> **Commitment:** 2–3 hours/day, 6 days/week  
> **Expectation:** Serious, consistent execution — no fluff, just results

---

## Table of Contents

1. [DSA Preparation](#1-dsa-preparation)
2. [System Design Preparation](#2-system-design-preparation)
3. [Required Skills for High-Paying Remote Jobs](#3-required-skills-for-high-paying-remote-jobs)
4. [Skill Gap Analysis](#4-skill-gap-analysis)
5. [Hands-On Preparation (Projects)](#5-hands-on-preparation-projects)
6. [Resume + LinkedIn Preparation](#6-resume--linkedin-preparation)
7. [Remote Job Strategy](#7-remote-job-strategy)
8. [Interview Preparation](#8-interview-preparation)
9. [Smart Preparation Tricks](#9-smart-preparation-tricks)
10. [Weekly Execution Plan](#10-weekly-execution-plan)

---

## 1. DSA Preparation

### Key Metrics
| Metric | Target |
|--------|--------|
| Daily problems | 3–5 (Easy 1, Medium 2–3, Hard 1) |
| Total problems | 250+ over 10 weeks |
| Primary resource | NeetCode 150 (neetcode.io) |
| Language | Java or Kotlin (use what you know) |

---

### Priority Order — Cover in This Exact Sequence

#### Week 1–2: Arrays & Strings ⭐ HIGHEST PRIORITY
- Two pointers
- Prefix sums
- Sliding window (fixed and variable size)
- In-place manipulation
- **Target:** 30 problems before moving on
- **Why first:** Appear in 60%+ of screen rounds

#### Week 2: HashMap & Sets
- Frequency counting
- Anagram detection
- Two-sum family problems
- **Key insight:** Almost every problem here has an O(n) hashmap solution

#### Week 3: Linked Lists & Stacks/Queues
- Reverse linked list (iterative + recursive)
- Cycle detection (Floyd's algorithm)
- Merge sorted lists
- Monotonic stack
- **Note:** Android devs often skip this — don't

#### Week 4–5: Trees & BST
- DFS (inorder, preorder, postorder)
- BFS / level-order traversal
- Lowest Common Ancestor
- Path sum problems
- Validate BST
- Serialize/deserialize binary tree
- **Why critical:** Most Amazon/Google rounds have at least one tree problem

#### Week 5–6: Graphs
- BFS/DFS on grid and adjacency list
- Union-Find (Disjoint Set Union)
- Topological sort
- Dijkstra's shortest path
- **Why critical:** High frequency in FAANG mid-level rounds

#### Week 7–8: Dynamic Programming
- 1D DP → 2D DP → Interval DP
- Core patterns: knapsack, LCS, LIS, coin change, house robber
- **Rule:** Don't memorize solutions — learn to identify sub-problem structure

#### Week 9–10: Heaps, Binary Search & Tries
- Top-K problems
- Merge K sorted lists
- Search in rotated array
- Binary search on answer (feasibility function)
- Trie for autocomplete
- **Why important:** Trie is a differentiator for senior-level roles

---

### Key Patterns — Learn These, Not Just Problems

| Pattern | Frequency | Core Use Case |
|---------|-----------|---------------|
| Sliding Window | 🔴 Very High | Variable/fixed size, substring problems |
| Two Pointers | 🔴 Very High | Sorted arrays, linked list cycle, palindromes |
| BFS on Matrix | 🟠 High | Shortest path, islands, rotten oranges |
| Backtracking | 🟠 High | Permutations, combinations, N-queens |
| Monotonic Stack | 🟠 High | Next greater element, histogram area |
| Binary Search on Answer | 🔵 Medium | Min/max problems with feasibility function |
| Union-Find (DSU) | 🔵 Medium | Connected components, Kruskal's |
| DP State Design | 🔵 Medium | Define state before writing a single line of code |

---

### Must-Do Problem List (50 Core Problems)

#### Arrays
- Two Sum
- Best Time to Buy and Sell Stock
- Contains Duplicate
- Product of Array Except Self
- Maximum Subarray (Kadane's)
- Trapping Rain Water
- 3Sum

#### Sliding Window
- Longest Substring Without Repeating Characters
- Minimum Window Substring
- Permutation in String
- Longest Repeating Character Replacement

#### Linked List
- Reverse Linked List
- Detect Cycle (Floyd's)
- Merge Two Sorted Lists
- Remove Nth Node From End
- Reorder List
- LRU Cache ⭐

#### Trees
- Binary Tree Level Order Traversal
- Maximum Depth of Binary Tree
- Lowest Common Ancestor of a BST
- Validate Binary Search Tree
- Serialize and Deserialize Binary Tree
- Kth Smallest Element in a BST

#### Graphs
- Number of Islands
- Clone Graph
- Course Schedule (topological sort)
- Pacific Atlantic Water Flow
- Dijkstra's on weighted graph
- Word Ladder

#### Dynamic Programming
- Climbing Stairs
- House Robber I & II
- Coin Change
- Longest Common Subsequence
- Edit Distance
- Word Break
- Longest Increasing Subsequence

#### Heap
- Top K Frequent Elements
- Find Median from Data Stream ⭐
- Merge K Sorted Lists ⭐
- Task Scheduler

#### Binary Search
- Search in Rotated Sorted Array
- Find Minimum in Rotated Sorted Array
- Koko Eating Bananas (binary search on answer)
- Median of Two Sorted Arrays ⭐

---

### Problem-Solving Approach (Use This Every Single Interview)

```
Minute 0–2:   CLARIFY
              → Ask about input range, edge cases, expected output
              → "Can I assume inputs are always valid?"
              → Never start coding immediately

Minute 2–5:   PLAN (out loud)
              → State brute force solution first
              → Then derive optimal: "I notice X, which allows Y optimization"
              → State time/space complexity of your plan before coding

Minute 5–15:  CODE (while narrating)
              → Say what each variable does as you write it
              → No silent coding — ever

Minute 15–20: VERIFY
              → Dry-run with a simple example
              → State final time & space complexity
              → Ask: "Would you like me to optimize further?"

If stuck:     NEVER go silent
              → Say: "Let me think about what information I'm discarding"
              → Or: "Let me try a simpler version of the problem first"
```

---

## 2. System Design Preparation

### Learning Path — 8 Weeks

#### Week 1–2: LLD Foundations
- SOLID principles (with code examples in Kotlin/Java)
- Design Patterns: Factory, Singleton, Observer, Strategy, Builder, Decorator
- Practice designing: parking lot, chess game, elevator system, vending machine
- Resource: *Head First Design Patterns* (book)

#### Week 3–4: HLD Core Concepts
Master these concepts — you must be able to explain AND draw each one:
- CAP theorem and what it means in practice
- Horizontal vs. vertical scaling
- Load balancing (round-robin, least connections, consistent hashing)
- Caching (Redis, Memcached, cache-aside, write-through, LRU/LFU eviction, TTL)
- CDN and edge caching
- Message queues (Kafka, RabbitMQ, pub-sub vs. point-to-point)
- API Gateway
- SQL vs. NoSQL tradeoffs, sharding, replication
- Consistency models (strong, eventual, causal)
- Rate limiting (token bucket, leaky bucket, sliding window counter)

#### Week 5–7: Real System Practice
Design 8 systems end-to-end. One system every 2–3 days. **Write it out on paper before watching any solution.**

#### Week 8: Mock Interviews
- 4 mock system design interviews on Pramp or Interviewing.io
- Focus feedback ask: "Was my communication clear? Did I miss any major component?"

---

### 8 Systems to Practice (Priority Order)

| # | System | Key Concepts Covered | Difficulty |
|---|--------|----------------------|------------|
| 1 | URL Shortener | Hashing, DB design, caching basics | 🟢 Beginner |
| 2 | Rate Limiter | Distributed counters, Redis, sliding window | 🟢 Beginner |
| 3 | WhatsApp / Chat System | WebSockets, message queues, storage, delivery receipts | 🟡 Medium |
| 4 | Instagram / Photo Feed | CDN, news feed fanout (push vs. pull), media storage | 🟡 Medium |
| 5 | Uber / Ride Sharing | Geo-indexing (Quadtree/S2), real-time matching, surge pricing | 🟡 Medium |
| 6 | YouTube | Video encoding pipeline, chunked upload, CDN edge caching | 🔴 Advanced |
| 7 | Search Autocomplete | Trie, Top-K, distributed prefix search | 🔴 Advanced |
| 8 | Distributed Key-Value Store | Consistent hashing, gossip protocol, quorum reads/writes | 🔴 Advanced |

---

### System Design Answering Framework — ROFAN

Use this structure in every system design interview:

```
R — Requirements
    → Functional: what the system does
    → Non-functional: scale, latency, availability, consistency
    → Always ask: "How many users? What's acceptable latency? 
                   What's the read/write ratio?"

O — Overall Design
    → Draw high-level boxes first: client → API gateway → services → DB → cache
    → Talk while drawing. Never draw in silence.

F — Flow
    → Walk through ONE key user flow end-to-end
    → Example: "User sends a message → API gateway → chat service → 
                 message queue → receiver's device"
    → Makes the design concrete and shows you understand data movement

A — APIs
    → Define 2–3 key REST/gRPC endpoints
    → Include: method, URL, request body, response shape

N — Numbers + Deep Dive
    → Back-of-envelope: QPS, storage estimate (show your math)
    → Deep dive on the hardest/most interesting component
    → Explicitly state tradeoffs: "I chose NoSQL here because... 
       the tradeoff is we lose strong consistency"
```

---

## 3. Required Skills for High-Paying Remote Jobs

### Core Engineering Skills

| Skill | What's Expected | How to Build It |
|-------|-----------------|-----------------|
| Clean Code | Meaningful names, small functions, DRY, YAGNI, no magic numbers | Read *Clean Code* (Robert Martin). Apply in every project. |
| SOLID Principles | Apply them in LLD interviews and code reviews | Write one example of each principle in Kotlin |
| Testing | Unit tests, integration tests, TDD mindset | Add JUnit + Mockito tests to all portfolio projects |
| Debugging | Systematic: reproduce → isolate → hypothesize → verify | Document your debugging process in your projects' READMEs |
| Code Review | Give and receive constructive reviews | Study merged PRs on popular open-source repos |

---

### Backend Knowledge (Must-Add for Android Devs)

- **REST APIs:** HTTP verbs, status codes, OAuth2, JWT auth, rate limiting, versioning, cursor-based pagination
- **Databases:** SQL joins, indexes, query planning, N+1 problem, connection pooling, ORM basics, MongoDB basics
- **Distributed systems basics:** CAP theorem, eventual consistency, idempotency — you don't need to build them, you need to reason about them
- **Backend framework:** Learn Spring Boot (your Java carries you). Goal: build a CRUD REST API with JWT auth in under a week

---

### System Design & Architecture Expectations

- Can design systems from scratch with clear tradeoffs
- Understands when to use microservices vs. monolith
- Knows database selection criteria (SQL vs. NoSQL vs. time-series vs. graph)
- Can estimate scale: QPS, storage, bandwidth back-of-envelope
- Understands event-driven architecture basics

---

### DevOps Basics (Enough to Be Credible)

- **Docker:** Containerize your projects. Learn Dockerfile, docker-compose. This alone impresses many companies.
- **CI/CD:** Set up GitHub Actions on one project. Auto-run tests on push, auto-deploy to Render/Railway.
- **Cloud (AWS or GCP):** S3 (storage), EC2/Cloud Run (compute), RDS (managed DB), CloudFront (CDN). AWS Cloud Practitioner certification is 2 weeks of prep and carries real weight.
- **Linux & shell:** Basic commands, cron jobs, file permissions, SSH — remote companies assume this.

---

### Communication Skills (The #1 Differentiator for Remote Roles)

- **Async writing:** Write clear Slack messages, GitHub PR descriptions, and technical docs. Practice by writing a README for every project.
- **Spoken technical English:** Join engineering Discord communities. Do mock interviews weekly. Practice explaining code choices out loud for 15 min/day.
- **Timezone-aware collaboration:** Over-communicate blockers, proactively share updates, document decisions. Remote companies hire for this explicitly.
- **Ownership signals:** In every interview, show you don't wait to be told what to do. Mention times you proactively identified and fixed problems.

---

## 4. Skill Gap Analysis

### Your Current Strengths (Android Background)

✅ Strong Java/Kotlin — translates directly to backend and DSA  
✅ OOP, design patterns (MVP, MVVM, Clean Architecture exposure)  
✅ Multithreading, coroutines, async programming (RxJava/Coroutines)  
✅ REST API consumption, JSON parsing, networking concepts  
✅ Git workflow, debugging, performance optimization  
✅ Jetpack components, dependency injection (Hilt/Dagger)

---

### Your Gaps — Prioritized for Immediate Action

#### 🔴 Fix First (Month 1): DSA Interview Fluency
You likely know algorithms conceptually but can't code them under 20-minute pressure.  
**Action:** Start NeetCode 150 today. Do it daily before anything else.

#### 🔴 Fix Second (Month 1–2): System Design Vocabulary
Android devs think client-side. You need server-side fluency.  
**Action:** Read *System Design Interview* by Alex Xu, Volumes 1 & 2. Design one system per week.

#### 🟠 Fix by Month 2: Backend Development
You consume APIs. You need to build them.  
**Action:** Build one REST API backend with Spring Boot + PostgreSQL + Docker. Deploy it.

#### 🟠 Fix by Month 2: Global Communication Style
Indian developers are often perceived as unclear in async written communication.  
**Action:** Write the README for every project you build. Write technical posts on LinkedIn weekly.

#### 🟡 Fix by Month 3: DevOps/Cloud Basics
Docker, CI/CD, basic AWS — needed for senior credibility.  
**Action:** Containerize your projects. Set up GitHub Actions. Get AWS Cloud Practitioner cert.

#### 🟡 Fix by Month 2: Resume and LinkedIn for Global Market
Your resume likely reads for the Indian market.  
**Action:** Complete rewrite using Jake's Resume template on Overleaf. See Section 6.

---

### How to Transition: Mobile Dev → Strong Software Engineer

1. **Stop identifying as "Android Developer"** — you are a "Software Engineer with deep mobile expertise." Frame every conversation this way.
2. **Add backend projects to your portfolio.** One full-stack project neutralizes the "mobile only" concern.
3. **Learn to speak in distributed systems terms** — even in Android interviews, it signals seniority.
4. **Frame past experience as engineering decisions**, not mobile features:
    - Instead of: "Built a feature using Jetpack Compose"
    - Say: "Designed a declarative UI architecture using Jetpack Compose, reducing state management bugs by 30% and improving team onboarding time"

---

## 5. Hands-On Preparation (Projects)

> **Rule:** Build 3 projects max. Build them exceptionally well. Quality >> Quantity.

---

### Project 1: Full-Stack Real-Time Chat App ⭐ BUILD FIRST

**Why this project:**  
Demonstrates backend (Spring Boot), WebSockets, database design, async messaging, and optionally an Android client — covers multiple system design concepts you're studying simultaneously.

**Tech Stack:**
- Backend: Spring Boot + PostgreSQL + Redis (pub-sub for real-time)
- DevOps: Docker + Docker Compose + GitHub Actions CI/CD
- Optional: Android client using Kotlin + Jetpack Compose + WebSocket

**Must-Have Features:**
- User registration and JWT authentication
- Real-time messaging via WebSockets
- Message delivery receipts (sent → delivered → read)
- Paginated message history
- Group chats with member management

**Differentiator:**  
Write a detailed `ARCHITECTURE.md` explaining why you chose WebSockets over HTTP polling, why Redis pub-sub, and how you'd scale this to 1M users. This shows system design thinking, not just coding.

---

### Project 2: Android App with Clean Architecture + Full Backend

**Why this project:**  
Shows you can architect at scale. Shows a full-stack mindset. Directly relevant to senior Android roles globally.

**Tech Stack:**
- Android: Kotlin + Jetpack Compose + Hilt + Coroutines + Retrofit
- Backend: Spring Boot REST API (your own) deployed on AWS/GCP
- Database: PostgreSQL with proper indexing

**Must-Have Features:**
- Offline-first with Room + sync mechanism (conflict resolution)
- Clean Architecture (data / domain / presentation layers, fully separated)
- Proper error handling (network, DB, user-facing)
- Unit tests for domain layer (use cases), UI tests for critical flows

**Differentiator:**  
Write a 500-word `README.md` section titled "Architecture Decisions" that explains each major technical choice and what tradeoff you made. Recruiters and engineers love seeing this.

---

### Project 3: Open-Source Developer Tool or SDK

**Why this project:**  
Open-source maintainership is rare among Android developers and impresses US/Europe companies significantly. One maintained library signals senior engineering judgment.

**Ideas:**
- An Android performance monitoring library (tracks frame drops, memory, startup time)
- A Kotlin multiplatform utility library
- A mock API server for Android UI testing
- A Compose component library with proper documentation and samples

**Differentiator:**
- Publish to Maven Central (detailed guide: central.sonatype.com)
- Add GitHub Actions to run tests and publish automatically
- Write proper KDoc/JavaDoc for every public API
- Aim for 10+ stars — even that is significant signal

---

### GitHub Portfolio Strategy

```
Profile README (username/username repo):
  → Your specialization in 1 sentence
  → Tech stack (logos or text list)
  → Links to your 3 pinned projects
  → "Open to remote SWE roles at US/Europe companies"

Pinned Repos (exactly 3–4):
  → Each must have: problem statement, architecture diagram/image,
    setup instructions, screenshots or demo GIF, link to live demo

Commit Discipline:
  → Small, meaningful commits
  → Good messages: "feat: add JWT refresh token rotation"
  → NOT: "fix stuff" or "wip"

Open Source Contributions:
  → Contribute to 1–2 well-known Android projects:
    Coil, Accompanist, Compose Multiplatform samples, Retrofit
  → Even one merged PR is a differentiator

Dead Repo Cleanup:
  → Archive or delete incomplete projects
  → 30 repos with no READMEs hurts more than it helps
```

---

## 6. Resume + LinkedIn Preparation

### Global-Standard Resume Rules

**Format:**
- 1 page only — no exceptions for 5 years of experience
- No photos, no "Objective" section, no references section
- Use Jake's Resume template on Overleaf (search "Jake's Resume Overleaf" — it's free)
- ATS-safe: no tables, no columns in complex layouts, no text boxes

**Each bullet point must follow this formula:**

```
[Action Verb] [What You Did] [Measurable Result]

❌ Bad:  "Worked on improving app performance"
✅ Good: "Reduced app startup time by 40% by implementing lazy 
          initialization and deferred loading of non-critical dependencies"

❌ Bad:  "Responsible for the payment module"
✅ Good: "Led refactor of payment module serving 2M daily active users, 
          reducing checkout errors by 65% and increasing conversion by 8%"
```

**ATS Keywords:**  
Mirror language directly from job descriptions. If the JD says "Kotlin, Coroutines, MVVM, REST APIs" — those exact terms must appear in your resume.

**Skills Section:**  
List skills you're confident using professionally. Do not rate yourself (no stars or percentages). Group them:
```
Languages:   Kotlin, Java, Python (basic)
Android:     Jetpack Compose, Hilt, Coroutines, Retrofit, Room, WorkManager
Backend:     Spring Boot, REST APIs, PostgreSQL, Redis, Docker
Tools:       Git, GitHub Actions, AWS (S3, EC2), Android Studio, IntelliJ IDEA
Concepts:    Clean Architecture, MVVM, SOLID, System Design, TDD
```

**Forbidden phrases:**
- "Responsible for..."
- "Assisted with..."
- "Worked on..."
- "Helped the team..."

**Required action verbs:**  
Designed, Built, Optimized, Led, Reduced, Shipped, Refactored, Architected, Implemented, Mentored, Drove, Delivered

---

### LinkedIn Optimization Checklist

**Headline:**
```
❌ "Android Developer at XYZ Company"
✅ "Android Engineer | Kotlin · Jetpack Compose · Clean Architecture | 
    Open to Remote SWE Roles"
```

**About Section (3 short paragraphs):**
1. What you build (your technical specialty)
2. What you're most proud of (1 specific achievement with numbers)
3. What you're looking for ("Open to senior Android/SWE remote roles at US and Europe-based companies")

**Open to Work:**
- Set visibility: **Recruiters only** (not public green banner)
- Job titles: Software Engineer, Android Engineer, Senior Software Engineer, Mobile Engineer
- Locations: Remote — target US, UK, Germany, Netherlands, Canada

**Featured Section:**
- Add your GitHub profile link
- Add your best project's demo video or README
- Add any published technical article

**Posting Strategy (1–2 posts/week):**
- Things you learned while preparing
- A bug you solved and how you debugged it
- An architecture decision you made and why
- System design concept explained simply

**Connection Strategy:**
- Connect with 10 people/week
- Target: recruiters at your target companies, Indian engineers working at US/EU companies, engineering managers
- Personalize every single connection request in 1–2 specific sentences
- Generic "I'd like to connect" gets ignored

---

## 7. Remote Job Strategy

### Best Platforms — Ranked by ROI

| Platform | Use Case | Frequency |
|----------|----------|-----------|
| LinkedIn Jobs | Highest volume, direct recruiter contact | Daily |
| Wellfound (AngelList) | Startups, remote-first, streamlined apply | Daily |
| Remote.com | Explicitly remote companies | 3x/week |
| We Work Remotely | Remote-first job board | 3x/week |
| Greenhouse / Lever boards | Mid-size tech companies' ATS | Weekly |
| Glassdoor | Research salary & culture before interviews | Research only |
| Hired.com | Companies approach you (good for senior engineers) | Set up once |
| Company career pages | Apply directly, not through 3rd party | Targeted |

---

### How to Get Shortlisted

**Referrals first — always:**
A referred resume has ~5x the shortlist rate of a cold application. Process:
1. Identify the company you want to apply to
2. Search LinkedIn: "[Company Name] Android Engineer" or "Software Engineer"
3. Find an Indian engineer or any engineer there
4. Send this exact message structure:
   > "Hi [Name], I'm applying for the [Role] position at [Company]. I have [X years] of [specific] experience and your company's work on [specific thing] really resonates with me. Would you be willing to share a referral? Happy to send my resume and chat briefly if that helps."

**Quality over volume:**
- Apply to 5 companies with deep customization, not 50 with a generic resume
- Customize your resume summary and top bullet points per company

**Warm outreach after applying:**
- Find the hiring manager or recruiter on LinkedIn within 24 hours of applying
- Send a 3-sentence note: what you applied for, why you're a fit, ask if they received your application
- Most candidates never do this — it moves your application to the top

**Best geographies for remote from India:**
- 🇳🇱 Netherlands — most remote-friendly, good visa pathways
- 🇩🇪 Germany — strong engineering culture, many remote roles
- 🇬🇧 UK — timezone overlap, many remote roles, English-first
- 🇨🇦 Canada — timezone flexibility, immigrant-friendly
- 🇺🇸 US — highest pay, hardest timezone overlap (doable for US East)

**Timing:**
- Jobs posted in first 48 hours get 3x more attention per applicant
- Check job boards at the same time every morning — build this as a habit

---

## 8. Interview Preparation

### Coding Interview Strategy

**The golden rules:**
1. **Never code silently.** Narrate everything. Interviewers evaluate process, not just output.
2. **Clarify before coding.** 2 minutes of clarification prevents 10 minutes of wrong code.
3. **State brute force first, then optimize.** Never jump to optimal — it looks like you memorized, not reasoned.
4. **State complexity.** Always. Before and after optimizing.

**Timing guide:**
- Easy: ≤15 minutes (if taking longer, something is wrong — ask for a hint)
- Medium: ≤25 minutes
- Hard: ≤35 minutes
- If stuck at 5 min: verbalize your thinking, then ask a targeted hint question

**What to do after writing code:**
```
1. Dry-run with the given example (say each step out loud)
2. State time complexity: O(n), O(n log n), etc.
3. State space complexity
4. Mention edge cases you handled
5. Ask: "Would you like me to optimize further or handle more edge cases?"
```

---

### System Design Interview Approach

Apply the ROFAN framework (see Section 2) and additionally:

- **Draw while talking.** Never describe a component without drawing it.
- **Drive the interview.** Don't wait to be asked questions — move through your framework proactively.
- **Make tradeoffs explicit.** "I chose PostgreSQL here for ACID compliance. The tradeoff is we'll need to shard it once we hit ~10M users."
- **Back-of-envelope estimates:**
    - QPS: (users × actions/day) / 86,400
    - Storage: QPS × record_size × seconds_per_year
    - Show your math. Round aggressively.
- **Deep dive on the hardest part.** Shows you know where systems actually fail.

---

### Behavioral Interview — STAR Method

**Prepare 8 stories, each mapped to one theme:**

| Story | Theme |
|-------|-------|
| 1 | Time you led without authority |
| 2 | Conflict with a teammate or manager |
| 3 | Your biggest technical failure and what you learned |
| 4 | Time you drove innovation or improvement proactively |
| 5 | Working under extreme time pressure |
| 6 | Making a decision with incomplete information |
| 7 | Feedback you gave that was difficult |
| 8 | Your single greatest technical achievement |

**STAR Structure:**
```
S — Situation: Brief context (2–3 sentences)
T — Task: Your specific responsibility (not the team's)
A — Action: What YOU specifically did (not "we")
R — Result: Quantified outcome — numbers, percentages, impact
```

**Example story:**
> *Situation:* "We had a 4% crash rate on our payment screen, affecting roughly 50,000 daily active users and directly impacting revenue.  
> *Task:* I was the engineer assigned to diagnose and fix it with a 48-hour deadline.  
> *Action:* I reproduced the crash using Android Vitals data, profiled with Android Profiler, and identified a race condition in our ViewModel's coroutine scope. I fixed it using a mutex and wrote a regression test to prevent recurrence. I also documented the pattern in our internal wiki.  
> *Result:* Crash rate dropped from 4% to 0.1% within 2 days, and the pattern documentation prevented 2 similar bugs from shipping in the following sprint."

**Common behavioral questions to prepare for:**
- Tell me about yourself (2 minutes, structured, not your life story)
- Why this company specifically?
- Where do you see yourself in 3 years?
- Tell me about a disagreement with your manager. What happened?
- Tell me about a time you failed. What did you learn?
- What's your greatest technical achievement?

---

## 9. Smart Preparation Tricks

### Time-Saving Hacks

- **Use NeetCode.io instead of random LeetCode browsing.** Problems are grouped by pattern with video solutions. The NeetCode 150 list is all you need.
- **Anki flashcards for system design.** 10 min/day of spaced repetition beats 1-hour study sessions. Add one concept per day.
- **Label every DSA solution with its pattern.** Write `// Pattern: Sliding Window` at the top of every solution. After 50 problems, pattern recognition becomes automatic.
- **Template your STAR stories in a Google Doc.** Read them once before each behavioral round. You'll sound natural, not rehearsed.
- **Job tracker is non-negotiable.** Use Notion or Google Sheets. Columns: Company, Role, Applied date, Status, Next action, Contact. Apply to max 5–10/week and track everything.
- **Pramp.com for free mock interviews.** Weekly from Month 3 onwards. Nothing substitutes real interview pressure.

---

### Common Mistakes Indian Developers Make (Avoid These)

❌ **Preparing too long before applying**  
Start applying at Month 3–4, not Month 6. The first 10 applications are learning, not job offers. Waiting longer only increases anxiety.

❌ **Underselling achievements**  
"I worked on X" → "I owned X, which reduced Y by Z%." Be specific. Be confident. Precision is not bragging.

❌ **Coding silently in interviews**  
Western interviewers expect constant verbalization. Silence reads as confusion, even when you're thinking deeply.

❌ **Over-focusing on DSA while ignoring system design**  
Senior roles (5+ YOE) heavily weight system design. At mid-senior level it's 50% of the evaluation. Treat it equally.

❌ **Applying only to FAANG**  
Mid-size companies (1,000–10,000 employees) in the EU/US often pay equally well, move faster, and are far more likely to hire remote engineers based in India.

❌ **2–3 page resume**  
1 page only. Recruiters spend 6 seconds on first scan. Everything must fit and be scannable.

❌ **"We" instead of "I" in interviews**  
Always say "I" when describing your contributions. Interviewers are evaluating you, not your team.

---

### How to Stand Out Globally

- **Technical writing:** Publish one blog post/month on dev.to, Medium, or a personal blog. A post that ranks on Google is a 24/7 recruiter. Write about problems you actually solved.
- **Community visibility:** Contribute to Android Reddit, answer on Stack Overflow in your niche, join the Android Dev Discord. Visibility compounds over months.
- **AWS Cloud Practitioner certification:** 2 weeks of prep, globally recognized, adds credibility for senior remote roles.
- **Ask sharp questions in interviews:**
    - "What does on-call rotation look like for this team?"
    - "What's the biggest architectural challenge you're currently facing?"
    - "How does the team handle technical debt against feature velocity?"  
      These signal seniority more than almost anything else.
- **Open source contribution:** One merged PR to a well-known Android library (Coil, Accompanist, Compose) is a differentiator that 95% of candidates don't have.

---

## 10. Weekly Execution Plan

### 3–6 Month Timeline

#### Month 1–2: Foundation Phase
**DSA:** Arrays, Strings, HashMap, Linked Lists, Stacks, Queues (target: 60 problems)  
**System Design:** LLD patterns + HLD core concepts  
**Build:** Start Project 1 (Chat App) — get backend running  
**Resume:** Complete rewrite to global standard  
**LinkedIn:** Full profile optimization

#### Month 3–4: Build & Apply Phase
**DSA:** Trees, Graphs, DP (target: 120 total problems)  
**System Design:** Practice 5 real systems using ROFAN framework  
**Build:** Complete Project 1, start Project 2  
**Apply:** 5 companies/week with tailored applications  
**Mock interviews:** Weekly on Pramp starting now

#### Month 5–6: Interview Conversion Phase
**DSA:** Heaps, Binary Search, Tries, Hard problems (target: 200+ total)  
**System Design:** Remaining 3 systems + 4 mock interviews  
**Apply:** 10+/week, aggressive referral outreach  
**Behavioral prep:** Polish all 8 STAR stories  
**Target outcome:** 2+ offers — negotiate compensation hard

---

### Weekly Schedule (2.5 hrs/day average)

| Day | Activities | Time |
|-----|------------|------|
| **Monday** | DSA: 1 new pattern study + 3 problems | 2 hrs |
| **Tuesday** | DSA: 3 problems on yesterday's pattern · Project work | 2.25 hrs |
| **Wednesday** | System Design: 1 system end-to-end on paper · Job applications (3 companies) | 2 hrs |
| **Thursday** | DSA: 3 problems (include 1 hard attempt) · Project work | 2.25 hrs |
| **Friday** | Mock interview (Pramp) · DSA problem revision | 2 hrs |
| **Saturday** | Deep project work (uninterrupted 2 hrs) · Job applications + networking | 2.5 hrs |
| **Sunday** | Revisit 3 hardest problems from the week · 1 LinkedIn post · Rest | 1 hr |

---

### Weekly Success Metrics (Track These)

| Metric | Target |
|--------|--------|
| DSA problems solved | 15–20/week |
| Mock interviews done | 1/week (from Month 3) |
| Job applications sent | 5–10/week (from Month 3) |
| LinkedIn connections made | 10/week |
| System design systems practiced | 1/week |
| Project commits | 5+/week |

---

## Resources

### Books
- *Clean Code* — Robert C. Martin
- *System Design Interview Vol. 1 & 2* — Alex Xu
- *Head First Design Patterns* — Eric Freeman

### Websites
- [neetcode.io](https://neetcode.io) — DSA patterns and problems
- [leetcode.com](https://leetcode.com) — Problem practice
- [pramp.com](https://pramp.com) — Free mock interviews
- [interviewing.io](https://interviewing.io) — Paid mock interviews (worth it in Month 4+)
- [highscalability.com](http://highscalability.com) — Real architecture case studies

### YouTube Channels
- **NeetCode** — DSA patterns explained
- **TechDummies (Narendra L)** — System design deep dives
- **ByteByteGo** — System design animations (Alex Xu's channel)
- **Gaurav Sen** — System design fundamentals

### Certifications
- AWS Cloud Practitioner — 2 weeks prep, sit the exam in Month 3
- (Optional) AWS Solutions Architect Associate — Month 5+

---

## Final Note

You have more leverage than you think. Five years of Android engineering means you already understand performance optimization, concurrency, architecture patterns, and shipping production software to millions of users. That's not mobile experience — that's engineering experience.

The gap is not your ability. The gap is your preparation for the interview format and your presentation to a global market. Both are fixable in 6 months with consistent execution.

**The single most important thing:** Start. Today. Open NeetCode, do 3 problems, and push this file to your GitHub. Momentum matters more than perfection.

---

*Last updated: 2025 · Built for serious, consistent execution*