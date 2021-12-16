package agh.idec.oop.gui;

import agh.idec.oop.World;
import agh.idec.oop.observables.INextSimulatedDayObserver;
import agh.idec.oop.utils.MapCanvasualizer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class App extends Application implements INextSimulatedDayObserver {

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;

    private final HashMap<World, MapCanvasualizer> gridualizerHashMap = new HashMap<>();

    @Override
    public void stop() throws Exception {
        super.stop();

        for (var world : gridualizerHashMap.keySet()) {
            world.stop();
            world.removeNextSimulatedDayObserver(this);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Zwierzaczki");
        primaryStage.setResizable(false);


        HBox main = new HBox();

        VBox leftPane = new VBox();
        leftPane.setMinWidth(600);
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10, 10, 10, 10));

        main.getChildren().addAll(leftPane);

        Canvas canvas = new Canvas(580, 580);

        Label label = new Label();
        leftPane.getChildren().add(label);
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex];
                frameTimes[frameTimeIndex] = now;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                if (frameTimeIndex == 0) {
                    arrayFilled = true;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                    label.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };

        frameRateMeter.start();


        World world = new World(true, 20, 20, 5, 5, 100, 30, 1,
                30, 1, 5);
        world.addNextSimulatedDayObserver(this);

        MapCanvasualizer gridualizer = new MapCanvasualizer(world.getMap(), canvas, 580, 580);
        gridualizer.updateCanvas();

        gridualizerHashMap.put(world, gridualizer);

        Button button1 = new Button("label1");


        leftPane.getChildren().addAll(canvas, button1);


        Scene scene = new Scene(main, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        world.run();
    }

    @Override
    public void onNextSimulatedDay(World world) {
        // creating future task will ensure us that we will see updated UI before next simulation day will start
        FutureTask<Void> updateUI = new FutureTask<>(() -> drawUI(world), null);

        Platform.runLater(updateUI);

        try {
            updateUI.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void drawUI(World world) {
        gridualizerHashMap.get(world).updateCanvas();
    }
}
