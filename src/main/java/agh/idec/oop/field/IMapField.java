package agh.idec.oop.field;

import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;

import java.util.List;
import java.util.TreeSet;

public interface IMapField {
    /**
     * Return type of field.
     *
     * @return The one type from {@link FieldType}
     */
    FieldType getType();


    /**
     * Add animal to field.
     *
     * @return True if animal added. False if animal exists.
     */
    boolean add(Animal animal);

    /**
     * Remove animal from field.
     *
     * @return True if animal remove. False if animal does not exists.
     */
    boolean remove(Animal animal);


    /**
     * Return list of elements on field.
     *
     * @return List of map elements.
     */
    TreeSet<Animal> getAnimals();


    /**
     * Return plant placed at field.
     *
     * @return Plant on field or null if not exists.
     */
    Plant getPlant();

    /**
     * Set plant on field.
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
     * Check if plant is on field.
     *
     * @return True if plant exists on field.
     */
    boolean hasPlant();


    /**
     * Check if animal is on field.
     *
     * @return True if animal exists on field.
     */
    boolean hasAnimal();

    /**
     * Check if the field is empty.
     *
     * @return True if the field has no elements on.
     */
    boolean isEmpty();

}
