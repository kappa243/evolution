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

    private static final Color DOMINANT_COLOR = Color.hsb(285, 1, 1);

    private static final Color ENERGY_COLOR = Color.hsb(30, 1, 1);

    private static final Color PLANT_COLOR = Color.rgb(25, 255, 0);
    private static final Color STEPPE_COLOR = Color.rgb(157, 182, 155);
    private static final Color JUNGLE_COLOR = Color.rgb(113, 145, 108);

    private final IMap map;
    private final Canvas canvas;


    private final Vector2D top_limit;
    private final Vector2D bottom_limit;
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
            Vector2D pos = mapMapPosToCanvasPos(map_pos);


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
    }


    /**
     * Return animal at clicked position of canvas.
     *
     * @param event MouseEvent of clicked canvas.
     * @return Animal at position or null if it does not exist.
     */
    public Animal getClickedAnimal(MouseEvent event) {
        Vector2D position = new Vector2D((int) (event.getX() / this.width), (int) (event.getY() / this.height));
        Vector2D mappedPosition = mapCanvasPosToMapPos(position);
        PriorityQueue<Animal> animals = this.map.getAnimalsAt(mappedPosition);

        return animals.peek();
    }

    /**
     * Fills position on canvas as dominant animal.
     *
     * @param position Position to fill.
     */
    public void fillDominant(Vector2D position) {
        position = mapCanvasPosToMapPos(position);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        this.width = this.canvas.getWidth() / map.getWidth();
        this.height = this.canvas.getHeight() / map.getHeight();

        gc.setFill(DOMINANT_COLOR);
        gc.fillOval(position.getX() * width + width / 4, position.getY() * height + height / 4, width / 2, height / 2);
    }

    /**
     * Transform vector of map position to 2D canvas coordinates (starting from 0 without negative numbers)
     *
     * @param position {@link Vector2D} position to transform.
     * @return 2D coordinate.
     */
    private Vector2D mapMapPosToCanvasPos(Vector2D position) {
        position = position.subtract(this.bottom_limit);
        position = new Vector2D(position.getX(), this.top_limit.getY() - this.bottom_limit.getY() - position.getY());

        return position;
    }

    /**
     * Transform vector of 2D canvas coordinates to map position.
     *
     * @param position {@link Vector2D} position to transform.
     * @return 2D coordinate.
     */
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
        if (energy.getEnergy() < 0) {
            System.out.println(this.map.getAnimalsAt(energy.getPosition()).stream().map(Animal::getEnergy).collect(Collectors.toList()));
            System.out.println(energy.getPosition());
        }
        return Color.hsb(ENERGY_COLOR.getHue(), (energy.getEnergy() * ENERGY_COLOR.getSaturation()) / maxEnergy, ENERGY_COLOR.getBrightness());
    }
}
