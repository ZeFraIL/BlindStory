# 🏛️ המבנה והארכיטקטורה של אפליקציית "BlindStory"

---

## 1. התמונה הגדולה (The Big Picture)

אפליקציית **BlindStory** ("הסיפור העיוור") היא משחק מילים חברתי רב-משתתפים. האפליקציה מורכבת משלושה חלקים מרכזיים: **הממשק הויזואלי** (המסכים שהמשתמש רואה), **לוגיקת המשחק** (ניהול התורות, הטיימרים והשפות), ו**שכבת הנתונים** (שמירת המשתמש והחיבור לשרת Supabase). 

### 🏠 אנלוגיה מהחיים האמיתיים: התיאטרון
כדי להבין מדוע הקוד מחולק לחלקים ולא כתוב בקובץ אחד ענקי, נוכל לדמיין **תיאטרון**:
- **הבמה והמסכים (`res/layout` + `Activities`):** זה מה שהקהל רואה — השחקנים, התפאורה והאורות.
- **מנהל הבמה והבמאי (`AuthManager` + `LocaleHelper`):** עובדים מאחורי הקלעים, דואגים שהשחקנים ידעו באיזו שפה לדבר ומתי להכניס משתמש רשום.
- **קופת התיאטרון והארכיון (`Supabase`):** שומרת את כרטיסי החברים הרשומים ואת כל מחזות הסיפור שנכתבו.

---

## 2. סקירת תיקיות וקבצים

בפרויקט אנדרואיד זה, הקוד מחולק למבנה של תיקיות וקובצי משאבים:

```
BlindStory/
├── app/src/main/
│   ├── java/zeev/fraiman/blindstory/    <-- קובצי קוד Java (הלוגיקה)
│   │   ├── StartActivity.java           <-- מסך פתיחה (Splash)
│   │   ├── LanguageActivity.java        <-- מסך בחירת שפה
│   │   ├── AuthActivity.java            <-- מסך הרשמה והתחברות
│   │   ├── MainActivity.java            <-- המסך הראשי
│   │   ├── CreateRoomActivity.java      <-- מסך הגדרת חדר
│   │   ├── LobbyActivity.java           <-- מסך לובי המתנה
│   │   ├── GameActivity.java            <-- מסך התור הפעיל
│   │   ├── FinalStoryActivity.java      <-- מסך הסיום וניתוח AI
│   │   ├── AuthManager.java             <-- מנהל סשן התחברות
│   │   ├── LocaleHelper.java            <-- מנהל שפות ו-Locale
│   │   └── SupabaseConfig.java          <-- הגדרות חיבור לשרת
│   └── res/                             <-- משאבים ויזואליים
│       ├── layout/                      <-- עיצובי המסכים ב-XML
│       └── values/                      <-- קובצי טקסטים, צבעים ונושאים
```

### הסבר המונחים המרכזיים:
- **Activity (מסך):** רכיב באנדרואיד האחראי על מסך יחיד שהמשתמש רואה ופועל איתו.
- **XML Layout (עיצוב):** קובץ המגדיר את מראה המסך (כפתורים, שדות טקסט, צבעים).
- **SharedPreferences (זיכרון מקומי):** מנגנון לשמירת נתונים קטנים בטלפון (כמו שפה נבחרת או שם משתמש).
- **Supabase Backend (שרת נתונים):** מסד נתונים מרוחק באינטרנט שבו נשמרים החדרים והסיפורים.

---

## 3. יחסים בין החלקים (Relationships)

התרשים הבא מציג את זרימת הנתונים והמעבר בין המסכים באפליקציה:

```
[StartActivity] (טיימר 4 שניות)
      │
      ▼
[LanguageActivity] (בחירת שפה + שמירה ב-LocaleHelper)
      │
      ▼
[AuthActivity] (הרשמה/התחברות + שמירה ב-AuthManager)
      │
      ▼
[MainActivity] (מסך ראשי) ───(לחץ "צור חדר")───> [CreateRoomActivity]
      │                                                │
      │                                       (יצירת קוד חדר)
      ▼                                                ▼
[LobbyActivity] <──────────────────────────────────────┘
      │
(לפחות 3 שחקנים -> לחץ "התחל משחק")
      ▼
[GameActivity] (כתיבת פראזות -> הגעה לסיבוב האחרון)
      │
      ▼
[FinalStoryActivity] (קריאת הסיפור המלא + ניתוח AI)
```

---

## 4. דוגמה מפרויקט זה: תרחיש יצירת חדר והתחלת משחק

נלווה תרחיש אמיתי מהקוד — מלוחצת המשתמש על "צור חדר" ועד לפתיחת המשחק:

1. **שלב 1 — המסך הראשי (`MainActivity.java`):**
   המשתמש לוחץ על הכפתור `btn_create_room`. נקראת השיטה:
   ```java
   btnCreateRoom.setOnClickListener(v -> {
       Intent intent = new Intent(MainActivity.java, CreateRoomActivity.class);
       startActivity(intent);
   });
   ```

2. **שלב 2 — הגדרת החדר (`CreateRoomActivity.java`):**
   המשתמש מזין שם חדר ונושא, ולוחץ על "צור חדר". נקראת השיטה `submitCreateRoom()` המייצרת קוד אקראי:
   ```java
   String roomCode = generateRandomRoomCode(); // למשל "KOT123"
   Intent intent = new Intent(CreateRoomActivity.this, LobbyActivity.class);
   intent.putExtra(EXTRA_ROOM_NAME, name);
   intent.putExtra(EXTRA_ROOM_CODE, roomCode);
   startActivity(intent);
   ```

3. **שלב 3 — הלובי (`LobbyActivity.java`):**
   הלובי מוסיף את המארח, מציג את הקוד `KOT123`, ונועל את כפתור ההתחלה עד שיהיו 3 שחקנים.
   כשמגיעים 3 שחקנים, השרת או המארח מפעיל את `startGame()`:
   ```java
   Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
   startActivity(intent);
   ```

4. **שלב 4 — מסך המשחק (`GameActivity.java`):**
   המשתמש רואה את המשפט הקודם, מזין את תשובתו ב-`etUserPhrase` ושולח!

---

## 📖 מילון מונחים קצר

1. **Activity (אקטיביטי):** מסך יחיד באפליקציה.
2. **Intent (אינטנט):** הודעה המבקשת מאנדרואיד לעבור למסך אחר.
3. **Locale (לוקאל):** הגדרת השפה והאזור של המשתמש.
4. **SharedPreferences:** זיכרון מקומי מהיר בטלפון.
5. **Backend (באלנד / שרת):** המחשב המרוחק באינטרנט המנהל את נתוני המשחק.
6. **CountDownTimer:** רכיב אנדרואיד המבצע ספירה לאחור בשניות.
