package agh.idec.oop.map;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;
import agh.idec.oop.field.FieldType;
import agh.idec.oop.field.IMapField;
import agh.idec.oop.observables.IPositionChangedObserver;
import agh.idec.oop.utils.MapVisualizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class AbstractMap implements IMap, IPositionChangedObserver {

    private final MapVisualizer mapVisualizer = new MapVisualizer(this);

    private final int width;
    private final int height;

    private final int jungleWidth;
    private final int jungleHeight;

    private final HashMap<Vector2D, Field> fields = new HashMap<>();
    private final HashSet<Animal> animals = new HashSet<>();
    private final HashSet<Plant> plants = new HashSet<>();

    public AbstractMap(int width, int height, int jungleWidth, int jungleHeight) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Size of map is too small.");
        }
        if (jungleHeight < 0 || jungleWidth < 0) {
            throw new IllegalArgumentException("Size of jungle is too small.");
        }
        if (jungleHeight > height || jungleWidth > width) {
            throw new IllegalArgumentException("Size of jungle is too big.");
        }

        this.width = width;
        this.height = height;

        this.jungleWidth = jungleWidth;
        this.jungleHeight = jungleHeight;

        generateFields();
    }

    private void generateFields() {
        int y = (height / 2) - (jungleHeight / 2);
        int x = (width / 2) - (jungleWidth / 2);

        for (int j = y; j < y + jungleHeight; j++) {
            for (int i = x; i < x + jungleWidth; i++) {
                Vector2D position = new Vector2D(i, j);
                this.fields.put(position, new Field(FieldType.JUNGLE, position));
            }
        }

        for (y = 0; y < this.height; y++) {
            for (x = 0; x < this.width; x++) {
                Vector2D position = new Vector2D(x, y);
                if (!this.fields.containsKey(position)) {
                    this.fields.put(position, new Field(FieldType.STEPPE, position));
                }
            }
        }
    }


    public boolean canMoveTo(Vector2D position) {
        return (position.getX() >= 0 && position.getX() < width && position.getY() >= 0 && position.getY() < height);
    }

    @Override
    public void place(IMapElement element) throws IllegalArgumentException {
        Vector2D position = element.getPosition();
        IMapField field = fields.get(position);

        if (field != null) {
            if (element instanceof Plant plant) {
                if (!field.hasPlant() && !field.hasAnimal()) {
                    field.setPlant(plant);

                    plants.add(plant);
                } else {
                    throw new IllegalArgumentException("Another plant or animal is placed in that position.");
                }

            } else if (element instanceof Animal animal) {
                Plant plant = field.removePlant();
                if (plant != null) {
                    plants.remove(plant);
                }

                animals.add(animal);
                animal.addPositionChangedObserver(this);
            }


        } else {
            throw new IllegalArgumentException("Element is out of bound.");
        }

    }

    @Override
    public void pop(IMapElement element) throws IllegalArgumentException {
        Vector2D position = element.getPosition();
        Field field = fields.get(position);

        if (field != null) {
            if (element instanceof Plant plant) {
                field.removePlant();
                plants.remove(plant);

            } else if (element instanceof Animal animal) {
                field.remove(animal);
                animals.remove(animal);
                animal.removePositionChangedObserver(this);
            }


        } else {
            throw new IllegalArgumentException("Element is out of bound.");
        }
    }

    @Override
    public HashMap<Vector2D, Field> getFields() {
        return this.fields;
    }

    public HashSet<Animal> getAnimals() {
        return animals;
    }

    public HashSet<Plant> getPlants() {
        return plants;
    }


    @Override
    public TreeSet<Animal> getAnimalsAt(Vector2D position) {
        Field field = fields.get(position);
        return field.getAnimals();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void positionChanged(Animal animal, Vector2D oldPosition) {
        Vector2D newPosition = animal.getPosition();

        Field oldField = fields.get(oldPosition);
        Field newField = fields.get(newPosition);

        oldField.remove(animal);
        newField.add(animal);
    }


    @Override
    public String toString() {
        return this.mapVisualizer.draw();
    }
}
