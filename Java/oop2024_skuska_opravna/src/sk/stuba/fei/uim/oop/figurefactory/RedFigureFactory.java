package sk.stuba.fei.uim.oop.figurefactory;

import sk.stuba.fei.uim.oop.figure.Figure;
import sk.stuba.fei.uim.oop.figure.RedBishop;
import sk.stuba.fei.uim.oop.figure.RedPawn;
import sk.stuba.fei.uim.oop.figure.RedQueen;

public class RedFigureFactory implements FigureFactory {

    @Override
    public Figure createPawn() {
        return new RedPawn();
    }

    @Override
    public Figure createBishop() {
        return new RedBishop();
    }

    @Override
    public Figure createQueen() {
        return new RedQueen();
    }
}
