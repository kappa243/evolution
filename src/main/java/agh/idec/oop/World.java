package agh.idec.oop;

import agh.idec.oop.element.Animal;
import agh.idec.oop.element.Plant;
import agh.idec.oop.field.Field;
import agh.idec.oop.field.FieldType;
import agh.idec.oop.map.IMap;
import agh.idec.oop.map.NormalMap;
import agh.idec.oop.map.WrapAroundMap;
import agh.idec.oop.observables.IMagicDayObserver;
import agh.idec.oop.observables.INextSimulatedDayObserver;
import agh.idec.oop.utils.WorldInformationLogger;

import java.util.*;
import java.util.stream.Collectors;

public class World {
    private final IMap map;
    private final WorldInformationLogger logger;

    private final HashSet<INextSimulatedDayObserver> nextSimulatedDayObservers = new HashSet<>();
    private final HashSet<IMagicDayObserver> magicDayObservers = new HashSet<>();


    private final float startEnergy;
    private final float moveEnergy;
    private final float plantEnergy;

    private final int plantsSteppe;
    private final int plantsJungle;

    private final boolean isMagic;
    private int magicDays = 3;

    private final long delay;

    private int day = 0;

    private boolean isRunning = false;

    public World(long delay, boolean wrapAround, int width, int height, float jungleRatio, int startAnimals,
                 float startEnergy, float moveEnergy, float plantEnergy, int plantsSteppe, int plantsJungle,
                 boolean isMagic) {
        // calculate jungle area
        int area = width * height;
        float sideRatio = (float) width / height;

        int jungleHeight = (int) Math.floor(Math.sqrt(area * jungleRatio / sideRatio));
        int jungleWidth = (int) Math.floor(jungleHeight * sideRatio);


        // wrap around or not
        if (wrapAround) {
            this.map = new WrapAroundMap(width, height, jungleWidth, jungleHeight);
        } else {
            this.map = new NormalMap(width, height, jungleWidth, jungleHeight);
        }

        this.delay = delay;

        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;

        this.plantsSteppe = plantsSteppe;
        this.plantsJungle = plantsJungle;

        this.isMagic = isMagic;


        // place starting animals
        Vector2D center = new Vector2D((int) Math.floor(width / 2f), (int) Math.floor(height / 2f));
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

    }

    public World(long delay, boolean wrapAround, int width, int height, float jungleRatio,
                 int startAnimals, float startEnergy, float moveEnergy,
                 float plantEnergy, int plantsSteppe, int plantsJungle) {
        this(delay, wrapAround, width, height, jungleRatio, startAnimals, startEnergy, moveEnergy,
                plantEnergy, plantsSteppe, plantsJungle, false);
    }

    /**
     * Return current day of simulation.
     *
     * @return Day of simulation as int.
     */
    public int getDay() {
        return this.day;
    }


    /**
     * Resume simulation at World.
     */
    public void run() {
        if (!this.isRunning) {
            this.isRunning = true;
            new Thread(() -> {
                while (this.isRunning) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    simulateDay();
                }
            }).start();
        }
    }

    /**
     * Pause simulation at World.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Check if simulation is running at World.
     *
     * @return True if simulation is running otherwise false.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Start new day in world
     */
    public void simulateDay() {


        this.logger.log();
//        // debug
//        System.out.println("Day: " + this.day);
//        System.out.println("Animals: " + this.logger.getAnimalsCount());
//        System.out.println("Plants: " + this.logger.getPlantsCount());
//        System.out.println("Average energy: " + this.logger.getAverageEnergy());
//        System.out.println("Average life length: " + this.logger.getAverageAnimalsLifeLength());
//        System.out.println("Average children count: " + this.logger.getAverageChildrenCount());
//
//        System.out.println();

        if (isMagic && magicDays > 0 && this.map.getAnimals().size() == 5) {
            List<Field> fieldsSorted = new ArrayList<>(this.map.getFields().values().stream().filter(field -> !field.isEmpty()).toList());

            Collections.shuffle(fieldsSorted);
            Iterator<Field> iter = fieldsSorted.iterator();

            ArrayList<Animal> animals = new ArrayList<>();
            for (Animal oldAnimal : this.map.getAnimals()) {
                if (iter.hasNext()) {
                    Vector2D position = iter.next().getPosition();
                    Animal animal = new Animal(this.map, position, new ArrayList<>(oldAnimal.getGenotype()), startEnergy);
                    animals.add(animal);
                } else {
                    break;
                }
            }

            animals.forEach(animal -> {
                this.map.place(animal);
                this.logger.startLife(animal);
            });

//            System.out.println("MAGIC DAY");
            this.magicDay();
            magicDays--;
        }

        makeDecisions();
        feedAnimals();
        breedAnimals();
        growPlants();
        drainEnergies();

        removeDeadAnimals();
        nextSimulatedDay();

        day++;
        this.logger.nextDay();

//        this.drawMap();
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
     */
    private void removeDeadAnimals() {
        ArrayList<Animal> animals = new ArrayList<>();
        for (Animal animal : this.map.getAnimals()) {
            if (animal.getEnergy() <= 0) {
                animals.add(animal);
            }
        }

        for (Animal animal : animals) {
            this.logger.endLife(animal);
            animal.selectedAnimalDeath();
            this.map.pop(animal);
        }
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
     */
    private void feedAnimals() {
        ArrayList<Plant> plants = new ArrayList<>();

        for (Plant plant : this.map.getPlants()) {
            Vector2D position = plant.getPosition();
            PriorityQueue<Animal> animals = this.map.getAnimalsAt(position);
            List<Animal> strongestAnimals = new ArrayList<>();
            Iterator<Animal> iter = animals.iterator();

            if (iter.hasNext()) { // ad the strongest animal at that position
                Animal animal = iter.next();
                float energy = animal.getEnergy();
                strongestAnimals.add(animal);

                while (iter.hasNext()) {
                    animal = iter.next();
                    if (animal.getEnergy() == energy) { //next animal in order same energy as the strongest
                        strongestAnimals.add(animal);
                    } else {
                        break; //next animal smaller energy than the strongest
                    }
                }
            }

            if (strongestAnimals.size() > 0) { // at least one animal at position
                for (Animal animal : strongestAnimals) {
                    animal.addEnergy(plantEnergy / strongestAnimals.size());
                }

                plants.add(plant);
            }

        }
        for (Plant plant : plants) {
            this.map.pop(plant);
        }
    }

    /**
     * Breed animals on map.
     */
    private void breedAnimals() {
        // set of positions where animals exist (animals fed).
        HashSet<Vector2D> positions = new HashSet<>();

        for (Animal animal : this.map.getAnimals()) {
            if (animal.getEnergy() > 0.5 * this.startEnergy) {
                positions.add(animal.getPosition());
            }
        }

        for (Vector2D position : positions) {
            PriorityQueue<Animal> animals = this.map.getAnimalsAt(position);

            Iterator<Animal> iter = animals.iterator();

            if (iter.hasNext()) {
                Animal strong1 = iter.next();
                if (iter.hasNext()) {
                    Animal strong2 = iter.next();

                    // now we provided 2 animals with the highest energy at position
                    // (we only know the first one provide required energy for bread)
                    Animal newborn = strong1.breed(strong2); //breed function return newborn if both provide required energy
                    if (newborn != null) {
                        this.map.place(newborn);

                        this.logger.startLife(newborn);
                        this.logger.newChild(strong1);
                        this.logger.newChild(strong2);
                    }
                }
            }
        }

    }


    /**
     * Grow plants on map based on field type.
     */
    private void growPlants() {
        HashMap<Vector2D, Field> fields = this.map.getFields();
        ArrayList<Field> steppeFields = fields.values().stream().filter(field -> field.getType() == FieldType.STEPPE && !field.hasAnimal() && !field.hasPlant()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Field> jungleFields = fields.values().stream().filter(field -> field.getType() == FieldType.JUNGLE && !field.hasAnimal() && !field.hasPlant()).collect(Collectors.toCollection(ArrayList::new));


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

    /**
     * Return map of World.
     */
    public IMap getMap() {
        return map;
    }

    /**
     * Return number of magic day that occurred.
     */
    public int getMagicDay() {
        return 3 - magicDays;
    }

    /**
     * Return information logger of world.
     */
    public WorldInformationLogger getLogger() {
        return logger;
    }


    public void addNextSimulatedDayObserver(INextSimulatedDayObserver observer) {
        this.nextSimulatedDayObservers.add(observer);
    }

    public void removeNextSimulatedDayObserver(INextSimulatedDayObserver observer) {
        this.nextSimulatedDayObservers.remove(observer);
    }

    private void nextSimulatedDay() {
        for (var observer : nextSimulatedDayObservers) {
            observer.onNextSimulatedDay(this);
        }
    }

    public void addMagicDayObserver(IMagicDayObserver observer) {
        this.magicDayObservers.add(observer);
    }

    public void removeMagicDayObserver(IMagicDayObserver observer) {
        this.magicDayObservers.remove(observer);
    }

    private void magicDay() {
        for (var observer : magicDayObservers) {
            observer.onMagicDay(this);
        }
    }

    /**
     * Logs preview of animals and grasses at world map.
     */
    public void drawMap() {
        System.out.print(map.toString());
    }
}
