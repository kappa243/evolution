package agh.idec.oop.gui;

import agh.idec.oop.Engine;
import agh.idec.oop.World;
import agh.idec.oop.observables.INextSimulatedDayObserver;
import agh.idec.oop.utils.MapGridualizer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;


public class App extends Application implements INextSimulatedDayObserver {

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;

    private final HashMap<Engine, MapGridualizer> gridualizerHashMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Zwierzaczki");
        primaryStage.setResizable(false);


        HBox main = new HBox();

        VBox leftPane = new VBox();
        leftPane.setMinWidth(600);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10, 10, 10, 10));

        main.getChildren().addAll(leftPane);

        GridPane grid = new GridPane();
        grid.setPrefWidth(580);
        grid.setPrefHeight(580);



        Label label = new Label();
        leftPane.getChildren().add(label);
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime ;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                    label.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };

        frameRateMeter.start();


//        Engine worldNormal = new Engine();
//        worldNormal.addSimulationDayObserver(this);
//        MapGridualizer mapGridualizer = new MapGridualizer(worldNormal.getMap(), grid, 580, 580);
//        gridualizerHashMap.put(worldNormal, mapGridualizer);
//
//        mapGridualizer.createGrid();

        World world = new World(true, 20, 20, 5, 5, 100, 30, 1,
                    30, 1, 3);
        MapGridualizer gridualizer = new MapGridualizer(world.getMap(), grid, 580, 580);
        gridualizer.createGrid();

        Button button1 = new Button("label1");


        leftPane.getChildren().addAll(grid, button1);



        Thread thread = new Thread(() -> {
            Runnable runnable = () -> {
                world.simulateDay();
                gridualizer.createGrid();
            };

            while(true){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(runnable);
            }
        });

        thread.setDaemon(true);

        Scene scene = new Scene(main, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        thread.start();
    }

    @Override
    public void onNextSimulatedDay(Engine engine) {
        gridualizerHashMap.get(engine).createGrid();
    }
}
