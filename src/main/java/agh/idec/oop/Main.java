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
//            World world = new World(true, 50, 50, 12, 5, 100, 30, 1,
//                    30, 20, 50);
//
//            while (world.getDay() != 10000) {
//                world.simulateDay();
////                world.drawMap();
//
////                TimeUnit.MILLISECONDS.sleep(1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}
