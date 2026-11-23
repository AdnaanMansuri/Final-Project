package edu.neu.foodrescue.model.workrequest;

import edu.neu.foodrescue.model.enums.RequestType;
import java.time.LocalDateTime;

/** WR3 (inter-enterprise): Charity asks the Supplier to reserve an item for pickup. */
public class FoodClaimRequest extends FoodItemRequest {

    private Integer charityOrgId;
    private String charityOrgName;
    private LocalDateTime pickupTime;

    @Override
    public RequestType getType() { return RequestType.FOOD_CLAIM; }

    @Override
    public boolean isInterEnterprise() { return true; }

    @Override
    public String getSummary() {
        return (charityOrgName == null ? "Charity" : charityOrgName) + " claims " + getFoodItemName();
    }

    public Integer getCharityOrgId() { return charityOrgId; }
    public void setCharityOrgId(Integer charityOrgId) { this.charityOrgId = charityOrgId; }
    public String getCharityOrgName() { return charityOrgName; }
    public void setCharityOrgName(String charityOrgName) { this.charityOrgName = charityOrgName; }
    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }
}
