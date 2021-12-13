package agh.idec.oop.observables;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;

public interface IPositionChangedObserver {
    void positionChanged(Animal animal, Vector2D oldPosition);
}
