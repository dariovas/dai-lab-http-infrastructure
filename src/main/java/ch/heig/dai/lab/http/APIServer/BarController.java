package ch.heig.dai.lab.http.APIServer;

import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BarController {
    private ConcurrentHashMap<Integer, Bar> bars = new ConcurrentHashMap<Integer, Bar>();
    private int lastId = 0;

    public BarController(){
        List<String> cocktails = new ArrayList<String>() {{
            add("Mojito");
            add("Sex on the bitch");
            add("Pina Colada");
            add("Sangria");
            add("Negroni");
            add("Gin Fizz");
            add("Caïpirinha");
        }};

        bars.put(++lastId, new Bar(lastId, "The Motel", "Lausanne", 450, cocktails));
        bars.put(++lastId, new Bar(lastId, "Thursday is salsa", "Genève", 150, cocktails));
        bars.put(++lastId, new Bar(lastId, "PEGI 18", "Neuchâtel", 300, cocktails));
        bars.put(++lastId, new Bar(lastId, "Good Time", "Biel/Bienne", 230, cocktails));
    }

    public void getAll(Context ctx) {
        ctx.json(bars);
    }

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Bar bar = bars.get(id);
        if (bar != null) {
            ctx.json(bar);
        } else {
            ctx.status(404).result("Bar not found");
        }
    }

    public void getBarCocktails(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Bar bar = bars.get(id);

        if (bar != null) {
            ctx.json(bar.getCocktails());
        } else {
            ctx.status(404).result("Bar not found");
        }
    }

    public void addBarCocktails(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        String cocktailName = ctx.formParam("cocktailName");

        Bar bar = bars.get(id);
        if (bar != null) {
            bar.addCocktail(cocktailName);
            ctx.json(bar.getCocktails());
        } else {
            ctx.status(404).result("Bar not found");
        }
    }

    public void create(Context ctx) {
        Bar bar = ctx.bodyAsClass(Bar.class);
        bars.put(++lastId, bar);
        ctx.status(201);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Bar bar = ctx.bodyAsClass(Bar.class);
        bars.put(id, bar);
        ctx.status(200);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        bars.remove(id);
        ctx.status(204);
    }
}
