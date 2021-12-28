package agh.idec.oop.element;

import agh.idec.oop.Vector2D;

public interface IMapElement {
    /**
     * Return position of element.
     *
     * @return Vector2d position of element.
     */
    Vector2D getPosition();


    /**
     * Set position of element.
     *
     * @param position Vector2D position of element to set.
     */
    void setPosition(Vector2D position);
}
