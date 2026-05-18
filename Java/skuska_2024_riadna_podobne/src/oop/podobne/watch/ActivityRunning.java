package oop.podobne.watch;

class ActivityRunning extends State {
    ActivityRunning(SmartWatch watch) {
        super(watch);
    }

    @Override
    void sideButtonPressed() {
        watch.setRunningApplication(null);
        watch.setState(new Off(watch));
    }

    @Override
    void backButtonPressed() {
        watch.setRunningApplication(null);
        if(watch.isRunningFromLockScreen()){
            watch.setState(new Off(watch));
        }else{
            watch.setState(new Menu(watch));
        }
    }

    @Override
    void install(WatchApplication application) {
        if(watch.getRunningApplication().canInstallApplication()){
            watch.addInstalledApplication(application);
        }
    }
}
