package btl.phamphilong.carogame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private EditText etPlayerX, etPlayerO;
    private SharedPreferences sharedPreferences;
    private boolean isMusicEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        etPlayerX = findViewById(R.id.etPlayerX);
        etPlayerO = findViewById(R.id.etPlayerO);
        Button btnPvP = findViewById(R.id.btnPvP);
        Button btnPvC = findViewById(R.id.btnPvC);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnHistory = findViewById(R.id.btnHistory);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        isMusicEnabled = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("musicEnabled", true);
        loadPlayerNames();

        if (isMusicEnabled) {
            MusicManager.startBackgroundMusic(this);
        }

        btnPvP.setOnClickListener(v -> {
            savePlayerNames();
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("game_mode", "pvp");
            intent.putExtra("player_x", etPlayerX.getText().toString());
            intent.putExtra("player_o", etPlayerO.getText().toString());
            intent.putExtra("sound_enabled", isMusicEnabled);
            startActivity(intent);
        });

        btnPvC.setOnClickListener(v -> {
            savePlayerNames();
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.putExtra("game_mode", "pvc");
            intent.putExtra("player_x", etPlayerX.getText().toString());
            intent.putExtra("player_o", "Computer");
            intent.putExtra("sound_enabled", isMusicEnabled);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void savePlayerNames() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("player_x", etPlayerX.getText().toString());
        editor.putString("player_o", etPlayerO.getText().toString());
        editor.apply();
    }

    private void loadPlayerNames() {
        etPlayerX.setText(sharedPreferences.getString("player_x", "Player X"));
        etPlayerO.setText(sharedPreferences.getString("player_o", "Player O"));
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
        // Không cần xử lý MediaPlayer nữa vì MusicManager đã đảm nhận
    }
}
