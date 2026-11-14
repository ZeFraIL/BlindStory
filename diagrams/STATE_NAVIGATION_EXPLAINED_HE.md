# 🧩 דיאגרמת ניווט ومצבים + מפת מחלקות וקשרים — "BlindStory"

---

## 1. דיאגרמה 1: ניווט ומצבים (Mermaid stateDiagram-v2)

```mermaid
stateDiagram-v2
    [*] --> StartActivity : הפעלת האפליקציה

    state StartActivity {
        [*] --> SplashShowing : הצגת מותג המשחק
        SplashShowing --> SplashTimeout : חולפות 4 שניות
    }

    StartActivity --> LanguageActivity : מעבר אוטומטי 4 שניות / לחיצה

    state LanguageActivity {
        [*] --> LanguageSelecting : בחירת שפה (en, ru, he, lt)
        LanguageSelecting --> SaveLocale : טיימר 5 שניות / לחיצה על המשך
    }

    LanguageActivity --> AuthActivity : משתמש לא מחובר
    LanguageActivity --> MainActivity : משתמש מחובר

    state AuthActivity {
        [*] --> LoginMode : טופס התחברות
        LoginMode --> RegisterMode : לחיצה "אין לי חשבון"
        RegisterMode --> LoginMode : לחיצה "יש לי חשבון"
        LoginMode --> AuthSuccess : הזנת פרטים / Google Sign-In
        RegisterMode --> AuthSuccess : הרשמה ואימות
    }

    AuthActivity --> MainActivity : התחברות הצליחה

    state MainActivity {
        [*] --> DashboardView : הצגת כפתורי הפעולה
        DashboardView --> JoinCodeDialog : לחיצה "הכנס לפי קוד"
        JoinCodeDialog --> DashboardView : הזנת קוד / ביטול
        DashboardView --> ProfileDialog : לחיצה "פרופיל"
        ProfileDialog --> DashboardView : צפייה / ביטול
    }

    MainActivity --> CreateRoomActivity : לחיצה "צור חדר"
    MainActivity --> LobbyActivity : קוד הוזן בדיאלוג
    ProfileDialog --> AuthActivity : לחיצה "התנתק"

    state CreateRoomActivity {
        [*] --> ConfigForm : הזנת שם, נושא, סיבובים, זמן, AI
        ConfigForm --> RoomGenerated : לחיצה "צור חדר"
    }

    CreateRoomActivity --> LobbyActivity : העברת נתונים וקוד חדר

    state LobbyActivity {
        [*] --> WaitingForPlayers : הצגת קוד ורשימת שחקנים
        WaitingForPlayers --> WaitingForPlayers : לחיצה "+ הוסף בוט"
        WaitingForPlayers --> GameReady : שחקנים >= 3
    }

    LobbyActivity --> GameActivity : המארח לוחץ "התחל משחק" (שחקנים >= 3)

    state GameActivity {
        [*] --> MyTurnState : הצגת משפט קודם ותקופת 180 תוים
        MyTurnState --> PhraseSubmitted : לחיצה "שלח"
        MyTurnState --> VotingState : לחיצה "הצע לסיים"
        VotingState --> MyTurnState : הצבעה הסתיימה / ביטול
        MyTurnState --> SkipState : לחיצה "דלג על התור"
        SkipState --> MyTurnState : אישור דילוג
    }

    GameActivity --> FinalStoryActivity : סיום כל הסיבובים / הצבעה עברה

    state FinalStoryActivity {
        [*] --> StoryReading : קריאת הסיפור המלא ושמות המחברים
        StoryReading --> Reacting : לחיצה על אימוג'י (😂, 😮, 🔥, ❤️)
        StoryReading --> AIAnalysisView : לחיצה "מה ה-AI יגיד"
    }

    FinalStoryActivity --> CreateRoomActivity : לחיצה "משחק חוזר"
    FinalStoryActivity --> [*] : יציאה מהאפליקציה
```

---

## 2. דיאגרמה 2: מפת מחלקות וקשרים (Mermaid flowchart TD)

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

  subgraph LEGEND["מקרא"]
    L_Activity["Activity<br/>[Activity]"]
    L_Data[("נתונים וקונפיגורציה<br/>[Data]")]
    L_Helper[["מנהלים ומסייעים<br/>[Helper]"]]
    class L_Activity activity
    class L_Data data
    class L_Helper helper
  end

  subgraph UI["שכבה: מסכים (Activities)"]
    StartActivity["StartActivity<br/>[Activity]"]
    LanguageActivity["LanguageActivity<br/>[Activity]"]
    AuthActivity["AuthActivity<br/>[Activity]"]
    MainActivity["MainActivity<br/>[Activity]"]
    CreateRoomActivity["CreateRoomActivity<br/>[Activity]"]
    LobbyActivity["LobbyActivity<br/>[Activity]"]
    GameActivity["GameActivity<br/>[Activity]"]
    FinalStoryActivity["FinalStoryActivity<br/>[Activity]"]
  end

  subgraph HELPERS["שכבה: מסייעים ומנהלים"]
    AuthManager[["AuthManager<br/>[Helper]"]]
    LocaleHelper[["LocaleHelper<br/>[Helper]"]]
  end

  subgraph DATA["שכבה: נתונים וקונפיגורציה"]
    SupabaseConfig[("SupabaseConfig<br/>[Data]")]
  end

  StartActivity -->|טיימר 4 שניות / לחיצה| LanguageActivity
  LanguageActivity -.->|שומר ומחיל Locale| LocaleHelper
  LanguageActivity -.->|בודק מחובר| AuthManager
  LanguageActivity -->|לא מחובר| AuthActivity
  LanguageActivity -->|מחובר| MainActivity

  AuthActivity -.->|saveSession| AuthManager
  AuthActivity -->|אימות הצליח| MainActivity

  MainActivity -->|לחיצה צור חדר| CreateRoomActivity
  MainActivity -->|הוזן קוד חדר| LobbyActivity
  MainActivity -.->|קורא פרופיל / התנתקות| AuthManager

  CreateRoomActivity -->|יוצר חדר ומעביר קוד| LobbyActivity

  LobbyActivity -->|שחקנים >= 3 ולחיצה התחל| GameActivity
  LobbyActivity -.->|קורא שם מארח| AuthManager

  GameActivity -->|סיום הסיבובים| FinalStoryActivity

  FinalStoryActivity -->|לחיצה משחק חוזר| CreateRoomActivity
  FinalStoryActivity -.->|קורא URL ומפתח| SupabaseConfig

  class StartActivity,LanguageActivity,AuthActivity,MainActivity,CreateRoomActivity,LobbyActivity,GameActivity,FinalStoryActivity activity
  class AuthManager,LocaleHelper helper
  class SupabaseConfig data
```

---

## 3. איך לקרוא את דיאגרמה 1

| אלמנט בדיאגרמה 1 | מה זה אומר |
|---|---|
| `[*]` בהתחלה ובסיום | נקודת הכניסה לאפליקציה / יציאה מהאפליקציה. |
| **מלבן מצב (State)** | מסך או מצב תצוגה של המשתמש (למשל `StartActivity`, `MyTurnState`). |
| **מצב מורכב (`state X { ... }`)** | מסך המכיל מצבים פנימיים (למשל מעבר בין התחברות להרשמה ב-`AuthActivity`). |
| **חץ מעבר** | אירוע או תנאי המעביר את האפליקציה ממצב אחד לאחר. |

---

## 4. איך לקרוא את דיאגרמה 2

| אלמנט בדיאגרמה 2 | צורה וצבע | סוג מחלקה ודוגמה |
|---|---|---|
| **Activity** | מלבן כחול `[ ]`, מסגרת עבה | `MainActivity<br/>[Activity]` |
| **Helper** | מלבן אפור מקווקו `[[ ]]` | `AuthManager<br/>[Helper]` |
| **Data** | צילינדר ורוד `[( )]` | `SupabaseConfig<br/>[Data]` |
| `-->` חץ מלא | קו רציף | **ניווט ומעבר מסכים** (`startActivity`, `Intent`). |
| `-.->` חץ מקווקו | קו מקווקו | **שימוש / קריאה לשיטה** (`AuthManager.isLoggedIn()`). |
| `==>` חץ מודגש | קו כפול | **זרימת נתונים** (`saveSession`, `loadLocale`). |

---

## 5. הסבר במילים פשוטות

### 💡 הרעיון המרכזי
בכל מילישניה, תוכנית מחשב נמצאת **בדיוק במצב אחד**. אירוע חיצוני (לחיצת כפתור, סיום טיימר, תשובת שרת) מעביר אותה למצב הבא. במדעי המחשב התיאורטיים, קוראים לזה **אוטומט סופי (Finite State Machine)**.

### 🚦 אנלוגיה מהחיים: מחסום במטרו / רכבת
- **מצב 1: נעול.** ממתין לכרטיס.
- **אירוע: העברת כרטיס.**
- **מעבר:** המנעול נפתח.
- **מצב 2: פתוח.** הנוסע עובר.
- **אירוע: החיישן מזהה מעבר.**
- **מעבר:** חוזר למצב 1 (נעול).

---

### 🔍 זמנים וערכים מדויקים מהקוד
1. **שרשרת ההפעלה:**
   - `StartActivity` מציגה את הלוגו ל-**4 שניות**.
   - `LanguageActivity` מציעה בחירת שפה ל-**5 שניות**. אם המשתמש לא נוגע, נשמרת ברירת המחדל (אנגלית `"en"`).
2. **תנאי 3 השחקנים בלובי:**
   - כפתור "התחל משחק" ב-`LobbyActivity` נעול כשיש פחות מ-3 שחקנים (*"דרושים לפחות 3 שחקנים (1/3)"*), ונפתח רק כשיש **≥ 3 שחקנים**.
3. **מגבלות במסך המשחק (`GameActivity`):**
   - טיימר תור של **60 שניות** (נדלק באדום ב-10 השניות האחרונות).
   - מגבלת תוים של **עד 180 תוים** (כפתור שליחה פעיל מ-2 עד 180 תוים).

---

## ❓ שאלות לבדיקה עצמית
1. **ערך מדויק:** כמה שניות מוצג מסך הפתיחה `StartActivity` לפני המעבר האוטומטי? (תשובה: 4 שניות).
2. **סף תנאי:** כמה שחקנים חייבים להיות בלובי כדי שכפתור "התחל משחק" יופעל? (תשובה: לפחות 3 שחקנים).
3. **מגבלת תוים:** מהו אורך הפראזה המרבי שניתן לשלוח במסך המשחק? (תשובה: 180 תוים).
4. **למה שתי דיאגרמות?** דיאגרמה 1 מראה *התנהגות ומצבי משתמש*, ודיאגרמה 2 מראה *מבנה קוד ותלויות*.
