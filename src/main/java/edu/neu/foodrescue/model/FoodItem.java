package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.FoodCategory;
import edu.neu.foodrescue.model.enums.FoodStatus;
import java.time.LocalDateTime;

/** A surplus food listing posted by a supplier. */
public class FoodItem implements Expirable {

    private int id;
    private int supplierOrgId;
    private String supplierOrgName;
    private int postedById;
    private String postedByName;
    private String name;
    private FoodCategory category;
    private double quantity;
    private String unit;
    private String allergens;
    private LocalDateTime pickupDeadline;
    private FoodStatus status = FoodStatus.PENDING_APPROVAL;
    private LocalDateTime createdAt;

    public FoodItem() { }

    @Override
    public LocalDateTime getDeadline() { return pickupDeadline; }

    /** Rough estimate used by the impact dashboard: 1 kg (or unit) feeds about 2 people. */
    public int estimatedMeals() {
        return (int) Math.round(quantity * 2);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSupplierOrgId() { return supplierOrgId; }
    public void setSupplierOrgId(int supplierOrgId) { this.supplierOrgId = supplierOrgId; }
    public String getSupplierOrgName() { return supplierOrgName; }
    public void setSupplierOrgName(String supplierOrgName) { this.supplierOrgName = supplierOrgName; }
    public int getPostedById() { return postedById; }
    public void setPostedById(int postedById) { this.postedById = postedById; }
    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }
    public LocalDateTime getPickupDeadline() { return pickupDeadline; }
    public void setPickupDeadline(LocalDateTime pickupDeadline) { this.pickupDeadline = pickupDeadline; }
    public FoodStatus getStatus() { return status; }
    public void setStatus(FoodStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return name + " - " + quantity + " " + unit; }
}
