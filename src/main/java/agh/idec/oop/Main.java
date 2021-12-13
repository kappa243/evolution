package agh.idec.oop;

import agh.idec.oop.world.World;

public class Main {
    public static void main(String[] args) {
        try{
            World world = new World(false, 8, 5, 10, 1, 5, 0.2f);

            world.drawMap();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
