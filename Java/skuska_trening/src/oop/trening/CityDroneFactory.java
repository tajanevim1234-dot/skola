package oop.trening;

public class CityDroneFactory implements DroneFactory {
    @Override
    public Drone createLightDrone() {
        return new BasicDrone("city-light", 2);
    }

    @Override
    public Drone createHeavyDrone() {
        return new BasicDrone("city-heavy", 1);
    }
}
