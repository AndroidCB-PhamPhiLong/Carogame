package btl.phamphilong.carogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {
    private GameState gameState;
    private Paint paint;
    private Paint textPaint;
    private int cellSize;
    private OnGameOverListener gameOverListener;
    private OnMoveListener onMoveListener;

    public interface OnGameOverListener {
        void onGameOver(Player winner);
    }

    public interface OnMoveListener {
        void onMoveMade();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(48);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        invalidate();
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.gameOverListener = listener;
    }

    public void setOnMoveListener(OnMoveListener listener) {
        this.onMoveListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameState == null) return;

        int boardSize = gameState.getBoardSize();
        int width = getWidth();
        int height = getHeight();
        cellSize = Math.min(width, height) / boardSize;

        // Vẽ nền bàn cờ với màu sắc xen kẽ (đỏ và xanh dương)
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if ((i + j) % 2 == 0) {
                    paint.setColor(Color.RED);
                } else {
                    paint.setColor(Color.BLUE);
                }
                canvas.drawRect(j * cellSize, i * cellSize, (j + 1) * cellSize, (i + 1) * cellSize, paint);
            }
        }

        // Vẽ đường lưới
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i <= boardSize; i++) {
            canvas.drawLine(0, i * cellSize, boardSize * cellSize, i * cellSize, paint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, boardSize * cellSize, paint);
        }

        // Vẽ quân cờ
        Player[][] board = gameState.getBoard();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] != Player.EMPTY) {
                    drawPlayer(canvas, i, j, board[i][j]);
                }
            }
        }
    }

    private void drawPlayer(Canvas canvas, int row, int col, Player player) {
        float centerX = col * cellSize + cellSize / 2f;
        float centerY = row * cellSize + cellSize / 2f;

        if (player == Player.X) {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5);
            canvas.drawLine(centerX - cellSize / 3, centerY - cellSize / 3,
                    centerX + cellSize / 3, centerY + cellSize / 3, paint);
            canvas.drawLine(centerX + cellSize / 3, centerY - cellSize / 3,
                    centerX - cellSize / 3, centerY + cellSize / 3, paint);
        } else if (player == Player.O) {
            paint.setColor(Color.BLUE);
            canvas.drawCircle(centerX, centerY, cellSize / 3, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX, centerY, cellSize / 3 - 5, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameState == null || gameState.isGameOver() || event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }

        int col = (int) (event.getX() / cellSize);
        int row = (int) (event.getY() / cellSize);

        if (row >= 0 && row < gameState.getBoardSize() &&
                col >= 0 && col < gameState.getBoardSize()) {

            if (gameState.makeMove(row, col)) {
                invalidate();

                if (onMoveListener != null) {
                    onMoveListener.onMoveMade();  // callback khi đi xong nước
                }

                if (gameState.isGameOver()) {
                    Player winner = checkWinner();
                    if (gameOverListener != null) {
                        gameOverListener.onGameOver(winner);
                    }
                }
            }
        }

        return true;
    }

    private Player checkWinner() {
        Player[][] board = gameState.getBoard();
        int size = gameState.getBoardSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != Player.EMPTY && gameState.checkWin(i, j)) {
                    return board[i][j];
                }
            }
        }

        return Player.EMPTY;
    }
}
