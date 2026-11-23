package edu.neu.foodrescue.model.workrequest;

import java.time.LocalDateTime;

/** Shared parent for the requests that are about one food item (WR1, WR2, WR3). */
public abstract class FoodItemRequest extends WorkRequest {

    private Integer foodItemId;
    private String foodItemName;
    // read-only details joined from food_item, for display in request tables
    private double foodQuantity;
    private String foodUnit;
    private LocalDateTime foodDeadline;
    private String supplierOrgName;

    /** e.g. "12 loaves" */
    public String getFoodAmount() {
        if (foodUnit == null) {
            return "";
        }
        String q = foodQuantity == Math.rint(foodQuantity) ? String.valueOf((long) foodQuantity) : String.valueOf(foodQuantity);
        return q + " " + foodUnit;
    }

    public Integer getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Integer foodItemId) { this.foodItemId = foodItemId; }
    public String getFoodItemName() { return foodItemName; }
    public void setFoodItemName(String foodItemName) { this.foodItemName = foodItemName; }
    public double getFoodQuantity() { return foodQuantity; }
    public void setFoodQuantity(double foodQuantity) { this.foodQuantity = foodQuantity; }
    public String getFoodUnit() { return foodUnit; }
    public void setFoodUnit(String foodUnit) { this.foodUnit = foodUnit; }
    public LocalDateTime getFoodDeadline() { return foodDeadline; }
    public void setFoodDeadline(LocalDateTime foodDeadline) { this.foodDeadline = foodDeadline; }
    public String getSupplierOrgName() { return supplierOrgName; }
    public void setSupplierOrgName(String supplierOrgName) { this.supplierOrgName = supplierOrgName; }
}
