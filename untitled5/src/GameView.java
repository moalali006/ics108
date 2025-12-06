import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends Application {

    private PentominoGame gameModel;
    private Stage primaryStage;

    // --- UI Elements ---
    private Label timerLabel = new Label("Time: 0s");
    private Button restartButton = new Button("Restart");
    private Pane piecePoolUI = new Pane();
    private GridPane gameBoardUI = new GridPane();
    private StackPane modalOverlay = new StackPane();

    // Constants
    private static final int SQUARE_SIZE = 40;
    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;
    private static final int BOARD_PIXEL_WIDTH = BOARD_COLS * SQUARE_SIZE;
    private static final int POOL_HEIGHT = 200;
    private static final int TOP_CONTROL_HEIGHT = 50;

    // Final Window Size Calculation
    private static final int WINDOW_WIDTH = BOARD_PIXEL_WIDTH + 40;
    private static final int WINDOW_HEIGHT = TOP_CONTROL_HEIGHT + POOL_HEIGHT + (BOARD_ROWS * SQUARE_SIZE) + 10;

    // State
    private GraphicalPiece draggedPiece = null;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameModel = new PentominoGame(this);

        primaryStage.setTitle("Pentomino Puzzle Game");
        primaryStage.setResizable(false);

        primaryStage.setScene(createStartScene());
        primaryStage.show();
    }

    private Scene createStartScene() {
        Image backgroundImage = null;
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/pentomino_bg.png"));
        } catch (Exception e) {
            System.out.println("Image 'pentomino_bg.png' not found. Using solid black background.");
        }

        ImageView backgroundView = null;
        if (backgroundImage != null) {
            backgroundView = new ImageView(backgroundImage);
            backgroundView.setFitWidth(WINDOW_WIDTH);
            backgroundView.setFitHeight(WINDOW_HEIGHT);
            backgroundView.setPreserveRatio(false);
        }

        // --- Title and Button Setup ---
        Label title = new Label("PENTOMINO PUZZLE GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);");
        title.setTextAlignment(TextAlignment.CENTER);

        Button startButton = new Button("START GAME");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        startButton.setPrefSize(200, 60);
        startButton.setOnAction(e -> primaryStage.setScene(createGameScene()));

        VBox content = new VBox(50, title, startButton);
        content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        if (backgroundView != null) {
            root.getChildren().add(backgroundView);
        } else {
            root.setStyle("-fx-background-color: black;");
        }
        root.getChildren().add(content);

        return new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private Scene createGameScene() {
        // --- 1. Setup Section 1: Control Panel ---
        HBox controlPanel = new HBox(20, timerLabel, restartButton);
        controlPanel.setStyle("-fx-padding: 10; -fx-alignment: center;");
        controlPanel.setPrefWidth(WINDOW_WIDTH);

        // --- 2. Setup Section 3: Game Board ---
        gameBoardUI.setGridLinesVisible(true);
        // FIX: Ensure the Grid doesn't block mouse clicks
        gameBoardUI.setMouseTransparent(true);

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                Rectangle cell = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.WHITE);
                cell.setStroke(Color.LIGHTGRAY);
                gameBoardUI.add(cell, c, r);
            }
        }

        // --- 3. Setup Layout ---
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(controlPanel);

        // VBox stacks the piece pool and grid vertically
        VBox centerStack = new VBox(0);
        centerStack.getChildren().addAll(piecePoolUI, gameBoardUI);
        centerStack.setAlignment(Pos.TOP_CENTER);

        // Layer the modal over the VBox stack
        StackPane layeredRoot = new StackPane(centerStack, modalOverlay);

        mainLayout.setCenter(layeredRoot);

        piecePoolUI.setMinHeight(POOL_HEIGHT);
        piecePoolUI.setStyle("-fx-background-color: #2e2e2e;");

        // --- 4. Setup Handlers and Start Game Logic ---
        restartButton.setOnAction(e -> gameModel.restartGame());

        Scene gameScene = new Scene(mainLayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Key listener for rotation
        gameScene.setOnKeyPressed(e -> {
            if (draggedPiece != null) {
                if (e.getCode() == KeyCode.RIGHT) {
                    draggedPiece.rotate(true);
                } else if (e.getCode() == KeyCode.LEFT) {
                    draggedPiece.rotate(false);
                }
                e.consume();
            }
        });

        gameModel.startGame();

        return gameScene;
    }

    // --- Utility Methods ---

    public void updateTimerDisplay(int time) {
        Platform.runLater(() -> timerLabel.setText("Time: " + time + "s"));
    }

    public void updateView() {
        Platform.runLater(() -> {
            renderGameBoard();
            renderPiecePool();
        });
    }

    private void renderGameBoard() {
        List<Rectangle> backgroundCells = new ArrayList<>();

        gameBoardUI.getChildren().forEach(node -> {
            if (node instanceof Rectangle && GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                if (((Rectangle)node).getFill().equals(Color.WHITE)) {
                    backgroundCells.add((Rectangle) node);
                }
            }
        });

        gameBoardUI.getChildren().clear();
        gameBoardUI.getChildren().addAll(backgroundCells);

        int[][] state = gameModel.getBoard().getGridState();

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                int pieceId = state[r][c];

                if (pieceId != 0) {
                    Color pieceColor = gameModel.getBoard().getColorForPiece(pieceId);

                    Rectangle placedSquare = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, pieceColor);
                    placedSquare.setStroke(Color.DARKGRAY);

                    gameBoardUI.add(placedSquare, c, r);
                }
            }
        }

        gameBoardUI.toFront();
    }

    private void renderPiecePool() {
        piecePoolUI.getChildren().clear();

        List<Piece> pieces = gameModel.getPiecePool().getAvailablePieces();

        int startX = 50;
        int spacing = 150;

        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            GraphicalPiece gp = new GraphicalPiece(piece, gameModel);

            int fixedX = startX + (i * spacing);
            int fixedY = 50;

            gp.setLayoutX(fixedX);
            gp.setLayoutY(fixedY);
            piecePoolUI.getChildren().add(gp);
        }
    }

    public void showGameOverModal(boolean didWin, String message) {
        Platform.runLater(() -> {
            modalOverlay.getChildren().clear();
            modalOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

            VBox modalContent = new VBox(20);
            modalContent.setAlignment(Pos.CENTER);
            modalContent.setPrefSize(300, 150);
            modalContent.setStyle("-fx-background-color: #333333; -fx-padding: 30; -fx-border-radius: 10; -fx-background-radius: 10;");

            Label statusLabel = new Label(didWin ? "WINNER!" : "GAME OVER!");
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            statusLabel.setTextFill(didWin ? Color.LIMEGREEN : Color.RED);

            Label messageLabel = new Label(message);
            messageLabel.setTextFill(Color.WHITE);

            Button restartBtn = new Button("Restart Game");
            restartBtn.setOnAction(e -> gameModel.restartGame());

            modalContent.getChildren().addAll(statusLabel, messageLabel, restartBtn);

            modalOverlay.getChildren().add(modalContent);
            modalOverlay.setVisible(true);
        });
    }

    public void hideGameOverModal() {
        Platform.runLater(() -> {
            modalOverlay.setVisible(false);
            modalOverlay.getChildren().clear();
        });
    }

    public void setDraggedPiece(GraphicalPiece piece) {
        this.draggedPiece = piece;
    }

    public int getSquareSize() {
        return SQUARE_SIZE;
    }

    public GridPane getGameBoardUI() {
        return gameBoardUI;
    }

    public static void main(String[] args) {
        launch(args);
    }
}