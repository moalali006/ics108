import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

public class PentominoGame {
    private final GameBoard board;
    private final PiecePool piecePool;
    private final GameView gameView;

    private int timeRemaining;
    private boolean isGameOver = false;
    private Timer gameTimer;

    public PentominoGame(GameView view) {
        this.board = new GameBoard();
        this.piecePool = new PiecePool();
        this.gameView = view;
    }

    public void startGame() {
        if (gameTimer != null) gameTimer.cancel();

        isGameOver = false;
        timeRemaining = 300;
        board.reset();
        piecePool.reset();

        piecePool.startPieceTimer(gameView);

        // Ensure immediate pieces before the 10s interval hits.
        piecePool.generateRandomPiece();
        piecePool.generateRandomPiece();
        piecePool.generateRandomPiece();

        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 1000, 1000);

        gameView.updateView();
        gameView.hideGameOverModal();
    }

    private void updateTimer() {
        if (isGameOver) {
            gameTimer.cancel();
            return;
        }

        timeRemaining--;
        gameView.updateTimerDisplay(timeRemaining);

        if (timeRemaining <= 0) {
            endGame(false, "Time Expired!");
        }

        // Check for loss condition (no moves left) every second
        // FIX: The methods called here are required by image_af9981.png
        if (!board.isFull() && !board.hasValidMove(piecePool.getAvailablePieces())) {
            endGame(false, "No More Moves!");
        }
    }

    public void restartGame() {
        startGame();
    }

    private void endGame(boolean didWin, String message) {
        isGameOver = true;
        gameTimer.cancel();
        piecePool.reset();

        gameView.showGameOverModal(didWin, message);
    }

    public boolean placePiece(Piece piece, int row, int col) {
        // FIX: The methods called here are required by image_af9981.png
        if (board.isPlacementValid(piece, row, col)) {
            board.place(piece, row, col);
            piecePool.removePiece(piece);

            piecePool.generateRandomPiece();

            gameView.updateView();

            if (board.isFull()) {
                endGame(true, "Grid Complete!");
            }
            else if (!board.hasValidMove(piecePool.getAvailablePieces())) {
                endGame(false, "No More Moves!");
            }
            return true;
        }
        return false;
    }

    public GameBoard getBoard() { return board; }
    public PiecePool getPiecePool() { return piecePool; }
    public GameView getGameView() { return gameView; }
}