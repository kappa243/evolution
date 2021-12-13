package agh.idec.oop.element;

import agh.idec.oop.Vector2D;

public abstract class AbstractMapElement implements IMapElement {
    private Vector2D positon;

    protected AbstractMapElement(Vector2D positon) {
        this.positon = positon;
    }

    @Override
    public Vector2D getPosition() {
        return positon;
    }

    @Override
    public void setPosition(Vector2D position) {
        this.positon = position;
    }
}
