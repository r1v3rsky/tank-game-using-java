import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public void start(Stage stage){
        TankSystem game = new TankSystem();
        game.start(stage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}