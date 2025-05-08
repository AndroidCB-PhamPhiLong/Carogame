package btl.phamphilong.carogame;

public class GameState {
    private final int boardSize;
    private final Player[][] board;
    private Player currentPlayer;
    private boolean gameOver;
    private static final int WIN_CONDITION = 5;

    private String playerXName;
    private String playerOName;

    private OnGameOverListener onGameOverListener;

    public interface OnGameOverListener {
        void onGameOver(Player winner);
    }

    public GameState(int size, String playerXName, String playerOName) {
        this.boardSize = size;
        this.board = new Player[size][size];
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = Player.EMPTY;
            }
        }
        currentPlayer = Player.X;
        gameOver = false;
    }

    public boolean makeMove(int row, int col) {
        if (!inBounds(row, col) || board[row][col] != Player.EMPTY || gameOver) return false;

        board[row][col] = currentPlayer;

        if (checkWin(row, col)) {
            gameOver = true;
            notifyGameOver(currentPlayer);
        } else if (isBoardFull()) {
            gameOver = true;
            currentPlayer = Player.EMPTY;
            notifyGameOver(Player.EMPTY);
        } else {
            switchPlayer();
        }

        return true;
    }

    public boolean checkWin(int row, int col) {
        Player player = board[row][col];
        return checkDirection(row, col, 1, 0, player) ||  // Vertical
                checkDirection(row, col, 0, 1, player) ||  // Horizontal
                checkDirection(row, col, 1, 1, player) ||  // Main Diagonal
                checkDirection(row, col, 1, -1, player);   // Anti Diagonal
    }

    private boolean checkDirection(int row, int col, int dx, int dy, Player player) {
        int count = 1;

        int i = row + dx, j = col + dy;
        while (inBounds(i, j) && board[i][j] == player) {
            count++;
            i += dx;
            j += dy;
        }

        i = row - dx;
        j = col - dy;
        while (inBounds(i, j) && board[i][j] == player) {
            count++;
            i -= dx;
            j -= dy;
        }

        return count >= WIN_CONDITION;
    }

    private boolean isBoardFull() {
        for (Player[] row : board) {
            for (Player cell : row) {
                if (cell == Player.EMPTY) return false;
            }
        }
        return true;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public void switchPlayer() {
        if (!gameOver) {
            currentPlayer = (currentPlayer == Player.X) ? Player.O : Player.X;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player[][] getBoard() {
        return board;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public String getPlayerXName() {
        return playerXName;
    }

    public String getPlayerOName() {
        return playerOName;
    }

    public void setPlayerXName(String name) {
        this.playerXName = name;
    }

    public void setPlayerOName(String name) {
        this.playerOName = name;
    }

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.onGameOverListener = listener;
    }

    private void notifyGameOver(Player winner) {
        if (onGameOverListener != null) {
            onGameOverListener.onGameOver(winner);
        }
    }

    public int getMoveCountForPlayer(Player player) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == player) {
                    count++;
                }
            }
        }
        return count;
    }

}
