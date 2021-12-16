package agh.idec.oop.field;

import agh.idec.oop.element.IMapElement;

import java.util.List;

public interface IMapField {
    /**
     * Return type of field.
     *
     * @return The one type from {@link FieldType}
     */
    FieldType getType();


    /**
     * Add element to field.
     *
     * @return True if element added. False if element exists.
     */
    boolean add(IMapElement element);

    /**
     * Remove element from field.
     *
     * @return True if element remove. False if element does not exists.
     */
    boolean remove(IMapElement element);


    /**
     * Return list of elements on field.
     *
     * @return List of map elements.
     */
    List<IMapElement> getElements();

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
