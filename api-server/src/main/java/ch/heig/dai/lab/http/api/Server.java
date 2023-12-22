package ch.heig.dai.lab.http.api;

import io.javalin.*;

public class Server {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7001);

        BarController barController = new BarController();

        app.get("/api/bars", barController::getAll);
        app.get("/api/bars/{id}", barController::getOne);
        app.get("/api/bars/{id}/cocktails", barController::getBarCocktails);

        app.post("/api/bars", barController::create);
        app.post("/api/bars/{id}/cocktails/add", barController::addBarCocktails);
        app.delete("/api/bars/{id}/cocktails/del", barController::removeBarCocktails);

        app.put("/api/bars/{id}", barController::update);
        app.delete("/api/bars/{id}", barController::delete);

    }
}
