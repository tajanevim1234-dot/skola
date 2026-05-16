package sk.stuba.fei.uim.oop.phone;

class ApplicationRunning extends State {
    private State previousState;

    ApplicationRunning(Phone phone, State previousState) {
        super(phone);
        this.previousState = previousState;
    }

    @Override
    void backButtonPressed() {
        phone.setRunningApplication(null);
        phone.setState(previousState);
    }

    @Override
    void install(sk.stuba.fei.uim.oop.application.Application application) {
        if (phone.getRunningApplication().canInstallApplication()) {
            phone.addApplication(application);
        }
    }

    @Override
    String getName() {
        return "ApplicationRunning";
    }

}
