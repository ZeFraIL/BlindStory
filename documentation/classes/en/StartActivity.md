# Class Description: StartActivity

#### 1. General Information
- **Class Name:** `StartActivity`
- **Type:** Activity
- **Role in Application:** Serves as the app splash screen. Displays the game branding, a button to navigate manually to Language Selection, and a 4-second auto-redirect timer.
- **Interaction:** Uses `LocaleHelper` to load saved locale and launches `LanguageActivity` via `Intent`.

#### 2. Fields

| Name | Type | Purpose | Used Where |
|------|------|---------|------------|
| `SPLASH_DURATION_MS` | `long` | Stores timer delay (4000ms = 4 seconds) | In `startAutoRedirectTimer()` |
| `countDownTimer` | `CountDownTimer` | Manages 4-second countdown | In timer logic and `onDestroy()` |
| `tvStartTimer` | `TextView` | Displays remaining countdown seconds | In timer callbacks |
| `isNavigated` | `boolean` | Flag preventing double navigation | In `navigateToLanguageActivity()` |

#### 3. Methods

**Method Name:** `attachBaseContext`
- **Visibility:** `protected` | **Return:** `void` | **Parameters:** `Context newBase`
- **Logic:** Retrieves saved locale via `LocaleHelper.getSavedLanguage()` and updates `Context` resources before activity launch.

**Method Name:** `onCreate`
- **Visibility:** `protected` | **Return:** `void` | **Parameters:** `Bundle savedInstanceState`
- **Logic:** Enables edge-to-edge layout, inflates `activity_start.xml`, binds views, and starts the 4-second timer.

**Method Name:** `navigateToLanguageActivity`
- **Visibility:** `private synchronized` | **Return:** `void`
- **Logic:** Cancels timer, sets `isNavigated = true`, launches `LanguageActivity`, and calls `finish()`.

#### 4. Lifecycle
- `onCreate()`: UI binding and timer startup.
- `onDestroy()`: Cancels `countDownTimer` to prevent memory leaks.

#### 5. Simplified Explanation
**Like movie opening credits:** Shows the game title for 4 seconds so you know you've arrived, then moves forward automatically—or lets you tap to skip immediately!
