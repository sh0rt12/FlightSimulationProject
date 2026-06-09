import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Random;

public class SimulationApp extends Application {

    @Override
    public void start(Stage stage) {
        SimulationConfig config = new SimulationConfig();

        VBox menuRoot = new VBox(20);
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(30));
        menuRoot.setStyle("-fx-background-color: #f4f6f9;");

        Label titleLabel = new Label("KONFIGURACJA SYMULACJI");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(12);

        // Dotychczasowe pola konfiguracyjne
        Label planesLabel = new Label("Liczba samolotów na drużynę:");
        TextField planesInput = new TextField(String.valueOf(config.getInitialPlanesPerTeam()));

        Label ammoLabel = new Label("Ilość amunicji:");
        TextField ammoInput = new TextField(String.valueOf(config.getStartingAmmo()));

        Label speedLabel = new Label("Szybkość samolotów:");
        TextField speedInput = new TextField(String.valueOf(config.getBaseSpeed()));

        Label hpLabel = new Label("Punkty zdrowia (HP):");
        TextField hpInput = new TextField(String.valueOf(config.getStartingHp()));

        Label projSpeedLabel = new Label("Szybkość pocisków:");
        TextField projSpeedInput = new TextField(String.valueOf(config.getProjectileSpeed()));

        // NOWE POLA: Zasięg wzroku (min/max) oraz Zasięg strzału
        Label detectMinLabel = new Label("Zasięg wzroku (min):");
        TextField detectMinInput = new TextField(String.valueOf(config.getDetectionRangeMin()));

        Label detectMaxLabel = new Label("Zasięg wzroku (max):");
        TextField detectMaxInput = new TextField(String.valueOf(config.getDetectionRangeMax()));

        Label fightLabel = new Label("Zasięg strzału (walki):");
        TextField fightInput = new TextField(String.valueOf(config.getFightRange()));

        // Układanie elementów w siatce
        grid.add(planesLabel, 0, 0);
        grid.add(planesInput, 1, 0);
        grid.add(ammoLabel, 0, 1);
        grid.add(ammoInput, 1, 1);
        grid.add(speedLabel, 0, 2);
        grid.add(speedInput, 1, 2);
        grid.add(hpLabel, 0, 3);
        grid.add(hpInput, 1, 3);
        grid.add(projSpeedLabel, 0, 4);
        grid.add(projSpeedInput, 1, 4);
        grid.add(detectMinLabel, 0, 5);
        grid.add(detectMinInput, 1, 5);
        grid.add(detectMaxLabel, 0, 6);
        grid.add(detectMaxInput, 1, 6);
        grid.add(fightLabel, 0, 7);
        grid.add(fightInput, 1, 7);

        Button startButton = new Button("URUCHOM");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10 30; -fx-cursor: hand;");

        menuRoot.getChildren().addAll(titleLabel, grid, startButton);

        // Zwiększyłem wysokość okna (z 400 na 520), aby wszystkie nowe opcje ładnie się pomieściły
        Scene menuScene = new Scene(menuRoot, 450, 520);

        startButton.setOnAction(e -> {
            try {
                // Zapisywanie wprowadzonych danych do konfiguracji
                config.setInitialPlanesPerTeam(Integer.parseInt(planesInput.getText()));
                config.setTargetPlanesPerTeam(Integer.parseInt(planesInput.getText()));
                config.setMaxAmmo(Integer.parseInt(ammoInput.getText()));
                config.setPlaneSpeed(Double.parseDouble(speedInput.getText()));
                config.setMaxHp(Integer.parseInt(hpInput.getText()));
                config.setProjectileSpeed(Double.parseDouble(projSpeedInput.getText()));

                // Nowe przypisania zasięgów
                config.setDetectionRangeMin(Float.parseFloat(detectMinInput.getText()));
                config.setDetectionRangeMax(Float.parseFloat(detectMaxInput.getText()));
                config.setFightRange(Float.parseFloat(fightInput.getText()));

                runSimulation(stage, config);
            } catch (NumberFormatException ex) {
                System.out.println("Wprowadzono niepoprawne dane w polach konfiguracji!");
            }
        });

        stage.setTitle("Flight Simulation - Setup");
        stage.setScene(menuScene);
        stage.show();
    }

    private void runSimulation(Stage stage, SimulationConfig config) {
        Simulation       simulation = new Simulation(config);
        Random           random     = new Random();

        for (int i = 1; i <= config.getInitialPlanesPerTeam(); i++) {
            float y = 50.0f + random.nextFloat() * 900.0f;
            RedPlane red = new RedPlane(i * 2 - 1, 100.0f, y, config);
            red.state = PlaneState.FLYING;
            simulation.addTestPlane(red);
        }

        for (int i = 1; i <= config.getInitialPlanesPerTeam(); i++) {
            float y = 50.0f + random.nextFloat() * 900.0f;
            BluePlane blue = new BluePlane(i * 2, 900.0f, y, config);
            blue.state = PlaneState.FLYING;
            simulation.addTestPlane(blue);
        }

        SimulationPanel panel = new SimulationPanel(simulation);
        StatsPanel statsPanel = new StatsPanel(simulation);

        BorderPane root = new BorderPane();
        root.setCenter(panel);
        root.setRight(statsPanel);

        Scene scene = new Scene(root, 1250, 800);
        stage.setTitle("Flight Simulation");
        stage.setScene(scene);

        panel.startLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}