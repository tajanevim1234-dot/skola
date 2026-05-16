package sk.stuba.fei.uim.oop.application;

import java.util.Objects;

public class Application {
    private String name;
    private boolean canInstall;
    private boolean startableFromLocked;

    public Application(String name, boolean canInstallApplication, boolean startableFromLockedScreen) {
        this.name = name;
        this.canInstall = canInstallApplication;
        this.startableFromLocked = startableFromLockedScreen;
    }

    public Application(String name) {
        this(name, false, false);
    }

    public String getName() {
        return name;
    }

    public boolean canInstallApplication() { // ci aplikacia sluzi na instalovanie dalsich aplikacii
        return canInstall;
    }

    public boolean startableFromLockedScreen() { // ci mozno spustit bez zadania hesla
        return startableFromLocked;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Application)) {
            return false;
        }
        Application that = (Application) object;
        return canInstall == that.canInstall
                && startableFromLocked == that.startableFromLocked
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, canInstall, startableFromLocked);
    }
}
