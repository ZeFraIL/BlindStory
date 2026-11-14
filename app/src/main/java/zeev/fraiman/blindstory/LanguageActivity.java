package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class LanguageActivity extends AppCompatActivity {

    private static final long WAIT_DURATION_MS = 5000;
    private CountDownTimer countDownTimer;
    private TextView tvLanguageTimer;
    private String selectedLanguageCode;
    private boolean isNavigated = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_language);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_language), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvLanguageTimer = findViewById(R.id.tv_language_timer);
        RadioGroup rgLanguages = findViewById(R.id.rg_languages);
        RadioButton rbEnglish = findViewById(R.id.rb_english);
        RadioButton rbRussian = findViewById(R.id.rb_russian);
        RadioButton rbHebrew = findViewById(R.id.rb_hebrew);
        RadioButton rbLithuanian = findViewById(R.id.rb_lithuanian);
        Button btnConfirmLanguage = findViewById(R.id.btn_confirm_language);

        selectedLanguageCode = LocaleHelper.getSavedLanguage(this);
        setupRadioGroupSelection(rbEnglish, rbRussian, rbHebrew, rbLithuanian);

        rgLanguages.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_english) {
                selectedLanguageCode = "en";
            } else if (checkedId == R.id.rb_russian) {
                selectedLanguageCode = "ru";
            } else if (checkedId == R.id.rb_hebrew) {
                selectedLanguageCode = "he";
            } else if (checkedId == R.id.rb_lithuanian) {
                selectedLanguageCode = "lt";
            }
        });

        btnConfirmLanguage.setOnClickListener(v -> navigateToNextScreen());

        startWaitTimer();
    }

    private void setupRadioGroupSelection(RadioButton rbEnglish, RadioButton rbRussian, RadioButton rbHebrew, RadioButton rbLithuanian) {
        switch (selectedLanguageCode) {
            case "ru":
                rbRussian.setChecked(true);
                break;
            case "he":
            case "iw":
                rbHebrew.setChecked(true);
                break;
            case "lt":
                rbLithuanian.setChecked(true);
                break;
            case "en":
            default:
                rbEnglish.setChecked(true);
                selectedLanguageCode = "en";
                break;
        }
    }

    private void startWaitTimer() {
        countDownTimer = new CountDownTimer(WAIT_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) Math.ceil(millisUntilFinished / 1000.0);
                if (tvLanguageTimer != null) {
                    tvLanguageTimer.setText(getString(R.string.auto_redirect_lang, secondsLeft));
                }
            }

            @Override
            public void onFinish() {
                if (tvLanguageTimer != null) {
                    tvLanguageTimer.setText(getString(R.string.auto_redirect_lang, 0));
                }
                navigateToNextScreen();
            }
        }.start();
    }

    private synchronized void navigateToNextScreen() {
        if (isNavigated) {
            return;
        }
        isNavigated = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        LocaleHelper.applyLocale(this, selectedLanguageCode);

        Intent intent;
        if (AuthManager.isLoggedIn(this)) {
            intent = new Intent(LanguageActivity.this, MainActivity.class);
        } else {
            intent = new Intent(LanguageActivity.this, AuthActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}