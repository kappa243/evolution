package agh.idec.oop;

import agh.idec.oop.element.Animal;
import agh.idec.oop.element.IMapElement;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;
import agh.idec.oop.field.FieldType;
import agh.idec.oop.map.IMap;
import agh.idec.oop.map.NormalMap;
import agh.idec.oop.map.WrapAroundMap;
import agh.idec.oop.utils.WorldInformationLogger;

import java.util.*;
import java.util.stream.Collectors;

public class World {
    private final IMap map;
    private final WorldInformationLogger logger;

    private final float startEnergy;
    private final float moveEnergy;
    private final float plantEnergy;

    private final int plantsSteppe;
    private final int plantsJungle;

    private final int magicSpawns;
    private final float magicSpawnChance;

    private int day = 0;

    public World(boolean wrapAround, int width, int height, int jungleWidth, int jungleHeight, int startAnimals,
                 float startEnergy, float moveEnergy, float plantEnergy, int plantsSteppe, int plantsJungle,
                 int magicSpawns,
                 float magicSpawnChance) {
        // wrap around or not
        if (wrapAround) {
            this.map = new WrapAroundMap(width, height, jungleWidth, jungleHeight);
        } else {
            this.map = new NormalMap(width, height, jungleWidth, jungleHeight);
        }

        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;

        this.plantsSteppe = plantsSteppe;
        this.plantsJungle = plantsJungle;

        this.magicSpawns = magicSpawns;
        this.magicSpawnChance = magicSpawnChance;


        // place starting animals
        Vector2D center = new Vector2D(Math.round(width / 2f), Math.round(height / 2f));
        for (int i = 0; i < startAnimals; i++) {
            ArrayList<Integer> gene = new ArrayList<>();
            Random rand = new Random();
            for (int g = 0; g < Animal.GENOTYPE_SIZE; g++) {
                gene.add(rand.nextInt(Animal.GENE_TYPE));
            }

            Animal animal = new Animal(this.map, center, gene, startEnergy);
            this.map.place(animal);

        }

        growPlants();

        this.logger = new WorldInformationLogger(this);

//        System.out.println(this.map);

    }

    public World(boolean wrapAround, int width, int height, int jungleWidth, int jungleHeight, int startAnimals, float startEnergy, float moveEnergy,
                 float plantEnergy, int plantsSteppe, int plantsJungle) {
        this(wrapAround, width, height, jungleWidth, jungleHeight, startAnimals, startEnergy, moveEnergy, plantEnergy, plantsSteppe, plantsJungle, 0, 0);
    }

    public int getDay(){
        return this.day;
    }


    /**
     * Start new day in world
     */
    public void simulateDay() {
//        System.out.println("Day: " + this.day);

        removeDeadAnimals();
        this.logger.log();

//        System.out.println("Animals: " + this.logger.getAnimalsCount());
//        System.out.println("Plants: " + this.logger.getPlantsCount());
//        System.out.println("Average energy: " + this.logger.getAverageEnergy());
//        System.out.println("Average life length: " + this.logger.getAverageAnimalsLifeLength());
//        System.out.println("Average children count: " + this.logger.getAverageChildrenCount());

//            System.out.println(this.map);
//        System.out.println();

        makeDecisions();
        feedAnimals();
        breedAnimals();
        growPlants();
        drainEnergies();


        day++;
        this.logger.nextDay();
    }

    /**
     * Remove energy from all animals on the map.
     * Value is based on move energy parameter.
     */
    private void drainEnergies() {
        for (Animal animal : this.map.getAnimals()) {
            animal.removeEnergy(this.moveEnergy);
        }
    }

    /**
     * Remove all animals with 0 or less energy.
     *
     * @return Number of dead animals remove from map.
     */
    private int removeDeadAnimals() {
        ArrayList<Animal> animals = new ArrayList<>();
        for (Animal animal : this.map.getAnimals()) {
            if (animal.getEnergy() <= 0) {
                animals.add(animal);
            }
        }

        for (Animal animal : animals) {
            this.logger.endLife(animal);
            this.map.pop(animal);
        }

        return animals.size();
    }


    /**
     * Let every animal decide what to do based
     * on its gene.
     */
    private void makeDecisions() {
        for (Animal animal : this.map.getAnimals()) {
            animal.decide();
        }
    }

    /**
     * Feed the strongest animal on plant field.
     *
     * @return Number of fed animals.
     */
    private int feedAnimals() {
        ArrayList<Plant> plants = new ArrayList<>();
        for (Plant plant : this.map.getPlants()) {
            Vector2D position = plant.getPosition();

            List<IMapElement> elements = this.map.getObjectsAt(position);
            List<Animal> animals = this.map.getAnimalsFromElements(elements);
            Animal animal = this.map.getStrongestAnimal(animals);

            if (animal != null) {
//                this.map.pop(plant);
                plants.add(plant);
                animal.addEnergy(plantEnergy);
            }
        }
        for (Plant plant : plants) {
            this.map.pop(plant);
        }

        return plants.size();
    }

    /**
     * Breed animals on map.
     *
     * @return Number of new animals.
     */
    private int breedAnimals() {
        // set of positions where animals exist (animals fed).
        HashSet<Vector2D> positions = new HashSet<>();

        for (Animal animal : this.map.getAnimals()) {
            if (animal.getEnergy() > 0.5 * this.startEnergy) {
                positions.add(animal.getPosition());
            }
        }

        int borned = 0;

        for (Vector2D position : positions) {
            List<IMapElement> elements = this.map.getObjectsAt(position);
            List<Animal> animals = this.map.getAnimalsFromElements(elements);

            Animal strong1 = this.map.getStrongestAnimal(animals);
            if (strong1 != null) {
                animals.remove(strong1);
                Animal strong2 = this.map.getStrongestAnimal(animals);
                if (strong2 != null) {
                    Animal newborn = strong1.breed(strong2);
                    if (newborn != null) {
                        this.map.place(newborn);

                        this.logger.startLife(newborn);

                        this.logger.newChildren(strong1);
                        this.logger.newChildren(strong2);

                        borned++;
                    }
                }
            }
        }

        return borned;
    }


    /**
     * Grow plants on map based on field type.
     */
    private void growPlants() {
        HashMap<Vector2D, Field> fields = this.map.getFields();
        ArrayList<Field> filteredFields = fields.values().stream().filter(field -> !field.hasPlant() && !field.hasAnimal()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Field> steppeFields = filteredFields.stream().filter(field -> field.getType() == FieldType.STEPPE).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Field> jungleFields = filteredFields.stream().filter(field -> field.getType() == FieldType.JUNGLE).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(steppeFields); // randomize grass generation
        Collections.shuffle(jungleFields); // randomize grass generation

        Iterator<Vector2D> iter = steppeFields.stream().map(Field::getPosition).iterator();
        int i = 0;
        while (iter.hasNext() && i < this.plantsSteppe) {
            Vector2D position = iter.next();
            this.map.place(new Plant(position));
            i++;
        }

        iter = jungleFields.stream().map(Field::getPosition).iterator();
        i = 0;
        while (iter.hasNext() && i < this.plantsJungle) {
            Vector2D position = iter.next();
            this.map.place(new Plant(position));
            i++;
        }

    }

    public IMap getMap() {
        return map;
    }

    /**
     * Preview of animals and grasses on world map.
     */
    public void drawMap() {
        System.out.print(map.toString());
    }
}
