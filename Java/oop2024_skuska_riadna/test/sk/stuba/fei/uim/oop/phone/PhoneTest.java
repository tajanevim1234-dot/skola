package sk.stuba.fei.uim.oop.phone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.oop.application.Application;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PhoneTest {
    private Phone phone;

    private Phone phoneInHomescreen() {
        Phone p = new Phone();
        p.powerButtonPressed();
        p.passwordEntered("123");
        assertEquals("Homescreen", p.getStateName());
        assertNull(p.getRunningApplication());
        return p;
    }

    private Phone phoneWithFirefoxRunning() {
        Phone p = phoneInHomescreen();
        Application firefox = applicationByName(p, "Firefox");
        p.start(firefox);
        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(firefox == p.getRunningApplication());
        assertEquals(3, p.getInstalledApplications().size());
        return p;
    }

    private Application applicationByName(Phone p, String applicationName) {
        for(Application a: p.getInstalledApplications()) {
            if(a.getName().equals(applicationName)) {
                return a;
            }
        }
        return null;
    }

    @BeforeEach
    void setUp() {
        phone = new Phone();
    }

    @Test
    void initState_1_() {
        assertEquals("Locked", phone.getStateName());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void preinstalledApplications_2_() {
        assertEquals(3, phone.getInstalledApplications().size());
        boolean[] check = {false, false, false};
        for(Application a: phone.getInstalledApplications()) {
            switch (a.getName()) {
                case "AppStore":
                    assertTrue(a.canInstallApplication());
                    assertFalse(a.startableFromLockedScreen());
                    check[0] = true;
                    break;
                case "Firefox":
                    assertFalse(a.canInstallApplication());
                    assertFalse(a.startableFromLockedScreen());
                    check[1] = true;
                    break;
                case "Camera":
                    assertFalse(a.canInstallApplication());
                    assertTrue(a.startableFromLockedScreen());
                    check[2] = true;
                    break;
            }
        }
        assertTrue(check[0] && check[1] && check[2], "Preinstalovane aplikacie musia byt podla zadania");
    }

    @Test
    void unlockStart_2_() {
        phone.powerButtonPressed();
        assertEquals("WaitingForPassword", phone.getStateName());
    }

    @Test
    void unlockPasswordCorrect_2_() {
        phone.powerButtonPressed();
        phone.passwordEntered("123");
        assertEquals("Homescreen", phone.getStateName());
    }

    @Test
    void unlockPasswordIncorrect_2_() {
        phone.powerButtonPressed();
        phone.passwordEntered("1234");
        assertEquals("WaitingForPassword", phone.getStateName());
        phone.passwordEntered("incorrectPassword");
        assertEquals("WaitingForPassword", phone.getStateName());
    }

    @Test
    void unlockPasswordIncorrectThanCorrect_2_() {
        phone.powerButtonPressed();
        phone.passwordEntered("incorrect");
        assertEquals("WaitingForPassword", phone.getStateName());
        phone.passwordEntered("123");
        assertEquals("Homescreen", phone.getStateName());
    }

    @Test
    void homescreenBack_2_() {
        Phone p = phoneInHomescreen();
        p.backButtonPressed();
        assertEquals("Homescreen", p.getStateName());
    }

    @Test
    void runApplicationFromHomescreen_2_() {
        Phone p = phoneInHomescreen();
        Application firefox = applicationByName(p, "Firefox");
        p.start(firefox);
        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(firefox == p.getRunningApplication());
    }

    @Test
    void runningApplicationBackToHomescreen_2_() {
        Phone p = phoneInHomescreen();
        Application firefox = applicationByName(p, "Firefox");
        p.start(firefox);
        p.backButtonPressed();
        assertEquals("Homescreen", p.getStateName());
        assertNull(p.getRunningApplication());
    }

    @Test
    void runApplicationFromLocked_2_() {
        Application camera = applicationByName(phone, "Camera");
        phone.powerButtonPressed();
        phone.start(camera);
        assertEquals("ApplicationRunning", phone.getStateName());
        assertTrue(camera == phone.getRunningApplication());
    }

    @Test
    void cannotRunApplicationFromLocked_2_() {
        Application camera = applicationByName(phone, "Firefox");
        phone.powerButtonPressed();
        phone.start(camera);
        assertEquals("WaitingForPassword", phone.getStateName());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void runningApplicationBackToLocked_2_() {
        Application camera = applicationByName(phone, "Camera");
        phone.powerButtonPressed();
        phone.start(camera);
        phone.backButtonPressed();
        assertEquals("Locked", phone.getStateName());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void homescreenToRunningApplicationToLocked_2_() {
        Phone p = phoneInHomescreen();
        Application firefox = applicationByName(p, "Firefox");
        p.start(firefox);
        p.powerButtonPressed();
        assertEquals("Locked", p.getStateName());
        assertNull(p.getRunningApplication());
    }

    @Test
    void lockedToRunningApplicationToLocked_2_() {
        Application camera = applicationByName(phone, "Camera");
        phone.powerButtonPressed();
        phone.start(camera);
        phone.powerButtonPressed();
        assertEquals("Locked", phone.getStateName());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void installApplication_2_() {
        Phone p = phoneInHomescreen();
        Application appStore = applicationByName(p, "AppStore");
        assertTrue(appStore.canInstallApplication());
        p.start(appStore);
        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(appStore == p.getRunningApplication());

        Application game = new Application("Game");
        p.install(game);

        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(appStore == p.getRunningApplication());
        assertTrue(p.getInstalledApplications().contains(game));
        assertEquals(4, p.getInstalledApplications().size());

        Application gps = new Application("GPS");
        p.install(gps);

        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(appStore == p.getRunningApplication());
        assertTrue(p.getInstalledApplications().contains(gps));
        assertEquals(5, p.getInstalledApplications().size());
    }

    @Test
    void cannotInstallApplication_2_() {
        Phone p = phoneInHomescreen();
        Application camera = applicationByName(p, "Camera");
        assertFalse(camera.canInstallApplication());
        p.start(camera);
        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(camera == p.getRunningApplication());

        assertEquals(3, p.getInstalledApplications().size());

        Application game = new Application("Game");
        p.install(game);

        assertEquals("ApplicationRunning", p.getStateName());
        assertTrue(camera == p.getRunningApplication());
        assertFalse(p.getInstalledApplications().contains(game));
        assertEquals(3, p.getInstalledApplications().size());
    }

    @Test
    void uninstallApplication_2_() {
        Phone p = phoneInHomescreen();
        Application firefox = applicationByName(p, "Firefox");
        p.uninstall(firefox);
        assertEquals("Homescreen", p.getStateName());
        assertEquals(2, p.getInstalledApplications().size());
    }

    @Test
    void uninstallApplicationNotOnPhone_2_() {
        Phone p = phoneInHomescreen();
        Application game = new Application("Game");
        p.uninstall(game);
        assertEquals("Homescreen", p.getStateName());
        assertEquals(3, p.getInstalledApplications().size());
        assertNull(p.getRunningApplication());
    }

    // --------------------------------------------------------------------------------------------

    @Test
    void doNothingLockedBack_1_() {
        phone.backButtonPressed();
        assertEquals("Locked", phone.getStateName());
    }

    @Test
    void doNothingLockedInstall_1_() {
        Application game = new Application("Game");
        phone.install(game);
        assertEquals("Locked", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
    }

    @Test
    void doNothingLockedUninstall_1_() {
        Application firefox = applicationByName(phone, "Firefox");
        phone.uninstall(firefox);
        assertEquals("Locked", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void doNothingLockedStart_1_() {
        Application firefox = applicationByName(phone, "Firefox");
        phone.start(firefox);
        assertEquals("Locked", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void doNothingLockedPassword_1_() {
        phone.passwordEntered("123");
        assertEquals("Locked", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void doNothingPasswordBack_1_() {
        phone.powerButtonPressed();
        assertEquals("WaitingForPassword", phone.getStateName());

        phone.backButtonPressed();
        assertEquals("WaitingForPassword", phone.getStateName());
    }

    @Test
    void doNothingPasswordInstall_1_() {
        phone.powerButtonPressed();

        Application game = new Application("Game");
        phone.install(game);
        assertEquals("WaitingForPassword", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertFalse(phone.getInstalledApplications().contains(game));
    }

    @Test
    void doNothingPasswordUninstall_1_() {
        phone.powerButtonPressed();

        Application firefox = applicationByName(phone, "Firefox");
        phone.uninstall(firefox);
        assertEquals("WaitingForPassword", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertTrue(phone.getInstalledApplications().contains(firefox));
    }

    @Test
    void doNothingPasswordStart_1_() {
        phone.powerButtonPressed();

        Application firefox = applicationByName(phone, "Firefox");
        phone.start(firefox);
        assertEquals("WaitingForPassword", phone.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void doNothingHomescreenBack_1_() {
        Phone p = phoneInHomescreen();
        p.backButtonPressed();
        assertEquals("Homescreen", p.getStateName());
    }

    @Test
    void doNothingHomescreenInstall_1_() {
        Phone p = phoneInHomescreen();
        Application game = new Application("Game");
        p.install(game);
        assertEquals("Homescreen", p.getStateName());
        assertEquals(3, phone.getInstalledApplications().size());
        assertNull(phone.getRunningApplication());
    }

    @Test
    void doNothingHomescreenPassword_1_() {
        Phone p1 = phoneInHomescreen();
        p1.passwordEntered("123");
        assertEquals("Homescreen", p1.getStateName());

        Phone p2 = phoneInHomescreen();
        p2.passwordEntered("12345");
        assertEquals("Homescreen", p2.getStateName());
    }

    @Test
    void doNothingApplicationRunningUninstall_1_() {
        Phone p = phoneWithFirefoxRunning();
        Application camera = applicationByName(p, "Camera");
        assertNotNull(camera);
        p.uninstall(camera);
        assertTrue(p.getInstalledApplications().contains(camera));
        assertEquals("ApplicationRunning", p.getStateName());
        assertEquals("Firefox", p.getRunningApplication().getName());
    }

    @Test
    void doNothingApplicationRunningStart_1_() {
        Phone p = phoneWithFirefoxRunning();
        Application camera = applicationByName(p, "Camera");
        p.start(camera);
        assertEquals("ApplicationRunning", p.getStateName());
        assertEquals("Firefox", p.getRunningApplication().getName());
    }

    @Test
    void doNothingApplicationRunningPassword_1_() {
        Phone p = phoneWithFirefoxRunning();
        p.passwordEntered("123");
        assertEquals("ApplicationRunning", p.getStateName());
        assertEquals("Firefox", p.getRunningApplication().getName());
    }

    // ----- vzor Stav ----------------------------------------------------------------------------

    @Test
    void phoneContainsState_2_() {
        for (Field f: Phone.class.getDeclaredFields()) {
            if(State.class.getName().equals(f.getType().getName()) ) {
                return;
            }
        }
        assertTrue(false, "Pouzite vzor Stav (angl. State). Stav reprezentujte podtypmi abstraktnej triedy State");
    }

    private State getState(Phone p) throws IllegalAccessException {
        State result = null;
        for (Field f: Phone.class.getDeclaredFields()) {
            if(State.class.getName().equals(f.getType().getName()) ) {
                f.setAccessible(true);
                result = (State) f.get(phone);
            }
        }
        assertNotNull(result, "Nepouzity stave (State)");
        return result;
    }

    @Test
    void stateChanging_5_() throws IllegalAccessException {
        State locked = getState(phone);
        phone.powerButtonPressed();
        State waiting = getState(phone);
        phone.passwordEntered("123");
        State homescreen = getState(phone);
        Application firefox = applicationByName(phone, "Firefox");
        phone.start(firefox);
        State running = getState(phone);

        assertNotNull(locked, "Stav je nepouzity");
        assertNotNull(waiting, "Stav je nepouzity");
        assertNotNull(homescreen, "Stav je nepouzity");
        assertNotNull(running, "Stav je nepouzity");

        assertFalse(Modifier.isPublic(locked.getClass().getModifiers()), "Stav implementujte v rovnakom baliku ako Telefon. Preto nemusi byt public");
        assertFalse(Modifier.isPublic(waiting.getClass().getModifiers()), "Stav implementujte v rovnakom baliku ako Telefon. Preto nemusi byt public");
        assertFalse(Modifier.isPublic(homescreen.getClass().getModifiers()), "Stav implementujte v rovnakom baliku ako Telefon. Preto nemusi byt public");
        assertFalse(Modifier.isPublic(running.getClass().getModifiers()), "Stav implementujte v rovnakom baliku ako Telefon. Preto nemusi byt public");

        assertNotEquals(locked, waiting, "Stav sa nemeni");
        assertNotEquals(locked, homescreen, "Stav sa nemeni");
        assertNotEquals(locked, running, "Stav sa nemeni");
        assertNotEquals(waiting, homescreen, "Stav sa nemeni");
        assertNotEquals(waiting, running, "Stav sa nemeni");
        assertNotEquals(homescreen, running, "Stav sa nemeni");
    }

    @Test
    void NotAddedPublicFunctionToPhone_1_() {
        long countPublicMethod = Arrays.stream(Phone.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .count();
        assertEquals(9, countPublicMethod, "Do triedy Phone nepridavajte verejne metody. Stavy implementujte v tom istom baliku");
    }

    // ----- Application equals() a hashCode() ----------------------------------------------------

    @Test
    void applicationEquals_1_() {
        Application a1 = new Application("App", true, false);
        Application a2 = new Application("App", true, false);
        assertTrue(a1.equals(a2));

        Application a3 = new Application("App", false, true);
        Application a4 = new Application("App", false, true);
        assertTrue(a3.equals(a4));

        assertFalse(a1.equals(a4));
    }

    @Test
    void applicatonHashCode_1_() {
        Application a1 = new Application("App", true, false);
        Application a2 = new Application("App", true, false);
        assertEquals(a1.hashCode(), a2.hashCode());
    }
}
