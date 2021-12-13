package agh.idec.oop.element;

import agh.idec.oop.MapDirection;
import agh.idec.oop.Vector2D;
import agh.idec.oop.map.IMap;
import agh.idec.oop.map.WrapAroundMap;
import agh.idec.oop.observables.IAnimalDeathObserver;
import agh.idec.oop.observables.IPositionChangedObserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static agh.idec.oop.MapDirection.*;

public class Animal extends AbstractMapElement {

    //    private static float MAX_ENERGY = 10;
    private final float startEnergy;

    private static final int GENE_SIZE = 4;

    private final ArrayList<Integer> gene;
    private float energy;
    private MapDirection direction = mapIntToDirection(new Random().nextInt(8));

    private final IMap map;
    private final HashSet<IPositionChangedObserver> positionChangedObserversobservers = new HashSet<>();
    private final HashSet<IAnimalDeathObserver> animalDeathObservers = new HashSet<>();

    public Animal(IMap map, Vector2D position, ArrayList<Integer> gene, float energy) {
        super(position);

        // validate gene
        if (gene.size() != GENE_SIZE) {
            throw new IllegalArgumentException("Gene has no proper amount of genotypes.");
        }
        for (Integer genotype : gene) {
            if (genotype < 0 || genotype > 7) {
                throw new IllegalArgumentException("Values of gene are not valid.");
            }
        }
        this.gene = gene;

        this.energy = energy;
        this.startEnergy = energy;
        this.map = map;
    }

    /**
     * Generate direction based on a gene of the Animal.
     *
     * @return Map direction.
     */
    private MapDirection decide() {
        Random rand = new Random();
        int decision = gene.get(rand.nextInt(GENE_SIZE));

        return mapIntToDirection(decision);
    }


    /**
     * Move or rotate animal
     */
    public void move() { //TODO connect with world simulate
        MapDirection direction = decide();

        switch (direction) {
            case N, S -> moveToVector(this.getPosition().add(direction.toUnitVector()));
            default -> this.direction = direction;
        }

    }


    /**
     * If can, move animal to given posiiton.
     *
     * @param position Position of animal to move.
     */
    private void moveToVector(Vector2D position) {
        if (this.map.canMoveTo(position)) {
            if (this.map instanceof WrapAroundMap wrapAroundMap) {
                position = wrapAroundMap.wrapPosition(position);
            }
            Vector2D oldPosition = this.getPosition();
            this.setPosition(position);
            positionChanged(oldPosition);
        }
    }

    /**
     * Feed animal with given value of energy.
     *
     * @param energy Float value of energy.
     */
    public void addEnergy(float energy) {
        this.energy += energy;
//        if(this.energy > MAX_ENERGY){
//            this.energy = MAX_ENERGY;
//        }
    }


    public void removeEnergy(float energy) {
        this.energy -= energy;
        if (this.energy <= 0) {
            onDeath();
        }
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    /**
     * Breed animal with a given partner.
     *
     * @param partner Animal to breed with.
     * @return If succeed return newborn animal, else null.
     */
    public Animal breed(Animal partner) {
        Animal strong = this;
        Animal weak = partner;

        // energy provided
        if (this.energy >= 0.5 * startEnergy && partner.energy >= 0.5 * startEnergy) {
            // select dominant
            if (!(this.energy > partner.energy)) {
                strong = partner;
                weak = this;
            }

            // create gene
            ArrayList<Integer> gene = new ArrayList<>();

            float sum = strong.energy + weak.energy;
            int split = Math.round(GENE_SIZE * (weak.energy / sum));

            Random rand = new Random(); //randomize split

            if (rand.nextBoolean()) {
                gene.addAll(weak.gene.subList(0, split));
                gene.addAll(strong.gene.subList(split, GENE_SIZE));
            } else {
                gene.addAll(strong.gene.subList(0, split + 1));
                gene.addAll(strong.gene.subList(split + 1, GENE_SIZE));
            }

            // remove energy (will not die)
            strong.energy -= 0.5 * startEnergy;
            weak.energy -= 0.5 * startEnergy;

            return new Animal(this.map, this.getPosition(), gene, startEnergy);
        } else {
            return null;
        }
    }


    /**
     * Return direction associated with a given number.
     *
     * @param num Number defining direction.
     * @return Map direction.
     */
    private MapDirection mapIntToDirection(int num) {
        return switch (num) {
            case 0 -> N;
            case 1 -> NE;
            case 2 -> E;
            case 3 -> SE;
            case 4 -> S;
            case 5 -> SW;
            case 6 -> W;
            case 7 -> NW;
            default -> throw new IllegalArgumentException("Number is not valid argument to map as direction.");
        };
    }


    public void addPositionChangedObserver(IPositionChangedObserver observer) {
        this.positionChangedObserversobservers.add(observer);
    }

    public void removePositionChangedObserver(IPositionChangedObserver observer) {
        this.positionChangedObserversobservers.remove(observer);
    }

    private void positionChanged(Vector2D oldPosition) {
        for (var observer : positionChangedObserversobservers) {
            observer.positionChanged(this, oldPosition);
        }
    }

    public void addAnimalDeathObserver(IAnimalDeathObserver observer) {
        this.animalDeathObservers.add(observer);
    }

    public void removeAnimalDeathObserver(IAnimalDeathObserver observer) {
        this.animalDeathObservers.remove(observer);
    }

    private void onDeath() {
        for (var observer : animalDeathObservers) {
            observer.onDeath(this);
        }
    }


    @Override
    public String toString() {
        return switch (this.direction) {
            case N -> "N";
            case NE -> "NE";
            case E -> "E";
            case SE -> "SE";
            case S -> "S";
            case SW -> "SW";
            case W -> "W";
            case NW -> "NW";
        };
    }
}
