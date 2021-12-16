package agh.idec.oop.utils;

import agh.idec.oop.element.Animal;
import agh.idec.oop.World;

import java.util.HashMap;

/**
 * Gather information from World and pass it to graphic interface.
 */
public class WorldInformationLogger {

    private final World world;

    private int animalsCount = 0;
    private int plantsCount = 0;

    private int topGenotype; //?

    private float averageEnergy = 0;

    private float averageAnimalsLifeLength = 0;
    private int deadAnimalsLifeLength = 0;
    private int deadAnimalsCount = 0;
    private final HashMap<Animal, Integer> animalsLifeLengths = new HashMap<>();

    private float averageChildrenCount = 0;
    private final HashMap<Animal, Integer> animalsChildrenCount = new HashMap<>();

    public WorldInformationLogger(World world) {
        this.world = world;

        for (Animal animal : this.world.getMap().getAnimals()) {
            this.startLife(animal);
        }
    }

    public void log() {
        this.setAnimalsCount();
        this.setPlantsCount();
        this.setAverageEnergy();
        this.setAverageLifeLength();
        this.setAverageChildrenCount();
    }

    public void nextDay() {
        this.animalsLifeLengths.forEach((animal, old) -> this.animalsLifeLengths.replace(animal, old + 1));
    }

    private void setAverageEnergy() {
        float sumEnergy = 0;
        for (Animal animal : this.world.getMap().getAnimals()) {
            sumEnergy += animal.getEnergy();
        }
        if (this.animalsCount != 0) {
            this.averageEnergy = sumEnergy / this.animalsCount;
        } else {
            this.averageEnergy = 0;
        }
    }

    private void setAnimalsCount() {
        this.animalsCount = this.world.getMap().getAnimals().size();
    }

    private void setPlantsCount() {
        this.plantsCount = this.world.getMap().getPlants().size();
    }

    private void setAverageLifeLength() {
        if (this.deadAnimalsCount > 0) {
            this.averageAnimalsLifeLength = (float) this.deadAnimalsLifeLength / deadAnimalsCount;
        } else {
            this.averageAnimalsLifeLength = 0;
        }
    }

    private void setAverageChildrenCount() {
        int sum = 0;
        for (int i : this.animalsChildrenCount.values())
            sum += i;

        if(this.animalsChildrenCount.size() != 0){
            this.averageChildrenCount = (float)sum / this.animalsChildrenCount.size();
        }else{
            this.averageChildrenCount = 0;
        }
    }

    public void newChildren(Animal animal) {
        this.animalsChildrenCount.replace(animal, this.animalsChildrenCount.get(animal) + 1);
    }

    public void startLife(Animal animal) {
        this.animalsLifeLengths.put(animal, 0);
        this.animalsChildrenCount.put(animal, 0);
    }

    public void endLife(Animal animal) {
        this.deadAnimalsCount++;
        this.deadAnimalsLifeLength += this.animalsLifeLengths.get(animal);
        this.animalsLifeLengths.remove(animal);
        this.animalsChildrenCount.remove(animal);
    }

    public int getAnimalsCount() {
        return animalsCount;
    }

    public int getPlantsCount() {
        return plantsCount;
    }

    public float getAverageEnergy() {
        return averageEnergy;
    }

    public float getAverageAnimalsLifeLength() {
        return averageAnimalsLifeLength;
    }

    public float getAverageChildrenCount() {
        return averageChildrenCount;
    }
}
