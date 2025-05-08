package btl.phamphilong.carogame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private static MediaPlayer winSound;
    private boolean isSoundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvMessage = findViewById(R.id.tvMessage);
        TextView tvWinnerMoves = findViewById(R.id.tvWinnerMoves);
        TextView tvTotalTime = findViewById(R.id.tvTotalTime);
        TextView tvBoardSize = findViewById(R.id.tvBoardSize); // LIÊN KẾT VIEW
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        Button btnSaveHistory = findViewById(R.id.btnSaveHistory);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String result = intent.getStringExtra("RESULT");
        String message = intent.getStringExtra("MESSAGE");
        int winnerMoves = intent.getIntExtra("WINNER_MOVES", 0);
        int totalTime = intent.getIntExtra("TOTAL_TIME", 0); // đơn vị: giây
        isSoundEnabled = intent.getBooleanExtra("SOUND_ENABLED", true);
        int boardSize = intent.getIntExtra("BOARD_SIZE", 5); // LẤY DỮ LIỆU BÀN CỜ

        tvResult.setText(result);
        tvMessage.setText(message);

        String winnerName = result.equals("Chiến thắng!") ? message : "Không có người thắng";

        if (result.equals("Chiến thắng!") && winnerMoves > 0) {
            tvWinnerMoves.setText("Số nước đi của người thắng: " + winnerMoves);
        } else {
            tvWinnerMoves.setText("Không có người thắng cuộc.");
        }

        int minutes = totalTime / 60;
        int seconds = totalTime % 60;
        String totalTimeFormatted = String.format("%02d:%02d", minutes, seconds);
        tvTotalTime.setText("Tổng thời gian: " + totalTimeFormatted);
        tvBoardSize.setText("Kích thước bàn cờ: " + boardSize + "x" + boardSize); // HIỂN THỊ

        if (isSoundEnabled) {
            winSound = MediaPlayer.create(this, R.raw.win_sound);
            winSound.start();
        }

        btnPlayAgain.setOnClickListener(v -> {
            Intent replayIntent = new Intent(ResultActivity.this, MainActivity.class);
            replayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(replayIntent);
            finish();
        });

        btnBackToMenu.setOnClickListener(v -> {
            Intent menuIntent = new Intent(ResultActivity.this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(menuIntent);
            finish();
        });

        btnSaveHistory.setOnClickListener(v -> {
            Intent historyIntent = new Intent(ResultActivity.this, HistoryActivity.class);
            historyIntent.putExtra("winnerName", winnerName);
            historyIntent.putExtra("winnerMoves", winnerMoves);
            historyIntent.putExtra("totalTime", totalTimeFormatted);
            historyIntent.putExtra("boardSize", boardSize); // THÊM DÒNG NÀY
            startActivity(historyIntent);
        });
    }

    @Override
    protected void onDestroy() {
        stopMusicCompletely(); // Đảm bảo tắt nhạc khi activity bị hủy
        super.onDestroy();
    }

    // Phương thức tĩnh để tắt nhạc hoàn toàn từ bất kỳ class nào
    public static void stopMusicCompletely() {
        if (winSound != null) {
            if (winSound.isPlaying()) {
                winSound.stop();
            }
            winSound.release();
            winSound = null;
        }
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


}
