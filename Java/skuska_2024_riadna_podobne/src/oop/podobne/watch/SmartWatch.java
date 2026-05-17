package oop.podobne.watch;

import java.util.ArrayList;
import java.util.List;

public class SmartWatch {
    private State state;
    private WatchApplication runningApplication;
    private List<WatchApplication> installedApplications;
    private boolean runningFromLockScreen;

    public SmartWatch() {
        this.installedApplications = new ArrayList<>();
        // TODO
    }

    public void sideButtonPressed() {
        // TODO
    }

    public void backButtonPressed() {
        // TODO
    }

    public void pinEntered(String pin) {
        // TODO
    }

    public void start(WatchApplication application) {
        // TODO
    }

    public void install(WatchApplication application) {
        // TODO
    }

    public void uninstall(WatchApplication application) {
        // TODO
    }

    public String getStateName() {
        return null;
    }

    public WatchApplication getRunningApplication() {
        return runningApplication;
    }

    public List<WatchApplication> getInstalledApplications() {
        return installedApplications;
    }

    void setState(State state) {
        this.state = state;
    }

    void setRunningApplication(WatchApplication runningApplication) {
        this.runningApplication = runningApplication;
    }

    boolean isRunningFromLockScreen() {
        return runningFromLockScreen;
    }

    void setRunningFromLockScreen(boolean runningFromLockScreen) {
        this.runningFromLockScreen = runningFromLockScreen;
    }
}
