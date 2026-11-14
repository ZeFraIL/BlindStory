package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {

    private static final String PREF_AUTH = "blind_story_auth";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_AUTH_PROVIDER = "auth_provider";

    public static String getSupabaseUrl() {
        return SupabaseConfig.SUPABASE_URL;
    }

    public static String getSupabaseAnonKey() {
        return SupabaseConfig.SUPABASE_ANON_KEY;
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static void saveSession(Context context, String email, String displayName, String provider) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_DISPLAY_NAME, displayName)
                .putString(KEY_AUTH_PROVIDER, provider)
                .apply();
    }

    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public static String getDisplayName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        return prefs.getString(KEY_DISPLAY_NAME, "Player");
    }

    public static String getAuthProvider(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AUTH_PROVIDER, "email");
    }
}