package edu.neu.foodrescue.model.workrequest;

/** Shared parent for the requests that are about one food item (WR1, WR2, WR3). */
public abstract class FoodItemRequest extends WorkRequest {

    private Integer foodItemId;
    private String foodItemName;

    public Integer getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Integer foodItemId) { this.foodItemId = foodItemId; }
    public String getFoodItemName() { return foodItemName; }
    public void setFoodItemName(String foodItemName) { this.foodItemName = foodItemName; }
}
