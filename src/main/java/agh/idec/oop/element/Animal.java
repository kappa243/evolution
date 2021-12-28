package agh.idec.oop.element;

import agh.idec.oop.Vector2D;
import agh.idec.oop.map.IMap;
import agh.idec.oop.map.WrapAroundMap;
import agh.idec.oop.observables.IPositionChangedObserver;
import agh.idec.oop.observables.ISelectedAnimalActionsObserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static agh.idec.oop.element.MapDirection.N;

public class Animal extends AbstractMapElement {

    private final float startEnergy;

    public static final int GENOTYPE_SIZE = 32;
    public static final int GENE_TYPE = 8;

    private final ArrayList<Integer> genotype;
    private float energy;
    private MapDirection direction = N.next(new Random().nextInt(8));

    private final IMap map;
    private final HashSet<IPositionChangedObserver> positionChangedObserversobservers = new HashSet<>();
    private final HashSet<ISelectedAnimalActionsObserver> selectedAnimalActionsObservers = new HashSet<>();

    public Animal(IMap map, Vector2D position, ArrayList<Integer> genotype, float energy) {
        super(position);

        // validate genotype
        if (genotype.size() != GENOTYPE_SIZE) {
            throw new IllegalArgumentException("Gene has no proper amount of genomes.");
        }
        for (Integer gene : genotype) {
            if (gene < 0 || gene > 7) {
                throw new IllegalArgumentException("Values of genotype are not valid.");
            }
        }
        this.genotype = genotype;

        this.energy = energy;
        this.startEnergy = energy;
        this.map = map;
    }

    /**
     * Generate direction based on a genotype of the Animal
     * and move/rotate using that direction.
     */
    public void decide() {
        Random rand = new Random();
        int decision = genotype.get(rand.nextInt(GENOTYPE_SIZE));
        MoveDirection direction = this.mapIntToDirection(decision);
//        System.out.println(this.getPosition() + " " + this.direction + " " + direction);

        this.move(direction);
    }

    /**
     * Move or rotate animal with given direction.
     *
     * @param direction Direction where to move or rotate animal.
     */
    private void move(MoveDirection direction) {
        switch (direction) {
            case FORWARD -> moveToVector(this.getPosition().add(this.direction.toUnitVector()));
            case BACKWARD -> moveToVector(this.getPosition().subtract(this.direction.toUnitVector()));
            case ROTATE45 -> this.direction = this.direction.next(1);
            case ROTATE90 -> this.direction = this.direction.next(2);
            case ROTATE135 -> this.direction = this.direction.next(3);
            case ROTATE225 -> this.direction = this.direction.previous(1);
            case ROTATE270 -> this.direction = this.direction.previous(2);
            case ROTATE315 -> this.direction = this.direction.previous(3);
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
    }

    /**
     * Drain energy from animal with given value.
     *
     * @param energy Float value of energy.
     */
    public void removeEnergy(float energy) {
        this.energy -= energy;
    }

    /**
     * Return animal's energy.
     *
     * @return Float value of energy.
     */
    public float getEnergy() {
        return energy;
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
        if (this.energy > 0.5 * startEnergy && partner.energy > 0.5 * startEnergy) {
            // select dominant
            if (!(this.energy > partner.energy)) {
                strong = partner;
                weak = this;
            }

            // create gene
            ArrayList<Integer> gene = new ArrayList<>();

            float sum = strong.energy + weak.energy;
            int split = Math.round(GENOTYPE_SIZE * (weak.energy / sum));

            Random rand = new Random(); //randomize split

            if (rand.nextBoolean()) {
                gene.addAll(weak.genotype.subList(0, split));
                gene.addAll(strong.genotype.subList(split, GENOTYPE_SIZE));
            } else {
                gene.addAll(strong.genotype.subList(0, split + 1));
                gene.addAll(strong.genotype.subList(split + 1, GENOTYPE_SIZE));
            }

            // remove energy (will not die)
            strong.energy -= 0.5 * startEnergy;
            weak.energy -= 0.5 * startEnergy;

            Animal newborn = new Animal(this.map, this.getPosition(), gene, startEnergy);
            this.selectedAnimalBreed(newborn);
            partner.selectedAnimalBreed(newborn);
            return newborn;
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
    private MoveDirection mapIntToDirection(int num) {
        return switch (num) {
            case 0 -> MoveDirection.FORWARD;
            case 1 -> MoveDirection.ROTATE45;
            case 2 -> MoveDirection.ROTATE90;
            case 3 -> MoveDirection.ROTATE135;
            case 4 -> MoveDirection.BACKWARD;
            case 5 -> MoveDirection.ROTATE225;
            case 6 -> MoveDirection.ROTATE270;
            case 7 -> MoveDirection.ROTATE315;
            default -> throw new IllegalArgumentException("Number is not valid argument to map as direction.");
        };
    }

    /**
     * Return genotype of animal
     *
     * @return ArrayList of a genotype consisting of genes.
     */
    public ArrayList<Integer> getGenotype() {
        return genotype;
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

    public void addSelectedAnimalActionsObserver(ISelectedAnimalActionsObserver observer) {
        this.selectedAnimalActionsObservers.add(observer);
    }

    public void removeSelectedAnimalActionsObserver(ISelectedAnimalActionsObserver observer) {
        this.selectedAnimalActionsObservers.remove(observer);
    }

    public void selectedAnimalDeath() {
        for (var observer : selectedAnimalActionsObservers) {
            observer.selectedAnimalDeath(this);
        }
    }

    public void selectedAnimalBreed(Animal newborn) {
        for (var observer : selectedAnimalActionsObservers) {
            observer.selectedAnimalBreed(this, newborn);
        }
    }

    @Override
    public String toString() {
//        return "■ ";
        return switch (this.direction) {
            case N -> "N ";
            case NE -> "NE";
            case E -> "E ";
            case SE -> "SE";
            case S -> "S ";
            case SW -> "SW";
            case W -> "W ";
            case NW -> "NW";
        };
    }
}
