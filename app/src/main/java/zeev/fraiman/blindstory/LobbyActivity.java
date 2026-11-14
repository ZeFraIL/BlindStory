package zeev.fraiman.blindstory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LobbyActivity extends AppCompatActivity {

    private String roomName;
    private String roomTopic;
    private String roomCode;
    private int rounds;
    private int timeLimitMins;
    private boolean aiEnabled;

    private TextView tvRoomTitle;
    private TextView tvTopic;
    private TextView tvRoomCode;
    private TextView tvPlayersHeader;
    private LinearLayout llPlayersList;
    private Button btnStartGame;

    private final List<PlayerItem> playersList = new ArrayList<>();
    private final String[] sampleBotNames = {"Марина", "Марк", "Алексей", "Анна", "Дмитрий"};
    private int botIndex = 0;

    private static class PlayerItem {
        String name;
        boolean isHost;

        PlayerItem(String name, boolean isHost) {
            this.name = name;
            this.isHost = isHost;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_lobby);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_lobby), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuHelper.setupMenuIcon(this, findViewById(R.id.ib_top_menu));

        readIntentData();
        initViews();
        setupHostPlayer();
        updatePlayersUi();
    }

    private void readIntentData() {
        Intent intent = getIntent();
        roomName = intent.getStringExtra(CreateRoomActivity.EXTRA_ROOM_NAME);
        if (roomName == null) roomName = "Космос";

        roomTopic = intent.getStringExtra(CreateRoomActivity.EXTRA_ROOM_TOPIC);
        if (roomTopic == null) roomTopic = roomName;

        roomCode = intent.getStringExtra(CreateRoomActivity.EXTRA_ROOM_CODE);
        if (roomCode == null) roomCode = "KOT123";

        rounds = intent.getIntExtra(CreateRoomActivity.EXTRA_ROUNDS, 2);
        timeLimitMins = intent.getIntExtra(CreateRoomActivity.EXTRA_TIME_LIMIT_MINS, 15);
        aiEnabled = intent.getBooleanExtra(CreateRoomActivity.EXTRA_AI_ENABLED, false);
    }

    private void initViews() {
        tvRoomTitle = findViewById(R.id.tv_lobby_room_title);
        tvTopic = findViewById(R.id.tv_lobby_topic);
        TextView tvDetails = findViewById(R.id.tv_lobby_details);
        tvRoomCode = findViewById(R.id.tv_lobby_room_code);
        tvPlayersHeader = findViewById(R.id.tv_players_count_header);
        llPlayersList = findViewById(R.id.ll_players_list);
        btnStartGame = findViewById(R.id.btn_start_game);

        Button btnCopyCode = findViewById(R.id.btn_copy_code);
        Button btnShareLink = findViewById(R.id.btn_share_link);
        Button btnAddSimulatedPlayer = findViewById(R.id.btn_add_simulated_player);

        tvRoomTitle.setText(getString(R.string.lobby_title, roomName));
        tvTopic.setText(roomTopic);

        String timeStr = timeLimitMins > 0 ? timeLimitMins + " min" : getString(R.string.time_unlimited);
        String aiStr = aiEnabled ? "ON" : "OFF";
        String summary = String.format(Locale.getDefault(), "%d Rounds • %s • AI: %s", rounds, timeStr, aiStr);
        tvDetails.setText(summary);
        tvRoomCode.setText(roomCode);

        btnCopyCode.setOnClickListener(v -> copyCodeToClipboard());
        btnShareLink.setOnClickListener(v -> shareInvitationLink());
        btnAddSimulatedPlayer.setOnClickListener(v -> addSimulatedBot());
        btnStartGame.setOnClickListener(v -> startGame());
    }

    private void setupHostPlayer() {
        String hostName = AuthManager.getDisplayName(this);
        playersList.add(new PlayerItem(hostName, true));
    }

    private void addSimulatedBot() {
        if (botIndex < sampleBotNames.length) {
            playersList.add(new PlayerItem(sampleBotNames[botIndex], false));
            botIndex++;
            updatePlayersUi();
        } else {
            Toast.makeText(this, "Maximum simulated players added", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePlayersUi() {
        int count = playersList.size();
        tvPlayersHeader.setText(getString(R.string.lobby_players_title, count));

        llPlayersList.removeAllViews();

        float density = getResources().getDisplayMetrics().density;
        int paddingPx = (int) (12 * density);

        for (PlayerItem player : playersList) {
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, (int) (6 * density), 0, (int) (6 * density));
            card.setLayoutParams(cardParams);
            card.setRadius(12 * density);
            card.setCardElevation(1 * density);

            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            // Avatar circle
            TextView avatar = new TextView(this);
            int avatarSize = (int) (40 * density);
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
            avatar.setLayoutParams(avatarParams);
            avatar.setGravity(Gravity.CENTER);
            avatar.setTextColor(getResources().getColor(R.color.white, getTheme()));
            avatar.setTextSize(18);
            avatar.setTypeface(null, android.graphics.Typeface.BOLD);
            avatar.setBackgroundResource(R.drawable.ic_launcher_background);

            String initial = !TextUtils.isEmpty(player.name) ? player.name.substring(0, 1).toUpperCase() : "P";
            avatar.setText(initial);

            // Name
            TextView tvName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            );
            nameParams.setMarginStart((int) (12 * density));
            tvName.setLayoutParams(nameParams);
            tvName.setText(player.name);
            tvName.setTextSize(16);
            tvName.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));

            row.addView(avatar);
            row.addView(tvName);

            // Host badge
            if (player.isHost) {
                TextView badge = new TextView(this);
                badge.setText(getString(R.string.lobby_host_badge));
                badge.setTextSize(12);
                badge.setTextColor(getResources().getColor(R.color.game_green_dark, getTheme()));
                badge.setBackgroundResource(R.drawable.ic_launcher_background);
                badge.setPadding((int) (8 * density), (int) (4 * density), (int) (8 * density), (int) (4 * density));
                row.addView(badge);
            }

            card.addView(row);
            llPlayersList.addView(card);
        }

        if (count >= 3) {
            btnStartGame.setEnabled(true);
            btnStartGame.setText(getString(R.string.btn_start_game));
        } else {
            btnStartGame.setEnabled(false);
            btnStartGame.setText(getString(R.string.btn_need_3_players, count));
        }
    }

    private void copyCodeToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Room Code", roomCode);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.code_copied, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareInvitationLink() {
        String shareText = getString(R.string.app_name) + "\n" +
                getString(R.string.lobby_title, roomName) + "\n" +
                getString(R.string.lobby_code_label) + " " + roomCode;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void startGame() {
        Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_ROOM_NAME, roomName);
        intent.putExtra(GameActivity.EXTRA_STARTING_PHRASE, getIntent().getStringExtra(CreateRoomActivity.EXTRA_STARTING_PHRASE));
        intent.putExtra(GameActivity.EXTRA_TOTAL_ROUNDS, rounds);
        startActivity(intent);
    }
}