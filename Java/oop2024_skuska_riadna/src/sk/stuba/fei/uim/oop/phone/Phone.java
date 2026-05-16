package sk.stuba.fei.uim.oop.phone;

import sk.stuba.fei.uim.oop.application.Application;

import java.util.HashSet;
import java.util.Set;

public class Phone {

    private Set<Application> applications;
    private State state;
    private Application runningApplication;

    public Phone(){
        this.applications = new HashSet<Application>();
        this.applications.add(new Application("AppStore", true, false));
        this.applications.add(new Application("Firefox", false, false));
        this.applications.add(new Application("Camera", false, true));
        this.runningApplication = null;
        this.state = new Locked(this);
    }

    public void powerButtonPressed() {
        state.powerButtonPressed();
    }

    void setState(State state) {
        this.state = state;
    }

    void addApplication(Application app) {
        applications.add(app);
    }

    void removeApplication(Application app) {
        applications.remove(app);
    }

    void setRunningApplication(Application runningApplication) {
        this.runningApplication = runningApplication;
    }

    public void backButtonPressed() {
        state.backButtonPressed();
    }

    public void install(Application app) {
        state.install(app);
    }

    public void uninstall(Application app) {
        state.uninstall(app);
    }

    public void start(Application app) {
        state.start(app);
    }

    public void passwordEntered(String password) {
        state.passwordEntered(password);
    }

    public Set<Application> getInstalledApplications() {
        return applications;
    }

    public Application getRunningApplication() {
        return runningApplication;
    }

    public String getStateName() {
        return state.getName();
    }
}
