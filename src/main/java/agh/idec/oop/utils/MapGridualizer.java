package agh.idec.oop.utils;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.field.Field;
import agh.idec.oop.map.IMap;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;

import java.util.Objects;

public class MapGridualizer {

    private static final Color MIN_ENERGY_COLOR = Color.rgb(255, 220, 200);
    private static final Color MAX_ENERGY_COLOR = Color.rgb(255, 100, 0);

    private static final Color PLANT_COLOR = Color.rgb(241, 115, 255);
    private static final Color STEPPE_COLOR = Color.rgb(252, 252, 111);
    private static final Color JUNGLE_COLOR = Color.rgb(144, 252, 111);

    private final IMap map;
    private final GridPane gridPane;
    private double gridWidth;
    private double gridHeight;

    private Vector2D top_limit;
    private Vector2D lower_limit;
    private double height;
    private double width;

    public MapGridualizer(IMap map, GridPane grid, double gridWidth, double gridHeight) {
        this.map = map;
        this.gridPane = grid;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        this.top_limit = new Vector2D(map.getWidth() - 1, map.getHeight() - 1);
        this.lower_limit = new Vector2D(0, 0);

        this.width = gridWidth / map.getWidth();
        this.height = gridHeight / map.getHeight();


        for (int x = 0; x <= top_limit.getX() - lower_limit.getX(); x++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(width));
        }

        for (int y = 0; y <= top_limit.getY() - lower_limit.getY(); y++) {
            gridPane.getRowConstraints().add(new RowConstraints(height));
        }

        gridPane.setGridLinesVisible(true);
        gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));


    }

    public void createGrid() {
        gridPane.getChildren().clear();

        float maxEnergy = getMaxEnergy();

        for (int x = lower_limit.getX(); x <= top_limit.getX(); x++) {
            for (int y = lower_limit.getY(); y <= top_limit.getY(); y++) {
                Vector2D map_pos = new Vector2D(x, y);
                Vector2D pos = mapPointToGrid(map_pos, top_limit, lower_limit);

                Field field = map.getFields().get(map_pos);


                if (field.hasPlant()) {
                    Ellipse ellipse = new Ellipse(0, 0, width / 3, height / 3);
                    ellipse.setFill(PLANT_COLOR);

                    gridPane.add(ellipse, pos.getX(), pos.getY(), 1, 1);
                    GridPane.setHalignment(ellipse, HPos.CENTER);
                } else if (field.hasAnimal()) {
                    Ellipse ellipse = new Ellipse(0, 0, width / 3, height / 3);
                    ellipse.setFill(getEnergyColor(this.map.getStrongestAnimal(this.map.getAnimalsFromElements(field.getElements())).getEnergy(), maxEnergy));

                    gridPane.add(ellipse, pos.getX(), pos.getY(), 1, 1);
                    GridPane.setHalignment(ellipse, HPos.CENTER);
                } else {
                    gridPane.add(createGridLabel(""), pos.getX(), pos.getY(), 1, 1);
                }
            }
        }
    }

    private Vector2D mapPointToGrid(Vector2D point, Vector2D top, Vector2D low) {
        point = point.subtract(low);
        point = new Vector2D(point.getX(), top.getY() - low.getY() - point.getY());

        return point;
    }

    private Label createGridLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font(1));
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    private float getMaxEnergy(){
        float max = 0;
        for(Animal animal : this.map.getAnimals()){
            if(animal.getEnergy() > max){
                max = animal.getEnergy();
            }
        }
        return max;
    }

    private Color getEnergyColor(float energy, float maxEnergy){
        int green = (int) (energy*MAX_ENERGY_COLOR.getGreen()/maxEnergy);
        int blue = (int) (energy*MAX_ENERGY_COLOR.getBlue()/maxEnergy);

        return Color.rgb(255, green, blue);
    }
}
