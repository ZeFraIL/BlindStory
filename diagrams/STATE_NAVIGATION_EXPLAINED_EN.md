# 🧩 State Navigation & Class Relationship Map — "BlindStory"

---

## 1. Diagram 1: State & Navigation (Mermaid stateDiagram-v2)

```mermaid
stateDiagram-v2
    [*] --> StartActivity : Launch App

    state StartActivity {
        [*] --> SplashShowing : Display App Branding
        SplashShowing --> SplashTimeout : 4 Seconds Elapse
    }

    StartActivity --> LanguageActivity : Auto-redirect 4s / Tap Button

    state LanguageActivity {
        [*] --> LanguageSelecting : Select Language (en, ru, he, lt)
        LanguageSelecting --> SaveLocale : 5s Timer / Tap Continue
    }

    LanguageActivity --> AuthActivity : User Not Logged In
    LanguageActivity --> MainActivity : User Logged In

    state AuthActivity {
        [*] --> LoginMode : Default Login Form
        LoginMode --> RegisterMode : Tap "Don't have account"
        RegisterMode --> LoginMode : Tap "Already have account"
        LoginMode --> AuthSuccess : Enter Credentials / Google Sign-In
        RegisterMode --> AuthSuccess : Enter Details & Register
    }

    AuthActivity --> MainActivity : Auth Success

    state MainActivity {
        [*] --> DashboardView : Show Main Action Buttons
        DashboardView --> JoinCodeDialog : Tap "Join by Code"
        JoinCodeDialog --> DashboardView : Enter 6-char Code / Cancel
        DashboardView --> ProfileDialog : Tap "Profile"
        ProfileDialog --> DashboardView : View Details / Cancel
    }

    MainActivity --> CreateRoomActivity : Tap "Create Room"
    MainActivity --> LobbyActivity : Code Entered in Join Dialog
    ProfileDialog --> AuthActivity : Tap "Log Out"

    state CreateRoomActivity {
        [*] --> ConfigForm : Enter Name, Topic, Rounds, Time, AI
        ConfigForm --> RoomGenerated : Tap "Create Room"
    }

    CreateRoomActivity --> LobbyActivity : Pass Room Data & Code

    state LobbyActivity {
        [*] --> WaitingForPlayers : Display Code & Player List
        WaitingForPlayers --> WaitingForPlayers : Tap "+ Add Bot"
        WaitingForPlayers --> GameReady : Players >= 3
    }

    LobbyActivity --> GameActivity : Host Taps "Start Game" (Players >= 3)

    state GameActivity {
        [*] --> MyTurnState : Display Prev Line & 180-char Box
        MyTurnState --> PhraseSubmitted : Tap "Submit Phrase"
        MyTurnState --> VotingState : Tap "Propose Ending"
        VotingState --> MyTurnState : Vote Submitted / Cancel
        MyTurnState --> SkipState : Tap "Skip Turn"
        SkipState --> MyTurnState : Confirm Skip
    }

    GameActivity --> FinalStoryActivity : All Rounds Completed / Voting Passed

    state FinalStoryActivity {
        [*] --> StoryReading : Read Full Story & Author Names
        StoryReading --> Reacting : Tap Emoji Reactions (😂, 😮, 🔥, ❤️)
        StoryReading --> AIAnalysisView : Tap "What will AI say"
    }

    FinalStoryActivity --> CreateRoomActivity : Tap "Rematch"
    FinalStoryActivity --> [*] : Exit App / Back to Home
```

---

## 2. Diagram 2: Class & Relationship Map (Mermaid flowchart TD)

```mermaid
flowchart TD
  classDef activity fill:#DBEAFE,stroke:#1E40AF,stroke-width:3px
  classDef fragment fill:#DCFCE7,stroke:#15803D,stroke-width:2px
  classDef dialog fill:#EDE9FE,stroke:#6D28D9,stroke-width:2px
  classDef model fill:#FEF9C3,stroke:#A16207,stroke-width:2px
  classDef data fill:#FCE7F3,stroke:#BE185D,stroke-width:2px
  classDef adapter fill:#FFEDD5,stroke:#C2410C,stroke-width:2px
  classDef helper fill:#F3F4F6,stroke:#6B7280,stroke-width:2px,stroke-dasharray:5 5
  classDef system fill:#E5E7EB,stroke:#9CA3AF,stroke-width:1px,stroke-dasharray:3 3

  subgraph LEGEND["Legend"]
    L_Activity["Activity<br/>[Activity]"]
    L_Data[("Data / Config<br/>[Data]")]
    L_Helper[["Helper / Manager<br/>[Helper]"]]
    class L_Activity activity
    class L_Data data
    class L_Helper helper
  end

  subgraph UI["Layer: Screens (Activities)"]
    StartActivity["StartActivity<br/>[Activity]"]
    LanguageActivity["LanguageActivity<br/>[Activity]"]
    AuthActivity["AuthActivity<br/>[Activity]"]
    MainActivity["MainActivity<br/>[Activity]"]
    CreateRoomActivity["CreateRoomActivity<br/>[Activity]"]
    LobbyActivity["LobbyActivity<br/>[Activity]"]
    GameActivity["GameActivity<br/>[Activity]"]
    FinalStoryActivity["FinalStoryActivity<br/>[Activity]"]
  end

  subgraph HELPERS["Layer: Helpers & Managers"]
    AuthManager[["AuthManager<br/>[Helper]"]]
    LocaleHelper[["LocaleHelper<br/>[Helper]"]]
  end

  subgraph DATA["Layer: Data & Config"]
    SupabaseConfig[("SupabaseConfig<br/>[Data]")]
  end

  StartActivity -->|Timer 4s / Tap| LanguageActivity
  LanguageActivity -.->|Saves and applies locale| LocaleHelper
  LanguageActivity -.->|Checks isLoggedIn| AuthManager
  LanguageActivity -->|Not logged in| AuthActivity
  LanguageActivity -->|Logged in| MainActivity

  AuthActivity -.->|saveSession| AuthManager
  AuthActivity -->|Auth success| MainActivity

  MainActivity -->|Click Create Room| CreateRoomActivity
  MainActivity -->|Code entered| LobbyActivity
  MainActivity -.->|Reads profile / clearSession| AuthManager

  CreateRoomActivity -->|Creates room and passes code| LobbyActivity

  LobbyActivity -->|Players >= 3 & Start clicked| GameActivity
  LobbyActivity -.->|Reads host name| AuthManager

  GameActivity -->|All rounds finished| FinalStoryActivity

  FinalStoryActivity -->|Click Rematch| CreateRoomActivity
  FinalStoryActivity -.->|Reads URL & Anon Key| SupabaseConfig

  class StartActivity,LanguageActivity,AuthActivity,MainActivity,CreateRoomActivity,LobbyActivity,GameActivity,FinalStoryActivity activity
  class AuthManager,LocaleHelper helper
  class SupabaseConfig data
```

---

## 3. How to Read Diagram 1

| Element in Diagram 1 | What It Means |
|---|---|
| `[*]` at Start / End | Application entry point / Application exit. |
| **State Box** | Screen or user view state (e.g. `StartActivity`, `MyTurnState`). |
| **Composite State (`state X { ... }`)** | A screen containing internal states (e.g. `AuthActivity` toggling between Login and Registration). |
| **Transition Arrow** | An event or condition triggering state transition. |

---

## 4. How to Read Diagram 2

| Element in Diagram 2 | Shape & Color | Class Type & Example |
|---|---|---|
| **Activity** | Blue rectangle `[ ]`, thick border | `MainActivity<br/>[Activity]` |
| **Helper** | Gray dashed rectangle `[[ ]]` | `AuthManager<br/>[Helper]` |
| **Data** | Pink cylinder `[( )]` | `SupabaseConfig<br/>[Data]` |
| `-->` Solid Arrow | Solid Line | **Navigation** (`startActivity`, `Intent`). |
| `-.->` Dashed Arrow | Dashed Line | **Usage / Calls Method** (`AuthManager.isLoggedIn()`). |
| `==>` Thick Arrow | Double Line | **Data Flow** (`saveSession`, `loadLocale`). |

---

## 5. Simplified Explanation

### 💡 Core Concept
At any given millisecond, an application is in **exactly one state**. An external event (user tap, timer timeout, network response) triggers a **transition** to the next state. In computer science theory, this is known as a **Finite State Machine (FSM)**.

### 🚦 Real-Life Analogy: A Subway Turnstile
- **State 1: Locked.** Waiting for token.
- **Event: Tap Card.**
- **Transition:** Lock releases.
- **State 2: Unlocked.** User passes through.
- **Event: Sensor detects pass.**
- **Transition:** Returns to State 1 (Locked).

---

### 🔍 App Lifecycle & Screen States
1. **Launch Chain:**
   - `StartActivity` shows branding for **4 seconds**.
   - `LanguageActivity` presents 4 languages for **5 seconds**. If untouched, auto-saves default language (`"en"`) via `LocaleHelper`.
   - Checks `AuthManager.isLoggedIn()`. If logged in -> `MainActivity`, else -> `AuthActivity`.
2. **Main Screen & Lobby:**
   - `CreateRoomActivity` configures parameters and generates a 6-character code (e.g. `KOT123`).
   - `LobbyActivity` displays code and player count. The "Start Game" button stays disabled (*"Need at least 3 players (1/3)"*) until count >= 3.
3. **Active Game (`GameActivity`):**
   - User sees **only the previous phrase** ("sheet of paper").
   - 60-second turn timer (turns red during last 10 seconds).
   - Character counter (`0 / 180`), submit button enabled for 2-180 chars.
   - Lock on submit: Shows *"Your phrase has been accepted!"*.

---

### 💻 Connection to Java Code

#### Navigation (`-->`)
```java
// LobbyActivity --> GameActivity
Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
startActivity(intent);
```

#### Helper Usage (`-.->`)
```java
// LanguageActivity -.-> AuthManager
if (AuthManager.isLoggedIn(this)) {
    Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
    startActivity(intent);
}
```

---

## ❓ Self-Check Questions
1. **Exact Value:** How many seconds does `StartActivity` display before auto-redirecting? (Answer: 4 seconds).
2. **Threshold:** How many players must join `LobbyActivity` before "Start Game" enables? (Answer: 3 players).
3. **Character Limit:** What is the maximum character limit in `GameActivity`? (Answer: 180 characters).
4. **Why Two Diagrams?** Diagram 1 shows *behavior and user states*, while Diagram 2 shows *code structure and dependencies*.
