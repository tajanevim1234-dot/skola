package sk.stuba.fei.uim.oop.phone;

import sk.stuba.fei.uim.oop.application.Application;

abstract class State {

    protected Phone phone;

    State(Phone phone) {
        this.phone = phone;
    }
    
    String getName() {
        return getClass().getSimpleName().replace("State", ""); // mozete upravit podla potreby
    }

    void powerButtonPressed() {
        phone.setRunningApplication(null);
        phone.setState(new Locked(phone));
    }

    void backButtonPressed() {
    }

    void passwordEntered(String password) {
    }

    void start(Application application) {
    }

    void install(Application application) {
    }

    void uninstall(Application application) {
    }
}





