package agh.idec.oop.gui;

import agh.idec.oop.World;
import agh.idec.oop.element.Animal;
import agh.idec.oop.field.Field;
import agh.idec.oop.observables.IMagicDayObserver;
import agh.idec.oop.observables.INextSimulatedDayObserver;
import agh.idec.oop.observables.ISelectedAnimalActionsObserver;
import agh.idec.oop.utils.CSVWriter;
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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class App extends Application implements INextSimulatedDayObserver, IMagicDayObserver {

    private class WorldWrapper implements ISelectedAnimalActionsObserver {

        final private World world;
        final private String world_name;
        private MapCanvasualizer canvasualizer;
        final private WorldInformationLogger logger;

        final private XYChart.Series<Number, Number> animalsCount = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> plantsCount = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> averageEnergy = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> averageLifeLength = new XYChart.Series<>();
        final private XYChart.Series<Number, Number> averageChildren = new XYChart.Series<>();

        final private List<Number> animalsCountData = new ArrayList<>();
        final private List<Number> plantsCountData = new ArrayList<>();
        final private List<Number> averageEnergyData = new ArrayList<>();
        final private List<Number> averageLifeLengthData = new ArrayList<>();
        final private List<Number> averageChildrenData = new ArrayList<>();

        private Label dominantLabel;
        private NumberAxis xAxis;

        private Animal selectedAnimal = null;
        private int childrenCount = 0;
        private List<Animal> selectedDescendants = new ArrayList<>();
        private boolean isDead = false;

        private Label genotypeLabel;
        private Label childrenLabel;
        private Label descendantsLabel;
        private Label deathLabel;

        public WorldWrapper(World world, String world_name) {
            this.world = world;
            this.world_name = world_name;
            this.logger = world.getLogger();

            this.animalsCount.setName("Animals count");
            this.plantsCount.setName("Plants count");
            this.averageEnergy.setName("Average energy");
            this.averageLifeLength.setName("Average life length");
            this.averageChildren.setName("Average children count");
        }

        public void setCanvasualizer(MapCanvasualizer canvasualizer) {
            this.canvasualizer = canvasualizer;
        }

        public MapCanvasualizer getCanvasualizer() {
            return canvasualizer;
        }

        public void setxAxis(NumberAxis xAxis) {
            this.xAxis = xAxis;
        }

        public void setDominantLabel(Label dominantLabel){
            this.dominantLabel = dominantLabel;
        }

        public World getWorld() {
            return this.world;
        }

        public void setGenotypeLabel(Label genotypeLabel) {
            this.genotypeLabel = genotypeLabel;
        }

        public void setChildrenLabel(Label childrenLabel) {
            this.childrenLabel = childrenLabel;
        }

        public void setDescendantsLabel(Label descendantsLabel) {
            this.descendantsLabel = descendantsLabel;
        }

        public void setDeathLabel(Label deathLabel) {
            this.deathLabel = deathLabel;
        }

        public void announceMagicDay() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Magic day " + this.world.getMagicDay() + " happened at " + this.world_name + "!", ButtonType.OK);
            alert.show();
            this.world.stop();

            alert.setOnCloseRequest(event -> this.world.run());
        }

        public void saveData(Stage stage) {
            List<String> header = Arrays.asList("day", "animals_count", "plants_count", "average_energy", "average_life_length", "average_children_count");
            List<List<Number>> dataList = Arrays.asList(this.animalsCountData, this.plantsCountData, this.averageEnergyData, this.averageLifeLengthData, this.averageChildrenData);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save stats");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

            File file = fileChooser.showSaveDialog(stage);
            try {
                CSVWriter csvWriter = new CSVWriter(file);
                csvWriter.writeSeries(header, dataList);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.toString(), ButtonType.OK);
                alert.show();
            }
        }

        public List<XYChart.Series<Number, Number>> getSeries() {
            return Arrays.asList(this.animalsCount, this.plantsCount, this.averageEnergy, this.averageLifeLength, this.averageChildren);
        }

        private void addDataToSeries() {
            this.animalsCount.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAnimalsCount()));
            this.animalsCountData.add(this.logger.getAnimalsCount());

            this.plantsCount.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getPlantsCount()));
            this.plantsCountData.add(this.logger.getPlantsCount());

            this.averageEnergy.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAverageEnergy()));
            this.averageEnergyData.add(this.logger.getAverageEnergy());

            this.averageLifeLength.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAverageAnimalsLifeLength()));
            this.averageLifeLengthData.add(this.logger.getAverageAnimalsLifeLength());

            this.averageChildren.getData().add(new XYChart.Data<>(this.world.getDay(), this.logger.getAverageChildrenCount()));
            this.averageChildrenData.add(this.logger.getAverageChildrenCount());


            xAxis.setLowerBound(Math.max(0, this.world.getDay() - 500));
            xAxis.setUpperBound(this.world.getDay());

            for (var series : this.getSeries()) {
                if (series.getData().size() > 500)
                    series.getData().remove(0);
            }
        }

        private void updateDominant() {
            this.dominantLabel.setText("Dominant genotype: " + this.logger.getDominantGenotype());
        }

        private void updateUI() {
            this.canvasualizer.updateCanvas();
            this.addDataToSeries();
            this.updateDominant();
            this.drawSelectedAnimalInfo();
        }

        public void drawDominants() {
            for (Field field : this.world.getMap().getFields().values()) {
                if (field.hasAnimal()) {
                    //noinspection ConstantConditions // we checked that field has at least one animal
                    if (field.getAnimals().peek().getGenotype().equals(this.logger.getGenotype())) {
                        this.canvasualizer.fillDominant(field.getPosition());
                    }
                }
            }
        }

        private void drawSelectedAnimalInfo() {
            if (this.selectedAnimal != null) {
                this.childrenLabel.setText("Children: " + this.childrenCount);
                this.descendantsLabel.setText("Descendants: " + this.selectedDescendants.size());
                this.deathLabel.setText("Is dead: " + this.isDead);
            }
        }

        public void setSelectedAnimal(Animal selectedAnimal) {
            if (this.selectedAnimal != null) this.selectedAnimal.removeSelectedAnimalActionsObserver(this);
            this.selectedAnimal = selectedAnimal;
            this.clearDescendants();
            this.childrenCount = 0;
            this.isDead = false;

            if (this.selectedAnimal != null) {
                StringBuilder genotype = new StringBuilder();
                for (var gene : this.selectedAnimal.getGenotype()) {
                    genotype.append(gene);
                }
                this.genotypeLabel.setText("Genotype: " + genotype);

                this.selectedAnimal.addSelectedAnimalActionsObserver(this);
            } else {
                this.genotypeLabel.setText("Genotype: ");
                this.childrenLabel.setText("Children: ");
                this.descendantsLabel.setText("Descendants: ");
                this.deathLabel.setText("Is dead: ");
            }
        }

        private void addDescendant(Animal animal) {
            this.selectedDescendants.add(animal);
            animal.addSelectedAnimalActionsObserver(this);
        }

        private void clearDescendants() {
            for (Animal animal : this.selectedDescendants) {
                animal.removeSelectedAnimalActionsObserver(this);
            }
            this.selectedDescendants = new ArrayList<>();
        }

        @Override
        public void selectedAnimalDeath(Animal animal) {
            this.isDead = true;
        }

        @Override
        public void selectedAnimalBreed(Animal animal, Animal newborn) {
            if (animal.equals(this.selectedAnimal)) {
                this.childrenCount++;
            }
            this.addDescendant(newborn);
        }
    }

    private class SettingsWrapper {
        public int width = 20;
        public int height = 20;
        public float jungleRatio = 0.1f;
        public boolean wrapAround = true;
        public int animals = 100;
        public float energy = 100;
        public float moveEnergy = 1.0f;
        public float plantEnergy = 10;
        public int steppePlants = 1;
        public int junglePlants = 3;
        public boolean magic = false;
    }

    private HashMap<World, WorldWrapper> worlds = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Evolution");
        primaryStage.setResizable(false);

        VBox main = new VBox();
        main.setPadding(new Insets(10, 10, 10, 10));
        main.setSpacing(5);

        SettingsWrapper world1 = new SettingsWrapper();
        SettingsWrapper world2 = new SettingsWrapper();

        Button world1Button = new Button("Configure world 1");
        world1Button.setPrefWidth(210);
        world1Button.setOnMouseClicked(event -> {
            Platform.runLater(() -> this.configWorld(primaryStage, world1));
            primaryStage.close();
        });

        Button world2Button = new Button("Configure world 2");
        world2Button.setPrefWidth(210);
        world2Button.setOnMouseClicked(event -> {
            this.configWorld(primaryStage, world2);
            primaryStage.close();
        });

        HBox div = new HBox();
        Label delay = new Label("Delay [ms]: ");
        TextField delay_input = new TextField("10");
        delay_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> delay_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });
        div.getChildren().addAll(delay, delay_input);

        Button startButton = new Button("Start");
        startButton.setPrefWidth(210);
        startButton.setOnMouseClicked(event -> {
            try {
                this.setupWorld(primaryStage, world1, world2, Integer.parseInt(delay_input.getText()));
            } catch (Exception e) {
                if (e.getClass().equals(NumberFormatException.class)) {
                    this.setupWorld(primaryStage, world1, world2, 0);
                } else {
                    e.printStackTrace();
                }
            }
        });

        main.getChildren().addAll(world1Button, world2Button, div, startButton);


        Scene scene = new Scene(main, 235, 140);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void setupWorld(Stage primaryStage, SettingsWrapper world1Settings, SettingsWrapper world2Settings, long delay) {
        primaryStage.close();

        Stage stage = new Stage();
        stage.setTitle("Evolution");
        stage.setResizable(true);
        stage.setMaximized(true);


        // worlds setup
        World world1 = new World(delay, world1Settings.wrapAround, world1Settings.width, world1Settings.height,
                world1Settings.jungleRatio, world1Settings.animals, world1Settings.energy, world1Settings.moveEnergy,
                world1Settings.plantEnergy, world1Settings.steppePlants, world1Settings.junglePlants,
                world1Settings.magic);
        world1.addNextSimulatedDayObserver(this);
        if (world1Settings.magic) world1.addMagicDayObserver(this);
        WorldWrapper wrapper1 = new WorldWrapper(world1, "Left world");
        this.worlds.put(world1, wrapper1);

        World world2 = new World(delay, world2Settings.wrapAround, world2Settings.width, world2Settings.height,
                world2Settings.jungleRatio, world2Settings.animals, world2Settings.energy, world2Settings.moveEnergy,
                world2Settings.plantEnergy, world2Settings.steppePlants, world2Settings.junglePlants,
                world2Settings.magic);
        world2.addNextSimulatedDayObserver(this);
        if (world2Settings.magic) world2.addMagicDayObserver(this);
        WorldWrapper wrapper2 = new WorldWrapper(world2, "Right world");
        this.worlds.put(world2, wrapper2);


        // split window into 2 columns
        HBox main = new HBox();

        // create both columns
        ScrollPane leftScrollPane = new ScrollPane();

        VBox leftPane = createWorldPane(stage, wrapper1);
        leftPane.prefWidthProperty().bind(leftScrollPane.widthProperty().subtract(20));
        leftScrollPane.setContent(leftPane);


        ScrollPane rightScrollPane = new ScrollPane();

        VBox rightPane = createWorldPane(stage, wrapper2);
        rightPane.prefWidthProperty().bind(rightScrollPane.widthProperty().subtract(20));
        rightScrollPane.setContent(rightPane);

        main.getChildren().addAll(leftScrollPane, rightScrollPane);

        HBox.setHgrow(leftScrollPane, Priority.ALWAYS);
        HBox.setHgrow(rightScrollPane, Priority.ALWAYS);


        Scene worldsScene = new Scene(main, 1200, 500);
        stage.setScene(worldsScene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            world1.stop();
            world1.removeNextSimulatedDayObserver(this);
            if (world1Settings.magic) world1.removeMagicDayObserver(this);
            this.worlds.remove(world1);

            world2.stop();
            world2.removeNextSimulatedDayObserver(this);
            if (world2Settings.magic) world2.removeMagicDayObserver(this);
            this.worlds.remove(world2);

            primaryStage.show();
        });

        world1.run();
        world2.run();
    }

    private void configWorld(Stage primaryStage, SettingsWrapper wrapper) {
        Stage stage = new Stage();
        stage.setTitle("Config world");
        stage.setResizable(false);

        HBox config = new HBox();
        config.setPadding(new Insets(10, 10, 10, 10));

        Label width = new Label("Width: ");
        TextField width_input = new TextField(Integer.toString(wrapper.width));
        width_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> width_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label height = new Label("Height: ");
        TextField height_input = new TextField(Integer.toString(wrapper.height));
        height_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> height_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label jungleRatio = new Label("Jungle ratio [0.0-1.0]: ");
        TextField jungleRatio_input = new TextField(Float.toString(wrapper.jungleRatio));
        jungleRatio_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                Platform.runLater(() -> jungleRatio_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label wrapAround = new Label("Wrap around: ");
        Button wrapAroundButton = new Button(Boolean.toString(wrapper.wrapAround));
        wrapAroundButton.setMinWidth(150);
        wrapAroundButton.setOnMouseClicked(event -> {
            if (Boolean.parseBoolean(wrapAroundButton.getText())) {
                wrapAroundButton.setText("false");
            } else {
                wrapAroundButton.setText("true");
            }
        });

        Label animals = new Label("Starting animals: ");
        TextField animals_input = new TextField(Integer.toString(wrapper.animals));
        animals_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> animals_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label energy = new Label("Starting energy: ");
        TextField energy_input = new TextField(Float.toString(wrapper.energy));
        energy_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> energy_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label moveEnergy = new Label("Move energy: ");
        TextField moveEnergy_input = new TextField(Float.toString(wrapper.moveEnergy));
        moveEnergy_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                Platform.runLater(() -> moveEnergy_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label plantEnergy = new Label("Plant energy: ");
        TextField plantEnergy_input = new TextField(Float.toString(wrapper.plantEnergy));
        plantEnergy_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                Platform.runLater(() -> plantEnergy_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label steppePlants = new Label("Steppe plants: ");
        TextField steppePlants_input = new TextField(Integer.toString(wrapper.steppePlants));
        steppePlants_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> steppePlants_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });

        Label junglePlants = new Label("Jungle plants: ");
        TextField junglePlants_input = new TextField(Integer.toString(wrapper.junglePlants));
        junglePlants_input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                Platform.runLater(() -> junglePlants_input.setText(newValue.replaceAll("[\\D+]", "")));
            }
        });


        Label magic = new Label("Is your world magic? ");
        Button magicButton = new Button(Boolean.toString(wrapper.magic));
        magicButton.setMinWidth(150);
        magicButton.setOnMouseClicked(event -> {
            switch (magicButton.getText()) {
                case "false" -> magicButton.setText("true");
                case "true" -> magicButton.setText("false");
            }
        });


        Button confirm = new Button("Confirm world");

        GridPane grid = new GridPane();
        grid.prefWidthProperty().bind(config.widthProperty());

        grid.addRow(0, width, width_input);
        grid.addRow(1, height, height_input);
        grid.addRow(2, jungleRatio, jungleRatio_input);
        grid.addRow(3, wrapAround, wrapAroundButton);
        grid.addRow(4, animals, animals_input);
        grid.addRow(5, energy, energy_input);
        grid.addRow(6, moveEnergy, moveEnergy_input);
        grid.addRow(7, plantEnergy, plantEnergy_input);
        grid.addRow(8, steppePlants, steppePlants_input);
        grid.addRow(9, junglePlants, junglePlants_input);
        grid.addRow(10, magic, magicButton);
        grid.addRow(11, confirm);
        config.getChildren().addAll(grid);

        confirm.setOnMouseClicked(event -> {
            try {
                wrapper.width = Integer.parseInt(width_input.getText());
                wrapper.height = Integer.parseInt(height_input.getText());
                wrapper.jungleRatio = Float.parseFloat(jungleRatio_input.getText());
                wrapper.wrapAround = Boolean.parseBoolean(wrapAroundButton.getText());
                wrapper.animals = Integer.parseInt(animals_input.getText());
                wrapper.energy = Float.parseFloat(energy_input.getText());
                wrapper.moveEnergy = Float.parseFloat(moveEnergy_input.getText());
                wrapper.plantEnergy = Float.parseFloat(plantEnergy_input.getText());
                wrapper.steppePlants = Integer.parseInt(steppePlants_input.getText());
                wrapper.junglePlants = Integer.parseInt(junglePlants_input.getText());
                wrapper.magic = Boolean.parseBoolean(magicButton.getText());

                primaryStage.show();
                stage.close();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "One of inputs is empty!", ButtonType.CLOSE).showAndWait();
            }
        });

        Scene scene = new Scene(config, 300, 325);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> primaryStage.show());
    }

    private VBox createWorldPane(Stage stage, WorldWrapper wrapper) {
        VBox pane = new VBox();

//        pane.setMinHeight(1200);
        pane.setSpacing(10);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setFillWidth(true);

        HBox div1 = new HBox();
        pane.getChildren().add(div1);
        div1.setSpacing(5);

        Button pauseButton = new Button("Toggle pause");
        pauseButton.setOnMouseClicked(event -> {
            if (wrapper.getWorld().isRunning()) {
                wrapper.getWorld().stop();
            } else {
                wrapper.getWorld().run();
            }
        });
        pane.getChildren().add(pauseButton);

        Button saveDataButton = new Button("Save data to csv file");
        saveDataButton.setOnMouseClicked(event -> {
            if (!wrapper.getWorld().isRunning()) {
                Platform.runLater(() -> wrapper.saveData(stage));
            }
        });
        pane.getChildren().add(saveDataButton);

        Label genotypeLabel = new Label("Genotype: ");
        Label childrenLabel = new Label("Children: ");
        Label descendantsLabel = new Label("Descendants: ");
        Label deathLabel = new Label("Is dead: ");
        wrapper.setGenotypeLabel(genotypeLabel);
        wrapper.setChildrenLabel(childrenLabel);
        wrapper.setDescendantsLabel(descendantsLabel);
        wrapper.setDeathLabel(deathLabel);

        div1.getChildren().addAll(pauseButton, saveDataButton, genotypeLabel, childrenLabel, descendantsLabel, deathLabel);


        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(pane.widthProperty().subtract(25));
        canvas.heightProperty().bind(canvas.widthProperty().multiply((double) wrapper.getWorld().getMap().getHeight() / wrapper.getWorld().getMap().getWidth()));
        wrapper.setCanvasualizer(new MapCanvasualizer(wrapper.getWorld().getMap(), canvas));

        pane.getChildren().add(canvas);

        canvas.setOnMouseClicked(event -> {
            // select animal
            Animal animal = null;
            if (!wrapper.getWorld().isRunning()) {
                animal = wrapper.getCanvasualizer().getClickedAnimal(event);
            }

            Animal finalAnimal = animal;
            Platform.runLater(() -> {
                wrapper.setSelectedAnimal(finalAnimal);
                wrapper.drawSelectedAnimalInfo();
            });
        });

        HBox div2 = new HBox();
        div2.setSpacing(5);
        Button showDominantButton = new Button("Show dominant genotypes");
        showDominantButton.setOnMouseClicked(event -> {
            if (!wrapper.getWorld().isRunning()) {
                Platform.runLater(wrapper::drawDominants);
            }
        });
        Label dominantGenotype = new Label("Dominant genotype: ");
        wrapper.setDominantLabel(dominantGenotype);
        div2.getChildren().addAll(showDominantButton, dominantGenotype);
        pane.getChildren().add(div2);



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
        lineChart.setTitle("Stats");
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);

        // add series to chart
        List<XYChart.Series<Number, Number>> seriesList = wrapper.getSeries();
        for (var series : seriesList) {
            lineChart.getData().add(series);
        }
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

    @Override
    public void onMagicDay(World world) {
        Platform.runLater(() -> this.worlds.get(world).announceMagicDay());
    }

    private void updateUI(World world) {
        try {
            WorldWrapper wrapper = this.worlds.get(world);
            wrapper.updateUI();
        } catch (Exception ignored) {
        } // when closing stage wrapper can be null

    }
}
