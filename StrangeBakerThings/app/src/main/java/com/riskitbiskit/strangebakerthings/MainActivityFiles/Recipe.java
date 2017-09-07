package com.riskitbiskit.strangebakerthings.MainActivityFiles;

public class Recipe {
    int id;
    String name;

    public Recipe(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
