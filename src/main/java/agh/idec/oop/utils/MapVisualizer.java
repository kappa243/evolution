package agh.idec.oop.utils;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.map.IMap;

import java.util.List;
import java.util.Optional;


/**
 * Convert a map into a string.<br>
 * <p>
 * Based on <a href="https://github.com/apohllo/obiektowe-lab/blob/master/lab4/java/MapVisualizer.java">apohllo's MapVisualizer</a>
 */
public class MapVisualizer {
    private static final String EMPTY_CELL = " ";
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
            for (int x = 0; x < map.getWidth(); x++) {
                if (y < 0 || y > map.getHeight() - 1) {
                    builder.append(drawFrame(x < map.getWidth()));
                } else {
                    builder.append(CELL_SEGMENT);
                    if (x < map.getWidth()) {
                        builder.append(drawObject(x, y));
                    }
                }
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }


    private String drawObject(int x, int y) {
        String result;
        List<IMapElement> elements = this.map.getObjectsAt(new Vector2D(x, y));
        Optional<IMapElement> element = elements.stream().findAny();
        if (element.isPresent()) {
            result = element.get().toString();
        } else {
            result = EMPTY_CELL;
        }

        return result;
    }

    private String drawFrame(boolean innerSegment) {
        if (innerSegment) {
            return FRAME_SEGMENT + FRAME_SEGMENT;
        } else {
            return FRAME_SEGMENT;
        }
    }

    private String drawHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(" y\\x ");
        for (int x = 0; x < map.getWidth(); x++) {
            builder.append(String.format("%2d", x));
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }
}
