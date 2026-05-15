package oop.zadanie4;

public class Payer {
    private static int NOT_POSITIVE_PAYMENTS = -1;
    private static int NOT_ENOUGH_MONEY = -2;

    public static int payByWallet(Wallet wallet, int price) {
        // TODO
        try {
            wallet.pay(price);
        } catch (NotPositivePaymentException e) {
            return NOT_POSITIVE_PAYMENTS;
        } catch (NotEnoughtMoneyException e) {
            return NOT_ENOUGH_MONEY;
        }
        return wallet.getCache();
    }
}
