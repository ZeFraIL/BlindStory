package zeev.fraiman.blindstory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;

public class FinalStoryActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_NAME = "extra_room_name";
    public static final String EXTRA_STORY_PHRASES = "extra_story_phrases";
    public static final String EXTRA_STORY_AUTHORS = "extra_story_authors";

    private String roomName;
    private ArrayList<String> storyPhrases;
    private ArrayList<String> storyAuthors;

    private TextView tvFinalTitle;
    private LinearLayout llStoryPhrases;
    private Button btnAiAnalysis;
    private MaterialCardView cardAiResult;

    private TextView tvAiTitles;
    private TextView tvAiSummary;
    private TextView tvAiFunniest;
    private TextView tvAiTwist;
    private TextView tvAiCoherence;
    private TextView tvAiGenre;
    private TextView tvAiMoral;

    private Button btnLaugh;
    private Button btnSurprise;
    private Button btnFire;
    private Button btnHeart;

    private int laughCount = 0;
    private int surpriseCount = 0;
    private int fireCount = 0;
    private int heartCount = 0;

    private boolean isLaughActive = false;
    private boolean isSurpriseActive = false;
    private boolean isFireActive = false;
    private boolean isHeartActive = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getSavedLanguage(newBase);
        super.attachBaseContext(LocaleHelper.updateResources(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_final_story);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_final_story), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MenuHelper.setupMenuIcon(this, findViewById(R.id.ib_top_menu));

        readIntentData();
        initViews();
        displayStoryPhrases();
        setupReactions();
        setupAiAnalysis();
        setupBottomActions();
    }

    private void readIntentData() {
        Intent intent = getIntent();
        roomName = intent.getStringExtra(EXTRA_ROOM_NAME);
        if (roomName == null) roomName = "Космос";

        storyPhrases = intent.getStringArrayListExtra(EXTRA_STORY_PHRASES);
        if (storyPhrases == null || storyPhrases.isEmpty()) {
            storyPhrases = new ArrayList<>();
            storyPhrases.add("Однажды в далекой галактике капитан увидел странный свет.");
            storyPhrases.add("Он решил, что ключ открывает портал в прошлое.");
            storyPhrases.add("Корабль внезапно набрал огромную скорость.");
            storyPhrases.add("Кот Маркиз случайно наступил на красную кнопку.");
            storyPhrases.add("Портал открылся прямо посередине уютной кухни.");
            storyPhrases.add("Так они нашли самый вкусный космический ужин.");
        }

        storyAuthors = intent.getStringArrayListExtra(EXTRA_STORY_AUTHORS);
        if (storyAuthors == null || storyAuthors.size() < storyPhrases.size()) {
            storyAuthors = new ArrayList<>();
            String[] defaultAuthors = {"Анна", "Марк", "Марина", "Алексей", "Дмитрий"};
            int phraseCount = storyPhrases.size();
            for (int index = 0; index < phraseCount; index++) {
                storyAuthors.add(defaultAuthors[index % defaultAuthors.length]);
            }
        }
    }

    private void initViews() {
        tvFinalTitle = findViewById(R.id.tv_final_title);
        TextView tvFinalStatus = findViewById(R.id.tv_final_status);
        llStoryPhrases = findViewById(R.id.ll_story_phrases);
        btnAiAnalysis = findViewById(R.id.btn_ai_analysis);
        cardAiResult = findViewById(R.id.card_ai_result);

        tvAiTitles = findViewById(R.id.tv_ai_titles);
        tvAiSummary = findViewById(R.id.tv_ai_summary);
        tvAiFunniest = findViewById(R.id.tv_ai_funniest);
        tvAiTwist = findViewById(R.id.tv_ai_twist);
        tvAiCoherence = findViewById(R.id.tv_ai_coherence);
        tvAiGenre = findViewById(R.id.tv_ai_genre);
        tvAiMoral = findViewById(R.id.tv_ai_moral);

        btnLaugh = findViewById(R.id.btn_reaction_laugh);
        btnSurprise = findViewById(R.id.btn_reaction_surprise);
        btnFire = findViewById(R.id.btn_reaction_fire);
        btnHeart = findViewById(R.id.btn_reaction_heart);

        tvFinalTitle.setText(getString(R.string.final_title, roomName));
        tvFinalStatus.setText(getString(R.string.final_status_completed, storyPhrases.size()));
    }

    private void displayStoryPhrases() {
        llStoryPhrases.removeAllViews();
        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < storyPhrases.size(); i++) {
            String phraseText = (i + 1) + ". " + storyPhrases.get(i);
            String authorText = "— " + storyAuthors.get(i);

            TextView tvPhrase = new TextView(this);
            tvPhrase.setText(phraseText);
            tvPhrase.setTextSize(18);
            tvPhrase.setLineSpacing(0f, 1.2f);
            tvPhrase.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            TextView tvAuthor = new TextView(this);
            tvAuthor.setText(authorText);
            tvAuthor.setTextSize(13);
            tvAuthor.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            LinearLayout.LayoutParams authorParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            authorParams.setMargins(0, (int) (2 * density), 0, (int) (16 * density));
            tvAuthor.setLayoutParams(authorParams);

            llStoryPhrases.addView(tvPhrase);
            llStoryPhrases.addView(tvAuthor);
        }
    }

    private void setupReactions() {
        btnLaugh.setOnClickListener(v -> {
            isLaughActive = !isLaughActive;
            laughCount += isLaughActive ? 1 : -1;
            btnLaugh.setText(String.format(Locale.getDefault(), "😂 %d", laughCount));
        });

        btnSurprise.setOnClickListener(v -> {
            isSurpriseActive = !isSurpriseActive;
            surpriseCount += isSurpriseActive ? 1 : -1;
            btnSurprise.setText(String.format(Locale.getDefault(), "😮 %d", surpriseCount));
        });

        btnFire.setOnClickListener(v -> {
            isFireActive = !isFireActive;
            fireCount += isFireActive ? 1 : -1;
            btnFire.setText(String.format(Locale.getDefault(), "🔥 %d", fireCount));
        });

        btnHeart.setOnClickListener(v -> {
            isHeartActive = !isHeartActive;
            heartCount += isHeartActive ? 1 : -1;
            btnHeart.setText(String.format(Locale.getDefault(), "❤️ %d", heartCount));
        });
    }

    private void setupAiAnalysis() {
        btnAiAnalysis.setOnClickListener(v -> {
            cardAiResult.setVisibility(View.VISIBLE);

            tvAiTitles.setText(getString(R.string.final_ai_suggested_titles) + "\n1. «Космический ключ»\n2. «Тайна кухонного портала»\n3. «Маркиз и гиперпривод»");
            tvAiSummary.setText(getString(R.string.final_ai_summary) + " Экипаж исследовал загадочный свет, когда кот спровоцировал портал в кухонный ужин.");
            tvAiFunniest.setText(getString(R.string.final_ai_funniest) + " Момент с котом Маркизом на красной кнопке.");
            tvAiTwist.setText(getString(R.string.final_ai_twist) + " Портал открылся прямо в кухонном шкафу.");
            tvAiCoherence.setText(getString(R.string.final_ai_coherence) + " ⭐⭐⭐⭐⭐ (5/5)");
            tvAiGenre.setText(getString(R.string.final_ai_genre) + " Космическая юмористическая фантастика");
            tvAiMoral.setText(getString(R.string.final_ai_moral) + " Главное в любом путешествии — вкусно поесть!");

            findViewById(R.id.main_final_story).post(() -> cardAiResult.getParent().requestChildFocus(cardAiResult, cardAiResult));
        });
    }

    private void setupBottomActions() {
        Button btnRematch = findViewById(R.id.btn_rematch);
        Button btnCopyStory = findViewById(R.id.btn_copy_story);

        btnRematch.setOnClickListener(v -> {
            Intent intent = new Intent(FinalStoryActivity.this, CreateRoomActivity.class);
            startActivity(intent);
            finish();
        });

        btnCopyStory.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.app_name)).append(" — ").append(roomName).append("\n\n");

            for (int i = 0; i < storyPhrases.size(); i++) {
                sb.append(i + 1).append(". ").append(storyPhrases.get(i)).append("\n");
                sb.append("— ").append(storyAuthors.get(i)).append("\n\n");
            }

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Story", sb.toString());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.final_story_copied, Toast.LENGTH_SHORT).show();
            }
        });
    }
}