package edu.neu.foodrescue.model.workrequest;

import edu.neu.foodrescue.model.enums.RequestType;
import java.time.LocalDateTime;

/** WR2 (intra-enterprise): Charity Coordinator asks Charity Manager to approve claiming an item. */
public class ClaimApprovalRequest extends FoodItemRequest {

    private Integer charityOrgId;
    private LocalDateTime pickupTime;

    @Override
    public RequestType getType() { return RequestType.CLAIM_APPROVAL; }

    @Override
    public boolean isInterEnterprise() { return false; }

    @Override
    public String getSummary() {
        return "Approve claim: " + getFoodItemName();
    }

    public Integer getCharityOrgId() { return charityOrgId; }
    public void setCharityOrgId(Integer charityOrgId) { this.charityOrgId = charityOrgId; }
    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }
}
