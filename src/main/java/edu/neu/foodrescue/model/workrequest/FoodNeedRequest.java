package edu.neu.foodrescue.model.workrequest;

import edu.neu.foodrescue.model.enums.RequestType;

/** WR4 (inter-enterprise): Charity broadcasts a food need to a Supplier enterprise. */
public class FoodNeedRequest extends WorkRequest {

    private Integer foodNeedId;
    private String foodNeedSummary;
    private Integer charityOrgId;
    private String charityOrgName;

    @Override
    public RequestType getType() { return RequestType.FOOD_NEED; }

    @Override
    public boolean isInterEnterprise() { return true; }

    @Override
    public String getSummary() {
        return (charityOrgName == null ? "Charity" : charityOrgName) + " needs " + foodNeedSummary;
    }

    public Integer getFoodNeedId() { return foodNeedId; }
    public void setFoodNeedId(Integer foodNeedId) { this.foodNeedId = foodNeedId; }
    public String getFoodNeedSummary() { return foodNeedSummary; }
    public void setFoodNeedSummary(String foodNeedSummary) { this.foodNeedSummary = foodNeedSummary; }
    public Integer getCharityOrgId() { return charityOrgId; }
    public void setCharityOrgId(Integer charityOrgId) { this.charityOrgId = charityOrgId; }
    public String getCharityOrgName() { return charityOrgName; }
    public void setCharityOrgName(String charityOrgName) { this.charityOrgName = charityOrgName; }
}
