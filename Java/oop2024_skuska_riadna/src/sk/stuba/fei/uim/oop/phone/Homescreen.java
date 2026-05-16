package sk.stuba.fei.uim.oop.phone;

class Homescreen extends State {
    Homescreen(Phone phone) {
        super(phone);
    }

    @Override
    String getName() {
        return "Homescreen";
    }

    @Override
    void start(sk.stuba.fei.uim.oop.application.Application application) {
        if (phone.getInstalledApplications().contains(application)) {
            phone.setRunningApplication(application);
            phone.setState(new ApplicationRunning(phone, this));
        }
    }

    @Override
    void uninstall(sk.stuba.fei.uim.oop.application.Application application) {
        phone.removeApplication(application);
    }
}
