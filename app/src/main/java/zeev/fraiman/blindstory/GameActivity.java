package zeev.fraiman.blindstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_NAME = "extra_room_name";
    public static final String EXTRA_STARTING_PHRASE = "extra_starting_phrase";
    public static final String EXTRA_TOTAL_ROUNDS = "extra_total_rounds";

    private String roomName;
    private String startingPhrase;
    private int totalRounds = 2;
    private int totalPhrases = 6;
    private int currentPhraseNumber = 1;

    private TextView tvGameProgress;
    private TextView tvGameTimer;
    private TextView tvPrevPhraseText;
    private TextInputEditText etUserPhrase;
    private TextView tvCharCounter;
    private Button btnSubmitPhrase;
    private TextView tvPhraseAccepted;
    private LinearLayout llMyTurnContainer;
    private MaterialCardView cardOtherTurn;
    private Button btnProposeEnd;
    private TextView tvSkipTurn;

    private CountDownTimer turnTimer;
    private static final long TURN_DURATION_MS = 60000; // 60 seconds for demo turn
    private final List<String> storyPhrases = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_game);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_game), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuHelper.setupMenuIcon(this, findViewById(R.id.ib_top_menu));

        readIntentData();
        initViews();
        setupInputWatcher();
        setupListeners();

        storyPhrases.add(startingPhrase);
        updateTurnUi();
    }

    private void readIntentData() {
        roomName = getIntent().getStringExtra(EXTRA_ROOM_NAME);
        if (roomName == null) roomName = "Космос";

        startingPhrase = getIntent().getStringExtra(EXTRA_STARTING_PHRASE);
        if (startingPhrase == null || startingPhrase.trim().isEmpty()) {
            startingPhrase = "Однажды в далекой галактике капитан увидел странный свет...";
        }

        totalRounds = getIntent().getIntExtra(EXTRA_TOTAL_ROUNDS, 2);
        totalPhrases = totalRounds * 3; // 3 players * totalRounds
    }

    private void initViews() {
        tvGameProgress = findViewById(R.id.tv_game_progress);
        tvGameTimer = findViewById(R.id.tv_game_timer);
        tvPrevPhraseText = findViewById(R.id.tv_prev_phrase_text);

        llMyTurnContainer = findViewById(R.id.ll_my_turn_container);
        etUserPhrase = findViewById(R.id.et_user_phrase);
        tvCharCounter = findViewById(R.id.tv_char_counter);
        btnSubmitPhrase = findViewById(R.id.btn_submit_phrase);
        tvPhraseAccepted = findViewById(R.id.tv_phrase_accepted);

        cardOtherTurn = findViewById(R.id.card_other_turn);

        btnProposeEnd = findViewById(R.id.btn_propose_end);
        tvSkipTurn = findViewById(R.id.tv_skip_turn);
    }

    private void setupInputWatcher() {
        tvCharCounter.setText(getString(R.string.game_char_counter, 0, 180));

        etUserPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s != null ? s.length() : 0;
                tvCharCounter.setText(getString(R.string.game_char_counter, length, 180));

                if (length > 180) {
                    tvCharCounter.setTextColor(ContextCompat.getColor(GameActivity.this, R.color.warning_red));
                } else {
                    tvCharCounter.setTextColor(ContextCompat.getColor(GameActivity.this, R.color.text_secondary));
                }

                btnSubmitPhrase.setEnabled(length >= 2 && length <= 180);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners() {
        btnSubmitPhrase.setOnClickListener(v -> submitPhrase());

        btnProposeEnd.setOnClickListener(v -> showVotingDialog());

        tvSkipTurn.setOnClickListener(v -> showSkipTurnConfirmation());
    }

    private void updateTurnUi() {
        tvGameProgress.setText(getString(R.string.game_turn_title, currentPhraseNumber, totalPhrases));

        String lastPhrase = storyPhrases.get(storyPhrases.size() - 1);
        tvPrevPhraseText.setText(lastPhrase);

        llMyTurnContainer.setVisibility(View.VISIBLE);
        cardOtherTurn.setVisibility(View.GONE);
        etUserPhrase.setEnabled(true);
        etUserPhrase.setText("");
        tvPhraseAccepted.setVisibility(View.GONE);

        startTurnTimer();
    }

    private void startTurnTimer() {
        if (turnTimer != null) {
            turnTimer.cancel();
        }

        turnTimer = new CountDownTimer(TURN_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                String timerText = String.format(Locale.getDefault(), "00:%02d", secondsLeft);
                tvGameTimer.setText(timerText);

                if (secondsLeft <= 10) {
                    tvGameTimer.setTextColor(ContextCompat.getColor(GameActivity.this, R.color.warning_red));
                } else {
                    tvGameTimer.setTextColor(ContextCompat.getColor(GameActivity.this, R.color.text_primary));
                }
            }

            @Override
            public void onFinish() {
                tvGameTimer.setText(String.format(Locale.getDefault(), "00:%02d", 0));
                Toast.makeText(GameActivity.this, R.string.game_skip_confirm_msg, Toast.LENGTH_SHORT).show();
                advanceToNextTurn("...");
            }
        }.start();
    }

    private void submitPhrase() {
        if (etUserPhrase.getText() == null) return;
        String userPhrase = etUserPhrase.getText().toString().trim();
        if (userPhrase.length() < 2) return;

        etUserPhrase.setEnabled(false);
        btnSubmitPhrase.setEnabled(false);
        tvPhraseAccepted.setVisibility(View.VISIBLE);

        if (turnTimer != null) {
            turnTimer.cancel();
        }

        findViewById(android.R.id.content).postDelayed(() -> advanceToNextTurn(userPhrase), 1500);
    }

    private void advanceToNextTurn(String submittedPhrase) {
        storyPhrases.add(submittedPhrase);
        currentPhraseNumber++;

        if (currentPhraseNumber > totalPhrases) {
            Intent intent = new Intent(GameActivity.this, FinalStoryActivity.class);
            intent.putExtra(FinalStoryActivity.EXTRA_ROOM_NAME, roomName);
            intent.putStringArrayListExtra(FinalStoryActivity.EXTRA_STORY_PHRASES, new ArrayList<>(storyPhrases));
            startActivity(intent);
            finish();
            return;
        }

        updateTurnUi();
    }

    private void showVotingDialog() {
        String[] options = new String[]{
                getString(R.string.vote_option_round),
                getString(R.string.vote_option_immediate)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.vote_title);
        builder.setSingleChoiceItems(options, 0, null);
        builder.setPositiveButton(R.string.dialog_join_btn, (dialog, which) -> {
            Toast.makeText(this, R.string.vote_submitted, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GameActivity.this, FinalStoryActivity.class);
            intent.putExtra(FinalStoryActivity.EXTRA_ROOM_NAME, roomName);
            intent.putStringArrayListExtra(FinalStoryActivity.EXTRA_STORY_PHRASES, new ArrayList<>(storyPhrases));
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showSkipTurnConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_skip_confirm_title);
        builder.setMessage(R.string.game_skip_confirm_msg);
        builder.setPositiveButton(R.string.game_skip_confirm_btn, (dialog, which) -> advanceToNextTurn("..."));
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (turnTimer != null) {
            turnTimer.cancel();
        }
    }
}