import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StatsPanel extends VBox {
    private final Simulation simulation;

    private final Label stepLabel = new Label("Krok symulacji: 0");
    private final Label weatherLabel = new Label("Pogoda: Brak wiatru");
    private final Label redActiveLabel = new Label("Aktywne samoloty: 0");
    private final Label redTotalLabel = new Label("Suma stworzonych: 0");
    private final Label redAccuracyLabel = new Label("Celność strzałów: 0.0%");
    private final Label blueActiveLabel = new Label("Aktywne samoloty: 0");
    private final Label blueTotalLabel = new Label("Suma stworzonych: 0");
    private final Label blueAccuracyLabel = new Label("Celność strzałów: 0.0%");

    public StatsPanel(Simulation simulation) {
        this.simulation = simulation;

        setPrefWidth(250);
        setPadding(new Insets(15));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #dcdcdc; -fx-border-width: 0 0 0 1;");

        Label titleG = new Label("OGÓLNE");
        titleG.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        VBox generalBox = new VBox(5, titleG, stepLabel, weatherLabel);
        generalBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label titleR = new Label("TEAM RED");
        titleR.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleR.setStyle("-fx-text-fill: red;");
        VBox redBox = new VBox(5, titleR, redActiveLabel, redTotalLabel, redAccuracyLabel);
        redBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label titleB = new Label("TEAM BLUE");
        titleB.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleB.setStyle("-fx-text-fill: blue;");
        VBox blueBox = new VBox(5, titleB, blueActiveLabel, blueTotalLabel, blueAccuracyLabel);
        blueBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");

        getChildren().addAll(generalBox, redBox, blueBox);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateStats();
            }
        };
        timer.start();
    }

    private void updateStats() {
        stepLabel.setText("Krok symulacji: " + simulation.getStepCount());

        if (simulation.isWeatherActive()) {
            String directionStr = switch (simulation.getWindDirection()) {
                case 0 -> "PÓŁNOC";
                case 1 -> "POŁUDNIE";
                case 2 -> "WSCHÓD";
                case 3 -> "ZACHÓD";
                default -> String.valueOf(simulation.getWindDirection());
            };
            weatherLabel.setText("Wiatr: Typ " + simulation.getCurrentWindType() + "\nKierunek: " + directionStr);
        } else {
            weatherLabel.setText("Pogoda: Brak wiatru");
        }

        int currentRed = 0;
        int currentBlue = 0;
        if (simulation.getBoard() != null && simulation.getBoard().getPlanes() != null) {
            for (Plane p : simulation.getBoard().getPlanes()) {
                if (p.isRedTeam()) currentRed++;
                else               currentBlue++;
            }
        }

        redActiveLabel.setText("Aktywne samoloty: " + currentRed);
        redTotalLabel.setText("Suma stworzonych: " + simulation.getTotalRedPlanes());
        redAccuracyLabel.setText(String.format("Celność strzałów: %.1f%%", simulation.getRedAccuracy()));

        blueActiveLabel.setText("Aktywne samoloty: " + currentBlue);
        blueTotalLabel.setText("Suma stworzonych: " + simulation.getTotalBluePlanes());
        blueAccuracyLabel.setText(String.format("Celność strzałów: %.1f%%", simulation.getBlueAccuracy()));
    }
}