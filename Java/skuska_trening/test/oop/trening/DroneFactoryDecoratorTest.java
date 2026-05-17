package oop.trening;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class DroneFactoryDecoratorTest {
    private void testSteps(Drone drone, int... positions) {
        assertEquals(positions[0], drone.getPosition());
        for (int i = 1; i < positions.length; i++) {
            drone.move();
            assertEquals(positions[i], drone.getPosition());
        }
    }

    @Test
    void factoriesCreateDifferentFamilies() {
        DroneFactory city = ExamFactory.createCityDroneFactory();
        DroneFactory mountain = ExamFactory.createMountainDroneFactory();

        assertNotEquals(city.getClass(), mountain.getClass());
        assertFalse(Modifier.isAbstract(city.getClass().getModifiers()));
        assertFalse(Modifier.isAbstract(mountain.getClass().getModifiers()));

        assertEquals("city-light", city.createLightDrone().getName());
        assertEquals("city-heavy", city.createHeavyDrone().getName());
        assertEquals("mountain-light", mountain.createLightDrone().getName());
        assertEquals("mountain-heavy", mountain.createHeavyDrone().getName());
    }

    @Test
    void basicDroneMovement() {
        DroneFactory city = ExamFactory.createCityDroneFactory();
        DroneFactory mountain = ExamFactory.createMountainDroneFactory();

        testSteps(city.createLightDrone(), 0, 2, 4, 6);
        testSteps(city.createHeavyDrone(), 0, 1, 2, 3);
        testSteps(mountain.createLightDrone(), 0, 3, 6, 9);
        testSteps(mountain.createHeavyDrone(), 0, 2, 4, 6);
    }

    @Test
    void decoratorsWorkAlone() {
        Drone cityLight = ExamFactory.createCityDroneFactory().createLightDrone();
        Drone turbo = ExamFactory.createTurbo(cityLight);
        testSteps(turbo, 0, 4, 8, 12);

        Drone mountainHeavy = ExamFactory.createMountainDroneFactory().createHeavyDrone();
        Drone shield = ExamFactory.createShield(mountainHeavy);
        testSteps(shield, 0, 3, 6, 9);
    }

    @Test
    void decoratorsCanBeCombinedAndOrderMatters() {
        Drone droneA = ExamFactory.createCityDroneFactory().createLightDrone();
        Drone shieldThenTurbo = ExamFactory.createTurbo(ExamFactory.createShield(droneA));
        testSteps(shieldThenTurbo, 0, 6, 12, 18);

        Drone droneB = ExamFactory.createCityDroneFactory().createLightDrone();
        Drone turboThenShield = ExamFactory.createShield(ExamFactory.createTurbo(droneB));
        testSteps(turboThenShield, 0, 5, 10, 15);
    }

    @Test
    void decoratorKeepsExistingPosition() {
        Drone drone = ExamFactory.createMountainDroneFactory().createLightDrone();
        drone.move();
        drone.move();
        assertEquals(6, drone.getPosition());

        Drone shield = ExamFactory.createShield(drone);
        assertEquals(6, shield.getPosition());
        shield.move();
        assertEquals(10, shield.getPosition());
    }
}
