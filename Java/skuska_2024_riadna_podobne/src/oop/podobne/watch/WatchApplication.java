package oop.podobne.watch;

public class WatchApplication {
    private String name;
    private boolean canInstallApplication;
    private boolean startableFromLockScreen;

    public WatchApplication(String name) {
        this.name=name;
        this.canInstallApplication=false;
        this.startableFromLockScreen=false;
    }

    public WatchApplication(String name, boolean canInstallApplication, boolean startableFromLockScreen) {
        this.name=name;
        this.canInstallApplication=canInstallApplication;
        this.startableFromLockScreen=startableFromLockScreen;
    }

    public String getName() {
        return this.name;
    }

    public boolean canInstallApplication() {
        return this.canInstallApplication;
    }

    public boolean startableFromLockScreen() {
        return this.startableFromLockScreen;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof WatchApplication other)) {
            return false;
        }

        return this.name.equals(other.name)
                && this.canInstallApplication == other.canInstallApplication
                && this.startableFromLockScreen == other.startableFromLockScreen;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, canInstallApplication, startableFromLockScreen);
    }
}
