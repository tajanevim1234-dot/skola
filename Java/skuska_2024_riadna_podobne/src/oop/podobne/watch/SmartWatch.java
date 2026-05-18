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
        installedApplications.add(new WatchApplication("Store",true,false));
        installedApplications.add(new WatchApplication("Timer",false,true));
        installedApplications.add(new WatchApplication("Weather", false, false));
        this.state= new Off(this);
    }

    public void sideButtonPressed() {
        state.sideButtonPressed();
    }

    public void backButtonPressed() {
        state.backButtonPressed();
    }

    public void pinEntered(String pin) {
        state.pinEntered(pin);
    }

    public void start(WatchApplication application) {
        state.start(application);
    }

    public void install(WatchApplication application) {
        state.install(application);
    }

    public void uninstall(WatchApplication application) {
        state.uninstall(application);
    }

    public String getStateName() {
        return state.getName();
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

    void addInstalledApplication(WatchApplication application) {
        installedApplications.add(application);
    }

    void removeInstalledApplication(WatchApplication application) {
        installedApplications.remove(application);
    }
}
