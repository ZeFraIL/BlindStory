# 🧩 مخطط الكيانات والعلاقات (ER-Diagram) لقاعدة البيانات — "BlindStory"

---

## 1. مخطط الكيانات والعلاقات (Mermaid erDiagram)

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

    users ||--o{ rooms : "يستضيف"
    rooms ||--o{ room_members : "يتضمن"
    users ||--o{ room_members : "ينضم"
    rooms ||--o{ phrases : "يحتوي"
    users ||--o{ phrases : "يكتب"
    rooms ||--o{ votes : "يجري"
    users ||--o{ votes : "يبدأ"
    rooms ||--|| final_stories : "ينتج"
    final_stories ||--|| ai_analyses : "يحلل"
```

---

## 2. كيفية قراءة المخطط

| الرمز | المعنى |
|---|---|
| **صندوق الكيان (`users`, `rooms`)** | جدول قاعدة بيانات يحفظ السجلات. |
| **الحقل (`id`, `name`, `text`)** | عمود يضم عنصر بيانات محدد. |
| **PK (المفتاح الرئيسي)** | المعرف الفريد لكل صف. |
| **FK (المفتاح الأجنبي)** | رابط يشير لمفتاح رئيسي في جدول آخر. |
| `||--o{` | **علاقة واحد إلى متعدد:** صف واحد يرتبط بعدة صفوف. |
| `||--||` | **علاقة واحد لواحد:** صف واحد يرتبط بصف واحد فقط. |

---

## 3. شرح بكلمات بسيطة

### 💡 الفكرة الرئيسية
لماذا نحتاج قاعدة بيانات؟ المتغيرات تختفي عند إغلاق التطبيق. تحفظ قاعدة البيانات (Supabase PostgreSQL) البيانات بشكل دائم على الخادم!
