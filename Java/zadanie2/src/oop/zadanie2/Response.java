package oop.zadanie2;

public class Response {
    private boolean status;
    private String data;

    public Response(boolean status, String data) {
        this.status = status;
        this.data = data;
    }

    public boolean getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }
}
