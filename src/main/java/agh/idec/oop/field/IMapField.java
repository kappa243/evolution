package agh.idec.oop.field;

import agh.idec.oop.element.Animal;
import agh.idec.oop.element.Plant;

import java.util.PriorityQueue;

public interface IMapField {
    /**
     * Return type of field.
     *
     * @return The one type from {@link FieldType}
     */
    FieldType getType();


    /**
     * Add animal to field.
     */
    void add(Animal animal);

    /**
     * Remove animal from field.
     */
    void remove(Animal animal);


    /**
     * Return list of elements at field.
     *
     * @return List of map elements.
     */
    PriorityQueue<Animal> getAnimals();


    /**
     * Return plant placed at field.
     *
     * @return Plant on field or null if not exists.
     */
    Plant getPlant();

    /**
     * Set plant at field.
     *
     * @param plant Added plant.
     */
    void setPlant(Plant plant);

    /**
     * Remove plant from field.
     *
     * @return Removed plant.
     */
    Plant removePlant();

    /**
     * Check if plant is at field.
     *
     * @return True if plant exists at field otherwise false.
     */
    boolean hasPlant();


    /**
     * Check if animal is on field.
     *
     * @return True if animal exists on field otherwise false.
     */
    boolean hasAnimal();

    /**
     * Check if the field is empty.
     *
     * @return True if the field has no elements on.
     */
    boolean isEmpty();

}
