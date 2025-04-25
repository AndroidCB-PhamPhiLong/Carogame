package btl.phamphilong.carogame;

public enum Player {
    X,
    O,
    EMPTY;

    @Override
    public String toString() {
        switch (this) {
            case X: return "X";
            case O: return "O";
            case EMPTY: return "";
        }
        return "";
    }
}