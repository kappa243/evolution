package agh.idec.oop.world;

import agh.idec.oop.Vector2D;
import agh.idec.oop.element.Animal;
import agh.idec.oop.map.IMap;
import agh.idec.oop.map.NormalMap;
import agh.idec.oop.map.WrapAroundMap;

import java.util.ArrayList;
import java.util.Arrays;

public class World {
    private final IMap map;

    private final float startEnergy;
    private final float moveEnergy;
    private final float plantEnergy;

    private final float jungleRatio;


    private final int magicSpawns;
    private final float magicSpawnChance;

    public World(boolean wrapAround, int width, int height, float startEnergy, float moveEnergy, float plantEnergy, float jungleRatio, int magicSpawns, float magicSpawnChance) {
        // wrap around or not
        if (wrapAround) {
            this.map = new WrapAroundMap(width, height);
        } else {
            this.map = new NormalMap(width, height);
        }

        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;

        this.jungleRatio = jungleRatio;

        this.magicSpawns = magicSpawns;
        this.magicSpawnChance = magicSpawnChance;

        this.map.place(new Animal(this.map, new Vector2D(2, 4), new ArrayList<>(Arrays.asList(1, 2, 3, 4)), 30f));
    }

    public World(boolean wrapAround, int width, int height, float startEnergy, float moveEnergy, float plantEnergy, float jungleRatio) {
        this(wrapAround, width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, 0, 0);
    }


    /**
     * Start new day in world
     */
    public void nextDay() {

    }

    /**
     * Preview of animals and grasses on world map.
     */
    public void drawMap() {
        System.out.print(map.toString());
    }

    @Override
    public String toString() {
        return "World{" +
                "map=" + map +
                ", startEnergy=" + startEnergy +
                ", moveEnergy=" + moveEnergy +
                ", plantEnergy=" + plantEnergy +
                ", jungleRatio=" + jungleRatio +
                ", magicSpawns=" + magicSpawns +
                ", magicSpawnChance=" + magicSpawnChance +
                '}';
    }
}
