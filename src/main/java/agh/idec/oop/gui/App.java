package agh.idec.oop.gui;

import agh.idec.oop.World;
import agh.idec.oop.observables.INextSimulatedDayObserver;
import agh.idec.oop.utils.MapCanvasualizer;
import agh.idec.oop.utils.WorldInformationLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class App extends Application implements INextSimulatedDayObserver {

    private class WorldWrapper {

        final private World world;
        private MapCanvasualizer canvasualizer;
        final private WorldInformationLogger logger;

        final private XYChart.Series<Number, Number> animalsCount = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> plantsCount = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> averageEnergy = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> averageLifeLength = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> averageChildren = new XYChart.Series<>();
        private NumberAxis xAxis;

        public WorldWrapper(World world) {
            this.world = world;
//            this.canvasualizer = new MapCanvasualizer(this.world.getMap(), canvas, width, height);
            this.logger = world.getLogger();

            this.animalsCount.setName("Animals count");
            this.plantsCount.setName("Plants count");
            this.averageEnergy.setName("Average energy");
            this.averageLifeLength.setName("Average life length");
            this.averageChildren.setName("Animals children count");
        }

        public void setCanvasualizer(MapCanvasualizer canvasualizer) {
            this.canvasualizer = canvasualizer;
        }

        public MapCanvasualizer getCanvasualizer() {
            return canvasualizer;
        }

        public void setxAxis(NumberAxis xAxis){
            this.xAxis = xAxis;
        }

        public World getWorld(){
            return this.world;
        }

        public List<XYChart.Series<Number, Number>> getSeries(){
            return Arrays.asList(this.animalsCount, this.plantsCount, this.averageEnergy, this.averageLifeLength, this.averageChildren);
        }

        private void addDataToSeries() {
            this.animalsCount.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAnimalsCount()));
            this.plantsCount.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getPlantsCount()));
            this.averageEnergy.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAverageEnergy()));
            this.averageLifeLength.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAverageAnimalsLifeLength()));
            this.averageChildren.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAverageChildrenCount()));

            for(var series : this.getSeries()){
                if (series.getData().size() > 500)
                    series.getData().remove(0);
                xAxis.setLowerBound(Math.max(0, this.world.getDay() - 500));
                xAxis.setUpperBound(this.world.getDay());
            }
        }

        private void updateUI() {
            this.canvasualizer.updateCanvas();
            this.addDataToSeries();
        }
    }

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;

    private HashMap<World, WorldWrapper> worlds = new HashMap<>();

    @Override
    public void stop() throws Exception {
        super.stop();

        for(var world : this.worlds.values()){
            world.getWorld().stop();
            world.getWorld().removeNextSimulatedDayObserver(this);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Zwierzaczki");
        primaryStage.setResizable(true);


        // world setup
        World world = new World(true, 30, 30, 10, 10, 100, 100, 1,
                5, 10, 30);
        world.addNextSimulatedDayObserver(this);
        WorldWrapper wrapper = new WorldWrapper(world);
        this.worlds.put(world, wrapper);

        World worldMagic = new World(false, 20, 20, 3, 3, 100, 100, 1,
                50, 1, 5);
        worldMagic.addNextSimulatedDayObserver(this);
        WorldWrapper wrapperMagic = new WorldWrapper(worldMagic);
        this.worlds.put(worldMagic, wrapperMagic);


        // split window into 2 columns
        HBox main = new HBox();

        // create both columns
        ScrollPane leftScrollPane = new ScrollPane();
        main.getChildren().addAll(leftScrollPane);

        VBox leftPane = createWorldPane(wrapper);
//        leftPane.setFillWidth(true);
        leftPane.prefWidthProperty().bind(leftScrollPane.widthProperty().subtract(20));
        leftScrollPane.setContent(leftPane);


        ScrollPane rightScrollPane = new ScrollPane();
        main.getChildren().addAll(rightScrollPane);

        VBox rightPane = createWorldPane(wrapperMagic);
//        rightPane.setFillWidth(true);
        rightPane.prefWidthProperty().bind(rightScrollPane.widthProperty().subtract(20));
        rightScrollPane.setContent(rightPane);


        HBox.setHgrow(leftScrollPane, Priority.ALWAYS);
        HBox.setHgrow(rightScrollPane, Priority.ALWAYS);


//        AnimationTimer frameRateMeter = new AnimationTimer() {
//
//            @Override
//            public void handle(long now) {
//                long oldFrameTime = frameTimes[frameTimeIndex];
//                frameTimes[frameTimeIndex] = now;
//                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
//                if (frameTimeIndex == 0) {
//                    arrayFilled = true;
//                }
//                if (arrayFilled) {
//                    long elapsedNanos = now - oldFrameTime;
//                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
//                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
//                    label.setText(String.format("Current frame rate: %.3f", frameRate));
//                }
//            }
//        };
//
//        frameRateMeter.start();


        Scene scene = new Scene(main, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        world.run();
        worldMagic.run();
    }

    private VBox createWorldPane(WorldWrapper wrapper) {
        VBox pane = new VBox();

        pane.setMinHeight(1200);
        pane.setSpacing(10);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setFillWidth(true);

        Canvas canvas = new Canvas(550, 550);
        wrapper.setCanvasualizer(new MapCanvasualizer(wrapper.getWorld().getMap(), canvas, 550, 550));

        Label label = new Label();
        pane.getChildren().add(label);

        canvas.setOnMouseClicked(event -> {
            // follow animal
            if (!wrapper.getWorld().isRunning()) label.setText(wrapper.getCanvasualizer().getClickedAnimal(event));
        });

        Button button1 = new Button("toogle pause");
        button1.setOnMouseClicked(event -> {
            if (wrapper.getWorld().isRunning()) {
                wrapper.getWorld().stop();
            } else {
                wrapper.getWorld().run();
            }
        });
        pane.getChildren().addAll(button1, canvas);

        //chart

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        xAxis.setAnimated(false);
        yAxis.setLabel("Value");
        yAxis.setAnimated(false);

        wrapper.setxAxis(xAxis);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(0);

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Realtime JavaFX Charts");
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);


        // add series to chart
        List<XYChart.Series<Number, Number>> seriesList = wrapper.getSeries();
        for(var series : seriesList){
            lineChart.getData().add(series);
        }
//        lineChart.prefWidthProperty().bind(pane.prefWidthProperty());
        lineChart.prefWidthProperty().bind(pane.widthProperty());

        pane.getChildren().add(lineChart);

        return pane;
    }


    @Override
    public void onNextSimulatedDay(World world) {
        // creating future task will ensure us that we will see updated UI before next simulation day will start
        FutureTask<Void> futureUI = new FutureTask<>(() -> updateUI(world), null);

        Platform.runLater(futureUI);

        try {
            futureUI.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(World world) {
        WorldWrapper wrapper = this.worlds.get(world);
        wrapper.updateUI();
    }
}
