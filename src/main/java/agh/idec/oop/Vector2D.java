package agh.idec.oop;

import java.util.Objects;

public class Vector2D {
    private final int x;
    private final int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }


    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Vector2D that))
            return false;

        return (this.x == that.x && this.y == that.y);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
