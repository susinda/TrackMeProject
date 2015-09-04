package com.example.susinda.carapp;

/**
 * Created by susinda on 6/15/15.
 */
public class Configuration {
    private String model;
    private String engine;
    private String color;
    private String interior;
    private String exterior;


    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getEngine() {
        return engine;
    }
    public void setEngine(String engine) {
        this.engine = engine;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getInterior() {
        return interior;
    }
    public void setInterior(String interior) {
        this.interior = interior;
    }
    public String getExterior() {
        return exterior;
    }
    public void setExterior(String exterior) {
        this.exterior = exterior;
    }

    public Configuration(String model, String engine, String color, String interior, String exterior) {
        this.model = model;
        this.color = color;
        this.engine = engine;
        this.interior = interior;
        this.exterior = exterior;
    }

    public Configuration() {
    }

}
