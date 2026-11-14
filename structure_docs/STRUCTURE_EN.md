# 🏛️ Architecture and Structure of "BlindStory"

---

## 1. The Big Picture

The **BlindStory** application is a multiplayer creative word party game. The codebase is organized into three core pillars: **Visual UI** (the screens the user sees), **Game Logic** (managing turns, timers, and locales), and **Data Layer** (local sessions and Supabase backend database).

### 🏠 Real-Life Analogy: The Theater
To understand why code is split into components rather than written in one giant file, imagine a **theater**:
- **The Stage & Scenes (`res/layout` + `Activities`):** What the audience sees—the actors, lighting, and stage layout.
- **Stage Director (`AuthManager` + `LocaleHelper`):** Works behind the scenes to ensure actors speak the right language and authenticated members are admitted.
- **Ticket Office & Archives (`Supabase`):** Stores registered member accounts and keeps records of all completed story plays.

---

## 2. Overview of Folders and Files

In this Android project, code is structured into source packages and resource directories:

```
BlindStory/
├── app/src/main/
│   ├── java/zeev/fraiman/blindstory/    <-- Java Source Code (Logic)
│   │   ├── StartActivity.java           <-- Splash Screen
│   │   ├── LanguageActivity.java        <-- Language Selector
│   │   ├── AuthActivity.java            <-- Login & Registration
│   │   ├── MainActivity.java            <-- Main Dashboard
│   │   ├── CreateRoomActivity.java      <-- Room Constructor
│   │   ├── LobbyActivity.java           <-- Waiting Lobby
│   │   ├── GameActivity.java            <-- Active Turn Screen
│   │   ├── FinalStoryActivity.java      <-- Final Story & AI View
│   │   ├── AuthManager.java             <-- Auth Session Manager
│   │   ├── LocaleHelper.java            <-- Language & Locale Helper
│   │   └── SupabaseConfig.java          <-- Supabase Server Credentials
│   └── res/                             <-- Visual Resources
│       ├── layout/                      <-- XML Layout Files
│       └── values/                      <-- Text Strings, Colors, Themes
```

### Key Terms Explained:
- **Activity:** An Android component responsible for a single screen user interface.
- **XML Layout:** A layout definition file specifying buttons, text fields, and visual styles.
- **SharedPreferences:** A lightweight key-value local storage mechanism on the phone.
- **Supabase Backend:** A remote cloud PostgreSQL database storing user profiles and game rooms.

---

## 3. Component Relationships

The diagram below illustrates the flow of control and data between app components:

```
[StartActivity] (4s Timer)
      │
      ▼
[LanguageActivity] (Select Language + Save via LocaleHelper)
      │
      ▼
[AuthActivity] (Login/Register + Save via AuthManager)
      │
      ▼
[MainActivity] (Dashboard) ───(Click "Create Room")───> [CreateRoomActivity]
      │                                                     │
      │                                           (Generate Room Code)
      ▼                                                     ▼
[LobbyActivity] <───────────────────────────────────────────┘
      │
(At least 3 players -> Click "Start Game")
      ▼
[GameActivity] (Submit phrases -> Final round reached)
      │
      ▼
[FinalStoryActivity] (Read full story + AI analysis)
```

---

## 4. Concrete Example: Room Creation to Game Start

Here is a step-by-step trace of what happens when a user creates a room:

1. **Step 1 — Dashboard (`MainActivity.java`):**
   User taps `btn_create_room`, triggering navigation:
   ```java
   btnCreateRoom.setOnClickListener(v -> {
       Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
       startActivity(intent);
   });
   ```

2. **Step 2 — Room Configuration (`CreateRoomActivity.java`):**
   User inputs room parameters and submits form via `submitCreateRoom()`:
   ```java
   String roomCode = generateRandomRoomCode(); // e.g. "KOT123"
   Intent intent = new Intent(CreateRoomActivity.this, LobbyActivity.class);
   intent.putExtra(EXTRA_ROOM_NAME, name);
   intent.putExtra(EXTRA_ROOM_CODE, roomCode);
   startActivity(intent);
   ```

3. **Step 3 — Waiting Lobby (`LobbyActivity.java`):**
   The lobby displays code `KOT123` and keeps "Start Game" disabled until 3 players are present. Once 3+ players join, host invokes `startGame()`:
   ```java
   Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
   startActivity(intent);
   ```

4. **Step 4 — Active Game Turn (`GameActivity.java`):**
   The game loads, showing only the last line, allowing the player to input and submit their phrase!

---

## 📖 Short Glossary

1. **Activity:** A single screen component in Android.
2. **Intent:** A messaging object used to request an action from another component.
3. **Locale:** User's language and regional preferences setting.
4. **SharedPreferences:** Fast key-value local storage on Android devices.
5. **Backend:** Remote server infrastructure running on the cloud.
6. **CountDownTimer:** Android class for non-blocking countdown timers.
