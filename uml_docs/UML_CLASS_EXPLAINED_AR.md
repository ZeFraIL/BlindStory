# 🧩 مخطط فئات UML وشرح تفصيلي — "BlindStory"

---

## 1. مخطط فئات UML (Mermaid classDiagram)

```mermaid
classDiagram
    class AppCompatActivity {
        <<Android Platform>>
    }

    class StartActivity {
        -long SPLASH_DURATION_MS
        -CountDownTimer countDownTimer
        -TextView tvStartTimer
        -boolean isNavigated
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -startAutoRedirectTimer()
        -navigateToLanguageActivity()
    }

    class LanguageActivity {
        -long WAIT_DURATION_MS
        -CountDownTimer countDownTimer
        -String selectedLanguageCode
        -boolean isNavigated
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -setupRadioGroupSelection()
        -startWaitTimer()
        -navigateToNextScreen()
    }

    class AuthActivity {
        -boolean isRegistrationMode
        -TextInputEditText etAuthEmail
        -TextInputEditText etAuthPassword
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -updateUiMode()
        -handlePrimaryAuthAction()
        -handleGoogleAuthAction()
    }

    class MainActivity {
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -showJoinByCodeDialog()
        -showProfileDialog()
    }

    class CreateRoomActivity {
        +String EXTRA_ROOM_NAME
        +String EXTRA_ROOM_CODE
        -TextInputEditText etRoomName
        -RadioGroup rgRounds
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -submitCreateRoom()
        -generateRandomRoomCode()
    }

    class LobbyActivity {
        -String roomName
        -String roomCode
        -List~PlayerItem~ playersList
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -addSimulatedBot()
        -updatePlayersUi()
        -copyCodeToClipboard()
        -shareInvitationLink()
        -startGame()
    }

    class GameActivity {
        -String startingPhrase
        -int currentPhraseNumber
        -CountDownTimer turnTimer
        -List~String~ storyPhrases
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -setupInputWatcher()
        -updateTurnUi()
        -startTurnTimer()
        -submitPhrase()
        -advanceToNextTurn()
        -showVotingDialog()
    }

    class FinalStoryActivity {
        -String roomName
        -ArrayList~String~ storyPhrases
        -ArrayList~String~ storyAuthors
        #onCreate(savedInstanceState)
        #attachBaseContext(newBase)
        -displayStoryPhrases()
        -setupReactions()
        -setupAiAnalysis()
    }

    class AuthManager {
        -String PREF_AUTH
        +isLoggedIn(context)$
        +saveSession(context, email, name, provider)$
        +clearSession(context)$
        +getUserEmail(context)$
        +getDisplayName(context)$
    }

    class LocaleHelper {
        -String PREF_NAME
        +getSavedLanguage(context)$
        +applyLocale(context, languageCode)$
        +updateResources(context, languageCode)$
    }

    class SupabaseConfig {
        +String SUPABASE_URL$
        +String SUPABASE_ANON_KEY$
    }

    AppCompatActivity <|-- StartActivity
    AppCompatActivity <|-- LanguageActivity
    AppCompatActivity <|-- AuthActivity
    AppCompatActivity <|-- MainActivity
    AppCompatActivity <|-- CreateRoomActivity
    AppCompatActivity <|-- LobbyActivity
    AppCompatActivity <|-- GameActivity
    AppCompatActivity <|-- FinalStoryActivity

    StartActivity --> LanguageActivity : Opens language screen
    LanguageActivity ..> LocaleHelper : Saves and applies language
    LanguageActivity ..> AuthManager : Checks login state
    LanguageActivity --> AuthActivity : Opens login screen if not logged in
    LanguageActivity --> MainActivity : Opens main screen if logged in
    AuthActivity ..> AuthManager : Saves auth session
    AuthActivity --> MainActivity : Opens main screen after auth
    MainActivity --> CreateRoomActivity : Opens room constructor
    MainActivity --> LobbyActivity : Opens lobby when entering code
    MainActivity ..> AuthManager : Reads profile and logs out
    CreateRoomActivity --> LobbyActivity : Creates room and passes data
    LobbyActivity --> GameActivity : Starts game when 3+ players ready
    LobbyActivity ..> AuthManager : Reads host name
    GameActivity --> FinalStoryActivity : Opens final when rounds end
    FinalStoryActivity --> CreateRoomActivity : Starts rematch
    FinalStoryActivity ..> SupabaseConfig : Uses AI backend credentials
```

---

## 2. كيفية قراءة المخطط

| الرمز | المعنى |
|---|---|
| **مربع الفئة** | يمثل فئة Java (مثل `StartActivity`). |
| **الجزء العلوي** | اسم الفئة. |
| **الجزء الأوسط** | المتغيرات (الحقول). |
| **الجزء السفلي** | الدوال (الدوال والأساليب). |
| **علامة `+`** | عنصر عام (`public`). |
| **علامة `-`** | عنصر خاص (`private`). |
| **علامة `#`** | عنصر محمي (`protected`). |
| **علامة `$`** | عنصر ثابت (`static`). |
| **سهم `<|--`** | الوراثة (`extends`). |
| **سهم منقط `..>`** | الاعتمادية ("يستخدم"). |
| **سهم متصل `-->`** | الانتقال بين الشاشات (`Intent`). |

---

## 3. شرح بكلمات بسيطة

### 💡 الفكرة الرئيسية
كل فئة مسؤولة عن **دور واحد فقط**. تجزئة الأكواد تجعل التطبيق منظمًا وسهل التعديل.

### 🏠 تشبيه من الحياة: الفندق
- **الأنشطة (Activities):** القاعات والغرف في الفندق.
- **`AuthManager` (موظف الاستقبال):** يتحقق من هويات الزوار.
- **`LocaleHelper` (المترجم):** يضمن عرض اللافتات باللغة المناسبة.
- **`SupabaseConfig` (خزنة الفندق):** تحفظ مفاتيح الاتصال بالخادم.
