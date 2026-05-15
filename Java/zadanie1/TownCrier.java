package zadanie1;

public class TownCrier {
    private String message;
    private int numberOfLastMessageAnnounced;

    public TownCrier() {
        this.message = null;
        this.numberOfLastMessageAnnounced = 0;
    }

    public void setMessage(String message) {
        this.message = message;
        this.numberOfLastMessageAnnounced = 0;
    }

    public String announce() {
        this.numberOfLastMessageAnnounced++;
        return this.message;
    }

    public int getNumberOfLastMessageAnnounced() {
        return this.numberOfLastMessageAnnounced;
    }
}