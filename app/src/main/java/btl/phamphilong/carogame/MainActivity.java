package btl.phamphilong.carogame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private BoardView boardView;
    private TextView tvStatus;
    private Button btnReset;
    private GameState gameState;
    private static final int REQUEST_CODE_RESULT = 1;

    private String playerXName = "Player X";
    private String playerOName = "Player O";

    private TextView tvTotalTime, tvTurnTime;
    private Handler totalTimeHandler = new Handler();
    private Runnable totalTimeRunnable;
    private long totalStartTime;

    private CountDownTimer turnTimer;
    private final int TURN_TIME_LIMIT = 10; // giây
    private boolean isPlayerXTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.boardView);
        tvStatus = findViewById(R.id.tvStatus);
        btnReset = findViewById(R.id.btnReset);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTurnTime = findViewById(R.id.tvTurnTime);

        TextView tvPlayerX = findViewById(R.id.playerXName);
        TextView tvPlayerO = findViewById(R.id.playerOName);

        // Lấy kích thước bàn cờ từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int boardSize = sharedPreferences.getInt("board_size", 0);  // Mặc định là 0 (3x3)

        // Chuyển đổi vị trí Spinner thành kích thước bàn cờ
        int size = 3;  // Kích thước mặc định là 3x3
        if (boardSize == 1) size = 5;
        else if (boardSize == 2) size = 10;
        else if (boardSize == 3) size = 15;

        // Cập nhật kích thước bàn cờ
        gameState = new GameState(size);
        boardView.setGameState(gameState);
        boardView.invalidate();


        Intent intent = getIntent();
        if (intent != null) {
            String nameX = intent.getStringExtra("player_x");
            String nameO = intent.getStringExtra("player_o");

            if (nameX != null) playerXName = nameX;
            if (nameO != null) playerOName = nameO;

            tvPlayerX.setText(playerXName);
            tvPlayerO.setText(playerOName);
        }

        gameState = new GameState(15);
        boardView.setGameState(gameState);
        boardView.setOnGameOverListener(this::showResultDialog);

        btnReset.setOnClickListener(v -> {
            Intent menuIntent = new Intent(MainActivity.this, MenuActivity.class);
            menuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(menuIntent);
            finish();
        });

        updateStatus();
        startTotalTimeCounter();
        startTurnTimer();
        setupTouchListener();
    }

    private void updateStatus() {
        String currentPlayer = gameState.getCurrentPlayer() == Player.X ? playerXName : playerOName;
        tvStatus.setText("Lượt đi: " + currentPlayer);
    }

    private void startTotalTimeCounter() {
        totalStartTime = System.currentTimeMillis();
        totalTimeRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = (System.currentTimeMillis() - totalStartTime) / 1000;
                long minutes = elapsed / 60;
                long seconds = elapsed % 60;
                tvTotalTime.setText(String.format("Thời gian: %02d:%02d", minutes, seconds));
                totalTimeHandler.postDelayed(this, 1000);
            }
        };
        totalTimeHandler.post(totalTimeRunnable);
    }

    private void startTurnTimer() {
        if (turnTimer != null) turnTimer.cancel();

        turnTimer = new CountDownTimer(TURN_TIME_LIMIT * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTurnTime.setText("Lượt còn: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                tvTurnTime.setText("Hết lượt!");
                switchTurn(); // Chuyển lượt
                startTurnTimer(); // Bắt đầu đếm cho người tiếp theo
            }
        }.start();
    }

    private void switchTurn() {
        isPlayerXTurn = !isPlayerXTurn;
        Player nextPlayer = isPlayerXTurn ? Player.X : Player.O;
        gameState.setCurrentPlayer(nextPlayer);
        updateStatus();
        boardView.invalidate();
    }

    private void setupTouchListener() {
        boardView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !gameState.isGameOver()) {
                int col = (int) (event.getX() / boardView.getCellSize());
                int row = (int) (event.getY() / boardView.getCellSize());

                if (row >= 0 && row < gameState.getBoardSize()
                        && col >= 0 && col < gameState.getBoardSize()) {

                    if (gameState.getBoard()[row][col] == Player.EMPTY) {
                        Player current = gameState.getCurrentPlayer();
                        if ((isPlayerXTurn && current == Player.X) || (!isPlayerXTurn && current == Player.O)) {
                            boolean moved = gameState.makeMove(row, col);
                            if (moved) {
                                boardView.invalidate();
                                if (gameState.isGameOver()) {
                                    showResultDialog(current);
                                } else {
                                    switchTurn();       // Chuyển lượt ngay sau khi đánh
                                    startTurnTimer();   // Bắt đầu đếm giờ cho lượt tiếp theo
                                }
                            }
                        }
                    }
                }
            }
            return true;
        });
    }

    private void showResultDialog(Player winner) {
        String result, message;
        if (winner == Player.EMPTY) {
            result = "HÒA!";
            message = "Hai bên ngang tài ngang sức";
        } else {
            result = (winner == Player.X ? "X THẮNG!" : "O THẮNG!");
            String winnerName = (winner == Player.X) ? playerXName : playerOName;
            message = "Chúc mừng người chơi " + winnerName + "!";
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("RESULT", result);
        intent.putExtra("MESSAGE", message);
        startActivityForResult(intent, REQUEST_CODE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESULT && resultCode == RESULT_OK) {
            resetGame();
        }
    }

    private void resetGame() {
        gameState = new GameState(15);
        boardView.setGameState(gameState);
        boardView.invalidate();
        isPlayerXTurn = true;
        updateStatus();
        startTurnTimer();
    }
}
