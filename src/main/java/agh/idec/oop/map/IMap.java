package agh.idec.oop.map;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The interface responsible for interacting with the map of World.
 */
public interface IMap {
    /**
     * Check if the element can move to a given position.
     *
     * @param position The position checked for movement possibility.
     * @return True if the element can move to position.
     */
    boolean canMoveTo(Vector2D position);


    /**
     * Place an element on the map.
     *
     * @param mapElement Element to place.
     * @throws IllegalArgumentException If the map is already occupied at element's position.
     */
    void place(IMapElement mapElement) throws IllegalArgumentException;


    /**
     * Remove an element from the map.
     *
     * @param mapElement Element to remove.
     * @throws IllegalArgumentException If the element does not exist on map.
     */
    void pop(IMapElement mapElement) throws IllegalArgumentException;

    /**
     * Return all elements at a given position.
     *
     * @param position A position on the map where to check existence of elements.
     * @return List of elements at a given position.
     */
    List<IMapElement> getObjectsAt(Vector2D position);


    /**
     * Return set of animals on map.
     *
     * @return HashSet of animals.
     */
    HashSet<Animal> getAnimals();


    /**
     * Return set of plants on map.
     *
     * @return HashSet of plants.
     */
    HashSet<Plant> getPlants();


    /**
     * Return list of map's fields.
     * @return HashMap of key vectors assigned to fields.
     */
    HashMap<Vector2D, Field> getFields();

    /**
     * Return the animal from given list
     * with the highest energy.
     *
     * @param animals List of animals.
     * @return {@link Animal} or null if there are no animals in list.
     */
    Animal getStrongestAnimal(List<Animal> animals);


    /**
     * Extract animals from list of elements.
     *
     * @param elements List of elements.
     * @return List of animals.
     */
    List<Animal> getAnimalsFromElements(List<IMapElement> elements);

    /**
     * Return width of map.
     *
     * @return Integer value.
     */
    int getWidth();

    /**
     * Return height of map.
     *
     * @return Integer value.
     */
    int getHeight();
}
