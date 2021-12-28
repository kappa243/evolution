package agh.idec.oop.map;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

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
     * Return all animals at a given position.
     *
     * @param position A position on the map where to check existence of animals.
     * @return Set of animals.
     */
    PriorityQueue<Animal> getAnimalsAt(Vector2D position);


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
     *
     * @return HashMap of key vectors assigned to field.
     */
    HashMap<Vector2D, Field> getFields();


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
