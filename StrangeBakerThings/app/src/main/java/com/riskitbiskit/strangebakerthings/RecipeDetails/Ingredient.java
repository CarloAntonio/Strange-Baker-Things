package com.riskitbiskit.strangebakerthings.RecipeDetails;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient {
    double quantity;
    String measurement;
    String ingredient;

    public Ingredient(double quantity, String measurement, String ingredient) {
        this.quantity = quantity;
        this.measurement = measurement;
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getIngredient() {
        return ingredient;
    }
}
