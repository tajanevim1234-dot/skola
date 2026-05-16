package sk.stuba.fei.uim.oop.item;

import sk.stuba.fei.uim.oop.figure.Figure;

public class Cloak implements Figure {

    private Figure figure;

    public  Cloak (Figure figure) {
        this.figure = figure;
    }

    @Override
    public void move() {
        int oldPosition = figure.getPosition();
        figure.move();
        int newPosition = figure.getPosition();
        int movement = newPosition - oldPosition;
        figure.setPosition(oldPosition + movement + 3);
    }

    @Override
    public int getPosition() {
        return figure.getPosition();
    }

    @Override
    public void setPosition(int position) {
        figure.setPosition(position);
    }
    
}

// Cloak(Cloak(figure));