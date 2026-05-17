package oop.trening;

class ShieldDrone implements Drone {
    private Drone drone;

    public ShieldDrone(Drone drone) {
        this.drone = drone;
    }

    @Override
    public void move() {
        // TODO
        drone.move();
    }

    @Override
    public int getPosition() {
        return drone.getPosition();
    }

    @Override
    public String getName() {
        return drone.getName();
    }
}
