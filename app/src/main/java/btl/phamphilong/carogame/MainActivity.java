package btl.phamphilong.carogame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE_SETTINGS = 1;
    private static final long TURN_DURATION_MS = 10_000;

    private BoardView boardView;
    private GameState gameState;
    private TextView textCurrentPlayer, tvTotalTime, tvTurnTime;
    private Button btnMainMenu, btnSettings;

    private int boardSize = 5;
    private String playerXName = "X", playerOName = "O";

    private boolean isMusicEnabled = true;

    private Handler totalTimeHandler = new Handler();
    private int totalSeconds = 0;

    private CountDownTimer turnTimer;

    private final Runnable updateTotalTimeRunnable = new Runnable() {
        @Override
        public void run() {
            totalSeconds++;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            tvTotalTime.setText(String.format("Thời gian: %02d:%02d", minutes, seconds));
            totalTimeHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getDataFromIntent();
        loadMusicPreference();
        setupBackgroundMusic();
        setupGame(boardSize);

        setupGameCallbacks();
        setupButtonEvents();
    }

    private void initViews() {
        boardView = findViewById(R.id.boardView);
        textCurrentPlayer = findViewById(R.id.textCurrentPlayer);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTurnTime = findViewById(R.id.tvTurnTime);
        btnMainMenu = findViewById(R.id.btnMainMenu);
        btnSettings = findViewById(R.id.btnSettings);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        playerXName = intent.getStringExtra("player_x");
        playerOName = intent.getStringExtra("player_o");
        boardSize = intent.getIntExtra("BOARD_SIZE", 5);

        if (playerXName == null || playerXName.trim().isEmpty()) playerXName = "X";
        if (playerOName == null || playerOName.trim().isEmpty()) playerOName = "O";
    }

    private void loadMusicPreference() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        isMusicEnabled = preferences.getBoolean("musicEnabled", true);
    }

    private void setupBackgroundMusic() {
        if (isMusicEnabled) {
            MusicManager.startBackgroundMusic(this);
        } else {
            MusicManager.stopMusic();
        }
    }

    private void setupGame(int size) {
        gameState = new GameState(size, playerXName, playerOName);
        boardView.setGameState(gameState);
        updateCurrentPlayerUI();

        totalSeconds = 0;
        tvTotalTime.setText("Thời gian: 00:00");
        tvTurnTime.setText("Lượt còn: 10s");

        totalTimeHandler.postDelayed(updateTotalTimeRunnable, 1000);
        startTurnTimer();
    }

    private void setupGameCallbacks() {
        boardView.setOnMoveListener(() -> {
            updateCurrentPlayerUI();
            restartTurnTimer();
        });

        boardView.setOnGameOverListener(winner -> {
            stopAllTimers();
            showGameResult(winner);
        });
    }

    private void setupButtonEvents() {
        btnMainMenu.setOnClickListener(v -> {
            stopAllTimers();
            navigateToMenu();
        });

        btnSettings.setOnClickListener(v -> {
            stopAllTimers();
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsIntent, REQUEST_CODE_SETTINGS);
        });
    }

    private void updateCurrentPlayerUI() {
        Player currentPlayer = gameState.getCurrentPlayer();
        String name = (currentPlayer == Player.X) ? playerXName : playerOName;
        textCurrentPlayer.setText("Lượt: " + name);
    }

    private void startTurnTimer() {
        turnTimer = new CountDownTimer(TURN_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTurnTime.setText("Lượt còn: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                gameState.switchPlayer();
                updateCurrentPlayerUI();
                boardView.invalidate();
                restartTurnTimer();
            }
        };
        turnTimer.start();
    }

    private void restartTurnTimer() {
        if (turnTimer != null) turnTimer.cancel();
        startTurnTimer();
    }

    private void stopAllTimers() {
        if (turnTimer != null) turnTimer.cancel();
        totalTimeHandler.removeCallbacks(updateTotalTimeRunnable);
    }

    private void showGameResult(Player winner) {
            MusicManager.stopMusic();

        String result, message;
        if (winner == Player.EMPTY) {
            result = "Hòa!";
            message = "Không ai chiến thắng.";
        } else {
            String winnerName = (winner == Player.X) ? playerXName : playerOName;
            result = "Chiến thắng!";
            message = winnerName + " đã giành chiến thắng!";
        }

        int winnerMoves = (winner != Player.EMPTY) ? gameState.getMoveCountForPlayer(winner) : 0;

        Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
        resultIntent.putExtra("RESULT", result);
        resultIntent.putExtra("MESSAGE", message);
        resultIntent.putExtra("SOUND_ENABLED", isMusicEnabled);
        resultIntent.putExtra("WINNER_MOVES", winnerMoves);
        resultIntent.putExtra("BOARD_SIZE", boardSize); // THÊM DÒNG NÀY
        resultIntent.putExtra("TOTAL_TIME", totalSeconds);
        startActivity(resultIntent);
    }

    private void navigateToMenu() {
        Intent menuIntent = new Intent(MainActivity.this, MenuActivity.class);
        menuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(menuIntent);
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK && data != null) {
            int newBoardSize = data.getIntExtra("boardSize", boardSize);
            if (newBoardSize != boardSize) {
                boardSize = newBoardSize;
                setupGame(boardSize); // Thiết lập lại bàn cờ với kích thước mới
            }
        }
    }


}
