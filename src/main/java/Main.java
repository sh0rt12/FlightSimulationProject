public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation();

        RedPlane red1 = new RedPlane(1, 350.0f, 400.0f);
        BluePlane blue1 = new BluePlane(2, 380.0f, 420.0f);

        red1.target = blue1;
        red1.state = PlaneState.FLYING;
        red1.fightTimer = 3;

        blue1.target = red1;
        blue1.state = PlaneState.FLYING;
        blue1.evadeTimer = 3;

        simulation.addTestPlane(red1);
        simulation.addTestPlane(blue1);

        System.out.println(">>> URUCHAMIANIE TESTU <<<");
        simulation.printCurrentStatus();

        for (int i = 0; i < 10; i++) {
            simulation.step();
            simulation.printCurrentStatus();

            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}