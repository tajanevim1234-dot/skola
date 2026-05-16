package sk.stuba.fei.uim.oop.examfactory;

import sk.stuba.fei.uim.oop.figure.Figure;
import sk.stuba.fei.uim.oop.figurefactory.BlueFactory;
import sk.stuba.fei.uim.oop.figurefactory.FigureFactory;
import sk.stuba.fei.uim.oop.figurefactory.RedFigureFactory;
import sk.stuba.fei.uim.oop.item.Boots;
import sk.stuba.fei.uim.oop.item.Cloak;

// Tovaren na vyrobu objektov pre automaticke testovanie
public class ExamFactory {

    // Vytvori a vrati tovaren na cervene figurky
    public static FigureFactory createRedFigureFactory() {
        return new RedFigureFactory();
    }

    // Vytvori a vrati tovaren na modre figurky
    public static FigureFactory createBlueFigureFactory() {
        return new BlueFactory();
    }

    // Vytvori a vrati zrychlujuci plast
    public static Figure createCoat(Figure figure) {
        return new Cloak(figure);
    }

    // Vytvori a vrati skakacie topanky
    public static Figure createShoes(Figure figure) {
        return new Boots(figure);
    }
}
