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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
            if (!field.add(element)) {
                throw new IllegalArgumentException("Element already exists in the map.");
            }

            if (element instanceof Plant plant) {
                plants.add(plant);
            }

            if (element instanceof Animal animal) {
                animals.add(animal);
                animal.addPositionChangedObserver(this);
            }
        } else {
            throw new IllegalArgumentException("Animal is out of the map bound.");
        }

    }

    @Override
    public void pop(IMapElement element) throws IllegalArgumentException {
        Vector2D position = element.getPosition();
        Field field = fields.get(position);

        if (!field.remove(element)) {
            throw new IllegalArgumentException("Element is not stored in map.");
        }

        if (element instanceof Plant plant) {
            plants.remove(plant);
        }

        if (element instanceof Animal animal) {
            animals.remove(animal);
            animal.removePositionChangedObserver(this);
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
    public List<IMapElement> getObjectsAt(Vector2D position) {
        Field field = fields.get(position);
        return field.getElements();
    }


    @Override
    public Animal getStrongestAnimal(List<Animal> animals) {
        if (!animals.isEmpty()) {
            animals.sort((a1, a2) -> {
                if (a1.getEnergy() < a2.getEnergy()) {
                    return -1;
                } else if (a1.getEnergy() == a2.getEnergy()) {
                    return 0;
                } else {
                    return 1;
                }
            });
            return animals.get(animals.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public List<Animal> getAnimalsFromElements(List<IMapElement> elements) {
        List<Animal> animals = new ArrayList<>();
        for (IMapElement el : elements) {
            if (el instanceof Animal) {
                animals.add((Animal) el);
            }
        }
        return animals;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

//    /**
//     * Wrap position inside map bound, but element can move to unwrapped position (ex. wrap-around map).
//     *
//     * @param position Position to wrap.
//     * @return Wrapped position.
//     */
//    public Vector2D wrapPosition(Vector2D position){
//        return position;
//    }

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
