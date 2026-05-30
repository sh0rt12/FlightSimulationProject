import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Random;

public class SimulationApp extends Application {

    @Override
    public void start(Stage stage) {
        Simulation simulation = new Simulation();
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            float y = 50.0f + random.nextFloat() * 900.0f;
            RedPlane red = new RedPlane(i * 2 - 1, 100.0f, y);
            red.state = PlaneState.FLYING;
            simulation.addTestPlane(red);
        }

        for (int i = 1; i <= 10; i++) {
            float y = 50.0f + random.nextFloat() * 900.0f;
            BluePlane blue = new BluePlane(i * 2, 900.0f, y);
            blue.state = PlaneState.FLYING;
            simulation.addTestPlane(blue);
        }

        SimulationPanel panel = new SimulationPanel(simulation);

        Scene scene = new Scene(panel, 1000, 800);
        stage.setTitle("Flight Simulation");
        stage.setScene(scene);
        stage.show();

        panel.startLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}