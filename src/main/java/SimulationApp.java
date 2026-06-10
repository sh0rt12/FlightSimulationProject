import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SimulationApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Flight Simulation - Setup");
        stage.setScene(buildMenuScene(stage));
        stage.show();
    }

    private Scene buildMenuScene(Stage stage) {
        SimulationConfig config = new SimulationConfig();

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("KONFIGURACJA SYMULACJI");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(12);

        TextField planesInput    = addRow(grid, 0, "Liczba samolotów na drużynę:", String.valueOf(config.getInitialPlanesPerTeam()));
        TextField ammoInput      = addRow(grid, 1, "Ilość amunicji:",               String.valueOf(config.getStartingAmmo()));
        TextField speedInput     = addRow(grid, 2, "Szybkość samolotów:",            String.valueOf(config.getBaseSpeed()));
        TextField hpInput        = addRow(grid, 3, "Punkty zdrowia (HP):",           String.valueOf(config.getStartingHp()));
        TextField projSpeedInput = addRow(grid, 4, "Szybkość pocisków:",             String.valueOf(config.getProjectileSpeed()));
        TextField detectMinInput = addRow(grid, 5, "Zasięg wzroku (min):",           String.valueOf(config.getDetectionRangeMin()));
        TextField detectMaxInput = addRow(grid, 6, "Zasięg wzroku (max):",           String.valueOf(config.getDetectionRangeMax()));
        TextField fightInput     = addRow(grid, 7, "Zasięg walki:",                  String.valueOf(config.getFightRange()));

        Button startButton = new Button("URUCHOM");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10 30; -fx-cursor: hand;");

        startButton.setOnAction(e -> {
            try {
                config.setInitialPlanesPerTeam(Integer.parseInt(planesInput.getText()));
                config.setStartingAmmo(Integer.parseInt(ammoInput.getText()));
                config.setBaseSpeed(Float.parseFloat(speedInput.getText()));
                config.setStartingHp(Integer.parseInt(hpInput.getText()));
                config.setProjectileSpeed(Float.parseFloat(projSpeedInput.getText()));
                config.setDetectionRangeMin(Float.parseFloat(detectMinInput.getText()));
                config.setDetectionRangeMax(Float.parseFloat(detectMaxInput.getText()));
                config.setFightRange(Float.parseFloat(fightInput.getText()));
                runSimulation(stage, config);
            } catch (NumberFormatException ex) {
                System.out.println("Niepoprawne dane w polach konfiguracji: " + ex.getMessage());
            }
        });

        root.getChildren().addAll(title, grid, startButton);
        return new Scene(root, 450, 520);
    }

    private TextField addRow(GridPane grid, int row, String labelText, String defaultValue) {
        grid.add(new Label(labelText), 0, row);
        TextField field = new TextField(defaultValue);
        grid.add(field, 1, row);
        return field;
    }

    private void runSimulation(Stage stage, SimulationConfig config) {
        Simulation simulation = new Simulation(config);

        SimulationPanel panel      = new SimulationPanel(simulation);
        StatsPanel      statsPanel = new StatsPanel(simulation);

        // Canvas (1000x1000) w kontenerze o stałej szerokości — nigdy się nie rozciąga
        StackPane canvasHolder = new StackPane(panel);
        canvasHolder.setMinWidth(1000);
        canvasHolder.setPrefWidth(1000);
        canvasHolder.setMaxWidth(1000);
        canvasHolder.setStyle("-fx-background-color: #cfe0f0;");

        // Panel po prawej rośnie wraz z oknem przy maksymalizacji
        HBox.setHgrow(statsPanel, Priority.ALWAYS);

        HBox root = new HBox(canvasHolder, statsPanel);
        root.setFillHeight(true);

        stage.setTitle("Flight Simulation");
        stage.setScene(new Scene(root, 1320, 1000));

        panel.startLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}