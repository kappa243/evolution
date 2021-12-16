//package agh.idec.oop;
//
//import agh.idec.oop.map.IMap;
//import agh.idec.oop.observables.INextSimulatedDayObserver;
//
//import java.util.HashSet;
//
//public class Engine {
//
//    private final HashSet<INextSimulatedDayObserver> observers = new HashSet<>();
//
//    World world;
//
//    public Engine() {
//        this.world = new World(true, 20, 20, 5, 5, 100, 30, 1,
//                30, 50, 3);
//    }
//
//    public void start() {
//        Thread thread = new Thread(() -> {
//            while (true) {
//                world.simulateDay();
//                onNextSimulationDay();
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
//    }
//
//    public void addSimulationDayObserver(INextSimulatedDayObserver observer) {
//        this.observers.add(observer);
//    }
//
//    public void removeSimulationDayObserver(INextSimulatedDayObserver observer) {
//        this.observers.remove(observer);
//    }
//
//    private void onNextSimulationDay() {
//        for (var observer : this.observers) {
//            observer.onNextSimulatedDay(this);
//        }
//    }
//
//    public IMap getMap(){
//        return world.getMap();
//    }
//}
