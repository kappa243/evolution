package agh.idec.oop.element;

import agh.idec.oop.Vector2D;

public interface IMapElement {
    /**
     * Return position of element.
     *
     * @return Current position of element.
     */
    Vector2D getPosition();


    /**
     * Set position of element.
     *
     * @param position Position of element to set.
     */
    void setPosition(Vector2D position);
}
