package btl.phamphilong.carogame;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsActivity extends Activity {
    private Spinner spinnerBoardSize;
    private Switch musicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerBoardSize = findViewById(R.id.spinnerBoardSize);
        musicSwitch = findViewById(R.id.switchMusic);
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Thiết lập danh sách kích thước bàn cờ
        String[] boardSizes = {"5x5", "10x10", "15x15"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, boardSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBoardSize.setAdapter(adapter);

        // Đọc cài đặt hiện tại
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isMusicEnabled = preferences.getBoolean("musicEnabled", true);
        int currentBoardSize = preferences.getInt("boardSize", 5);

        // Gán giá trị đã lưu
        musicSwitch.setChecked(isMusicEnabled);
        if (currentBoardSize == 10) {
            spinnerBoardSize.setSelection(1);
        } else if (currentBoardSize == 15) {
            spinnerBoardSize.setSelection(2);
        } else {
            spinnerBoardSize.setSelection(0);
        }

        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Lưu trạng thái nhạc vào SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("musicEnabled", isChecked);
            editor.apply();

            // Bật hoặc tắt nhạc tương ứng
            if (isChecked) {
                MusicManager.startBackgroundMusic(SettingsActivity.this);
            } else {
                MusicManager.stopMusic();
                ResultActivity.stopMusicCompletely(); // Nếu win_sound đang phát, dừng luôn
            }
        });


        // Sự kiện nút lưu
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected = (String) spinnerBoardSize.getSelectedItem();
                int size = selected.equals("10x10") ? 10 : selected.equals("15x15") ? 15 : 5;

                boolean musicOn = musicSwitch.isChecked();
                SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                editor.putInt("boardSize", size);               // ✅ Lưu kích thước bàn cờ
                editor.putBoolean("musicEnabled", musicOn);     // ✅ Lưu trạng thái nhạc
                editor.apply();

                // Nếu tắt nhạc thì yêu cầu các activity khác dừng nhạc
                if (!musicOn) {
                    // Gọi hàm tĩnh dừng nhạc ở các activity có phát nhạc nền
                    ResultActivity.stopMusicCompletely();
                }

                // Trả về kết quả cho activity gọi
                Intent intent = new Intent();
                intent.putExtra("boardSize", size);
                setResult(RESULT_OK, intent);
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
        MusicManager.stopMusic();
    }


}
