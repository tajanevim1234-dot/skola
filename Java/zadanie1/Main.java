package zadanie1;

public class Main {
    public static void main(String[] args) {
        TownCrier crier = new TownCrier();

        System.out.println(crier.getNumberOfLastMessageAnnounced());

        crier.setMessage("Vtaky lietaju nizko, burka je blizko");
        System.out.println(crier.announce());
        System.out.println(crier.announce());
        System.out.println(crier.getNumberOfLastMessageAnnounced());

        crier.setMessage("V skole sa zacina vykurovacia sezona. Kazdy ziak musi doniest poleno dreva denne");
        System.out.println(crier.announce());
        System.out.println(crier.getNumberOfLastMessageAnnounced());
    }
}
