package agh.idec.oop.field;

import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Field implements IMapField {
    private final FieldType type;

    /**
     * Store elements on field. Grass cannot be stored along with Animals.
     */
    private final HashSet<IMapElement> elements = new HashSet<>();

    /**
     * @param type Type of field.
     */
    public Field(FieldType type) {
        this.type = type;
    }


    @Override
    public FieldType getType() {
        return this.type;
    }

    @Override
    public boolean add(IMapElement element) {
        return elements.add(element);
    }

    @Override
    public boolean remove(IMapElement element) {
        return elements.remove(element);
    }

    @Override
    public List<IMapElement> getElements() {
        return new ArrayList<>(this.elements);
    }

    @Override
    public boolean hasPlant() {
        return this.elements.stream().anyMatch(o -> o instanceof Plant);
    }

    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }
}
