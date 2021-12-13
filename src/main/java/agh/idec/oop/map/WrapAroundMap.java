package agh.idec.oop.map;

import agh.idec.oop.Vector2D;

public class WrapAroundMap extends AbstractMap{
    public WrapAroundMap(int width, int height) {
        super(width, height);
    }

    /**
     * Wrap position inside map bound.
     *
     * @param position Position to wrap.
     * @return Wrapped position.
     */
    public Vector2D wrapPosition(Vector2D position){
        int height = this.getHeight();
        int width = this.getWidth();

        int x = position.getX() % width;
        int y = position.getY() % height;

        return new Vector2D(x, y);
    }

    @Override
    public boolean canMoveTo(Vector2D position) {
        return true;
    }
}
