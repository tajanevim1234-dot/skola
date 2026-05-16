package sk.stuba.fei.uim.oop.figure;

public class BluePawn implements Figure {
    private int pos;

    public BluePawn() {
        this.pos = 0;
    }

    @Override
    public void move() {
        this.pos += 1;
    }

    @Override
    public int getPosition() {
        return this.pos;
    }

    @Override
    public void setPosition(int position) {
        this.pos = position;
    }
}
