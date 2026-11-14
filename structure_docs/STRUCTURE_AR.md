# 🏛️ الهيكلية والمعمارية لتطبيق "BlindStory"

---

## 1. الصورة الكبيرة (The Big Picture)

تطبيق **BlindStory** ("القصة العمياء") هو لعبة كلمات جماعية. تنقسم البنية البرمجية للتطبيق إلى ثلاثة أجزاء رئيسية: **واجهة المستخدم Visual UI** (الشاشات التي يراها المستخدم)، **منطق اللعبة Game Logic** (إدارة الأدوار والمؤقتات واللغات)، و**طبقة البيانات Data Layer** (الجلسات المحلية وقاعدة بيانات Supabase).

### 🏠 تشبيه من الحياة الواقعية: المسرح
لفهم سبب تقسيم الكود إلى أجزاء بدلاً من كتابته في ملف واحد ضخم، تخيل **المسرح**:
- **خشبة المسرح ومشاهد العرض (`res/layout` + `Activities`):** ما يراه الجمهور — الممثلون والإضاءة والديكور.
- **مخرج المسرح والمشرف (`AuthManager` + `LocaleHelper`):** يعملون خلف الكواليس لضمان تحدث الممثلين باللغة الصحيحة والتحقق من بطاقات الحضور.
- **شباك التذاكر والأرشيف (`Supabase`):** يحفظ حسابات الأعضاء المسجلين وسجلات القصص المكتوبة.

---

## 2. نظرة عامة على المجلدات والملفات

في مشروع أندرويد هذا، تتوزع الملفات على النحو التالي:

```
BlindStory/
├── app/src/main/
│   ├── java/zeev/fraiman/blindstory/    <-- كود Java (المنطق)
│   │   ├── StartActivity.java           <-- شاشة البداية
│   │   ├── LanguageActivity.java        <-- شاشة اختيار اللغة
│   │   ├── AuthActivity.java            <-- شاشة التسجيل والدخول
│   │   ├── MainActivity.java            <-- الشاشة الرئيسية
│   │   ├── CreateRoomActivity.java      <-- شاشة إعداد الغرفة
│   │   ├── LobbyActivity.java           <-- صالة الانتظار
│   │   ├── GameActivity.java            <-- شاشة اللعب النشط
│   │   ├── FinalStoryActivity.java      <-- شاشة النهاية وتحليل الذكاء الاصطناعي
│   │   ├── AuthManager.java             <-- مدير الجلسة
│   │   ├── LocaleHelper.java            <-- مدير اللغات
│   │   └── SupabaseConfig.java          <-- إعدادات الخادم
│   └── res/                             <-- الموارد المرئية
│       ├── layout/                      <-- ملفات XML للواجهات
│       └── values/                      <-- النصوص والألوان والأنماط
```

---

## 3. العلاقات بين المكونات (Relationships)

يوضح المخطط التالي تدفق البيانات والتنقل بين الشاشات:

```
[StartActivity] (مؤقت 4 ثوانٍ)
      │
      ▼
[LanguageActivity] (اختيار اللغة + الحفظ عبر LocaleHelper)
      │
      ▼
[AuthActivity] (التسجيل/الدخول + الحفظ عبر AuthManager)
      │
      ▼
[MainActivity] (الشاشة الرئيسية) ───(النقر على "إنشاء غرفة")───> [CreateRoomActivity]
      │                                                                  │
      │                                                          (توليد الرمز)
      ▼                                                                  ▼
[LobbyActivity] <────────────────────────────────────────────────────────┘
      │
(3 لاعبين على الأقل -> النقر على "بدء اللعبة")
      ▼
[GameActivity] (إرسال الجمل -> الوصول للجولة الأخيرة)
      │
      ▼
[FinalStoryActivity] (قراءة القصة + تحليل الذكاء الاصطناعي)
```

---

## 4. مثال عملي: إنشاء غرفة وبدء اللعبة

تتبع المسار البرمجي عند إنشاء غرفة:

1. **الخطوة 1 — الشاشة الرئيسية (`MainActivity.java`):**
   ينقر المستخدم على `btn_create_room`:
   ```java
   btnCreateRoom.setOnClickListener(v -> {
       Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
       startActivity(intent);
   });
   ```

2. **الخطوة 2 — إعداد الغرفة (`CreateRoomActivity.java`):**
   يدخل المستخدم البيانات وينقر "إنشاء غرفة". يتم تنفيذ `submitCreateRoom()` لتوليد الرمز:
   ```java
   String roomCode = generateRandomRoomCode(); // مثل "KOT123"
   Intent intent = new Intent(CreateRoomActivity.this, LobbyActivity.class);
   intent.putExtra(EXTRA_ROOM_NAME, name);
   intent.putExtra(EXTRA_ROOM_CODE, roomCode);
   startActivity(intent);
   ```

3. **الخطوة 3 — صالة الانتظار (`LobbyActivity.java`):**
   تعرض الصالة الرمز `KOT123` وتقفل زر البدء حتى ينضم 3 لاعبين. عند انضمامهم، ينقر المضيف `startGame()`:
   ```java
   Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
   startActivity(intent);
   ```

4. **الخطوة 4 — شاشة الدور (`GameActivity.java`):**
   تعرض الجملة السابقة فقط، فيدخل اللاعب رده ويرسله!

---

## 📖 قاموس مصطلحات قصير

1. **Activity:** شاشة واحدة في أندرويد.
2. **Intent:** كائن يطلب الانتقال إلى شاشة أخرى.
3. **Locale:** إعدادات اللغة والمنطقة للمستخدم.
4. **SharedPreferences:** تخزين محلي سريع على الهاتف.
5. **Backend:** الخادم الخارجي على الإنترنت الذي يدير البيانات.
6. **CountDownTimer:** فئة في أندرويد للمؤقتات دون حجب الواجهة.
