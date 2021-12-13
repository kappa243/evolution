package agh.idec.oop.observables;

import agh.idec.oop.element.Animal;

public interface IAnimalDeathObserver {
    void onDeath(Animal animal);
}
