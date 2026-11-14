package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 4000;
    private CountDownTimer countDownTimer;
    private TextView tvStartTimer;
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
        setContentView(R.layout.activity_start);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_start), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvStartTimer = findViewById(R.id.tv_start_timer);
        Button btnChooseLanguage = findViewById(R.id.btn_choose_language);

        btnChooseLanguage.setOnClickListener(v -> navigateToLanguageActivity());

        startAutoRedirectTimer();
    }

    private void startAutoRedirectTimer() {
        countDownTimer = new CountDownTimer(SPLASH_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) Math.ceil(millisUntilFinished / 1000.0);
                if (tvStartTimer != null) {
                    tvStartTimer.setText(getString(R.string.auto_redirect_splash, secondsLeft));
                }
            }

            @Override
            public void onFinish() {
                if (tvStartTimer != null) {
                    tvStartTimer.setText(getString(R.string.auto_redirect_splash, 0));
                }
                navigateToLanguageActivity();
            }
        }.start();
    }

    private synchronized void navigateToLanguageActivity() {
        if (isNavigated) {
            return;
        }
        isNavigated = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Intent intent = new Intent(StartActivity.this, LanguageActivity.class);
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