# 🧩 סכמת בסיס נתונים (ER-Diagram) ותיאור מפורט — "BlindStory"

---

## 1. דיאגרמת ER לבסיס הנתונים (Mermaid erDiagram)

```mermaid
erDiagram
    users {
        uuid id PK
        text name
        text avatar_url
        text auth_provider
        timestamptz created_at
    }

    rooms {
        uuid id PK
        string room_code
        text name
        text topic
        text starting_phrase
        int rounds
        int time_limit_mins
        boolean is_public
        boolean ai_enabled
        boolean ai_show_names
        string state
        uuid host_id FK
        timestamptz created_at
    }

    room_members {
        uuid id PK
        uuid room_id FK
        uuid user_id FK
        string role
        string status
        int queue_order
        int passes_count
        timestamptz created_at
    }

    phrases {
        uuid id PK
        uuid room_id FK
        uuid author_id FK
        int phrase_number
        text text
        int round
        boolean is_passed
        timestamptz created_at
    }

    votes {
        uuid id PK
        uuid room_id FK
        string vote_type
        uuid initiator_id FK
        jsonb votes_data
        string result
        timestamptz created_at
    }

    final_stories {
        uuid id PK
        uuid room_id FK
        text full_text
        jsonb authors_data
        timestamptz created_at
    }

    ai_analyses {
        uuid id PK
        uuid story_id FK
        jsonb analysis_result
        timestamptz created_at
    }

    users ||--o{ rooms : "מארח"
    rooms ||--o{ room_members : "מכיל"
    users ||--o{ room_members : "משתתף"
    rooms ||--o{ phrases : "כולל"
    users ||--o{ phrases : "כותב"
    rooms ||--o{ votes : "מנהל"
    users ||--o{ votes : "יוזם"
    rooms ||--|| final_stories : "מפיק"
    final_stories ||--|| ai_analyses : "מנותח"
```

---

## 2. איך לקרוא את הדיאגרמה

| אלמנט בדיאגרמה | מה זה אומר |
|---|---|
| **מלבן ישות (`users`, `rooms`)** | טבלת בסיס נתונים המאחסנת שורות של רשומות. |
| **שדה (`id`, `name`, `text`)** | עמודה בטבלה המחזיקה נתון ספציפי. |
| **PK (Primary Key - מפתח ראשי)** | מזהה ייחודי עבור כל שורה בטבלה זו. |
| **FK (Foreign Key - מפתח זר)** | קישור המצביע על מפתח ראשי בטבלה אחרת. |
| `||--o{` | **קשר אחד-לרבים:** שורת אב אחת קשורה לאפס, אחת או הרבה שורות בנות. |
| `||--||` | **קשר אחד-לאחד:** שורת אב אחת קשורה בדיוק לשורת בת אחת. |

---

## 3. הסבר במילים פשוטות

### 💡 הרעיון המרכזי
למה צריך בסיס נתונים אם יש משתנים ב-Java? כשסוגרים אפליקציה או מפעילים מחדש את הטלפון, משתנים בזיכרון ה-RAM נעלמים! בסיס נתונים (Supabase PostgreSQL) שומר נתונים מובנים באופן קבוע בשרת באינטרנט.

### 📚 אנלוגיה מהחיים: יומן בית ספר
דמיינו יומן בית ספר:
- טבלה אחת מחזיקה את רשימת **התלמידים** (תעודת זהות, שם מלא, כיתה).
- טבלה נפרדת מחזיקה את **הציונים** (מזהה ציון, מזהה תלמיד, מקצוע, ציון).
- במקום לרשום את שם התלמיד המלא ליד כל ציון, רושמים רק את מזהה התלמיד (Foreign Key).

---

## ❓ שאלות לבדיקה עצמית
1. **שורה יחידה:** מה מייצגת שורה אחת בטבלת `phrases`? (תשובה: פראזה יחידה שנשלחה בתוך משחק).
2. **מפתח זר:** למה `host_id` בטבלת `rooms` הוא מפתח זר שמצביע על `users.id`? (תשובה: כדי לדעת מי המארח מבלי לשכפל את כל פרטי המשתמש).
3. **כלל מחיקה:** מה יקרה לפראזות בטבלת `phrases` אם שורת ה-`room` שלהן תימחק? (תשובה: הן יימחקו אוטומטית הודות ל-`ON DELETE CASCADE`).
