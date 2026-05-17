package oop.trening;

class BasicDrone implements Drone {
    private String name;
    private int step;
    private int position;

    BasicDrone(String name, int step) {
        this.name = name;
        this.step = step;
    }

    @Override
    public void move() {
        position += step;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getName() {
        return name;
    }
}
