package edu.neu.foodrescue.model.enums;

public enum FoodCategory {
    BAKERY("Bakery"),
    PRODUCE("Produce"),
    DAIRY("Dairy"),
    PREPARED_MEAL("Prepared Meal"),
    PACKAGED("Packaged");

    private final String label;

    FoodCategory(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
