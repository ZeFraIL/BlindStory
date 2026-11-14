# Class Description: LanguageActivity

#### 1. General Information
- **Class Name:** `LanguageActivity`
- **Type:** Activity
- **Role in Application:** Interface language selector offering 4 languages (English, Russian, Hebrew, Lithuanian). Defaults to English on first launch, waits 5 seconds, saves choice to `SharedPreferences`, and applies `Locale`.
- **Interaction:** Uses `LocaleHelper` for locale application, `AuthManager` to check auth status, and redirects to `AuthActivity` or `MainActivity`.

#### 2. Fields

| Name | Type | Purpose |
|------|------|---------|
| `WAIT_DURATION_MS` | `long` | 5-second countdown delay (5000ms) |
| `countDownTimer` | `CountDownTimer` | Manages 5s countdown |
| `selectedLanguageCode` | `String` | Selected language code (`"en"`, `"ru"`, `"he"`, `"lt"`) |

#### 3. Key Methods
- `onCreate()`: Sets up radio options, checks saved language, and starts 5s timer.
- `navigateToNextScreen()`: Saves locale with `LocaleHelper.applyLocale()`, checks `AuthManager.isLoggedIn()`, and launches `AuthActivity` or `MainActivity`.

#### 4. Simplified Explanation
**Like a TV language selector:** Asks which language you prefer. If you don't tap anything in 5 seconds, it continues in English or your previously selected language!
