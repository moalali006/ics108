import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PiecePool {
    private List<Piece> availablePieces;
    private Random random;
    private Timer dynamicPieceTimer;

    public PiecePool() {
        this.availablePieces = new ArrayList<>();
        this.random = new Random();
    }

    public void reset() {
        availablePieces.clear();
        if (dynamicPieceTimer != null) {
            dynamicPieceTimer.cancel();
            dynamicPieceTimer = null;
        }
    }

    public void startPieceTimer(GameView view) {
        if (dynamicPieceTimer != null) {
            dynamicPieceTimer.cancel();
        }

        dynamicPieceTimer = new Timer();
        dynamicPieceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                availablePieces.clear();
                generateRandomPiece();
                generateRandomPiece();
                generateRandomPiece();

                view.updateView();
            }
        }, 10000, 10000);
    }

    public void generateRandomPiece() {
        // This ensures a random selection from all 12 shapes defined above
        PentominoType[] types = PentominoType.values();
        PentominoType type = types[random.nextInt(types.length)];

        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Piece newPiece = new Piece(type, color);
        availablePieces.add(newPiece);
    }

    public List<Piece> getAvailablePieces() { return availablePieces; }

    public void removePiece(Piece piece) {
        availablePieces.remove(piece);
    }

    public void generateSpecificPiece(PentominoType type) {
        Color color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Piece newPiece = new Piece(type, color);
        availablePieces.add(newPiece);
    }
}