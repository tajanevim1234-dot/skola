package oop.zadanie3;

public class Squarer implements Function {
    private int input;

    public Squarer() {
        this.input = 0;
    }

    public Squarer(int input) {
        this.input = input;
    }

    @Override
    public void setInput(int input) {
        this.input = input;
    }

    @Override
    public int getOutput() {
        return 2 * input;
    }
}