# 📱 Android Application Documentation — "BlindStory"

---

### 🧾 General Information
- **Project Name:** BlindStory ("Слепой рассказ" / "The Blind Story")
- **Author(s):** Zeev Fraiman
- **Date:** 2026
- **Language:** Java (with Gradle Kotlin DSL)
- **IDE:** Android Studio (2026.2.2)
- **Android Version:** `minSdk: 28` (Android 9.0) / `targetSdk: 37` (Android 17)

---

### 🎯 Project Goal
- **Problem Solved:** BlindStory is a creative multiplayer party game based on the classic "blind story" mechanics. It solves the problem of boredom in social gatherings or remote group chats by enabling players to write funny and unpredictable stories together, where each player can see **only the last phrase** written before their turn.
- **Why It Matters:** Fosters creativity, social bonding, humor, and teamwork with unexpected story outcomes.
- **Target Audience:** Friends, families, gaming groups, and young adults looking for a fast, fun social word game on mobile.

---

### 📌 Application Requirements

**Functional Requirements:**
1. **Splash & Language Selection:**
   - Auto-proceeds from Splash screen in 4 seconds or on button tap.
   - Interface language selection screen (English, Russian, Hebrew, Lithuanian) with 5-second auto-proceed timer, `SharedPreferences` persistence, and instant Locale application.
2. **Registration & Authentication (Auth):**
   - Sign up and login via Email/Password or Google Sign-In using Supabase Auth.
   - Built-in explanation card informing users why registration is required.
3. **Main Dashboard:**
   - Action buttons: Create Room, Join by Code (6-character code), My Stories, Profile, and Open Public Rooms list.
4. **Room Creation & Lobby:**
   - Configure room name, topic, starting phrase, rounds (1-3), turn time limit, privacy, and AI options.
   - Unique room code generation (e.g., `KOT123`), copy code/share link buttons, real-time player list with "Host" badge.
   - Start Game button remains locked until at least 3 players join.
5. **Turn Screen (Game Process):**
   - Displays **only the previous phrase** ("sheet of paper" principle), 60-second turn timer (turns red during last 10 seconds), 180-character limit counter.
   - "Propose Ending" voting button (majority or unanimous) and "Skip Turn" option.
6. **Final Screen & AI Analysis:**
   - Displays full completed story with author names listed under each phrase.
   - Interactive emoji reaction buttons (😂, 😮, 🔥, ❤️).
   - Purple AI button "What will AI say about our story?" opening AI analysis card (suggested titles, summary, funniest moment, twist, 5/5 coherence rating, moral, and disclaimer).
   - "Rematch" and "Copy Story" buttons.

**Non-Functional Requirements:**
- **Performance:** Instant UI response, fast loading, zero frame drops.
- **Usability:** Modern Material 3 UI design, full RTL support for Hebrew and Arabic, responsive layouts.
- **Reliability:** Data privacy secured via Supabase Row Level Security (RLS) preventing phrase peeking before game completion.

---

### 🧠 General Architecture
- **Chosen Approach:** Activity-Based Modular Component Architecture combined with Manager/Helper services.
- **Why Chosen:** Ensures clear separation between navigation flow, local UI state management, and backend communication.
- **Main Components:**
  - **UI Layer:** Activities and XML Layouts.
  - **Locale & Auth Helpers:** `LocaleHelper` for language management and `AuthManager` for user session management.
  - **Backend Services:** Supabase Auth, Supabase Database (PostgreSQL), and Supabase Edge Functions.

---

### 🧩 UML Diagram
```
[StartActivity] ──(4s / Click)──> [LanguageActivity] ──(5s / Click)──> [AuthActivity]
                                                                             │
                                                                       (Logged In)
                                                                             ▼
[LobbyActivity] <──(Create / Join)── [MainActivity] <────────────────────────┘
       │
 (Start Game)
       ▼
 [GameActivity] ──(Finish Story)──> [FinalStoryActivity]
```

---

### 🧩 Detailed Description of Classes

**📌 `StartActivity`**
- **Role:** Splash Screen.
- **Responsibility:** Shows app branding, runs 4-second auto-redirect timer to LanguageActivity.

**📌 `LanguageActivity`**
- **Role:** Interface Language Selector.
- **Responsibility:** Selects language from 4 options (English, Russian, Hebrew, Lithuanian), runs 5-second timer, saves choice and applies Locale.

**📌 `AuthActivity`**
- **Role:** Login & Registration Screen.
- **Responsibility:** Handles login/signup mode toggle, inputs (Email, Password, Display Name), Google Auth, and displays security explanation.

**📌 `AuthManager`**
- **Role:** Local Session Manager.
- **Responsibility:** Persists and reads logged-in user details in `SharedPreferences`.

**📌 `LocaleHelper`**
- **Role:** Locale & Language Manager.
- **Responsibility:** Updates app `Resources` configuration and applies `AppCompatDelegate.setApplicationLocales`.

**📌 `SupabaseConfig`**
- **Role:** Supabase Configuration.
- **Responsibility:** Stores `SUPABASE_URL` and `SUPABASE_ANON_KEY`.

**📌 `MainActivity`**
- **Role:** Main Dashboard.
- **Responsibility:** Houses main navigation buttons (Create Room, Join by Code, My Stories, Profile) and public rooms card.

**📌 `CreateRoomActivity`**
- **Role:** Room Builder.
- **Responsibility:** Form inputs for room name, topic, starting phrase, rounds, time, privacy, and AI toggles; generates room code.

**📌 `LobbyActivity`**
- **Role:** Room Lobby.
- **Responsibility:** Displays room code, share/copy actions, player list, bot simulation button, and enforces 3-player start requirement.

**📌 `GameActivity`**
- **Role:** Active Game Turn Screen.
- **Responsibility:** Displays only the previous phrase, 60s timer, character limit (180 chars), phrase submission, voting, and turn skipping.

**📌 `FinalStoryActivity`**
- **Role:** Final Story View & AI Analysis.
- **Responsibility:** Full story rendering with author credits, emoji reactions, AI analysis display, rematch, and story copying.

---

### 🔄 Application Workflow
1. **Launch:** `StartActivity` ──(4s)──> `LanguageActivity`.
2. **Language Selection:** User picks language (or waits 5s) ──> Locale applied ──> `AuthActivity`.
3. **Authentication:** User logs in / registers ──> `MainActivity`.
4. **Room Creation:** User clicks "Create Room" ──> `CreateRoomActivity` ──> Configures parameters ──> `LobbyActivity`.
5. **Lobby Waiting:** Players join (at least 3) ──> Host clicks "Start Game" ──> `GameActivity`.
6. **Game Turns:** Each player sees only the previous phrase, submits response ──> Turns advance.
7. **Completion:** Game completes ──> `FinalStoryActivity` ──> Full story reading, reactions, and AI analysis!

---

### 🎨 UI/UX Analysis
- **Simplicity:** Each screen focuses on one primary action (Lobby: Start, Game: Submit, Final: Read).
- **Color Coding:**
  - **Green (`#2E7D32`):** Primary game actions (Start Game, Submit, Create Room).
  - **Purple (`#7B1FA2`):** AI Commentator features.
  - **Red (`#D32F2F`):** Warnings and last 10 seconds of timer.
- **Accessibility:** Full LTR and RTL layout support.

---

### ⚙️ Threads & Timers
- **CountDownTimer:** Used in `StartActivity`, `LanguageActivity`, and `GameActivity` for smooth non-blocking UI countdowns.
- **Handlers:** Used for smooth post-submission transitions.
- **ANR Prevention:** Timers canceled in `onDestroy()` to prevent memory leaks.

---

### 💾 Data Management
- **Database:** Supabase (PostgreSQL) with 7 tables (`users`, `rooms`, `room_members`, `phrases`, `votes`, `final_stories`, `ai_analyses`).
- **Local Storage:** `SharedPreferences` for language and auth session.

---

### 🌐 Networking
- **Supabase REST & RPC API:** Secure database queries and server function calls.
- **Permissions:** `INTERNET` and `ACCESS_NETWORK_STATE` declared in `AndroidManifest.xml`.

---

### 🔐 Security
- **Row Level Security (RLS):** Direct `SELECT` queries on `phrases` disabled at database level.
- **Server Functions (`get_current_turn_phrase`):** Returns strictly the latest single phrase to prevent peeking.

---

### 🧪 Testing
- **Compilation:** Clean Gradle `:app:assembleDebug` builds.
- **Static Analysis:** `analyze_file` inspections.
- **UI Flow Validation:** Verified navigation, timers, localization, and user interactions.

---

### 🐞 Error Handling
- Input validation on form fields with localized error messages.
- Button enablement logic based on string length (2 to 180 chars).

---

### ⚡ Performance
- Lightweight views using Material 3 components.
- Instant UI responsiveness.

---

### 🚀 Future Enhancements
1. Push notifications for turn reminders.
2. Dark Theme support.
3. Exporting completed stories to stylized images / PDF files.
