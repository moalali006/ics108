import javafx.scene.paint.Color;
import java.util.concurrent.atomic.AtomicInteger;

public class Piece {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private final int id;

    private int[][] shapeMatrix;
    private final PentominoType type;
    private final Color color;

    public Piece(PentominoType type, Color color) {
        this.id = nextId.getAndIncrement();
        this.type = type;
        this.color = color;
        this.shapeMatrix = type.getInitialShape();
    }

    public void rotateClockwise() {
        int rows = shapeMatrix.length;
        int cols = shapeMatrix[0].length;
        int[][] newMatrix = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newMatrix[j][rows - 1 - i] = shapeMatrix[i][j];
            }
        }
        this.shapeMatrix = newMatrix;
    }

    public void rotateCounterClockwise() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    public int getId() { return id; }
    public int[][] getShapeMatrix() { return shapeMatrix; }
    public Color getColor() { return color; }
}