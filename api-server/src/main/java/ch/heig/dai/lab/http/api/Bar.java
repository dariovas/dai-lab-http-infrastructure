package ch.heig.dai.lab.http.api;

import java.util.List;
import java.util.ArrayList;

public class Bar {
    private int id;
    private String name;
    private String city;
    private int capacity;
    private List<String> cocktails;

    public Bar(){}

    public Bar(int id, String name, String city, int capacity, List<String> cocktails) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.capacity = capacity;
        this.cocktails = new ArrayList<String> (cocktails);
    }

    public int getId(){
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getCocktails() {
        return cocktails;
    }

    public void setCocktails(List<String> cocktails){
        if(cocktails == null){
            throw new RuntimeException("The cocktails list is empty.");
        }

        this.cocktails = new ArrayList<>(cocktails);
    }

    public void addCocktail(String name){
        cocktails.add(name);
    }

    public void removeCocktail(String name){
        cocktails.remove(name);
    }
}
