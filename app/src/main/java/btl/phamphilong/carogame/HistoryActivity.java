package btl.phamphilong.carogame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoryActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayList;

    private static final String PREF_NAME = "game_history";
    private static final String KEY_HISTORY = "history_items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isMusicEnabled = settings.getBoolean("musicEnabled", true);

        // Bắt đầu nhạc nền nếu được bật
        if (isMusicEnabled) {
            MusicManager.startBackgroundMusic(this);
        }

        listView = findViewById(R.id.historyListView);
        displayList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Set<String> savedHistory = prefs.getStringSet(KEY_HISTORY, new HashSet<>());

        // Nhận dữ liệu từ Intent nếu có trận thắng mới
        String newWinner = getIntent().getStringExtra("winnerName");
        int winnerMoves = getIntent().getIntExtra("winnerMoves", 0);
        String totalTime = getIntent().getStringExtra("totalTime");
        int boardSize = getIntent().getIntExtra("boardSize", 5);

        if (newWinner != null && totalTime != null) {
            String newEntry = "Người thắng: " + newWinner + "\n"
                    + "Số nước đi: " + winnerMoves + "\n"
                    + "Tổng thời gian: " + totalTime + "\n"
                    + "Kích thước bàn cờ: " + boardSize;

            displayList.add(newEntry);

            Set<String> updatedHistory = new HashSet<>(savedHistory);
            updatedHistory.add(newEntry);
            prefs.edit().putStringSet(KEY_HISTORY, updatedHistory).apply();
        }

        for (String entry : savedHistory) {
            if (!displayList.contains(entry)) {
                displayList.add(entry);
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        // Xử lý nút quay về Menu
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.resumeMusic(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không cần xử lý MediaPlayer thủ công nữa vì đã giao cho MusicManager
    }
}
 