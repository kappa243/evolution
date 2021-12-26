package agh.idec.oop.utils;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.field.Field;
import agh.idec.oop.map.IMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;
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


    private Vector2D top_limit;
    private Vector2D bottom_limit;
    private double height;
    private double width;

    public MapCanvasualizer(IMap map, Canvas canvas) {
        this.map = map;
        this.canvas = canvas;

        this.top_limit = new Vector2D(map.getWidth() - 1, map.getHeight() - 1);
        this.bottom_limit = new Vector2D(0, 0);
    }

    /**
     * Updates canvas with values from map.
     */
    public void updateCanvas() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        this.width = this.canvas.getWidth() / map.getWidth();
        this.height = this.canvas.getHeight() / map.getHeight();

        float maxEnergy = getMaxEnergy();

        HashMap<Vector2D, Field> fields = this.map.getFields();

        fields.values().forEach(field -> {
            Vector2D map_pos = field.getPosition();
            Vector2D pos = mapMapPosToCanvasPos(map_pos, top_limit, bottom_limit);


            switch (field.getType()) {
                case STEPPE -> gc.setFill(STEPPE_COLOR);
                case JUNGLE -> gc.setFill(JUNGLE_COLOR);
            }
            gc.fillRect(pos.getX() * width, pos.getY() * height, width, height);


            if (field.hasPlant()) {
                gc.setFill(PLANT_COLOR);
                gc.fillOval(pos.getX() * width + width / 4, pos.getY() * height + height / 4, width / 2, height / 2);
            } else if (field.hasAnimal()) {
                gc.setFill(getEnergyColor(Objects.requireNonNull(field.getAnimals().peek()), maxEnergy));
                gc.fillOval(pos.getX() * width + width / 4, pos.getY() * height + height / 4, width / 2, height / 2);
            }
        });


//        for (int x = lower_limit.getX(); x <= top_limit.getX(); x++) {
//            for (int y = lower_limit.getY(); y <= top_limit.getY(); y++) {
//                Vector2D map_pos = new Vector2D(x, y);
//                Vector2D pos = mapPointToGrid(map_pos, top_limit, lower_limit);
//
//                Field field = map.getFields().get(map_pos);
//
//                switch (field.getType()) {
//                    case STEPPE -> gc.setFill(STEPPE_COLOR);
//                    case JUNGLE -> gc.setFill(JUNGLE_COLOR);
//                }
//                gc.fillRect(pos.getX() * width, pos.getY() * height, width, height);
//
//
//                if (field.hasPlant()) {
//                    gc.setFill(PLANT_COLOR);
//                    gc.fillOval(pos.getX() * width + width / 4, pos.getY() * height + height / 4, width / 2, height / 2);
//                } else if (field.hasAnimal()) {
//                    gc.setFill(getEnergyColor(field.getAnimals().peek(), maxEnergy));
//                    gc.fillOval(pos.getX() * width + width / 4, pos.getY() * height + height / 4, width / 2, height / 2);
//                }
//            }
//        }
    }


    public String getClickedAnimal(MouseEvent event) {
        Vector2D position = new Vector2D((int) (event.getX() / this.width), (int) (event.getY() / this.height));
        Vector2D mappedPosition = mapCanvasPosToMapPos(position);
        PriorityQueue<Animal> animals = this.map.getAnimalsAt(mappedPosition);

        Animal animal = animals.peek();
        return "X: " + position.getX() + ", Y: " + position.getY() + ", Energy: " + (animal != null ? animal.getEnergy() : (this.map.getFields().get(mappedPosition)).hasPlant() ? "Grass" : "nothing");
    }


    /**
     * Transform vector of map position to 2D canvas coordinates (starting from 0 without negative numbers)
     *
     * @param position   {@link Vector2D} position to transform.
     * @param topRight   {@link Vector2D} top-right position of map.
     * @param bottomLeft {@link Vector2D} bottom-left position of map.
     * @return 2D coordinate.
     */
    private Vector2D mapMapPosToCanvasPos(Vector2D position, Vector2D topRight, Vector2D bottomLeft) {
        position = position.subtract(bottomLeft);
        position = new Vector2D(position.getX(), topRight.getY() - bottomLeft.getY() - position.getY());

        return position;
    }

    private Vector2D mapCanvasPosToMapPos(Vector2D position) {
        position = new Vector2D(position.getX(), (top_limit.getY() - bottom_limit.getY()) - position.getY());
        position = position.add(bottom_limit);

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
        if (energy.getEnergy() < 0) {
            System.out.println(this.map.getAnimalsAt(energy.getPosition()).stream().map(Animal::getEnergy).collect(Collectors.toList()));
            System.out.println(energy.getPosition());
        }
        return Color.hsb(ENERGY_COLOR.getHue(), (energy.getEnergy() * ENERGY_COLOR.getSaturation()) / maxEnergy, ENERGY_COLOR.getBrightness());
    }
}
