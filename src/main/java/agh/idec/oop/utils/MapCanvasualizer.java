package agh.idec.oop.utils;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.field.Field;
import agh.idec.oop.map.IMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.stream.Collectors;


/**
 * Converts map into 2D canvas with colorized elements.
 */
public class MapCanvasualizer {
    //Color.rgb(255, 200, 200);
    //Color.rgb(255, 100, 0);
    private static final int MIN_ENERGY_GREEN = 200;
    private static final int MIN_ENERGY_BLUE = 200;
    private static final int MAX_ENERGY_GREEN = 100;
    private static final int MAX_ENERGY_BLUE = 0;

    private static final Color ENERGY_COLOR = Color.hsb(30, 1, 1);

    private static final Color PLANT_COLOR = Color.rgb(25, 255, 0);
    private static final Color STEPPE_COLOR = Color.rgb(157, 182, 155);
    private static final Color JUNGLE_COLOR = Color.rgb(113, 145, 108);

    private final IMap map;
    private final Canvas canvas;

    private double canvasWidth;
    private double canvasHeight;

    private Vector2D top_limit;
    private Vector2D lower_limit;
    private double height;
    private double width;

    public MapCanvasualizer(IMap map, Canvas canvas, double gridWidth, double gridHeight) {
        this.map = map;
        this.canvas = canvas;
        this.canvasWidth = gridWidth;
        this.canvasHeight = gridHeight;

        this.top_limit = new Vector2D(map.getWidth() - 1, map.getHeight() - 1);
        this.lower_limit = new Vector2D(0, 0);

        this.width = gridWidth / map.getWidth();
        this.height = gridHeight / map.getHeight();
    }

    /**
     * Updates canvas with values from map.
     */
    public void updateCanvas() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        float maxEnergy = getMaxEnergy();

        for (int x = lower_limit.getX(); x <= top_limit.getX(); x++) {
            for (int y = lower_limit.getY(); y <= top_limit.getY(); y++) {
                Vector2D map_pos = new Vector2D(x, y);
                Vector2D pos = mapPointToGrid(map_pos, top_limit, lower_limit);

                Field field = map.getFields().get(map_pos);

                switch (field.getType()) {
                    case STEPPE -> gc.setFill(STEPPE_COLOR);
                    case JUNGLE -> gc.setFill(JUNGLE_COLOR);
                }
                gc.fillRect(pos.getX() * width, pos.getY() * height, width, height);


                if (field.hasPlant()) {
                    gc.setFill(PLANT_COLOR);
                    gc.fillOval(pos.getX() * width + width / 4, pos.getY() * height + height / 4, width / 2, height / 2);
                } else if (field.hasAnimal()) {
                    gc.setFill(getEnergyColor(field.getAnimals().peek(), maxEnergy));
                    gc.fillOval(pos.getX() * width + width / 4, pos.getY() * height + height / 4, width / 2, height / 2);
                }
            }
        }
    }

    /**
     * Transform vector map position to 2D graphic coordinates (starting from 0 without negative numbers)
     *
     * @param position   {@link Vector2D} position to transform.
     * @param topRight   {@link Vector2D} top-right position of map.
     * @param bottomLeft {@link Vector2D} bottom-left position of map.
     * @return 2D coordinate.
     */
    private Vector2D mapPointToGrid(Vector2D position, Vector2D topRight, Vector2D bottomLeft) {
        position = position.subtract(bottomLeft);
        position = new Vector2D(position.getX(), topRight.getY() - bottomLeft.getY() - position.getY());

        return position;
    }

    /**
     * Get maximum energy of all animals in map.
     *
     * @return Float number of energy.
     */
    private float getMaxEnergy() {
        float max = 0;
        for (Animal animal : this.map.getAnimals()) {
            if (animal.getEnergy() > max) {
                max = animal.getEnergy();
            }
        }
        return max;
    }

    /**
     * Return color based on maximum energy of animals.
     *
     * @param energy    Energy of animal we want to color.
     * @param maxEnergy Maximum energy of all animals.
     * @return Color of energy.
     */
    private Color getEnergyColor(Animal energy, float maxEnergy) {
//        int green = (int) (energy * (MIN_ENERGY_GREEN - MAX_ENERGY_GREEN) / maxEnergy);
//        int blue = (int) (energy * (MIN_ENERGY_BLUE - MAX_ENERGY_BLUE) / maxEnergy);
//
//        return Color.rgb(255, MIN_ENERGY_GREEN - green, MIN_ENERGY_BLUE - blue);
        if(energy.getEnergy() < 0){
            System.out.println(this.map.getAnimalsAt(energy.getPosition()).stream().map(Animal::getEnergy).collect(Collectors.toList()));
            System.out.println(energy.getPosition());
        }
        return Color.hsb(ENERGY_COLOR.getHue(), (energy.getEnergy() * ENERGY_COLOR.getSaturation()) / maxEnergy, ENERGY_COLOR.getBrightness());
    }
}
