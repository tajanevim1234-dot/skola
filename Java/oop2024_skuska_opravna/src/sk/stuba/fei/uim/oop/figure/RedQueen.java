package sk.stuba.fei.uim.oop.figure;

public class RedQueen implements Figure {
    private int pos;

    public RedQueen() {
        this.pos = 0;
    }

    @Override
    public void move() {
        this.pos += 6;
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
