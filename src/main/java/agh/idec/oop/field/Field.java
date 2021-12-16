package agh.idec.oop.field;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.element.Plant;
import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class Field implements IMapField {
    private final FieldType type;
    private final Vector2D position;


    private Plant plant = null;

    /**
     * Store animals on field.
     */
    private final PriorityQueue<Animal> animals = new PriorityQueue<>((o1, o2) -> Float.compare(o2.getEnergy(), o1.getEnergy()));

    /**
     * @param type Type of field.
     */
    public Field(FieldType type, Vector2D position) {
        this.type = type;
        this.position = position;
    }

    public Vector2D getPosition() {
        return position;
    }

    public Plant getPlant() {
        return plant;
    }

    @Override
    public FieldType getType() {
        return this.type;
    }

    @Override
    public boolean add(Animal animal) {
        return animals.add(animal);
    }

    @Override
    public boolean remove(Animal animal) {
        return animals.remove(animal);
    }

    @Override
    public PriorityQueue<Animal> getAnimals() {
        return this.animals;
    }

    @Override
    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Plant removePlant() {
        Plant plant = this.plant;
        this.plant = null;
        return plant;
    }

    @Override
    public boolean hasPlant() {
        return this.plant != null;
    }

    @Override
    public boolean hasAnimal() {
        return this.animals.size() > 0;
    }

    @Override
    public boolean isEmpty() {
        return this.animals.isEmpty() && this.plant == null;
    }
}
