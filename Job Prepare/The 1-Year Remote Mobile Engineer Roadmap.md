# The 1-Year Remote Mobile Engineer Roadmap
### From 5+ Years Android Engineer → High-Paying Remote Android/iOS Role
**Budget: 1–1.5 hrs/day, full-time job, realistic pacing**

---

## 0. How This Roadmap Is Built (Read This First)

**Your unfair advantage:** You already have 5+ years of Android production experience. You do **not** need another "learn Kotlin from scratch" plan. What separates you from a mid-level engineer today is: (1) DSA fluency for interviews, (2) System Design communication, (3) breadth into iOS for cross-platform roles, (4) a portfolio that *proves* seniority, and (5) interview mechanics (behavioral + remote-work signaling).

**Time budget reality check:** 1–1.5 hrs/day × 365 days ≈ **420–500 hours/year**. That's enough to become interview-ready for Senior/Staff mobile roles at remote-first companies — but only if every hour is spent on high-leverage work. This plan is sequenced so early months build the foundation that later months compound on.

**Allocation philosophy across the year (~450 hrs):**
| Bucket | % | Hours |
|---|---|---|
| DSA | 25% | ~110 |
| Android deep/advanced topics | 20% | ~90 |
| iOS (Swift/SwiftUI/UIKit) | 20% | ~90 |
| System Design (mobile-focused) | 12% | ~55 |
| Projects/Portfolio/GitHub | 12% | ~55 |
| Behavioral + Resume + Applications | 6% | ~25 |
| Mock Interviews (concentrated late) | 5% | ~25 |

**Golden rules:**
1. Never skip Sunday revision — spaced repetition is what makes 1 hr/day work over 1 hr/day forgetting.
2. Build in public. Every project ships to GitHub with a real README, not a code dump.
3. Track everything (trackers are in Section 17). What isn't tracked doesn't get finished.
4. From Month 9 onward, you are *applying* while still preparing — don't wait for "ready."

---

## 1. Weekly Timetable Template (repeats all year, content changes by month)

| Day | Time | Focus |
|---|---|---|
| Mon | 1 hr | DSA (1 problem + concept notes) |
| Tue | 1 hr | Android/iOS deep-dive topic (reading + small code) |
| Wed | 1 hr | DSA (1 problem, different pattern) |
| Thu | 1 hr | Android/iOS deep-dive topic (continued) or System Design reading |
| Fri | 1 hr | Project work (portfolio app increments) |
| Sat | 1.5 hr | Project work (60 min) + Open source / GitHub polish (30 min) |
| Sun | 1.5 hr | Revision (40 min: flashcards/notes) + System Design (30 min) + Weekly retro/planning (20 min) |

**Weekly hour total:** ~9 hrs. Over 52 weeks ≈ 468 hrs — matches your annual budget.

From **Month 9**, Friday shifts to mock interviews / applications instead of new project work (projects should be largely done by then).

---

## 2. Daily Schedule Options (pick what fits your day)

- **Early bird:** 45 min before work + 30–45 min after dinner.
- **Lunch-break method:** 30 min at lunch (reading/notes, no coding needed) + 45–60 min evening (hands-on).
- **Commute hack:** If you commute, use audio (podcasts/articles read aloud) for passive CS/System Design reinforcement — doesn't count against your coding hour but accelerates revision.
- **Non-negotiable:** Even on your busiest day, do a 15-minute "floor" — one DSA problem read + approach sketch, or 10 flashcards. Never a zero day.

---

## 3. 12-Month Phase Roadmap

### **Month 1 — Foundations Reset + DSA Kickoff**
**Theme:** Rebuild fundamentals precisely (not relearn), start DSA muscle memory, set up all trackers.

- **Learning goals:** Get comfortable with a DSA problem-solving routine; audit current Android knowledge gaps; set up GitHub/portfolio skeleton.
- **Topics:** Big-O complexity analysis, Arrays, Strings, HashMap basics. Android: Kotlin advanced features you may under-use (sealed classes, inline/value classes, delegation, coroutine exception handling deep-dive).
- **Weekly targets:** 8–10 DSA problems (easy), read 2 Android architecture articles, set up GitHub profile README + pin repos.
- **Projects:** Start **Project 1: Modular News/Reader App** (see Section 9) — scaffold only (module structure, DI setup).
- **Revision:** Create your flashcard system (Anki or Notion) — start with 20 core DSA complexity + Kotlin concept cards.
- **Mock interviews:** None yet — self-assessment quiz on Android fundamentals instead.
- **Portfolio:** GitHub profile polish, LinkedIn headline updated to reflect "Android/iOS Engineer."
- **Interview prep:** None yet.
- **Milestone check:** Can solve easy array/string/hashmap problems in <20 min consistently.

### **Month 2 — DSA Patterns I + Android Architecture Depth**
- **Topics:** Two Pointer, Sliding Window, Binary Search. Android: MVVM → Clean Architecture migration patterns, UseCases, Repository pattern done "properly" (not tutorial-level).
- **Weekly targets:** 8 DSA problems, refactor one existing personal project layer to Clean Architecture.
- **Projects:** Project 1 — implement networking + Room caching layer (offline-first basics).
- **Revision:** Sunday spaced-repetition review of Month 1 cards + new cards.
- **Mock interviews:** None.
- **Portfolio:** Write first technical blog post: "Migrating MVVM to Clean Architecture" (Medium/Hashnode/personal site).
- **Milestone:** Comfortable explaining Clean Architecture layers out loud in 2 minutes.

### **Month 3 — DSA Patterns II + Jetpack Compose Mastery**
- **Topics:** Stack, Queue, Linked List, Recursion basics. Android: Compose state hoisting, recomposition internals, side-effects (LaunchedEffect, DisposableEffect, SideEffect), custom layouts.
- **Weekly targets:** 8 DSA problems, build 3 custom Compose components from scratch (no library).
- **Projects:** Project 1 — full Compose UI, navigation with Compose Navigation.
- **Revision:** Cards + redo 3 hardest problems from Month 2 without looking.
- **Mock interviews:** First self-mock — record yourself explaining a project architecture (10 min, no audience needed yet).
- **Milestone:** Can explain recomposition + stability from memory; Project 1 UI functionally complete.

### **Month 4 — DSA Trees + Android Performance & Testing**
- **Topics:** Trees, BST, Tree traversals. Android: Unit testing (JUnit5, Turbine for Flow, MockK), UI testing (Compose testing APIs), basic performance profiling (Android Studio Profiler, StrictMode).
- **Weekly targets:** 8 DSA problems, write unit tests for Project 1 (target 60%+ coverage on ViewModel/UseCase layers).
- **Projects:** Project 1 — testing suite complete; push to GitHub with CI (GitHub Actions running tests on PR).
- **Revision:** Sunday review; convert weak topics into mind-map format.
- **Mock interviews:** 1 peer/AI mock focused on "walk me through a project" only.
- **Milestone:** Project 1 has CI badge, >60% test coverage, README with architecture diagram.

### **Month 5 — DSA Graphs I + iOS Swift Fundamentals Begin**
- **Topics:** Graph representations, BFS/DFS. iOS: Swift syntax deep-dive (optionals, closures, protocols, generics, value vs reference types, ARC/memory management).
- **Weekly targets:** 6 DSA problems (slightly reduced to make room for Swift), daily 20-min Swift Playgrounds practice (separate from the 1hr — or fold into Tue/Thu slots).
- **Projects:** Start **Project 2: iOS mini app** — a simple SwiftUI to-do or weather app (UIKit optional exposure only).
- **Revision:** DSA cards + new Swift syntax cards.
- **Mock interviews:** None (heavy learning month).
- **Milestone:** Comfortable writing idiomatic Swift (protocols, extensions, closures) without constant reference lookup.

### **Month 6 — DSA Graphs II + SwiftUI & iOS Architecture**
- **Topics:** Topological sort, Union-Find, shortest path basics (Dijkstra conceptually). iOS: SwiftUI state management (@State, @Binding, @ObservedObject, @StateObject, @EnvironmentObject), MVVM in SwiftUI, Combine basics, async/await in Swift.
- **Weekly targets:** 6 DSA problems, complete Project 2 core features.
- **Projects:** Project 2 — networking (URLSession + async/await), local persistence (SwiftData or Core Data basics).
- **Revision:** Mid-year checkpoint — full review of Months 1–6 flashcard deck (this is a longer Sunday session, budget 1 full hour).
- **Mock interviews:** 1 mock — Android architecture deep dive (peer or AI).
- **Portfolio:** Update resume draft v1 — add Project 1 + Project 2, quantify impact.
- **Milestone (HALF-YEAR CHECKPOINT):** ✅ 90+ DSA problems solved, ✅ 2 portfolio projects live on GitHub, ✅ can build a basic iOS app unaided, ✅ resume draft exists.

### **Month 7 — DSA Heaps/Backtracking + Android Advanced Systems**
- **Topics:** Heap/Priority Queue, Backtracking, Bit Manipulation. Android: WorkManager, Foreground Services, Paging 3, Notifications, Deep Links/App Links.
- **Weekly targets:** 6 DSA problems, implement Paging 3 + WorkManager in Project 1 or a new feature branch.
- **Projects:** Enhance Project 1 with offline sync + background work (this becomes your "offline-first" showcase project).
- **Revision:** Cards + redo 2 backtracking problems cold.
- **Mock interviews:** 1 mock — DSA live coding (peer/platform).
- **Milestone:** Project 1 demonstrates offline-first + background sync — flagship project status.

### **Month 8 — DSA Dynamic Programming I + iOS Concurrency & Testing**
- **Topics:** DP fundamentals (knapsack, LIS, LCS patterns). iOS: Structured concurrency (async/await, actors, Task), XCTest, dependency injection patterns in Swift.
- **Weekly targets:** 6 DSA problems (DP is dense — quality over quantity), add tests to Project 2.
- **Projects:** Project 2 — add unit tests, refactor to protocol-oriented DI.
- **Revision:** DP problems get extra spaced repetition (redo weekly, not just Sunday).
- **Mock interviews:** 1 mock — iOS technical Q&A.
- **Portfolio:** Publish blog post #2: "What I learned going from Android-only to cross-platform."
- **Milestone:** Can identify DP problem patterns within 5 minutes of reading.

### **Month 9 — System Design Focus + Start Applying**
- **Topics:** Mobile System Design fundamentals (see Section 12): offline-first, caching, pagination, sync strategies, API design for mobile. Light DSA maintenance only.
- **Weekly targets:** 4 DSA problems (maintenance mode), 2 system design case studies written up (e.g., "Design Instagram feed," "Design a chat app").
- **Projects:** Start **Project 3: System-design-showcase app** (e.g., offline-first chat or feed app demonstrating pagination + caching + sync).
- **Revision:** Full-deck spaced repetition pass.
- **Mock interviews:** 2 mocks — 1 System Design, 1 DSA.
- **Portfolio:** Resume v2 — tailored for remote roles, ATS-checked.
- **Job applications:** ✅ **Begin applying** — 5–10 applications this month to mid-tier targets (not dream companies yet) to calibrate against real interviews.
- **Milestone:** First real interview loop experienced (even if not your dream company).

### **Month 10 — DP II / Graphs Advanced + Mobile System Design Depth**
- **Topics:** Advanced DP, MST, advanced graph problems (as needed based on target companies). System Design: push notifications architecture, media streaming basics, analytics pipelines, SDK design principles.
- **Weekly targets:** 5 DSA problems, 2 more system design write-ups, apply to 8–10 companies.
- **Projects:** Project 3 — polish, write full system design README (diagrams + trade-off discussion).
- **Revision:** Continue full-deck reviews; retire mastered cards, add new weak-area cards.
- **Mock interviews:** 2 mocks (1 System Design, 1 behavioral).
- **Milestone:** Can whiteboard (verbally/on virtual tool) a mobile system design end-to-end in 30–40 min.

### **Month 11 — Interview Sprint: All Tracks Simultaneously**
- **Topics:** Weak-area triage — spend this month exclusively on your personal weak spots identified from Months 9–10 interviews/mocks.
- **Weekly targets:** 5 DSA problems (mixed review), 1 system design mock, 1 behavioral mock, apply to 10+ companies.
- **Projects:** Final portfolio polish — all 3 flagship projects have complete READMEs, demo GIFs/videos, live APK/TestFlight links if possible.
- **Revision:** Daily 10-min flashcard review (increase frequency, shorten session).
- **Mock interviews:** 3–4 mocks across DSA/System Design/Behavioral.
- **Portfolio:** Personal website live (if not already) linking all projects, blog, resume.
- **Milestone:** Comfortable and calibrated in every interview format; multiple loops in progress.

### **Month 12 — Final Push, Negotiation Prep, Close Offers**
- **Topics:** Company-specific prep for active loops (research each company's tech stack, values, recent engineering blog posts). Salary negotiation research (levels.fyi, Glassdoor, remote-salary databases).
- **Weekly targets:** Light DSA maintenance (3/week), heavy mock interview volume, application follow-ups.
- **Projects:** Only bug fixes / final polish — no new feature work.
- **Revision:** Final full-deck pass in week 1; taper after.
- **Mock interviews:** As many as scheduling allows — prioritize real company loops.
- **Portfolio/Resume:** Final resume iteration per role/company.
- **Milestone:** Offer(s) in hand or in final rounds; negotiation strategy ready (see Section 17 for the final 30-day plan, which overlaps this month).

---

## 4. Android Mastery Checklist (What "Senior/Staff Mobile" Actually Requires)

| Category | Topics | Priority | Why It Matters |
|---|---|---|---|
| **Architecture** | MVVM, MVI, Clean Architecture, Unidirectional Data Flow | Critical | Every senior interview probes architecture reasoning, not just syntax |
| **DI** | Hilt (you know), Koin basics, manual DI understanding | High | Interviewers test if you understand DI *concepts*, not just annotations |
| **UI** | Compose (deep), XML/View system (legacy maintenance), Custom Views, Animations (Compose + View-based) | Critical | Compose is now default; XML fluency still needed for legacy codebases |
| **Concurrency** | Coroutines (structured concurrency, supervisorJob, exception handling), Flow/StateFlow/SharedFlow, WorkManager | Critical | #1 source of production bugs; heavily tested |
| **Data** | Room, Paging 3, Offline-first patterns, DataStore (replacing SharedPreferences) | Critical | Almost every real app needs offline/caching strategy |
| **Networking** | Retrofit, OkHttp interceptors, GraphQL basics (Apollo), gRPC awareness | High | Companies like Shopify/Stripe expect API design fluency |
| **Modularization** | Feature modules, Dynamic Feature Delivery, Gradle build optimization, multi-module DI | High (Senior+) | Large-scale codebases are modular; this is a strong signal of seniority |
| **Performance** | Baseline Profiles, Macrobenchmark, memory leak detection (LeakCanary), startup optimization, ANR debugging | High | Staff-level differentiator |
| **Security** | EncryptedSharedPreferences/DataStore, Keystore, certificate pinning, ProGuard/R8, obfuscation | Medium-High | Fintech (Revolut, Coinbase, Stripe) weighs this heavily |
| **Platform features** | Notifications, Deep Links/App Links, Foreground Services, Camera/Media APIs, Bluetooth basics, Location/Maps | Medium | Breadth signal, not always deeply tested but comes up |
| **Accessibility & Localization** | TalkBack support, content descriptions, RTL layouts, string resources/i18n | Medium | Remote/global companies (Booking.com, Revolut) explicitly care |
| **Testing** | JUnit, MockK, Turbine, Compose UI testing, Espresso (legacy), Robolectric | Critical | Senior candidates are expected to design testable code, not just write tests |
| **CI/CD & Release** | Gradle deep understanding, GitHub Actions/Bitrise/Fastlane, Play Store release process, staged rollouts | High | Ownership signal — shows you ship, not just code |
| **Payments/Monetization** | Google Play Billing basics | Low-Medium | Only critical if targeting fintech/commerce companies specifically |
| **SDK Development** | Public API design, versioning, backward compatibility | Medium (Staff) | Relevant for companies with SDK products (Stripe, analytics platforms) |

---

## 5. iOS Mastery Checklist

| Category | Topics | Priority |
|---|---|---|
| **Language** | Swift (protocols, generics, closures, value/reference semantics, ARC), Optionals deep-dive | Critical |
| **UI** | SwiftUI (state management, view lifecycle, animations), UIKit fundamentals (enough to read/maintain legacy code) | Critical |
| **Concurrency** | async/await, Actors, Task groups, Combine (still relevant in many codebases) | Critical |
| **Architecture** | MVVM in SwiftUI, Coordinator pattern (UIKit), Clean Architecture translation | High |
| **Data** | Core Data, SwiftData (newer, know both), Keychain for secure storage | High |
| **Networking** | URLSession, Codable, async networking layers | Critical |
| **DI** | Protocol-oriented DI, lightweight frameworks (Factory, Swinject) awareness | Medium-High |
| **Testing** | XCTest, XCUITest, snapshot testing awareness | High |
| **Navigation** | NavigationStack (SwiftUI), UINavigationController (UIKit) | Medium |
| **Notifications & Background** | Push notifications (APNs basics), Background Tasks framework | Medium |
| **Performance** | Instruments (Time Profiler, Allocations), memory management debugging | Medium-High |
| **Accessibility** | VoiceOver support, Dynamic Type | Medium |
| **Publishing** | App Store Connect flow, TestFlight, provisioning profiles/certificates basics | Medium |

**Realistic goal for iOS given your timeline:** You are **not** trying to become an iOS-first senior engineer in one year alongside a full-time Android job — that's not credible or necessary. Your realistic target is **"Android-primary, iOS-capable"**: strong enough to pass a mid-level iOS technical screen, build and ship a real SwiftUI app, and credibly claim cross-platform capability for roles that value it (many remote/startup roles want "mobile generalist," not two separate senior specialists).

---

## 6. Cross-Platform Frameworks — Should You Learn Them?

| Framework | Learn? | How Much | When | Why |
|---|---|---|---|---|
| **Kotlin Multiplatform (KMP)** | **Yes** | Moderate (~15–20 hrs total, folded into Android months 7–10) | After Month 6, once Android+iOS basics solid | Strategically the best bet: it's Kotlin (your strength), growing fast, and companies like **Block/Square** actively hired KMP engineers; JetBrains + Google backing it heavily as of 2025-26. Strong resume differentiator for exactly the companies on your list. |
| **Compose Multiplatform** | **Yes, lightly** | Small (~5–8 hrs) | Same window as KMP | Natural extension of KMP; worth a demo project but not a deep specialization |
| **Flutter** | **No (skip)** | — | — | Different language (Dart) with no leverage from your existing skills; splits focus without clear ROI given your target companies use native or KMP, not Flutter |
| **React Native** | **No (skip)** | — | — | Same reasoning — JS ecosystem, no synergy with your Kotlin/Swift investment; only reconsider if you pivot toward specific RN-only job postings |

**Bottom line:** KMP is the only cross-platform investment worth your limited hours. It reinforces Kotlin (no context switch), is genuinely used by companies on your target list, and turns your "Android+iOS" story into "I can share business logic across platforms" — a strong senior/staff-level narrative.

---

## 7. Computer Science Fundamentals Checklist

| Area | Topics | Interview Relevance |
|---|---|---|
| **Networking** | HTTP/HTTPS, TLS handshake, REST principles, WebSockets, gRPC basics, DNS, CDN | High — mobile engineers are asked about API design and network reliability constantly |
| **OS Concepts** | Processes vs Threads, Memory management (stack/heap), Scheduling basics, Deadlocks | Medium-High — especially for concurrency-heavy questions |
| **Databases** | SQL fundamentals (joins, indexing), NoSQL basics (when to use), transactions, ACID | Medium — Room/Core Data usage plus backend collaboration |
| **Security** | Authentication vs Authorization, OAuth2/OIDC flow, JWT structure/risks, encryption basics (symmetric vs asymmetric), certificate pinning | High — fintech/remote-first companies test this explicitly |
| **Systems basics** | Caching strategies (LRU, cache invalidation), Compression, Serialization (JSON/Protobuf), Storage systems | Medium-High — directly tied to mobile system design |
| **DevOps basics** | Docker fundamentals (conceptual, not deep), Linux/terminal fluency, Git internals (rebase vs merge, cherry-pick) | Medium — remote teams expect self-sufficiency |
| **Cloud basics** | Firebase (you know), AWS/GCP basic literacy (S3, Lambda concepts) | Low-Medium — nice to have, rarely deeply tested for mobile roles |

**Study approach:** Don't take a full CS course — you have production experience already touching most of this. Instead, do targeted reading (1 topic per week during Months 1, 5, 9 "connector" weeks) and make sure you can *explain* each concept in interview language, not just use it.

---

## 8. Complete DSA Roadmap

**Target volume:** ~180–200 problems total over the year (heavier front-loaded, maintenance-mode later) — this is realistic and sufficient at senior level, where DSA is a *filter*, not the main event (unlike new-grad interviews).

| Topic | Why It Matters | Frequency | Difficulty | Problems | Revision |
|---|---|---|---|---|---|
| Complexity Analysis | Foundation for everything | Every interview | Easy | N/A (concept) | Ongoing |
| Arrays & Strings | Most common warm-up questions | Very High | Easy-Medium | 20 | Weekly |
| HashMap/HashSet | Optimizes majority of problems | Very High | Easy-Medium | 15 | Weekly |
| Two Pointer | Common in array/string optimization | High | Easy-Medium | 12 | Bi-weekly |
| Sliding Window | Substring/subarray problems | High | Medium | 12 | Bi-weekly |
| Binary Search | Search-space problems, very testable | High | Medium | 12 | Bi-weekly |
| Stack & Queue | Parsing, monotonic stack problems | Medium-High | Easy-Medium | 12 | Bi-weekly |
| Linked List | Classic but less common at senior level | Medium | Easy-Medium | 10 | Monthly |
| Recursion | Foundation for trees/backtracking/DP | High | Medium | 8 | Ongoing |
| Trees & BST | Very common, tests recursive thinking | Very High | Medium | 20 | Bi-weekly |
| Trie | Autocomplete-style problems | Low-Medium | Medium | 5 | Monthly |
| Heap/Priority Queue | Top-K, scheduling problems | Medium-High | Medium | 10 | Monthly |
| Graphs (BFS/DFS) | System-design-adjacent, common at senior level | High | Medium-Hard | 15 | Bi-weekly |
| Topological Sort | Dependency resolution problems | Medium | Medium | 5 | Monthly |
| Union-Find | Connectivity problems | Low-Medium | Medium | 5 | Monthly |
| Shortest Path (Dijkstra) | Occasionally asked, good to know conceptually | Low | Medium-Hard | 3 | Once, then concept-only |
| MST | Rarely asked directly for mobile roles | Low | Medium-Hard | 2 | Concept-only |
| Segment Tree | Rare for mobile interviews — deprioritize | Very Low | Hard | 0–2 (optional) | Skip unless targeting hardcore algo-heavy companies |
| Backtracking | Combinatorial problems | Medium-High | Medium-Hard | 10 | Monthly |
| Bit Manipulation | Quick wins, sometimes asked | Medium | Easy-Medium | 8 | Monthly |
| Dynamic Programming | Highest-difficulty filter at senior interviews | High | Medium-Hard | 20 | Weekly (hardest topic — needs most repetition) |
| Greedy | Often paired with sorting | Medium | Medium | 8 | Monthly |

**Revision strategy for DSA specifically:**
- **Weekly:** Redo 1 problem from 2 weeks ago cold (no notes).
- **Monthly:** Full pattern review — for each pattern, can you state the "recognition trigger" (e.g., "sorted array + target sum → two pointer") in one sentence?
- **Quarterly:** Timed mixed-problem set (5 problems, 90 min, no topic hints) to simulate real pressure.

**Where to practice:** LeetCode (primary), NeetCode 150 as your core problem list (it's curated exactly to this kind of pattern-based prep), Blind 75 as a faster subset if time-crunched in any given month.

---

## 9. Portfolio Projects (8–10, Each Teaching Different Concepts)

| # | Project | Difficulty | Key Features | Architecture | Backend | Testing | Deployment | Interview Value |
|---|---|---|---|---|---|---|---|---|
| 1 | **Offline-First News/Reader App** (flagship) | Medium-High | Article feed, offline caching, background sync, search, bookmarks | Clean Architecture + MVVM, multi-module | Public REST API (NewsAPI or similar) | Unit + UI tests, CI | Play Store internal track or APK release | Demonstrates offline-first, modularization, testing — your #1 talking point |
| 2 | **SwiftUI Habit/Task Tracker** (iOS) | Medium | CRUD, local persistence, notifications, widgets | MVVM, SwiftData | None (local-only) or simple Firebase sync | XCTest basics | TestFlight | Proves genuine iOS capability, not just tutorial-following |
| 3 | **System-Design Showcase: Chat/Feed App** | High | Pagination, real-time updates (WebSocket or Firebase), caching, optimistic UI | Clean Architecture, KMP shared business logic (Android+iOS) | Firebase or custom backend | Integration tests | APK + TestFlight | Directly maps to "design Instagram/WhatsApp" interview questions — huge leverage |
| 4 | **KMP Demo: Shared Business Logic App** | Medium | Same core logic (e.g., currency converter or expense tracker) running natively on Android + iOS | KMP + Compose Multiplatform for shared UI where practical | Public API (exchange rates) | Shared module unit tests | Both platforms | Directly shows the exact skill Block/Square-type companies want |
| 5 | **Open Source Contribution(s)** | Varies | N/A — contributions to existing repos | N/A | N/A | Follows project standards | N/A | Signals collaboration ability, code review comfort — huge for remote roles |
| 6 | **SDK/Library Project** (mini) | Medium | Publish a small Kotlin library (e.g., a networking wrapper or analytics helper) to Maven Central/JitPack | Public API design focus | N/A | Unit tests, documentation | Published package | Directly demonstrates "SDK design" — a named requirement in your Android checklist |
| 7 | **Performance Case Study** | Medium | Take Project 1, deliberately profile it, document before/after optimization (startup time, memory, APK size) | N/A (analysis project) | N/A | Macrobenchmark tests | Blog post + repo | Staff-level differentiator — very few candidates show measured performance work |
| 8 | **Security-Focused Mini App** (fintech-flavored) | Medium-High | Biometric auth, encrypted local storage, certificate pinning, secure token handling | Clean Architecture | Mock/local backend | Security-focused tests | APK | Directly targets Revolut/Coinbase/Stripe-style interviews |
| 9 (optional) | **Accessibility & Localization Retrofit** | Low-Medium | Take an existing project and add full TalkBack/VoiceOver support + 2 additional locales/RTL | N/A | N/A | Accessibility scanner + manual testing | Repo update + blog post | Booking.com/global companies explicitly value this; low effort, high signal |
| 10 (optional) | **CI/CD & Release Automation Project** | Medium | Set up full pipeline: lint → test → build → staged rollout simulation using Fastlane/GitHub Actions | N/A | N/A | Pipeline validates itself | Automated releases | Shows ownership of the full SDLC, not just coding |

**Sequencing:** Projects 1–2 (Months 1–6), Project 3–4 (Months 7–10, this is where KMP pays off), Projects 5 ongoing throughout, Projects 6–10 opportunistically fit into lighter weeks (especially Month 11 polish phase).

---

## 10. GitHub Strategy

**Repository organization:**
- Pin exactly **4–6 repositories** on your profile — quality over quantity. Archive/hide old tutorial-following repos that don't reflect current skill level.
- Each flagship project = **one repo**, never dump multiple unrelated projects into one repo.
- Maintain a **`.github` profile repo** with a strong profile README: short bio, tech stack badges, links to blog/portfolio site, and a "currently learning" line (shows growth mindset).

**README format (every project repo):**
1. One-line project description + a demo GIF/screenshot at the top.
2. **Architecture diagram** (even simple boxes-and-arrows, made in Excalidraw).
3. Tech stack list.
4. "What this project demonstrates" section — explicitly call out the concepts (offline-first, modularization, etc.) so reviewers don't have to guess.
5. How to run it locally.
6. Testing approach + coverage badge if available.
7. What you'd improve with more time (shows self-awareness/seniority).

**Documentation:** Inline KDoc/DocC for public APIs in SDK-style projects; module-level README for each feature module in multi-module projects.

**Branch strategy:** `main` (protected, always green via CI) → `develop` (optional for larger projects) → feature branches (`feature/offline-sync`) → PRs even when solo, so your commit history shows realistic PR-based workflow (interviewers do look at this).

**Commit style:** Conventional Commits format (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`) — signals professional habits at a glance.

---

## 11. Open Source Contribution Plan

- **How to start:** Pick 1–2 repos in your actual stack (not random popular repos) — look for issues labeled `good first issue` or `help wanted`.
- **Good first issue sources:** GitHub's own "good first issue" search filter, plus checking issue trackers of libraries you already use.
- **Target repos (Android/Kotlin):** Look at actively maintained Kotlin/Compose libraries you personally use day-to-day (you'll get the most context here) — e.g., networking, DI, or testing utility libraries with open issue boards.
- **Target repos (iOS/Swift):** Popular Swift Package Manager libraries with labeled beginner issues — same principle: contribute to something you actually use.
- **Target repos (Compose/KMP):** JetBrains and Google sample repositories often have documentation or small-bug issues open — a great low-risk entry point.
- **How much is enough:** **3–5 meaningful merged PRs** across the year is a strong signal — don't chase quantity. One well-reviewed, non-trivial PR (a real bug fix or small feature, with back-and-forth code review) is worth more than ten typo fixes.
- **Timing:** Fold into Saturday's "Open source/GitHub polish" 30-min slot starting Month 3 onward, once your own project workflow is established.

---

## 12. Mobile System Design Roadmap

| Topic | Covers | Priority |
|---|---|---|
| **Offline-First Design** | Local-first data, conflict resolution, sync strategies (last-write-wins vs CRDT-lite thinking) | Critical |
| **Caching** | Multi-layer caching (memory/disk/network), cache invalidation strategies, TTL design | Critical |
| **Pagination** | Cursor vs offset pagination, Paging 3 internals, infinite scroll UX + data implications | High |
| **API Design** | REST resource design, versioning, error handling contracts, GraphQL trade-offs | High |
| **Push Notifications** | FCM/APNs architecture, topic vs token-based delivery, reliability considerations | Medium-High |
| **Authentication Systems** | OAuth2 flows on mobile, token refresh strategy, secure storage of credentials | High |
| **Payment Systems (conceptual)** | PCI-compliance awareness, tokenization, how mobile apps avoid touching raw card data | Medium (High if targeting fintech) |
| **Media Streaming** | Adaptive bitrate concepts, pre-fetching/buffering strategy, ExoPlayer/AVPlayer architecture | Medium |
| **Location Tracking & Maps** | Battery-efficient location updates, geofencing, map clustering at scale | Medium |
| **Analytics Pipelines** | Event batching, offline event queuing, privacy-conscious design | Medium |
| **Monitoring & Crash Reporting** | Crashlytics-style architecture, ANR detection, remote logging trade-offs | Medium |
| **CI/CD for Mobile** | Build pipelines, staged rollouts, feature flags, A/B testing infrastructure | High |
| **SDK/API Design for Mobile** | Public interface design, backward compatibility, semantic versioning | Medium-High (Staff level) |
| **Modularization at Scale** | Multi-module Gradle/Xcode project structure, build time optimization, ownership boundaries per team | High (Senior/Staff) |
| **Microservices Awareness** | Enough backend literacy to reason about API contracts and service boundaries — not deep backend expertise | Medium |

**Practice format:** For each topic, write a **1-page design doc** (problem → requirements → high-level architecture diagram → trade-offs → what you'd do differently at scale). This is exactly the format used in real system design interviews. Target: **8–10 completed design docs by Month 10.**

---

## 13. Behavioral Interview Preparation

**Framework:** STAR (Situation, Task, Action, Result) — but go further: prepare a **STAR-R** version (add "Reflection" — what you'd do differently) since senior/staff interviews probe self-awareness.

**Story bank to prepare (aim for 8–10 stories covering):**
1. A time you led a technical initiative without formal authority (ownership).
2. A conflict with a teammate/PM over technical direction (conflict resolution).
3. A time you mentored a junior engineer (mentoring).
4. A project that failed or missed goals — and what you learned (failure story — this is the most-skipped prep area; don't skip it).
5. Your proudest technical achievement, with measurable impact (success story).
6. A time you had to make an architecture decision under ambiguity (architecture discussion).
7. A time you disagreed with a code review comment — how you handled it (code review discussion).
8. A time you worked effectively async/remote across time zones (remote work behavior — critical for your target companies).
9. A time you had to explain a technical concept to a non-technical stakeholder/client (communication/client handling).
10. A time you took on something outside your comfort zone (growth mindset).

**Remote-specific behavioral signals to prepare explicitly** (interviewers at Automattic, GitLab, DuckDuckGo *specifically screen for this*): written communication samples (your GitHub PR descriptions/blog posts double as evidence), async collaboration habits, self-direction without daily standups, timezone flexibility, documentation habits.

**Practice cadence:** Draft all 10 stories in Month 9 (dedicate one Sunday). Refine and drill in Months 10–12 mocks.

---

## 14. Resume, Portfolio & Personal Brand

- **Resume:** One page (senior engineers with 5+ YOE can occasionally justify 1.5 pages, never 2+). Every bullet = **action + technical detail + measurable impact** (e.g., "Reduced app cold-start time by 35% by implementing Baseline Profiles and optimizing DI graph"). Tailor per application in Month 9+ (keep a master version, fork per role).
- **LinkedIn:** Headline states target role explicitly ("Senior Android/iOS Engineer | Kotlin, Swift, KMP | Open to Remote"). Weekly light activity (share your blog posts, comment thoughtfully on relevant posts) from Month 6 onward — recruiters do check activity.
- **GitHub:** Covered in Section 10.
- **Personal website:** Simple one-page site (even a GitHub Pages static site is fine) linking projects, blog, resume PDF, contact. Build in Month 9–10 using your own portfolio project skills (or a quick static site generator) — don't over-invest engineering time here, it's a distribution channel, not a project.
- **Technical blog:** Minimum **6 posts across the year** (roughly one every 2 months) — write about what you actually built/learned (architecture migrations, KMP experience, performance work). This is high-leverage for remote roles because it's async proof of communication ability.
- **Conference talks / community:** Optional stretch goal — a local meetup lightning talk or a recorded talk submission (e.g., to a Kotlin/Android community) in Month 10–11 if bandwidth allows. Not required, but strong signal if you have capacity.

---

## 15. Interview Preparation by Format

| Format | How to Prepare | When to Start |
|---|---|---|
| **Android Technical Interviews** | Deep-dive Q&A drills on Section 4 topics; be ready to write small snippets live (Coroutines, Compose) | Month 7 onward |
| **iOS Technical Interviews** | Same for Section 5 topics; expect lighter bar than Android given your positioning | Month 8 onward |
| **DSA/Live Coding** | Practice on a shared-doc/whiteboard tool (not just IDE) to simulate real conditions; narrate your thinking out loud | Month 9 onward (after volume is built) |
| **System Design** | Use your 8–10 design docs as rehearsed talking points; practice drawing diagrams verbally describable in under 2 minutes | Month 9 onward |
| **Behavioral** | Drill STAR-R stories until they're natural, not memorized-sounding | Month 9 onward |
| **Machine Coding** (build a mini-feature in 60–90 min) | Practice timed feature builds (e.g., "build a paginated list with search" in 90 min) using your existing project skeletons as a base | Month 10 onward |
| **Take-Home Assignments** | Keep a personal checklist/template (README format, test coverage, git history hygiene) ready so you can execute fast when one arrives | Have ready by Month 9 |
| **Pair Programming Rounds** | Practice explaining your reasoning while coding with a mock partner — this is as much a communication test as a coding one | Month 10 onward |

---

## 16. Mock Interviews

- **When to start:** Light self-mocks from Month 3 (recording yourself); real mocks (peer or platform) from Month 4; frequency ramps up sharply from Month 9.
- **How many total:** Aim for **20–25 mocks across the year**, concentrated: ~1/month (Months 4–8), ~2–4/month (Months 9–12).
- **Where to practice:** Peer-matching platforms (e.g., Pramp-style peer mock interview services), AI-based mock interview tools for volume/low-stakes reps, and — most valuable — actual early-stage interview loops with companies you're less attached to (Month 9's "calibration applications" serve this purpose intentionally).
- **Peer interviews:** Trade Android/iOS mocks with other engineers in online communities (Kotlin/Android Slack or Discord communities) — mutual benefit, free, and realistic.
- **AI interviews:** Useful for DSA/system-design volume practice and getting comfortable articulating thoughts out loud before human mocks.
- **Self-review:** After every mock (human or AI), spend 10 minutes writing down: what went well, what you fumbled, one concrete fix for next time. This closes the loop — mocks without review are just performance, not improvement.

---

## 17. Progress Tracking System

> Copy each tracker below into a spreadsheet or Notion database. Update weekly (Sunday planning session).

### 17.1 Monthly Checklist Template
- [ ] DSA problems solved this month: ___ / target
- [ ] Android/iOS topics studied: ___
- [ ] Project milestone hit: Y/N
- [ ] Blog post published (if scheduled month): Y/N
- [ ] Mock interview(s) completed: ___
- [ ] Flashcard deck reviewed at least 3x this month: Y/N
- [ ] Weekly retro done all 4 weeks: Y/N

### 17.2 Quarterly Milestones
| Quarter | Milestone |
|---|---|
| Q1 (M1–3) | DSA routine established, Project 1 scaffolded + core UI done, GitHub/LinkedIn set up |
| Q2 (M4–6) | Project 1 flagship-complete with tests+CI, iOS fundamentals solid, Project 2 shipped, resume draft v1, 90+ DSA problems |
| Q3 (M7–9) | Offline-first/advanced Android done, iOS concurrency+testing done, System Design docs started, applications begin |
| Q4 (M10–12) | KMP project shipped, all mock formats drilled, active interview loops, offer(s) closed |

### 17.3 Skill Matrix (rate yourself 1–5 monthly)
| Skill | M1 | M3 | M6 | M9 | M12 |
|---|---|---|---|---|---|
| Kotlin/Coroutines/Flow | | | | | |
| Compose | | | | | |
| Clean Architecture | | | | | |
| Testing | | | | | |
| Swift/SwiftUI | | | | | |
| KMP | | | | | |
| DSA (patterns fluency) | | | | | |
| System Design | | | | | |
| Behavioral storytelling | | | | | |

### 17.4 Interview Readiness Score (self-assess out of 100, monthly from Month 6)
`(DSA readiness × 0.3) + (Android/iOS depth × 0.3) + (System Design × 0.2) + (Behavioral × 0.2)`
Target: 70+ by Month 9 (start applying), 85+ by Month 11 (targeting dream companies).

### 17.5 Individual Trackers (create as spreadsheet tabs/tables)
- **DSA Tracker:** Problem name | Pattern | Date solved | Time taken | Solved cleanly? | Redo date
- **Android Tracker:** Topic | Studied date | Applied in project? | Confidence (1–5)
- **iOS Tracker:** Topic | Studied date | Applied in project? | Confidence (1–5)
- **System Design Tracker:** Design doc title | Date | Reviewed/mocked? | Confidence (1–5)
- **Project Tracker:** Project name | Status | Features done | Tests done | README done | Deployed?
- **GitHub Tracker:** Repo | Last updated | README quality (1–5) | CI passing?
- **Resume Tracker:** Version | Date | Tailored for (role/company) | Feedback received
- **Job Application Tracker:** Company | Role | Date applied | Stage | Next action | Notes
- **Mock Interview Tracker:** Date | Format | Interviewer/tool | Score/feedback | Fix-it action item

---

## 18. Resources (Curated, No Outdated Fluff)

**Android:**
- Official Android Developers docs + "Now in Android" sample app (Google's own reference architecture — study this codebase directly)
- *Philipp Lackner* and *Coding in Flow* YouTube channels for current Compose/Kotlin content
- Android Developers Backstage podcast

**iOS:**
- Official Apple Developer documentation + Apple's sample SwiftUI apps
- *Sean Allen* and *Paul Hudson (Hacking with Swift)* — both actively maintained, high-quality
- Swift by Sundell (blog + podcast)

**KMP:**
- JetBrains official KMP documentation and sample projects (kotlinlang.org multiplatform docs)
- Touchlab's KMP blog (team specializing in production KMP)

**DSA:**
- NeetCode (YouTube + neetcode.io problem lists — this is your primary structured resource)
- LeetCode (practice platform)

**System Design:**
- *"Designing Data-Intensive Applications"* by Martin Kleppmann (foundational, not mobile-specific but essential for the caching/sync/consistency reasoning mobile system design draws on)
- Mobile-specific: engineering blogs of your actual target companies (Airbnb, Spotify, Shopify engineering blogs) — read how they've written about real mobile architecture decisions; this is more current and relevant than any course.

**Behavioral/Career:**
- *"Cracking the PM Interview"*-style STAR frameworks (concept transfers well) — but prioritize writing your own stories over consuming more frameworks.
- levels.fyi and Glassdoor for compensation research (Month 12 negotiation prep).

**General:**
- Kotlin Weekly / Android Weekly newsletters for staying current without deep time investment.
- iOS Dev Weekly for the same purpose on iOS side.

*(Deliberately avoiding naming specific paid courses/books beyond the above — the free official documentation + curated channels above are consistently rated current and high-quality by working engineers; verify freshness of any paid course before buying, since mobile tooling changes fast.)*

---

## 19. Final 30-Day Interview Preparation Plan (runs within Month 12, or whenever your active loops concentrate)

**Week 1 — Full-spectrum refresh**
- Daily: 1 DSA problem (mixed pattern, timed) + review 1 system design doc.
- Re-read all 3 flagship project READMEs out loud — you must narrate each project's architecture in under 3 minutes without notes.
- Refresh all 10 behavioral stories — say each one out loud once.

**Week 2 — Company-specific targeting**
- For each company in active loops: read their engineering blog (last 6–12 months), understand their tech stack, prepare 2–3 thoughtful questions to ask them.
- 1 mock interview per active format (DSA, System Design, Behavioral).
- Review your resume/portfolio through the lens of "what would this specific company care about most" and adjust talking points (not the resume file itself, unless a new tailored version is warranted).

**Week 3 — High-intensity mocks**
- 3–4 mock interviews this week, mixing formats.
- Daily 20-min flashcard review (Kotlin/Swift/CS fundamentals — the "quick factual" questions that trip people up under pressure).
- Sleep and stress management explicitly on your calendar — performance under pressure depends on this as much as prep.

**Week 4 — Taper and execute**
- Reduce new learning to near-zero. Light review only (20 min/day max).
- Re-read your own project code the night before any onsite/final loop — familiarity beats cramming.
- After each interview: 10-minute self-review log (Section 17.5 Mock Tracker format) — even for real interviews, not just mocks.
- Negotiation prep: know your target range (from levels.fyi/market research done in Month 12 start), know your walk-away number, prepare to negotiate every offer (even ones you're excited about) — this is expected, not rude, especially in remote/international hiring.

---

## 20. Closing Notes

- This plan assumes consistency over intensity. Missing a day is fine; missing a *week* without adjusting the plan is not — if you fall behind, cut DSA volume before you cut project/portfolio work, since projects are your strongest differentiator at your experience level.
- Re-assess and adjust this roadmap at each quarterly milestone (Section 17.2) — treat it as a living document, not a fixed contract.
- Your biggest edge against other candidates isn't going to be DSA (many candidates grind DSA) — it's the combination of **production seniority + genuine cross-platform capability + visible, well-documented projects + strong remote-work communication signals**. Weight your energy accordingly when time is tight.