package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.FoodCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** A charity's request for a kind of food it needs by a certain date. */
public class FoodNeed implements Expirable {

    private int id;
    private int charityOrgId;
    private String charityOrgName;
    private FoodCategory category;
    private double quantity;
    private String description;
    private LocalDate neededBy;

    public FoodNeed() { }

    @Override
    public LocalDateTime getDeadline() {
        return neededBy == null ? null : LocalDateTime.of(neededBy, LocalTime.MAX);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCharityOrgId() { return charityOrgId; }
    public void setCharityOrgId(int charityOrgId) { this.charityOrgId = charityOrgId; }
    public String getCharityOrgName() { return charityOrgName; }
    public void setCharityOrgName(String charityOrgName) { this.charityOrgName = charityOrgName; }
    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getNeededBy() { return neededBy; }
    public void setNeededBy(LocalDate neededBy) { this.neededBy = neededBy; }

    @Override
    public String toString() { return category + " x " + quantity + " by " + neededBy; }
}
