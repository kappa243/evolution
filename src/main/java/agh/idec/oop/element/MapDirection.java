package agh.idec.oop.element;

import agh.idec.oop.Vector2D;

public enum MapDirection {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    @Override
    public String toString() {
        return switch (this) {
            case N -> "Północ";
            case NE -> "Północny wschód";
            case E -> "Wschód";
            case SE -> "Południowy wschód";
            case S -> "Południe";
            case SW -> "Południowy zachód";
            case W -> "Zachód";
            case NW -> "Północny zachód";
        };
    }

    /**
     * Return next direction clockwise.
     *
     * @param num How many times next element, ex. for value 2 return second direction clockwise from current.
     * @return Direction.
     */
    public MapDirection next(int num) {
        MapDirection direction = this;
        for (int i = 0; i < num; i++) {
            direction = direction.getMapDirection(NE, E, SE, S, SW, W, NW, N);
        }
        return direction;
    }

    /**
     * Return next direction counterclockwise.
     *
     * @param num How many times previous element, ex. for value 2 return second direction counterclockwise from current.
     * @return Direction.
     */
    public MapDirection previous(int num) {
        MapDirection direction = this;
        for (int i = 0; i < num; i++) {
            direction = direction.getMapDirection(NW, N, NE, E, SE, S, SW, W);
        }
        return getMapDirection(NW, N, NE, E, SE, S, SW, W);
    }

    private MapDirection getMapDirection(MapDirection nw, MapDirection n, MapDirection ne, MapDirection e, MapDirection se, MapDirection s, MapDirection sw, MapDirection w) {
        return switch (this) {
            case N -> nw;
            case NE -> n;
            case E -> ne;
            case SE -> e;
            case S -> se;
            case SW -> s;
            case W -> sw;
            case NW -> w;
        };
    }

    /**
     * Return unit Vector2D of given direction.
     */
    public Vector2D toUnitVector() {
        return switch (this) {
            case N -> new Vector2D(0, 1);
            case NE -> new Vector2D(1, 1);
            case E -> new Vector2D(1, 0);
            case SE -> new Vector2D(1, -1);
            case S -> new Vector2D(0, -1);
            case SW -> new Vector2D(-1, -1);
            case W -> new Vector2D(-1, 0);
            case NW -> new Vector2D(-1, 1);
        };
    }
}
