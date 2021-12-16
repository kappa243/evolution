package agh.idec.oop.utils;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;
import agh.idec.oop.field.FieldType;
import agh.idec.oop.map.IMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * Convert a map into a string.<br>
 * <p>
 * Based on <a href="https://github.com/apohllo/obiektowe-lab/blob/master/lab4/java/MapVisualizer.java">apohllo's MapVisualizer</a>
 */
public class MapVisualizer {
    private static final String EMPTY_CELL = "  ";
    private static final String FRAME_SEGMENT = "-";
    private static final String CELL_SEGMENT = "|";

    private final IMap map;

    public MapVisualizer(IMap map) {
        this.map = map;
    }

    public String draw() {
        StringBuilder builder = new StringBuilder();
        builder.append(drawHeader());
        for (int y = map.getHeight(); y >= -1; y--) {
            builder.append(String.format("%3d: ", y));
            for (int x = 0; x < map.getWidth() + 1; x++) {
                if (y < 0 || y > map.getHeight() - 1) {
                    builder.append(drawFrame(x < map.getWidth()));
                } else {
                    builder.append(CELL_SEGMENT);
                    if (x < map.getWidth()) {
                        builder.append(drawField(x, y));
                        builder.append(drawObject(x, y));
                        builder.append("\u001b[0m");

                    }
                }

            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }


    private String drawObject(int x, int y) {
        String result;
        Vector2D position = new Vector2D(x, y);
        Field field = this.map.getFields().get(position);
        TreeSet<Animal> animals = field.getAnimals();

        if(field.hasPlant()){
            result = "\u001b[35m" + field.getPlant() + "\u001b[0m";
        }else if (field.hasAnimal()){
            result = "\u001b[31m" +  animals.first() + "\u001b[0m"; // just get animal, irrelevant which one
        }else{
            result = EMPTY_CELL;
        }

        return result;
    }

    private String drawFrame(boolean innerSegment) {
        if (innerSegment) {
            return FRAME_SEGMENT + FRAME_SEGMENT + FRAME_SEGMENT;
        } else {
            return FRAME_SEGMENT;
        }
    }

    private String drawHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(" y\\x ");
        for (int x = 0; x < map.getWidth(); x++) {
            builder.append(String.format("%3d", x));
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    private String drawField(int x, int y){
        StringBuilder builder = new StringBuilder();
        if (this.map.getFields().get(new Vector2D(x, y)).getType() == FieldType.JUNGLE) {
            builder.append("\u001b[42m");
        }
        return builder.toString();
    }
}
