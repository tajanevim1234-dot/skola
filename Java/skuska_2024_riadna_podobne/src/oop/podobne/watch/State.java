package oop.podobne.watch;

abstract class State {
    protected SmartWatch watch;

    State(SmartWatch watch) {
        this.watch = watch;
    }

    void sideButtonPressed() {
    }

    void backButtonPressed() {
    }

    void pinEntered(String pin) {
    }

    void start(WatchApplication application) {
    }

    void install(WatchApplication application) {
    }

    void uninstall(WatchApplication application) {
    }

    String getName() {
        return getClass().getSimpleName();
    }
}
