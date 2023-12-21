package ch.heig.dai.lab.http.APIServer;

import io.javalin.*;

public class API {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7001);

        BarController barController = new BarController();
        app.get("/api/bars", barController::getAll);
        app.get("/api/bars/{id}", barController::getOne);
        app.get("/api/bars/{id}/cocktails", barController::getBarCocktails);

        app.post("/api/bars", barController::create);
        app.post("/api/bars/{id}/cocktails/add", barController::addBarCocktails);

        app.put("/api/bars/{id}", barController::update);
        app.delete("/api/bars/{id}", barController::delete);

    }
}
