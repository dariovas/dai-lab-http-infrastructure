package ch.heig.dai.lab.http.api;

import io.javalin.*;

public class Server {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7001);

        BarController barController = new BarController();

        // Creates methods
        app.post("/api/bars", barController::create);
        app.post("/api/bars/{id}/cocktails/add", barController::addBarCocktails);

        // Read methods
        app.get("/api/bars", barController::getAll);
        app.get("/api/bars/{id}", barController::getOne);
        app.get("/api/bars/{id}/cocktails", barController::getBarCocktails);

        // Update method
        app.put("/api/bars/{id}", barController::update);

        // Delete methods
        app.delete("/api/bars/{id}/cocktails/del", barController::removeBarCocktails);
        app.delete("/api/bars/{id}", barController::delete);
    }
}
