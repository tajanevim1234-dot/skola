package oop.zadanie4;

public class Main {
    public static void main(String[] args) {
        testPaymentSuccessful();
        testNegativePaymentsException();
        testNotEnoughMoneyException();
    }

    static void testPaymentSuccessful() {
        Wallet w = new Wallet(120);
        int result = Payer.payByWallet(w, 100);

        if (result == 20) {
            System.out.println("paymentSuccessful: OK");
        } else {
            System.out.println("paymentSuccessful: FAIL, result = " + result);
        }
    }

    static void testNegativePaymentsException() {
        Wallet w = new Wallet(100);
        int result = Payer.payByWallet(w, -10);

        if (result == -1) {
            System.out.println("negativePaymentsException: OK");
        } else {
            System.out.println("negativePaymentsException: FAIL, result = " + result);
        }
    }

    static void testNotEnoughMoneyException() {
        Wallet w = new Wallet(100);
        int result = Payer.payByWallet(w, 200);

        if (result == -2) {
            System.out.println("notEnoughMoneyException: OK");
        } else {
            System.out.println("notEnoughMoneyException: FAIL, result = " + result);
        }
    }
}