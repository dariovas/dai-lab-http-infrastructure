package ch.heig.dai.lab.http.api;

import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the CRUD operations possible on the bars.
 */
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

    /**
     * Gets a bar by a id.
     * @param ctx HTTP request information.
     * @return a bar, otherwise a HTTP response 404.
     */
    private Bar findBarById(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Bar bar = bars.get(id);

        if (bar == null) {
            ctx.status(404).result("Bar not found");
        }
        return bar;
    }

    /**
     * Retrieves data of all bars.
     * @param ctx HTTP request information.
     */
    public void getAll(Context ctx) {
        ctx.status(200).json(bars);
    }

    /**
     * Retrieves data of a specific bar based on its id.
     * @param ctx HTTP request
     */
    public void getOne(Context ctx) {
        Bar bar = findBarById(ctx);

        if (bar != null) {
            ctx.status(200).json(bar);
        }
    }

    /**
     * Retrieves cocktails of a specific bar based on its id.
     * @param ctx HTTP request information.
     */
    public void getBarCocktails(Context ctx){
        Bar bar = findBarById(ctx);
        if (bar != null) {
            ctx.status(200).json(bar.getCocktails());
        }
    }

    /**
     * Modifies the cocktails list of a bar based on its id.
     * @param ctx HTTP request information.
     * @param isAdding true if we want to add a cocktail, false if we want to remove a cocktail.
     */
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

            ctx.status(200).json(bar.getCocktails());
        }

    }

    /**
     * Adds a cocktail to a bar based on its id.
     * @param ctx HTTP request information.
     */
    public void addBarCocktails(Context ctx){
        modifyBarCocktails(ctx, true);
    }

    /**
     * Removes a cocktail to a bar based on its id.
     * @param ctx HTTP request information.
     */
    public void removeBarCocktails(Context ctx){
        modifyBarCocktails(ctx, false);
    }

    /**
     * Creates a new bar.
     * @param ctx HTTP request information.
     */
    public void create(Context ctx) {
        Bar newBar = ctx.bodyAsClass(Bar.class);
        bars.put(++lastId, new Bar(lastId, newBar.getName(), newBar.getCity(), newBar.getCapacity(), newBar.getCocktails()));
        ctx.status(201);
    }

    /**
     * Updates the properties of a specific bar.
     * @param ctx HTTP request information.
     */
    public void update(Context ctx) {
        Bar currentBar = findBarById(ctx);

        if(currentBar != null){
            Bar updatedBar = ctx.bodyAsClass(Bar.class);
            updateBarProperties(currentBar, updatedBar);

            ctx.status(200).json(currentBar);
        }
        else {
            ctx.status(404).result("Bar not found");
        }
    }

    /**
     * Checks which fields must be updated, then proceed with updates.
     * @param currentBar -
     * @param updatedBar -
     */
    private void updateBarProperties(Bar currentBar, Bar updatedBar){
        if(updatedBar.getName() != null){
            currentBar.setName(updatedBar.getName());
        }

        if (updatedBar.getCity() != null) {
            currentBar.setCity(updatedBar.getCity());
        }

        if(updatedBar.getCapacity() != 0){
            currentBar.setCapacity(updatedBar.getCapacity());
        }

        if(updatedBar.getCocktails() != null){
            currentBar.setCocktails(updatedBar.getCocktails());
        }
    }

    /**
     * Deletes a bar based on its id.
     * @param ctx HTTP request information.
     */
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));

        if(bars.get(id) != null){
            bars.remove(id);
            ctx.status(204);
        }
        else {
            ctx.status(404).result("Bar can't be deleted because it doesn't exist.");
        }
    }
}
