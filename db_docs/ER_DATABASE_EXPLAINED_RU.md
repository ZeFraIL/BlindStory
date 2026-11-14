# 🧩 ER-Диаграмма базы данных и подробное объяснение — «Слепой рассказ»

---

## 1. ER-Диаграмма базы данных (Mermaid erDiagram)

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

    users ||--o{ rooms : "создает"
    rooms ||--o{ room_members : "содержит"
    users ||--o{ room_members : "входит"
    rooms ||--o{ phrases : "включает"
    users ||--o{ phrases : "пишет"
    rooms ||--o{ votes : "проводит"
    users ||--o{ votes : "инициирует"
    rooms ||--|| final_stories : "формирует"
    final_stories ||--|| ai_analyses : "анализируется"
```

---

## 2. Как читать ER-Диаграмму

| Символ на диаграмме | Что это значит |
|---|---|
| **Сущность (`users`, `rooms`)** | Таблица базы данных, хранящая строки записей. |
| **Поле (`id`, `name`, `text`)** | Колонка таблицы, содержащая конкретный элемент данных. |
| **PK (Primary Key)** | Первичный ключ — уникальный идентификатор каждой строки. |
| **FK (Foreign Key)** | Внешний ключ — ссылка на первичный ключ в другой таблице. |
| `||--o{` | **Один-ко-Многим:** одна запись родителя связана с 0, 1 или несколькими записями. |
| `||--||` | **Один-к-Одному:** одна запись строго связана с одной записью. |

---

## 3. Объяснение простыми словами

### 💡 Главная идея
Зачем нужна база данных, если есть переменные в Java? Переменные в оперативной памяти сбрасываются при закрытии приложения! База данных (Supabase PostgreSQL) надежно хранит данные на сервере в интернете.

### 📚 Наглядная аналогия: Школьный журнал
- Таблица **Ученики** (ID ученика, Имя, Класс).
- Таблица **Оценки** (ID оценки, ID ученика, Предмет, Оценка).
- Вместо того чтобы писать имя ученика в каждой оценке, мы ставим `ID ученика` как ссылку (Внешний ключ / Foreign Key).

---

## ❓ Вопросы для самопроверки
1. **Одна запись:** Что хранит одна строка в таблице `phrases`? (Ответ: Одну отправленную фразу рассказа).
2. **Внешний ключ:** Зачем поле `host_id` в `rooms` ссылается на `users.id`? (Ответ: Чтобы знать, кто ведущий комнаты, без дублирования его имени и данных).
3. **Каскадное удаление:** Что произойдет с фразами в `phrases`, если удалить комнату в `rooms`? (Ответ: Они автоматически удалятся благодаря `ON DELETE CASCADE`).
