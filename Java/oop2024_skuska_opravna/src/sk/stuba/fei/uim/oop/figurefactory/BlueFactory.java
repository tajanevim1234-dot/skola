package sk.stuba.fei.uim.oop.figurefactory;

import sk.stuba.fei.uim.oop.figure.BlueBishop;
import sk.stuba.fei.uim.oop.figure.BluePawn;
import sk.stuba.fei.uim.oop.figure.BlueQueen;
import sk.stuba.fei.uim.oop.figure.Figure;

public class BlueFactory implements FigureFactory {

    @Override
    public Figure createPawn() {
        return new BluePawn();
    }

    @Override
    public Figure createBishop() {
        return new BlueBishop();      
    }

    @Override
    public Figure createQueen() {
        return new BlueQueen();
    }
  


}
