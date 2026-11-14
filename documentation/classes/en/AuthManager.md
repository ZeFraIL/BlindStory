# Class Description: AuthManager

#### 1. General Information
- **Class Name:** `AuthManager`
- **Type:** Utility Class
- **Role in Application:** Manages local user authentication state in `SharedPreferences` (`isLoggedIn`, `userEmail`, `displayName`, `authProvider`).
- **Interaction:** Called by `LanguageActivity`, `AuthActivity`, `MainActivity`, and `LobbyActivity`.

#### 2. Simplified Explanation
**Like a small pass in your wallet:** Remembers that you are logged in so you don't have to re-enter your details every time you open the app!
