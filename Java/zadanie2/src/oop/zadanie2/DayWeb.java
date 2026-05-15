package oop.zadanie2;

public class DayWeb extends AbstractWeb {

    private static final String[] DAYS = {
        "pondelok", "utorok", "streda",
        "stvrtok", "piatok", "sobota", "nedela"
    };

    public DayWeb(User[] users) {
        super(users);
    }

    @Override
    public Response getById(Request req) {
        if (!authenticate(req)) {
            return new Response(false, "");
        }
        int id = req.getId();
        if (id < 1 || id > 7) {
            return new Response(false, "");
        }
        return new Response(true, DAYS[id - 1]);
    }

    @Override
    public Response getAll(Request req) {
        if (!authenticate(req)) {
            return new Response(false, "");
        }
        return new Response(true, String.join(", ", DAYS));
    }
}