package oop.podobne.watch;

class Menu extends State {
    Menu(SmartWatch watch) {
        super(watch);
    }

    @Override
    void sideButtonPressed() {
        watch.setRunningApplication(null);
        watch.setState(new Off(watch));
    }

    @Override
    void start(WatchApplication application) {
        if(watch.getInstalledApplications().contains(application)){
            watch.setRunningApplication(application);
            watch.setRunningFromLockScreen(false);
            watch.setState(new ActivityRunning(watch));
        }
    }

    @Override
    void uninstall(WatchApplication application) {
        if(watch.getInstalledApplications().contains(application)){
            watch.removeInstalledApplication(application);
        }
    }
}
