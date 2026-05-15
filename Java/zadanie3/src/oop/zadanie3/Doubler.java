package oop.zadanie3;

public class Doubler implements Function {
    private int input;

    public Doubler() {
        this.input = 0;
    }

    public Doubler(int input) {
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