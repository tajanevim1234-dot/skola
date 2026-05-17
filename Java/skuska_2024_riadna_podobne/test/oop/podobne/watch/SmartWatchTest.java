package oop.podobne.watch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SmartWatchTest {
    private SmartWatch watch;

    private WatchApplication applicationByName(SmartWatch watch, String name) {
        for (WatchApplication application : watch.getInstalledApplications()) {
            if (application.getName().equals(name)) {
                return application;
            }
        }
        return null;
    }

    private SmartWatch watchInMenu() {
        SmartWatch result = new SmartWatch();
        result.sideButtonPressed();
        result.pinEntered("1111");
        assertEquals("Menu", result.getStateName());
        return result;
    }

    private SmartWatch watchWithWeatherRunning() {
        SmartWatch result = watchInMenu();
        WatchApplication weather = applicationByName(result, "Weather");
        result.start(weather);
        assertEquals("ActivityRunning", result.getStateName());
        assertTrue(weather == result.getRunningApplication());
        return result;
    }

    @BeforeEach
    void setUp() {
        watch = new SmartWatch();
    }

    @Test
    void initState() {
        assertEquals("Off", watch.getStateName());
        assertNull(watch.getRunningApplication());
    }

    @Test
    void preinstalledApplications() {
        assertEquals(3, watch.getInstalledApplications().size());

        WatchApplication store = applicationByName(watch, "Store");
        WatchApplication timer = applicationByName(watch, "Timer");
        WatchApplication weather = applicationByName(watch, "Weather");

        assertNotNull(store);
        assertTrue(store.canInstallApplication());
        assertFalse(store.startableFromLockScreen());

        assertNotNull(timer);
        assertFalse(timer.canInstallApplication());
        assertTrue(timer.startableFromLockScreen());

        assertNotNull(weather);
        assertFalse(weather.canInstallApplication());
        assertFalse(weather.startableFromLockScreen());
    }

    @Test
    void unlockWithCorrectPin() {
        watch.sideButtonPressed();
        assertEquals("WaitingForPin", watch.getStateName());

        watch.pinEntered("1111");
        assertEquals("Menu", watch.getStateName());
    }

    @Test
    void wrongPinKeepsWaiting() {
        watch.sideButtonPressed();
        watch.pinEntered("1234");
        assertEquals("WaitingForPin", watch.getStateName());

        watch.pinEntered("1111");
        assertEquals("Menu", watch.getStateName());
    }

    @Test
    void startApplicationFromMenu() {
        SmartWatch menuWatch = watchInMenu();
        WatchApplication weather = applicationByName(menuWatch, "Weather");

        menuWatch.start(weather);

        assertEquals("ActivityRunning", menuWatch.getStateName());
        assertTrue(weather == menuWatch.getRunningApplication());
    }

    @Test
    void backFromMenuApplicationReturnsToMenu() {
        SmartWatch runningWatch = watchWithWeatherRunning();

        runningWatch.backButtonPressed();

        assertEquals("Menu", runningWatch.getStateName());
        assertNull(runningWatch.getRunningApplication());
    }

    @Test
    void startTimerFromLockScreen() {
        WatchApplication timer = applicationByName(watch, "Timer");

        watch.sideButtonPressed();
        watch.start(timer);

        assertEquals("ActivityRunning", watch.getStateName());
        assertTrue(timer == watch.getRunningApplication());
    }

    @Test
    void cannotStartWeatherFromLockScreen() {
        WatchApplication weather = applicationByName(watch, "Weather");

        watch.sideButtonPressed();
        watch.start(weather);

        assertEquals("WaitingForPin", watch.getStateName());
        assertNull(watch.getRunningApplication());
    }

    @Test
    void backFromLockScreenApplicationReturnsToOff() {
        WatchApplication timer = applicationByName(watch, "Timer");

        watch.sideButtonPressed();
        watch.start(timer);
        watch.backButtonPressed();

        assertEquals("Off", watch.getStateName());
        assertNull(watch.getRunningApplication());
    }

    @Test
    void sideButtonTurnsOffFromMenuAndRunning() {
        SmartWatch menuWatch = watchInMenu();
        menuWatch.sideButtonPressed();
        assertEquals("Off", menuWatch.getStateName());

        SmartWatch runningWatch = watchWithWeatherRunning();
        runningWatch.sideButtonPressed();
        assertEquals("Off", runningWatch.getStateName());
        assertNull(runningWatch.getRunningApplication());
    }

    @Test
    void storeCanInstallApplications() {
        SmartWatch menuWatch = watchInMenu();
        WatchApplication store = applicationByName(menuWatch, "Store");
        WatchApplication notes = new WatchApplication("Notes");

        menuWatch.start(store);
        menuWatch.install(notes);

        assertEquals("ActivityRunning", menuWatch.getStateName());
        assertTrue(menuWatch.getInstalledApplications().contains(notes));
        assertEquals(4, menuWatch.getInstalledApplications().size());
    }

    @Test
    void weatherCannotInstallApplications() {
        SmartWatch runningWatch = watchWithWeatherRunning();
        WatchApplication notes = new WatchApplication("Notes");

        runningWatch.install(notes);

        assertFalse(runningWatch.getInstalledApplications().contains(notes));
        assertEquals(3, runningWatch.getInstalledApplications().size());
    }

    @Test
    void uninstallOnlyFromMenu() {
        SmartWatch menuWatch = watchInMenu();
        WatchApplication weather = applicationByName(menuWatch, "Weather");

        menuWatch.uninstall(weather);

        assertFalse(menuWatch.getInstalledApplications().contains(weather));
        assertEquals(2, menuWatch.getInstalledApplications().size());
    }

    @Test
    void doNothingInWrongStates() {
        WatchApplication notes = new WatchApplication("Notes");
        WatchApplication weather = applicationByName(watch, "Weather");

        watch.install(notes);
        watch.uninstall(weather);
        watch.backButtonPressed();
        watch.pinEntered("1111");

        assertEquals("Off", watch.getStateName());
        assertEquals(3, watch.getInstalledApplications().size());
        assertNull(watch.getRunningApplication());
    }

    @Test
    void watchContainsState() {
        for (Field field : SmartWatch.class.getDeclaredFields()) {
            if (State.class.getName().equals(field.getType().getName())) {
                return;
            }
        }
        fail("Pouzi vzor State: SmartWatch ma mat atribut typu State");
    }

    @Test
    void stateActuallyChanges() throws IllegalAccessException {
        State off = getState(watch);
        watch.sideButtonPressed();
        State waiting = getState(watch);
        watch.pinEntered("1111");
        State menu = getState(watch);
        watch.start(applicationByName(watch, "Weather"));
        State running = getState(watch);

        assertNotEquals(off, waiting);
        assertNotEquals(waiting, menu);
        assertNotEquals(menu, running);

        assertFalse(Modifier.isPublic(off.getClass().getModifiers()));
        assertFalse(Modifier.isPublic(waiting.getClass().getModifiers()));
        assertFalse(Modifier.isPublic(menu.getClass().getModifiers()));
        assertFalse(Modifier.isPublic(running.getClass().getModifiers()));
    }

    private State getState(SmartWatch watch) throws IllegalAccessException {
        for (Field field : SmartWatch.class.getDeclaredFields()) {
            if (State.class.getName().equals(field.getType().getName())) {
                field.setAccessible(true);
                return (State) field.get(watch);
            }
        }
        fail("SmartWatch nema atribut typu State");
        return null;
    }

    @Test
    void notAddedPublicMethodsToSmartWatch() {
        long countPublicMethod = Arrays.stream(SmartWatch.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .count();
        assertEquals(9, countPublicMethod);
    }

    @Test
    void applicationEqualsAndHashCode() {
        WatchApplication first = new WatchApplication("App", true, false);
        WatchApplication same = new WatchApplication("App", true, false);
        WatchApplication different = new WatchApplication("App", false, false);

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
    }
}
