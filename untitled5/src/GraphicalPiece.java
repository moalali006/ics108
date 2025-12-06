import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.geometry.Point2D;

public class GraphicalPiece extends Pane {
    private final Piece modelPiece;
    private final PentominoGame gameModel;
    private double mouseAnchorX, mouseAnchorY;

    public GraphicalPiece(Piece piece, PentominoGame controller) {
        this.modelPiece = piece;
        this.gameModel = controller;
        renderShape();
        setupMouseHandlers();
    }

    public void renderShape() {
        this.getChildren().clear();
        int[][] shape = modelPiece.getShapeMatrix();
        Color pieceColor = modelPiece.getColor();
        int squareSize = gameModel.getGameView().getSquareSize();

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] == 1) {
                    Rectangle square = new Rectangle(squareSize, squareSize, pieceColor);
                    square.setStroke(Color.BLACK);
                    square.setTranslateX(c * squareSize);
                    square.setTranslateY(r * squareSize);
                    this.getChildren().add(square);
                }
            }
        }
    }

    public void rotate(boolean clockwise) {
        if (clockwise) {
            modelPiece.rotateClockwise();
        } else {
            modelPiece.rotateCounterClockwise();
        }
        renderShape();
    }


    private void setupMouseHandlers() {
        GameView gameView = gameModel.getGameView();

        // Drag Start
        this.setOnMousePressed((MouseEvent event) -> {
            gameView.setDraggedPiece(this);

            // Calculate anchor: distance from the top-left of the piece to the mouse click point
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();

            // Get piece's absolute position relative to the SCENE
            Point2D pieceScenePos = this.localToScene(0, 0);

            // Navigate up to the BorderPane root
            Parent currentParent = this.getParent(); // piecePoolUI (Pane)
            Parent stackPaneRoot = currentParent.getParent(); // VBox
            Parent borderPaneRoot = stackPaneRoot.getParent(); // BorderPane (mainLayout)

            if (borderPaneRoot instanceof BorderPane && currentParent instanceof Pane) {
                // 1. Remove from current parent
                ((Pane) currentParent).getChildren().remove(this);

                // 2. Add to the BorderPane root (highest Z-order)
                ((BorderPane) borderPaneRoot).getChildren().add(this);

                // 3. Set the piece's layout position using its absolute SCENE coordinates
                this.setLayoutX(pieceScenePos.getX());
                this.setLayoutY(pieceScenePos.getY());

                this.toFront();
            }

            event.consume();
        });

        // Drag
        this.setOnMouseDragged((MouseEvent event) -> {
            // New position is calculated relative to the BorderPane root
            this.setLayoutX(event.getSceneX() - mouseAnchorX);
            this.setLayoutY(event.getSceneY() - mouseAnchorY);

            this.toFront();

            event.consume();
        });

        // Drag End (Attempt Placement on Board)
        this.setOnMouseReleased((MouseEvent event) -> {
            gameView.setDraggedPiece(null);

            GridPane gameBoardUI = gameView.getGameBoardUI();
            int squareSize = gameView.getSquareSize();

            double boardXStart = gameBoardUI.localToScene(0, 0).getX();
            double boardYStart = gameBoardUI.localToScene(0, 0).getY();

            double pieceSceneX = event.getSceneX() - mouseAnchorX;
            double pieceSceneY = event.getSceneY() - mouseAnchorY;

            int col = (int) Math.round((pieceSceneX - boardXStart) / squareSize);
            int row = (int) Math.round((pieceSceneY - boardYStart) / squareSize);

            boolean success = false;

            if (pieceSceneX >= boardXStart && pieceSceneY >= boardYStart) {
                if (gameModel.placePiece(modelPiece, row, col)) {
                    success = true;
                }
            }

            // Restore/remove piece from the scene graph
            Parent borderPaneRoot = this.getParent();

            if (borderPaneRoot instanceof BorderPane) {
                // Get VBox (centerStack) -> Get piecePoolUI (Pane at index 0)
                VBox centerStack = (VBox) ((BorderPane) borderPaneRoot).getCenter();
                Pane poolUI = (Pane) centerStack.getChildren().get(0);

                if (!success) {
                    // If placement failed, move the piece back to its original parent (piecePoolUI)
                    ((BorderPane) borderPaneRoot).getChildren().remove(this);
                    poolUI.getChildren().add(this);

                    // Restore layout coordinates relative to the piecePoolUI's origin
                    this.setLayoutX(pieceSceneX - poolUI.localToScene(0, 0).getX());
                    this.setLayoutY(pieceSceneY - poolUI.localToScene(0, 0).getY());
                } else {
                    // If placement succeeded, remove from the root
                    ((BorderPane) borderPaneRoot).getChildren().remove(this);
                }
            }

            gameView.updateView();
            event.consume();
        });
    }
}