package oop.zadanie4;

public class Wallet {
    private int cache;

    public Wallet(int cache) {
        this.cache = cache;
    }

    public int getCache() {
        return cache;
    }

    public void pay(int amount) throws NotEnoughtMoneyException, NotPositivePaymentException {
        if (amount <= 0) {
            throw new NotPositivePaymentException();
        }
        if (cache < amount) {
            throw new NotEnoughtMoneyException();
        }
        cache -= amount;
    }
}
