# 🧩 Диаграмма состояний, навигации и карта классов — «Слепой рассказ»

---

## 1. Диаграмма 1: Навигация и состояния (Mermaid stateDiagram-v2)

```mermaid
stateDiagram-v2
    [*] --> StartActivity : Запуск приложения

    state StartActivity {
        [*] --> SplashShowing : Отображение логотипа
        SplashShowing --> SplashTimeout : Истекают 4 секунды
    }

    StartActivity --> LanguageActivity : Автопереход 4 сек / Нажатие

    state LanguageActivity {
        [*] --> LanguageSelecting : Выбор языка (en, ru, he, lt)
        LanguageSelecting --> SaveLocale : Таймер 5 сек / Нажатие Продолжить
    }

    LanguageActivity --> AuthActivity : Пользователь не авторизован
    LanguageActivity --> MainActivity : Пользователь авторизован

    state AuthActivity {
        [*] --> LoginMode : Форма входа
        LoginMode --> RegisterMode : Нажатие "Нет аккаунта"
        RegisterMode --> LoginMode : Нажатие "Есть аккаунт"
        LoginMode --> AuthSuccess : Ввод данных / Google Sign-In
        RegisterMode --> AuthSuccess : Регистрация
    }

    AuthActivity --> MainActivity : Авторизация успешна

    state MainActivity {
        [*] --> DashboardView : Главные кнопки
        DashboardView --> JoinCodeDialog : Нажатие "Войти по коду"
        JoinCodeDialog --> DashboardView : Ввод кода / Отмена
        DashboardView --> ProfileDialog : Нажатие "Профиль"
        ProfileDialog --> DashboardView : Просмотр / Отмена
    }

    MainActivity --> CreateRoomActivity : Нажатие "Создать комнату"
    MainActivity --> LobbyActivity : Ввод кода комнаты
    ProfileDialog --> AuthActivity : Нажатие "Выйти"

    state CreateRoomActivity {
        [*] --> ConfigForm : Ввод темы, кругов, времени, ИИ
        ConfigForm --> RoomGenerated : Нажатие "Создать комнату"
    }

    CreateRoomActivity --> LobbyActivity : Передача данных комнаты

    state LobbyActivity {
        [*] --> WaitingForPlayers : Код и список игроков
        WaitingForPlayers --> WaitingForPlayers : Нажатие "+ Добавить бота"
        WaitingForPlayers --> GameReady : Игроков >= 3
    }

    LobbyActivity --> GameActivity : Нажатие "Начать игру" (Игроков >= 3)

    state GameActivity {
        [*] --> MyTurnState : Показ предыдущей фразы (180 симв)
        MyTurnState --> PhraseSubmitted : Нажатие "Отправить"
        MyTurnState --> VotingState : Нажатие "Предложить финал"
        VotingState --> MyTurnState : Голосование окончено / Отмена
        MyTurnState --> SkipState : Нажатие "Пропустить ход"
        SkipState --> MyTurnState : Подтверждение пропуска
    }

    GameActivity --> FinalStoryActivity : Все круги завершены / Голосование прошло

    state FinalStoryActivity {
        [*] --> StoryReading : Полный рассказ и имена авторов
        StoryReading --> Reacting : Нажатие эмодзи (😂, 😮, 🔥, ❤️)
        StoryReading --> AIAnalysisView : Нажатие "Что скажет ИИ"
    }

    FinalStoryActivity --> CreateRoomActivity : Нажатие "Реванш"
    FinalStoryActivity --> [*] : Выход из приложения
```

---

## 2. Диаграмма 2: Карта классов и связей (Mermaid flowchart TD)

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

  subgraph LEGEND["Легенда"]
    L_Activity["Activity<br/>[Activity]"]
    L_Data[("Данные<br/>[Data]")]
    L_Helper[["Помощник<br/>[Helper]"]]
    class L_Activity activity
    class L_Data data
    class L_Helper helper
  end

  subgraph UI["Слой: Экраны (Activities)"]
    StartActivity["StartActivity<br/>[Activity]"]
    LanguageActivity["LanguageActivity<br/>[Activity]"]
    AuthActivity["AuthActivity<br/>[Activity]"]
    MainActivity["MainActivity<br/>[Activity]"]
    CreateRoomActivity["CreateRoomActivity<br/>[Activity]"]
    LobbyActivity["LobbyActivity<br/>[Activity]"]
    GameActivity["GameActivity<br/>[Activity]"]
    FinalStoryActivity["FinalStoryActivity<br/>[Activity]"]
  end

  subgraph HELPERS["Слой: Помощники"]
    AuthManager[["AuthManager<br/>[Helper]"]]
    LocaleHelper[["LocaleHelper<br/>[Helper]"]]
  end

  subgraph DATA["Слой: Данные и Конфигурация"]
    SupabaseConfig[("SupabaseConfig<br/>[Data]")]
  end

  StartActivity -->|Таймер 4 сек / Клик| LanguageActivity
  LanguageActivity -.->|Сохраняет и применяет локаль| LocaleHelper
  LanguageActivity -.->|Проверяет isLoggedIn| AuthManager
  LanguageActivity -->|Не авторизован| AuthActivity
  LanguageActivity -->|Авторизован| MainActivity

  AuthActivity -.->|saveSession| AuthManager
  AuthActivity -->|Успешный вход| MainActivity

  MainActivity -->|Клик Создать комнату| CreateRoomActivity
  MainActivity -->|Код введен| LobbyActivity
  MainActivity -.->|Профиль / Выход| AuthManager

  CreateRoomActivity -->|Создает комнату и передает код| LobbyActivity

  LobbyActivity -->|Игроков >= 3 и старт| GameActivity
  LobbyActivity -.->|Читает имя ведущего| AuthManager

  GameActivity -->|Круги завершены| FinalStoryActivity

  FinalStoryActivity -->|Клик Реванш| CreateRoomActivity
  FinalStoryActivity -.->|Читает URL и ключ| SupabaseConfig

  class StartActivity,LanguageActivity,AuthActivity,MainActivity,CreateRoomActivity,LobbyActivity,GameActivity,FinalStoryActivity activity
  class AuthManager,LocaleHelper helper
  class SupabaseConfig data
```

---

## 3. Объяснение простыми словами

### 💡 Главная идея
В любой момент времени приложение находится **ровно в одном состоянии**. Внешнее событие (клик, таймер) переводит его в следующее состояние. В теории информатики это называется **Конечным автоматом (Finite State Machine)**.

### 🚦 Наглядная аналогия: Турникет в метро
- **Состояние 1: Заблокирован.** Ждет карту.
- **Событие: Прикосновение картой.**
- **Переход:** Замок открывается.
- **Состояние 2: Открыт.** Пассажир проходит.
- **Событие: Датчик фиксирует проход.**
- **Переход:** Возвращается в Состояние 1.

---

## ❓ Вопросы для самопроверки
1. **Точное значение:** Сколько секунд отображается `StartActivity` до автоматического перехода? (Ответ: 4 секунды).
2. **Порог:** Сколько игроков должно быть в лобби, чтобы кнопка «Начать игру» активировалась? (Ответ: минимум 3 игрока).
3. **Лимит:** Какова максимальная длина фразы в `GameActivity`? (Ответ: 180 символов).
