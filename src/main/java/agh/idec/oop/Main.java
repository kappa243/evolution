package agh.idec.oop;

import agh.idec.oop.gui.App;
import javafx.application.Application;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {
            Application.launch(App.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

//        try {
//            World world = new World(true, 20, 20, 5, 5, 100, 30, 1,
//                    30, 1, 3);
//
//            while (world.getDay() != 10000) {
//                world.simulateDay();
////                world.drawMap();
//
//                TimeUnit.MILLISECONDS.sleep(10);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}
