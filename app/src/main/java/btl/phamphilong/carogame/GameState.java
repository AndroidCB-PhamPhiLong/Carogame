package btl.phamphilong.carogame;

public class GameState {
    private final int boardSize;
    private final Player[][] board;
    private Player currentPlayer;
    private boolean gameOver;

    public GameState(int size) {
        this.boardSize = size;
        this.board = new Player[size][size];
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
        if (board[row][col] == Player.EMPTY && !gameOver) {
            board[row][col] = currentPlayer;
            if (checkWin(row, col)) {
                gameOver = true;
            } else if (isBoardFull()) {
                gameOver = true;
                currentPlayer = Player.EMPTY; // Trường hợp hòa
            } else {
                // Chuyển lượt cho người chơi tiếp theo
                currentPlayer = (currentPlayer == Player.X) ? Player.O : Player.X;
            }
            return true;
        }
        return false;
    }

    public boolean checkWin(int row, int col) {
        Player player = board[row][col];
        return checkDirection(row, col, 0, 1, player)     // Ngang
                || checkDirection(row, col, 1, 0, player)     // Dọc
                || checkDirection(row, col, 1, 1, player)     // Chéo chính
                || checkDirection(row, col, 1, -1, player);   // Chéo phụ
    }

    private boolean checkDirection(int row, int col, int dx, int dy, Player player) {
        int count = 1;
        int i = row + dx;
        int j = col + dy;
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

        return count >= 5;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == Player.EMPTY) return false;
            }
        }
        return true;
    }

    public Player[][] getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getBoardSize() {
        return boardSize;
    }
}
