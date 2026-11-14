# 🧩 UML Диаграмма классов и подробное объяснение — «Слепой рассказ»

---

## 1. UML Диаграмма классов (Mermaid classDiagram)

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

## 2. Как читать диаграмму

| Символ на диаграмме | Что это значит |
|---|---|
| **Прямоугольник класса** | Обозначение класса в Java (например `StartActivity`). |
| **Верхняя часть** | Имя класса. |
| **Средняя часть** | Переменные (поля) класса. |
| **Нижняя часть** | Методы (функции) класса. |
| **Знак `+` (Плюс)** | `public` — публичный доступ из любых классов. |
| **Знак `-` (Минус)** | `private` — приватный доступ только внутри этого класса. |
| **Знак `#` (Решетка)** | `protected` — доступен дочерним классам. |
| **Знак `$` (Доллар)** | `static` — статический элемент, принадлежащий самому классу. |
| **Сплошная стрелка с треугольником `<|--`** | Наследование (`extends`). |
| **Пунктирная стрелка `..>`** | Зависимость («Использует»). |
| **Сплошная стрелка `-->`** | Ассоциация и переход между экранами (`Intent`). |

---

## 3. Объяснение простыми словами

### 💡 Главная идея
Каждый класс отвечает за **одну конкретную задачу**. Директор школы не моет полы, а учитель физкультуры не ведет математику. Разделение обязанностей делает код чистым, понятным и надежным.

### 🏠 Наглядная аналогия: Отель
- **Activity (Экраны):** Холлы и номера отеля (Лобби, Ресторан, Игровой зал).
- **`AuthManager` (Администратор на ресепшн):** Проверяет ключи и регистрирует гостей.
- **`LocaleHelper` (Переводчик отеля):** Следит, чтобы все таблички были на языке гостя.
- **`SupabaseConfig` (Сейф отеля):** Хранит адреса и ключи доступа к серверу.

---

## ❓ Вопросы для самопроверки
1. **Почему?** Почему логика сессии выделена в `AuthManager`, а не хранится внутри `AuthActivity`?
2. **Что изменится, если...** Что произойдет, если мы удалим `LocaleHelper.updateResources()` из `attachBaseContext()`?
3. **Поиск по диаграмме:** Найдите класс, отвечающий за ограничение символов (до 180).
4. **Наследование против зависимости:** Какая стрелка соединяет `MainActivity` с `AppCompatActivity`, а какая с `AuthManager`?
