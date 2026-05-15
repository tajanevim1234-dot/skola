package oop.zadanie2;

public class Request {
    private String login;
    private String password;
    private int id; // nemusi byt vzdy pouzite

    public Request(String login, String password, int id) {
        this.login = login;
        this.password = password;
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }
}
