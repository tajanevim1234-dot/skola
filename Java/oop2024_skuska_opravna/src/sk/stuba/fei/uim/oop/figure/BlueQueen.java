package sk.stuba.fei.uim.oop.figure;

public class BlueQueen implements Figure {
    private int pos;

    public BlueQueen() {
        this.pos = 0;
    }

    @Override
    public void move() {
        this.pos += 5;
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
