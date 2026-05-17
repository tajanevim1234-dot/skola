package oop.trening;

public class ExamFactory {
    public static DroneFactory createCityDroneFactory() {
        return new CityDroneFactory();
    }

    public static DroneFactory createMountainDroneFactory() {
        return new MountainDroneFactory();
    }

    public static Drone createTurbo(Drone drone) {
        return new TurboDrone(drone);
    }

    public static Drone createShield(Drone drone) {
        return new ShieldDrone(drone);
    }
}
