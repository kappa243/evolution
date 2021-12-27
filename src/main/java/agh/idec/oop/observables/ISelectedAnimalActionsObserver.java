package agh.idec.oop.observables;

import agh.idec.oop.element.Animal;

public interface ISelectedAnimalActionsObserver {
    void selectedAnimalDeath(Animal animal);

    void selectedAnimalBreed(Animal animal, Animal newborn);
}
