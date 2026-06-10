import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StatsPanel extends VBox {
    private final Simulation simulation;

    private final Label stepLabel         = valueLabel("—");
    private final Label weatherLabel      = valueLabel("Brak wiatru");
    private final Label redActiveLabel    = valueLabel("—");
    private final Label redTotalLabel     = valueLabel("—");
    private final Label redAccuracyLabel  = valueLabel("—");
    private final Label blueActiveLabel   = valueLabel("—");
    private final Label blueTotalLabel    = valueLabel("—");
    private final Label blueAccuracyLabel = valueLabel("—");

    private final Region redActiveBar  = new Region();
    private final Region blueActiveBar = new Region();

    private static final String RED  = "#d23b3b";
    private static final String BLUE = "#2f6fd6";
    private static final String GRAY = "#8a94a3";

    public StatsPanel(Simulation simulation) {
        this.simulation = simulation;

        setPrefWidth(320);
        setMinWidth(320);
        setPadding(new Insets(18));
        setSpacing(14);
        setStyle("-fx-background-color: #eef1f5; -fx-border-color: #d4d9e0; -fx-border-width: 0 0 0 1;");

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(
                buildSection("OGÓLNE",    GRAY, buildGeneralRows(), null),
                buildSection("TEAM RED",  RED,  buildRedRows(),  redActiveBar),
                buildSection("TEAM BLUE", BLUE, buildBlueRows(), blueActiveBar),
                spacer,
                buildFooter()
        );

        AnimationTimer timer = new AnimationTimer() {
            @Override public void handle(long now) { updateStats(); }
        };
        timer.start();
    }

    private VBox buildSection(String title, String accent, VBox rows, Region bar) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titleLabel.setStyle("-fx-text-fill: " + accent + ";");

        VBox content = new VBox(10, titleLabel);
        content.getChildren().addAll(rows.getChildren());

        if (bar != null) {
            bar.setPrefHeight(5);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setStyle("-fx-background-color: " + accent + "; -fx-background-radius: 3;");
            VBox barTrack = new VBox(bar);
            barTrack.setStyle("-fx-background-color: #dfe3e9; -fx-background-radius: 3;");
            barTrack.setPrefHeight(5);
            content.getChildren().add(barTrack);
        }

        VBox card = new VBox(content);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + accent + " #e2e6ec #e2e6ec #e2e6ec;" +
                        "-fx-border-width: 0 0 0 3;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-radius: 6;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 1);"
        );
        return card;
    }

    private VBox buildGeneralRows() {
        return new VBox(10,
                buildRow("Krok symulacji", stepLabel),
                buildRow("Pogoda",         weatherLabel)
        );
    }

    private VBox buildRedRows() {
        return new VBox(10,
                buildRow("Aktywne samoloty", redActiveLabel),
                buildRow("Suma stworzonych", redTotalLabel),
                buildRow("Celność strzałów", redAccuracyLabel)
        );
    }

    private VBox buildBlueRows() {
        return new VBox(10,
                buildRow("Aktywne samoloty", blueActiveLabel),
                buildRow("Suma stworzonych", blueTotalLabel),
                buildRow("Celność strzałów", blueAccuracyLabel)
        );
    }

    private HBox buildRow(String key, Label value) {
        Label keyLabel = new Label(key);
        keyLabel.setFont(Font.font("Arial", 12));
        keyLabel.setStyle("-fx-text-fill: #6b7280;");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        HBox row = new HBox(keyLabel, gap, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildFooter() {
        Label footer = new Label("Bartosz Niski i Krzysztof Przybysz");
        footer.setFont(Font.font("Arial", 10));
        footer.setStyle("-fx-text-fill: #9aa3b0;");
        HBox box = new HBox(footer);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private static Label valueLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        l.setStyle("-fx-text-fill: #1f2430;");
        return l;
    }

    private void updateStats() {
        stepLabel.setText(String.valueOf(simulation.getStepCount()));

        if (simulation.isWeatherActive()) {
            String strength = switch (simulation.getCurrentWindType()) {
                case 1 -> "5%";
                case 2 -> "10%";
                case 3 -> "15%";
                default -> "?";
            };
            String dir = switch (simulation.getWindDirection()) {
                case 0 -> "↑ Północ";
                case 1 -> "↓ Południe";
                case 2 -> "→ Wschód";
                case 3 -> "← Zachód";
                default -> "?";
            };
            weatherLabel.setText(dir + "  +" + strength);
            weatherLabel.setStyle("-fx-text-fill: #d98000; -fx-font-family: Arial; -fx-font-weight: bold; -fx-font-size: 14;");
        } else {
            weatherLabel.setText("Brak wiatru");
            weatherLabel.setStyle("-fx-text-fill: #1f2430; -fx-font-family: Arial; -fx-font-weight: bold; -fx-font-size: 14;");
        }

        int currentRed = 0, currentBlue = 0;
        if (simulation.getBoard() != null) {
            for (Plane p : simulation.getBoard().getPlanes()) {
                if (p.isRedTeam()) currentRed++;
                else               currentBlue++;
            }
        }

        int target = simulation.getConfig().getTargetPlanesPerTeam();

        redActiveLabel.setText(currentRed + " / " + target);
        redTotalLabel.setText(String.valueOf(simulation.getTotalRedPlanes()));
        redAccuracyLabel.setText(String.format("%.1f%%", simulation.getRedAccuracy()));
        updateBar(redActiveBar, currentRed, target);

        blueActiveLabel.setText(currentBlue + " / " + target);
        blueTotalLabel.setText(String.valueOf(simulation.getTotalBluePlanes()));
        blueAccuracyLabel.setText(String.format("%.1f%%", simulation.getBlueAccuracy()));
        updateBar(blueActiveBar, currentBlue, target);
    }

    private void updateBar(Region bar, int current, int target) {
        double ratio = (target > 0) ? Math.min(1.0, (double) current / target) : 0;
        Region track = (Region) bar.getParent();
        if (track != null && track.getWidth() > 0) {
            bar.setPrefWidth(track.getWidth() * ratio);
        }
    }
}