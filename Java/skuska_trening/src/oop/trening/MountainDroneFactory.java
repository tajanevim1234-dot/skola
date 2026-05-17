package oop.trening;

public class MountainDroneFactory implements DroneFactory {
    @Override
    public Drone createLightDrone() {
        return new BasicDrone("mountain-light", 3);
    }

    @Override
    public Drone createHeavyDrone() {
        return new BasicDrone("mountain-heavy", 2);
    }
}
