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
//            World world = new World(300, true, 6, 6, 2, 2, 10, 30, 3,
//                    3, 3, 5, true);
//
//            world.run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}
