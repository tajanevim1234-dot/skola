package oop.podobne.watch;

class WaitingForPin extends State {
    WaitingForPin(SmartWatch watch) {
        super(watch);
    }

    @Override
    void pinEntered(String pin) {
        if(pin.equals("1111")){
            watch.setState(new Menu(watch));
        }
    }

    @Override
    void start(WatchApplication application) {
        if (watch.getInstalledApplications().contains(application) && application.startableFromLockScreen()) {
            watch.setRunningFromLockScreen(true);
            watch.setRunningApplication(application);
            watch.setState(new ActivityRunning(watch));
        }
    }
}
