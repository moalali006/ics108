public enum PentominoType {
    // 1. I-pentomino (1x5 stick)
    I(new int[][] {{1, 1, 1, 1, 1}}),

    // 2. L-pentomino
    L(new int[][] {
            {1, 0, 0},
            {1, 0, 0},
            {1, 1, 1}}),

    // 3. P-pentomino
    P(new int[][] {{1, 1},
            {1, 1},
            {1, 0}}),

    // 4. N-pentomino
    N(new int[][] {{0, 1, 1},
            {1, 1, 0},
            {1, 0, 0}}),

    // 5. F-pentomino
    F(new int[][] {{0, 1, 1},
            {1, 1, 0},
            {0, 1, 0}}),

    // 6. T-pentomino
    T(new int[][] {{1, 1, 1},
            {0, 1, 0},
            {0, 1, 0}}),

    // 7. U-pentomino
    U(new int[][] {{1, 0, 1},
            {1, 1, 1}}),

    // 8. V-pentomino
    V(new int[][] {{1, 0, 0},
            {1, 0, 0},
            {1, 1, 1}}),

    // 9. W-pentomino
    W(new int[][] {{1, 0, 0},
            {1, 1, 0},
            {0, 1, 1}}),

    // 10. X-pentomino
    X(new int[][] {{0, 1, 0},
            {1, 1, 1},
            {0, 1, 0}}),

    // 11. Y-pentomino
    Y(new int[][] {{0, 1, 0, 0},
            {1, 1, 1, 1}}),

    // 12. Z-pentomino
    Z(new int[][] {{1, 1, 0},
            {0, 1, 0},
            {0, 1, 1}});

    private final int[][] initialShape;

    PentominoType(int[][] shape) {
        this.initialShape = shape;
    }

    /**
     * Returns a deep copy of the shape matrix to ensure that
     * rotation on one Piece object does not affect others.
     */
    public int[][] getInitialShape() {
        int rows = initialShape.length;
        int cols = initialShape[0].length;
        int[][] deepCopy = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            // Copy each row array individually (deep copy)
            System.arraycopy(initialShape[i], 0, deepCopy[i], 0, cols);
        }
        return deepCopy;
    }
}