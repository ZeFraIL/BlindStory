package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private boolean isRegistrationMode = false;

    private TextView tvAuthTitle;
    private TextInputLayout tilAuthDisplayName;
    private TextInputLayout tilAuthConfirmPassword;
    private TextInputEditText etAuthEmail;
    private TextInputEditText etAuthPassword;
    private TextInputEditText etAuthConfirmPassword;
    private TextInputEditText etAuthDisplayName;

    private Button btnAuthPrimary;
    private TextView tvAuthToggleMode;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_auth);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_auth), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
        updateUiMode();
    }

    private void initViews() {
        tvAuthTitle = findViewById(R.id.tv_auth_title);
        tilAuthDisplayName = findViewById(R.id.til_auth_display_name);
        tilAuthConfirmPassword = findViewById(R.id.til_auth_confirm_password);

        etAuthEmail = findViewById(R.id.et_auth_email);
        etAuthPassword = findViewById(R.id.et_auth_password);
        etAuthConfirmPassword = findViewById(R.id.et_auth_confirm_password);
        etAuthDisplayName = findViewById(R.id.et_auth_display_name);

        btnAuthPrimary = findViewById(R.id.btn_auth_primary);
        Button btnAuthGoogle = findViewById(R.id.btn_auth_google);
        tvAuthToggleMode = findViewById(R.id.tv_auth_toggle_mode);

        btnAuthGoogle.setOnClickListener(v -> handleGoogleAuthAction());
    }

    private void setupListeners() {
        tvAuthToggleMode.setOnClickListener(v -> {
            isRegistrationMode = !isRegistrationMode;
            updateUiMode();
        });

        btnAuthPrimary.setOnClickListener(v -> handlePrimaryAuthAction());
    }

    private void updateUiMode() {
        if (isRegistrationMode) {
            tvAuthTitle.setText(R.string.auth_title_register);
            tilAuthDisplayName.setVisibility(View.VISIBLE);
            tilAuthConfirmPassword.setVisibility(View.VISIBLE);
            btnAuthPrimary.setText(R.string.auth_btn_register);
            tvAuthToggleMode.setText(R.string.auth_toggle_to_login);
        } else {
            tvAuthTitle.setText(R.string.auth_title_login);
            tilAuthDisplayName.setVisibility(View.GONE);
            tilAuthConfirmPassword.setVisibility(View.GONE);
            btnAuthPrimary.setText(R.string.auth_btn_login);
            tvAuthToggleMode.setText(R.string.auth_toggle_to_register);
        }
    }

    private void handlePrimaryAuthAction() {
        String email = etAuthEmail.getText() != null ? etAuthEmail.getText().toString().trim() : "";
        String password = etAuthPassword.getText() != null ? etAuthPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.auth_err_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isRegistrationMode) {
            String confirmPassword = etAuthConfirmPassword.getText() != null ? etAuthConfirmPassword.getText().toString().trim() : "";
            String displayName = etAuthDisplayName.getText() != null ? etAuthDisplayName.getText().toString().trim() : "";

            if (TextUtils.isEmpty(displayName) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, R.string.auth_err_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Objects.equals(password, confirmPassword)) {
                Toast.makeText(this, R.string.auth_err_password_match, Toast.LENGTH_SHORT).show();
                return;
            }

            AuthManager.saveSession(this, email, displayName, "email");
            Toast.makeText(this, R.string.auth_success_register, Toast.LENGTH_SHORT).show();
        } else {
            String displayName = !TextUtils.isEmpty(email) && email.contains("@") ? email.split("@")[0] : "Player";
            AuthManager.saveSession(this, email, displayName, "email");
            Toast.makeText(this, R.string.auth_success_login, Toast.LENGTH_SHORT).show();
        }

        navigateToMainActivity();
    }

    private void handleGoogleAuthAction() {
        String googleEmail = "user@gmail.com";
        String googleName = "Google Player";
        AuthManager.saveSession(this, googleEmail, googleName, "google");
        Toast.makeText(this, R.string.auth_success_login, Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}