package sk.stuba.fei.uim.oop.figure;

public class RedBishop implements Figure {

    private int pos;

    public RedBishop(){
        this.pos = 0;
    }

    @Override
    public void move() {
        this.pos += 3;  
    }

    @Override
    public int getPosition() {
        return pos;
    }

    @Override
    public void setPosition(int position) {
        this.pos=position;
    }
    
}
