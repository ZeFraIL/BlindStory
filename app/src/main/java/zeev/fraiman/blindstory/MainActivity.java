package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuHelper.setupMenuIcon(this, findViewById(R.id.ib_top_menu));

        Button btnCreateRoom = findViewById(R.id.btn_create_room);
        Button btnJoinByCode = findViewById(R.id.btn_join_by_code);
        Button btnMyStories = findViewById(R.id.btn_my_stories);
        Button btnProfile = findViewById(R.id.btn_profile);

        btnCreateRoom.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
            startActivity(intent);
        });

        btnJoinByCode.setOnClickListener(v -> showJoinByCodeDialog());
        btnMyStories.setOnClickListener(v -> showComingSoonToast(getString(R.string.main_my_stories)));
        btnProfile.setOnClickListener(v -> showProfileDialog());
    }

    private void showJoinByCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_join_title);

        final EditText input = new EditText(this);
        input.setHint(R.string.dialog_join_hint);
        input.setSingleLine(true);

        builder.setView(input);

        builder.setPositiveButton(R.string.dialog_join_btn, (dialog, which) -> {
            String roomCode = input.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(roomCode)) {
                Toast.makeText(MainActivity.this, R.string.code_empty_error, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
                intent.putExtra(CreateRoomActivity.EXTRA_ROOM_NAME, roomCode);
                intent.putExtra(CreateRoomActivity.EXTRA_ROOM_CODE, roomCode);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showProfileDialog() {
        String name = AuthManager.getDisplayName(this);
        String email = AuthManager.getUserEmail(this);
        String message = name + "\n" + (TextUtils.isEmpty(email) ? "" : email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.main_profile);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.auth_btn_logout, (dialog, which) -> {
            AuthManager.clearSession(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showComingSoonToast(String featureName) {
        Toast.makeText(this, featureName + " — " + getString(R.string.feature_coming_soon), Toast.LENGTH_SHORT).show();
    }
}