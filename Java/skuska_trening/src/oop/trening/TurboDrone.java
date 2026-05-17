package oop.trening;

class TurboDrone implements Drone {
    private Drone drone;
    private int position;

    public TurboDrone(Drone drone) {
        this.drone = drone;
        this.position = drone.getPosition();
    }

    @Override
    public void move() {
        int before = drone.getPosition();
        drone.move();
        int step = drone.getPosition() - before;

        this.position += step * 2;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public String getName() {
        return drone.getName();
    }
}