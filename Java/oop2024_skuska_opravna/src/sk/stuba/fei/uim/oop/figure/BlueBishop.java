package sk.stuba.fei.uim.oop.figure;

public class BlueBishop implements Figure {
    private int pos;

    public BlueBishop() {
        this.pos = 0;
    }

    @Override
    public void move() {
        this.pos += 4;
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
