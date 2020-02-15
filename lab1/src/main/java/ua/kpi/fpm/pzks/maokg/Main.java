package ua.kpi.fpm.pzks.maokg;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene (root, 300, 250);
        scene.setFill(Color.rgb(127, 255, 0));

        Rectangle tv = new Rectangle(50, 55, 200, 140);
        root.getChildren().add(tv);
        tv.setFill(Color.rgb(255,165,0));

        Rectangle screen = new Rectangle(60, 65, 140, 120);
        root.getChildren().add(screen);
        screen.setFill(Color.rgb(128, 128, 128));
        screen.setArcHeight(20);
        screen.setArcWidth(20);

        for (int i = 0; i < 3; i++) {
            Circle dot = new Circle(230,120 + 25 * i,5, Color.BLACK);
            root.getChildren().add(dot);
        }

        Polyline antenna = new Polyline(120, 25, 150, 55, 180, 25);
        root.getChildren().add(antenna);
        antenna.setStroke(Color.BLACK);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}