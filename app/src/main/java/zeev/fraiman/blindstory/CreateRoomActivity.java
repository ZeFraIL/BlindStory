package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_NAME = "extra_room_name";
    public static final String EXTRA_ROOM_TOPIC = "extra_room_topic";
    public static final String EXTRA_STARTING_PHRASE = "extra_starting_phrase";
    public static final String EXTRA_ROUNDS = "extra_rounds";
    public static final String EXTRA_TIME_LIMIT_MINS = "extra_time_limit_mins";
    public static final String EXTRA_IS_PUBLIC = "extra_is_public";
    public static final String EXTRA_AI_ENABLED = "extra_ai_enabled";
    public static final String EXTRA_AI_SHOW_NAMES = "extra_ai_show_names";
    public static final String EXTRA_ROOM_CODE = "extra_room_code";

    private TextInputEditText etRoomName;
    private TextInputEditText etRoomTopic;
    private TextInputEditText etStartingPhrase;
    private RadioGroup rgRounds;
    private RadioGroup rgTimeLimit;
    private RadioGroup rgPrivacy;
    private TextView tvRoundsWarning;
    private MaterialSwitch switchAiEnabled;
    private MaterialSwitch switchAiShowNames;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_create_room);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_create_room), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuHelper.setupMenuIcon(this, findViewById(R.id.ib_top_menu));

        initViews();
        setupListeners();
    }

    private void initViews() {
        etRoomName = findViewById(R.id.et_room_name);
        etRoomTopic = findViewById(R.id.et_room_topic);
        etStartingPhrase = findViewById(R.id.et_starting_phrase);
        rgRounds = findViewById(R.id.rg_rounds);
        rgTimeLimit = findViewById(R.id.rg_time_limit);
        rgPrivacy = findViewById(R.id.rg_privacy);
        tvRoundsWarning = findViewById(R.id.tv_rounds_warning);
        switchAiEnabled = findViewById(R.id.switch_ai_enabled);
        switchAiShowNames = findViewById(R.id.switch_ai_show_names);

        Button btnSubmit = findViewById(R.id.btn_create_room_submit);
        btnSubmit.setOnClickListener(v -> submitCreateRoom());
    }

    private void setupListeners() {
        rgRounds.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_rounds_3) {
                tvRoundsWarning.setVisibility(View.VISIBLE);
            } else {
                tvRoundsWarning.setVisibility(View.GONE);
            }
        });
    }

    private void submitCreateRoom() {
        String name = etRoomName.getText() != null ? etRoomName.getText().toString().trim() : "";
        String topic = etRoomTopic.getText() != null ? etRoomTopic.getText().toString().trim() : "";
        String startingPhrase = etStartingPhrase.getText() != null ? etStartingPhrase.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etRoomName.setError(getString(R.string.auth_err_empty));
            return;
        }

        int selectedRounds;
        int checkedRoundsId = rgRounds.getCheckedRadioButtonId();
        if (checkedRoundsId == R.id.rb_rounds_1) {
            selectedRounds = 1;
        } else if (checkedRoundsId == R.id.rb_rounds_3) {
            selectedRounds = 3;
        } else {
            selectedRounds = 2;
        }

        int timeLimitMins = 15;
        int checkedTimeId = rgTimeLimit.getCheckedRadioButtonId();
        if (checkedTimeId == R.id.rb_time_10m) {
            timeLimitMins = 10;
        } else if (checkedTimeId == R.id.rb_time_30m) {
            timeLimitMins = 30;
        } else if (checkedTimeId == R.id.rb_time_unlimited) {
            timeLimitMins = 0; // Unlimited
        }

        boolean isPublic = rgPrivacy.getCheckedRadioButtonId() == R.id.rb_privacy_public;
        boolean aiEnabled = switchAiEnabled.isChecked();
        boolean aiShowNames = switchAiShowNames.isChecked();

        String roomCode = generateRandomRoomCode();

        Intent intent = new Intent(CreateRoomActivity.this, LobbyActivity.class);
        intent.putExtra(EXTRA_ROOM_NAME, name);
        intent.putExtra(EXTRA_ROOM_TOPIC, topic);
        intent.putExtra(EXTRA_STARTING_PHRASE, startingPhrase);
        intent.putExtra(EXTRA_ROUNDS, selectedRounds);
        intent.putExtra(EXTRA_TIME_LIMIT_MINS, timeLimitMins);
        intent.putExtra(EXTRA_IS_PUBLIC, isPublic);
        intent.putExtra(EXTRA_AI_ENABLED, aiEnabled);
        intent.putExtra(EXTRA_AI_SHOW_NAMES, aiShowNames);
        intent.putExtra(EXTRA_ROOM_CODE, roomCode);

        startActivity(intent);
        finish();
    }

    private String generateRandomRoomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int count = 0;
        while (count < 6) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
            count++;
        }
        return sb.toString();
    }
}