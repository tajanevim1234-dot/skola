package sk.stuba.fei.uim.oop.figure;

public class RedPawn implements Figure {
    private int pos;

    public RedPawn() {
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
