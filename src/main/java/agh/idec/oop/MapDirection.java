package agh.idec.oop;

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

    public MapDirection next() {
        return getMapDirection(NE, E, SE, S, SW, W, NW, N);
    }

    public MapDirection previous() {
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
