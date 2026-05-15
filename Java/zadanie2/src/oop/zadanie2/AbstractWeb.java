package oop.zadanie2;

public abstract class AbstractWeb {
    private User[] users;

    public AbstractWeb(User[] users) {
        this.users = users;
    }

    protected boolean authenticate(Request request) {
        for (User user : users) {
            if (user.getLogin().equals(request.getLogin()) && user.getPassword().equals(request.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public abstract Response getById(Request request);

    public abstract  Response getAll(Request request);
}
