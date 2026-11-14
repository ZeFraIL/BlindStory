# 🧩 مخطط التنقل والحالات وخريطة الفئات — "BlindStory"

---

## 1. המخطط 1: التنقل والحالات (Mermaid stateDiagram-v2)

```mermaid
stateDiagram-v2
    [*] --> StartActivity : تشغيل التطبيق

    state StartActivity {
        [*] --> SplashShowing : عرض الشعار
        SplashShowing --> SplashTimeout : انقضاء 4 ثوانٍ
    }

    StartActivity --> LanguageActivity : انتقال تلقائي 4 ثوانٍ / نقل

    state LanguageActivity {
        [*] --> LanguageSelecting : اختيار اللغة (en, ru, he, lt)
        LanguageSelecting --> SaveLocale : مؤقت 5 ثوانٍ / نقر متابعة
    }

    LanguageActivity --> AuthActivity : غير مسجل الدخول
    LanguageActivity --> MainActivity : مسجل الدخول

    state AuthActivity {
        [*] --> LoginMode : نموذج الدخول
        LoginMode --> RegisterMode : نقر "ليس لدي حساب"
        RegisterMode --> LoginMode : نقر "لدي حساب"
        LoginMode --> AuthSuccess : إدخال البيانات / Google
        RegisterMode --> AuthSuccess : التسجيل
    }

    AuthActivity --> MainActivity : نجاح المصادقة

    state MainActivity {
        [*] --> DashboardView : الأزرار الرئيسية
        DashboardView --> JoinCodeDialog : نقر "دخول برمز"
        JoinCodeDialog --> DashboardView : إدخال الرمز / إلغاء
        DashboardView --> ProfileDialog : نقر "الملف الشخصي"
        ProfileDialog --> DashboardView : عرض / إلغاء
    }

    MainActivity --> CreateRoomActivity : نقر "إنشاء غرفة"
    MainActivity --> LobbyActivity : إدخال الرمز
    ProfileDialog --> AuthActivity : نقر "تسجيل الخروج"

    state CreateRoomActivity {
        [*] --> ConfigForm : إدخال الموضوع والجولات والوقت
        ConfigForm --> RoomGenerated : نقر "إنشاء غرفة"
    }

    CreateRoomActivity --> LobbyActivity : نقل البيانات والرمز

    state LobbyActivity {
        [*] --> WaitingForPlayers : الرمز وقائمة اللاعبين
        WaitingForPlayers --> WaitingForPlayers : نقر "+ إضافة بوت"
        WaitingForPlayers --> GameReady : اللاعبون >= 3
    }

    LobbyActivity --> GameActivity : المضيف ينقر "بدء اللعبة" (اللاعبون >= 3)

    state GameActivity {
        [*] --> MyTurnState : عرض الجملة السابقة (180 حرفًا)
        MyTurnState --> PhraseSubmitted : نقر "إرسال"
        MyTurnState --> VotingState : نقر "اقتراح النهاية"
        VotingState --> MyTurnState : اكتمال التصويت / إلغاء
        MyTurnState --> SkipState : نقر "تخطي الدور"
        SkipState --> MyTurnState : تأكيد التخطي
    }

    GameActivity --> FinalStoryActivity : اكتمال الجولات / قبول التصويت

    state FinalStoryActivity {
        [*] --> StoryReading : القصة الكاملة وأسماء المؤلفين
        StoryReading --> Reacting : تفاعلات الإيموجي (😂, 😮, 🔥, ❤️)
        StoryReading --> AIAnalysisView : نقر "ماذا سيقول الذكاء الاصطناعي"
    }

    FinalStoryActivity --> CreateRoomActivity : نقر "إعادة اللعب"
    FinalStoryActivity --> [*] : الخروج من التطبيق
```

---

## 2. المخطط 2: خريطة الفئات (Mermaid flowchart TD)

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

  subgraph LEGEND["الدليل"]
    L_Activity["Activity<br/>[Activity]"]
    L_Data[("البيانات<br/>[Data]")]
    L_Helper[["المساعد<br/>[Helper]"]]
    class L_Activity activity
    class L_Data data
    class L_Helper helper
  end

  subgraph UI["الشاشات (Activities)"]
    StartActivity["StartActivity<br/>[Activity]"]
    LanguageActivity["LanguageActivity<br/>[Activity]"]
    AuthActivity["AuthActivity<br/>[Activity]"]
    MainActivity["MainActivity<br/>[Activity]"]
    CreateRoomActivity["CreateRoomActivity<br/>[Activity]"]
    LobbyActivity["LobbyActivity<br/>[Activity]"]
    GameActivity["GameActivity<br/>[Activity]"]
    FinalStoryActivity["FinalStoryActivity<br/>[Activity]"]
  end

  subgraph HELPERS["المساعدون"]
    AuthManager[["AuthManager<br/>[Helper]"]]
    LocaleHelper[["LocaleHelper<br/>[Helper]"]]
  end

  subgraph DATA["البيانات والتكوين"]
    SupabaseConfig[("SupabaseConfig<br/>[Data]")]
  end

  StartActivity -->|مؤقت 4 ثوانٍ / نقر| LanguageActivity
  LanguageActivity -.->|يحفظ ويطبق اللغة| LocaleHelper
  LanguageActivity -.->|يفحص حالة الدخول| AuthManager
  LanguageActivity -->|غير مسجل| AuthActivity
  LanguageActivity -->|مسجل الدخول| MainActivity

  AuthActivity -.->|saveSession| AuthManager
  AuthActivity -->|نجاح المصادقة| MainActivity

  MainActivity -->|نقر إنشاء غرفة| CreateRoomActivity
  MainActivity -->|إدخال الرمز| LobbyActivity
  MainActivity -.->|الملف الشخصي / الخروج| AuthManager

  CreateRoomActivity -->|ينشئ الغرفة وينقل الرمز| LobbyActivity

  LobbyActivity -->|اللاعبون >= 3 وبدء| GameActivity
  LobbyActivity -.->|يقرأ اسم المضيف| AuthManager

  GameActivity -->|اكتمال الجولات| FinalStoryActivity

  FinalStoryActivity -->|نقر إعادة اللعب| CreateRoomActivity
  FinalStoryActivity -.->|يقرأ الرابط والمفتاح| SupabaseConfig

  class StartActivity,LanguageActivity,AuthActivity,MainActivity,CreateRoomActivity,LobbyActivity,GameActivity,FinalStoryActivity activity
  class AuthManager,LocaleHelper helper
  class SupabaseConfig data
```

---

## 3. شرح بكلمات بسيطة

### 💡 الفكرة الرئيسية
في أي لحظة يكون التطبيق في **حالة واحدة بالضبط**. حادث خارجي (نقر، مؤقت) ينقله إلى الحالة التالية. في علوم الحاسوب يُسمى هذا **آلة الحالات المنتهية (Finite State Machine)**.

### 🚦 تشبيه من الحياة: البوابة الإلكترونية
- **الحالة 1: مقفلة.** تنتظر التذكرة.
- **الحدث: تمرير التذكرة.**
- **الانتقال:** تفتح البوابة.
- **الحالة 2: مفتوحة.** يمر المسافر.
- **الحدث: استشعار المرور.**
- **الانتقال:** تعود للحالة 1 (مقفلة).
