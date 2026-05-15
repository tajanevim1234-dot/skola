package oop.zadanie3;

public class main {

    public static void main(String[] args) {
        Doubler d1 = new Doubler(0);
        Function d2 = new Doubler(2);
        d1.getOutput();        // vrati 0
        System.out.println(d1.getOutput());
        d1.isOutputPositive(); // vrati false
        d2.getOutput();        // vrati 4
        d2.isOutputPositive(); // vrati true
        d1.setInput(3);
        d2.setInput(-4);
        d1.getOutput();        // vrati  6
        d1.isOutputPositive(); // vrati true
        d2.getOutput();        // vrati -8
        d2.isOutputPositive(); // vrati false

        Squarer s1 = new Squarer(4);
        Function s2 = new Squarer();
        s1.getOutput();        // vrati 16
        s1.isOutputPositive(); // vrati true
        s2.getOutput();        // vrati 0
        s2.isOutputPositive(); // vrati false
        s1.setInput(-5);
        s2.setInput(6);
        s1.getOutput();        // vrati 25
        s1.isOutputPositive(); // vrati true
        s2.getOutput();        // vrati 36
        s2.isOutputPositive(); // vrati true
    }
}
