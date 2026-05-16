package sk.stuba.fei.uim.oop.phone;

class WaitingForPassword extends State {
    WaitingForPassword(Phone phone) {
        super(phone);
    }

    @Override
    String getName() {
        return "WaitingForPassword";
    }

    @Override
    void passwordEntered(String password) {
        if (password.equals("123")) {
            phone.setState(new Homescreen(phone));
        }
    }

    @Override
    void start(sk.stuba.fei.uim.oop.application.Application application) {
        if (phone.getInstalledApplications().contains(application) && application.startableFromLockedScreen()) {
            phone.setRunningApplication(application);
            phone.setState(new ApplicationRunning(phone, new Locked(phone)));
        }
    }
}
