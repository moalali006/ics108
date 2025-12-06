import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

public class GameBoard {
    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;
    private int[][] grid = new int[BOARD_ROWS][BOARD_COLS];

    private Map<Integer, Color> pieceColors = new HashMap<>();

    public void reset() {
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                grid[i][j] = 0;
            }
        }
        pieceColors.clear();
    }

    // METHOD REQUIRED by PentominoGame
    public boolean isPlacementValid(Piece piece, int row, int col) {
        int[][] shape = piece.getShapeMatrix();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 1) {
                    int boardRow = row + i;
                    int boardCol = col + j;

                    if (boardRow < 0 || boardRow >= BOARD_ROWS ||
                            boardCol < 0 || boardCol >= BOARD_COLS ||
                            grid[boardRow][boardCol] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void place(Piece piece, int row, int col) {
        int[][] shape = piece.getShapeMatrix();
        int pieceId = piece.getId();

        pieceColors.put(pieceId, piece.getColor());

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (shape[i][j] == 1) {
                    grid[row + i][col + j] = pieceId;
                }
            }
        }
    }

    // METHOD REQUIRED by PentominoGame
    public boolean hasValidMove(List<Piece> availablePieces) {
        if (availablePieces.isEmpty()) {
            return false;
        }

        for (Piece piece : availablePieces) {
            for (int r = 0; r < BOARD_ROWS; r++) {
                for (int c = 0; c < BOARD_COLS; c++) {
                    if (isPlacementValid(piece, r, c)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // METHOD REQUIRED by PentominoGame
    public boolean isFull() {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getGridState() { return grid; }

    public Color getColorForPiece(int pieceId) {
        return pieceColors.getOrDefault(pieceId, Color.GRAY);
    }
}