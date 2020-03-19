package ua.kpi.fpm.pzks.maokg;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600);

        drawWinnieThePooh(root);

        Path movement = new Path(
            new MoveTo(120, 120),
            new CubicCurveTo(270, 10, 470, 500, 880, 480)
        );

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(5000));
        pathTransition.setPath(movement);
        pathTransition.setNode(root);
        pathTransition.setAutoReverse(true);

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(2500), root);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(3);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(5000), root);
        scaleTransition.setToX(0.4);
        scaleTransition.setToY(0.4);
        scaleTransition.setAutoReverse(true);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
            rotateTransition,
            scaleTransition,
            pathTransition
        );
        parallelTransition.setCycleCount(Timeline.INDEFINITE);
        parallelTransition.setAutoReverse(true);
        parallelTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawWinnieThePooh(Group group) {
        Color bodyColor = Color.rgb(201, 154, 102);
        Color shirtColor = Color.rgb(198, 55, 51);


        // leg background start section
        Path legBackgroundMain = new Path(
            new MoveTo(62, 169),
            new QuadCurveTo(42, 163, 14, 170),
            new QuadCurveTo(6, 168, 5, 158),
            new LineTo(6, 132),
            new QuadCurveTo(25, 118, 27, 141),
            new QuadCurveTo(42, 131, 56, 133)
        );
        legBackgroundMain.setFill(bodyColor);
        legBackgroundMain.setStroke(Color.BLACK);
        group.getChildren().add(legBackgroundMain);

        QuadCurve backgroundFootTopLine = new QuadCurve(27, 141, 28, 145, 24, 151);
        backgroundFootTopLine.setStroke(Color.BLACK);
        backgroundFootTopLine.setFill(Color.TRANSPARENT);
        group.getChildren().add(backgroundFootTopLine);

        Ellipse backgroundFoot = new Ellipse(8, 147, 3, 17);
        backgroundFoot.setFill(Color.TRANSPARENT);
        backgroundFoot.setStroke(Color.BLACK);
        group.getChildren().add(backgroundFoot);
        // leg background end section


        // arm background start section
        Path armBackground = new Path(
            new MoveTo(88, 103),
            new LineTo(97, 95),
            new LineTo(103, 103)
        );
        armBackground.setFill(bodyColor);
        armBackground.setStroke(Color.BLACK);
        group.getChildren().add(armBackground);
        // arm background end section


        // shirt back start section
        var backOfShirt = new Polyline(167, 169, 156, 166, 168, 149);
        backOfShirt.setFill(shirtColor);
        backOfShirt.setStroke(Color.BLACK);
        group.getChildren().add(backOfShirt);
        // shirt back end section


        // stomach start section
        Circle stomachMain = new Circle(95, 145, 40, bodyColor);
        stomachMain.setStroke(Color.BLACK);
        group.getChildren().add(stomachMain);

        Path stomachTopAndRightPart = new Path(
            new MoveTo(64, 170),
            new QuadCurveTo(90, 210, 135, 185),
            new QuadCurveTo(157, 172, 165, 155),
            new LineTo(118, 102),
            new QuadCurveTo(95, 94, 72, 112)
        );
        stomachTopAndRightPart.setFill(bodyColor);
        stomachTopAndRightPart.setStroke(Color.BLACK);
        group.getChildren().add(stomachTopAndRightPart);
        // stomach end section


        // leg foreground start section
        Path legForegroundMain = new Path(
            new MoveTo(49, 185),
            new CubicCurveTo(48, 178, 68, 174, 71, 188),
            new QuadCurveTo(105, 152, 122, 172),
            new CubicCurveTo(143, 195, 57, 228, 52, 222)
        );
        legForegroundMain.setFill(bodyColor);
        legForegroundMain.setStroke(Color.BLACK);
        group.getChildren().add(legForegroundMain);

        QuadCurve foregroundFootTopLine = new QuadCurve(71, 188, 74, 191, 68, 210);
        foregroundFootTopLine.setStroke(Color.BLACK);
        foregroundFootTopLine.setFill(Color.TRANSPARENT);
        group.getChildren().add(foregroundFootTopLine);

        Ellipse foregroundFoot = new Ellipse(51, 203, 5, 19);
        foregroundFoot.setFill(bodyColor);
        foregroundFoot.setStroke(Color.BLACK);
        group.getChildren().add(foregroundFoot);
        // leg foreground end section


        // shirt body start section
        Path shirtMain = new Path(
            new MoveTo(95, 100),
            new CubicCurveTo(124, 92, 150, 148, 166, 168),
            new QuadCurveTo(166, 156, 172, 151),
            new LineTo(203, 125),
            new CubicCurveTo(188, 110, 207, 94, 194, 91),
            new QuadCurveTo(209, 75, 187, 75),
            new LineTo(106, 75),
            new QuadCurveTo(108, 84, 94, 90),
            new QuadCurveTo(97, 95, 98, 100)
        );
        shirtMain.setFill(shirtColor);
        shirtMain.setStroke(Color.BLACK);
        group.getChildren().add(shirtMain);
        // shirt body end section


        // arm foreground start section
        Path armForeground = new Path(
            new MoveTo(197, 169),
            new CubicCurveTo(217, 165, 214, 191, 188, 183),
            new QuadCurveTo(177, 179, 167, 134),
            new LineTo(195, 127),
            new LineTo(202, 168)
        );
        armForeground.setFill(bodyColor);
        armForeground.setStroke(Color.BLACK);
        group.getChildren().add(armForeground);
        // arm foreground end section


        // head start section
        Circle backgroundEar = new Circle(142, 16, 7, bodyColor);
        backgroundEar.setStroke(Color.BLACK);
        group.getChildren().add(backgroundEar);

        Path headMain = new Path(
            new MoveTo(127, 102),
            new QuadCurveTo(94, 67, 102, 56),
            new LineTo(118, 48),
            new QuadCurveTo(115, 40, 110, 38),
            new QuadCurveTo(108, 32, 113, 30),
            new QuadCurveTo(130, 30, 147, 18),
            new QuadCurveTo(182, 5, 193, 55),
            new CubicCurveTo(196, 64, 186, 73, 193, 84)
        );
        headMain.setFill(bodyColor);
        headMain.setStroke(Color.BLACK);
        group.getChildren().add(headMain);

        Path foregroundEar = new Path(
            new MoveTo(172, 24),
            new QuadCurveTo(162, 14, 173, 5),
            new QuadCurveTo(193, 2, 182, 34)
        );
        foregroundEar.setFill(bodyColor);
        foregroundEar.setStroke(Color.BLACK);
        group.getChildren().add(foregroundEar);

        Circle leftEye = new Circle(121, 42, 1.5, Color.BLACK);
        group.getChildren().add(leftEye);

        Circle rightEye = new Circle(141, 39, 1.5, Color.BLACK);
        group.getChildren().add(rightEye);

        QuadCurve leftEyebrow = new QuadCurve(115, 38, 113, 31, 120, 32);
        leftEyebrow.setFill(Color.TRANSPARENT);
        leftEyebrow.setStroke(Color.BLACK);
        group.getChildren().add(leftEyebrow);

        QuadCurve rightEyebrow = new QuadCurve(138, 27, 147, 23, 150, 30);
        rightEyebrow.setFill(Color.TRANSPARENT);
        rightEyebrow.setStroke(Color.BLACK);
        group.getChildren().add(rightEyebrow);

        QuadCurve underRightEyeLine = new QuadCurve(140, 46, 143, 40, 149, 41);
        underRightEyeLine.setFill(Color.TRANSPARENT);
        underRightEyeLine.setStroke(Color.BLACK);
        group.getChildren().add(underRightEyeLine);

        QuadCurve noseBridge = new QuadCurve(125, 48, 128, 42, 124, 36);
        noseBridge.setFill(Color.TRANSPARENT);
        noseBridge.setStroke(Color.BLACK);
        group.getChildren().add(noseBridge);

        QuadCurve mouth = new QuadCurve(143, 58, 123, 93, 110, 62);
        mouth.setFill(Color.TRANSPARENT);
        mouth.setStroke(Color.BLACK);
        group.getChildren().add(mouth);

        Line mouthRightCorner = new Line(146, 57, 141, 56);
        mouthRightCorner.setFill(Color.TRANSPARENT);
        mouthRightCorner.setStroke(Color.BLACK);
        group.getChildren().add(mouthRightCorner);

        QuadCurve underMouthLine = new QuadCurve(120, 78, 122, 84, 127, 86);
        underMouthLine.setFill(Color.TRANSPARENT);
        underMouthLine.setStroke(Color.BLACK);
        group.getChildren().add(underMouthLine);

        QuadCurve nosePlace = new QuadCurve(110, 62, 107, 42, 128, 49);
        nosePlace.setFill(Color.TRANSPARENT);
        nosePlace.setStroke(Color.BLACK);
        group.getChildren().add(nosePlace);

        CubicCurve nose = new CubicCurve(114, 60, 101, 51, 114, 39, 120, 52);
        nose.setFill(Color.BLACK);
        group.getChildren().add(nose);
        // head end section


        // shirt section start
        Path collar = new Path(
            new MoveTo(196, 77),
            new QuadCurveTo(203, 85, 172, 83),
            new CubicCurveTo(153, 79, 126, 113, 119, 94),
            new LineTo(118, 101),
            new LineTo(158, 139)
        );
        collar.setFill(shirtColor);
        collar.setStroke(Color.BLACK);
        var line = new Polyline(118, 97, 118, 101, 158, 139);
        line.setStroke(shirtColor);
        line.setStrokeWidth(3);
        group.getChildren().addAll(collar, line);

        QuadCurve shoulderLine = new QuadCurve(161, 94, 171, 88, 190, 88);
        shoulderLine.setFill(Color.TRANSPARENT);
        shoulderLine.setStroke(Color.BLACK);
        group.getChildren().add(shoulderLine);

        Path sleeve = new Path(
            new MoveTo(161, 108),
            new QuadCurveTo(163, 119, 167, 121),
            new LineTo(166, 134),
            new CubicCurveTo(179, 142, 193, 124, 203, 125)
        );
        sleeve.setFill(shirtColor);
        sleeve.setStroke(Color.BLACK);
        group.getChildren().addAll(sleeve);

        QuadCurve creaseTopLine = new QuadCurve(113, 86, 110, 88, 110, 95);
        creaseTopLine.setFill(Color.TRANSPARENT);
        creaseTopLine.setStroke(Color.BLACK);
        group.getChildren().add(creaseTopLine);

        QuadCurve creaseBottomLine = new QuadCurve(116, 95, 101, 93, 99, 98);
        creaseBottomLine.setFill(Color.TRANSPARENT);
        creaseBottomLine.setStroke(Color.BLACK);
        group.getChildren().add(creaseBottomLine);
        // shirt section end
    }

}
