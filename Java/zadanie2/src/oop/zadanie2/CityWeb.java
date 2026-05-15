package oop.zadanie2;

public class CityWeb extends AbstractWeb {

    private String[] cities;

    public CityWeb(User[] users, String[] cities) {
        super(users);
        this.cities = cities;
    }

    @Override
    public Response getById(Request req) {
        if (!authenticate(req)) {
            return new Response(false, "");
        }
        int id = req.getId();
        if (id < 0 || id >= cities.length) {
            return new Response(false, "");
        }
        return new Response(true, cities[id]);
    }

    @Override
    public Response getAll(Request req) {
        if (!authenticate(req)) {
            return new Response(false, "");
        }
        return new Response(true, String.join(", ", cities));
    }
}