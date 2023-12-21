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

    private Bar findBarById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Bar bar = bars.get(id);

        if (bar == null) {
            ctx.status(404).result("Bar not found");
        }
        return bar;
    }

    public void getAll(Context ctx) {
        ctx.json(bars);
    }

    public void getOne(Context ctx) {
        Bar bar = findBarById(ctx);

        if (bar != null) {
            ctx.json(bar);
        }
    }

    public void getBarCocktails(Context ctx){
        Bar bar = findBarById(ctx);
        if (bar != null) {
            ctx.json(bar.getCocktails());
        }
    }

    private void modifyBarCocktails(Context ctx, boolean isAdding){
        Bar bar = findBarById(ctx);

        if(bar != null){
            String cocktailName = ctx.formParam("cocktailName");
            boolean cocktailExists = bar.getCocktails().contains(cocktailName);

            if (isAdding && cocktailExists) {
                ctx.status(400).result("Cocktail already exists in this bar.");
                return;
            } else if (!isAdding && !cocktailExists) {
                ctx.status(400).result("Cocktail doesn't exist in this bar.");
                return;
            }

            if(isAdding){
                bar.addCocktail(cocktailName);
            }
            else {
                bar.removeCocktail(cocktailName);
            }

            ctx.json(bar.getCocktails());
        }

    }

    public void addBarCocktails(Context ctx){
        modifyBarCocktails(ctx, true);
    }

    public void removeBarCocktails(Context ctx){
        modifyBarCocktails(ctx, false);
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
