package oop.podobne.watch;

class Off extends State {
    Off(SmartWatch watch) {
        super(watch);
        watch.setRunningFromLockScreen(false);
    }

    @Override
    void sideButtonPressed() {
        watch.setState(new WaitingForPin(watch));
    }
}
