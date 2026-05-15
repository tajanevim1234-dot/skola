package oop.zadanie3;

public interface Function {
    void setInput(int input);

    int getOutput();

    default boolean isOutputPositive() {
        return getOutput() > 0;
    }
}
