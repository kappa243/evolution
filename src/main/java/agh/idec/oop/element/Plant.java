package agh.idec.oop.element;

import agh.idec.oop.Vector2D;

public class Plant extends AbstractMapElement {
    public Plant(Vector2D positon) {
        super(positon);
    }

    @Override
    public String toString() {
        return "■ ";
    }
}
