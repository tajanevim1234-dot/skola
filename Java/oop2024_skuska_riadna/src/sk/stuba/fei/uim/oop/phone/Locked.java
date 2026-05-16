package sk.stuba.fei.uim.oop.phone;

class Locked extends State {
    Locked(Phone phone) {
        super(phone);
    }

    @Override
    String getName() {
        return "Locked";
    }

    @Override
    void powerButtonPressed() {
        phone.setState(new WaitingForPassword(phone));
    }
}