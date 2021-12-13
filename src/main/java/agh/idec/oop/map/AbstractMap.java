package agh.idec.oop.map;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;
import agh.idec.oop.field.FieldType;
import agh.idec.oop.field.IMapField;
import agh.idec.oop.observables.IAnimalDeathObserver;
import agh.idec.oop.observables.IPositionChangedObserver;
import agh.idec.oop.utils.MapVisualizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AbstractMap implements IMap, IPositionChangedObserver, IAnimalDeathObserver {

    private final MapVisualizer mapVisualizer = new MapVisualizer(this);

    private final int width;
    private final int height;

    private final HashMap<Vector2D, Field> fields = new HashMap<>();
    private final HashSet<Animal> animals = new HashSet<>();
    private final HashSet<Plant> plants = new HashSet<>();

    public AbstractMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Size of map is too small.");
        }
//        if(steppeRadius < 1){
//            throw new IllegalArgumentException("Size of map is too small.");
//        }
//        if(steppeRadius <= jungleRadius){
//            throw new IllegalArgumentException("Jungle size is higher than steppe size.");
//        }
//        this.steppeSize = steppeRadius;
//        this.jungleSize = jungleRadius;

        this.width = width;
        this.height = height;

        generateFields();
    }

    private void generateFields() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                fields.put(new Vector2D(x, y), new Field(FieldType.STEPPE));
            }
        }
    }

//    private void generateFields(int steppeRadius, int jungleRadius){
//        for(int y = 0; y < steppeRadius*2 + 1; y++){
//            for(int x = 0; x < steppeRadius*2 + 1; x++){
//                fields.put(new Vector2D(x, y), new Field(FieldType.STEPPE));
//            }
//        }
//    }

    public boolean canMoveTo(Vector2D position) {
        return (position.getX() >= 0 && position.getX() < width && position.getY() >= 0 && position.getY() < height);
    }

    @Override
    public void place(IMapElement element) throws IllegalArgumentException {
        Vector2D position = element.getPosition();
        IMapField field = fields.get(position);

        if (field != null) {
            if (!field.add(element)) {
                throw new IllegalArgumentException("Element cannot be stored in map.");
            }

            if (element instanceof Plant plant) {
                plants.add(plant);
            }

            if (element instanceof Animal animal) {
                animals.add(animal);
                animal.addAnimalDeathObserver(this);
                animal.addPositionChangedObserver(this);
            }
        } else {
            throw new IllegalArgumentException("Animal is out of map bound.");
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
            animal.removeAnimalDeathObserver(this);
            animal.removePositionChangedObserver(this);
        }
    }

    @Override
    public List<IMapElement> getObjectsAt(Vector2D position) {
        Field field = fields.get(position);
        return field.getElements();
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
    public void onDeath(Animal animal) {
        Field field = fields.get(animal.getPosition());
        field.remove(animal);

        animals.remove(animal);
        animal.removeAnimalDeathObserver(this);
        animal.removePositionChangedObserver(this);
    }

    @Override
    public String toString() {
        return this.mapVisualizer.draw();
    }
}
