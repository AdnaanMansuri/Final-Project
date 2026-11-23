package edu.neu.foodrescue.model.workrequest;

import edu.neu.foodrescue.model.enums.RequestType;

/** WR1 (intra-enterprise): Store Staff asks Supplier Manager to publish a surplus item. */
public class DonationListingRequest extends FoodItemRequest {

    @Override
    public RequestType getType() { return RequestType.DONATION_LISTING; }

    @Override
    public boolean isInterEnterprise() { return false; }

    @Override
    public String getSummary() {
        return "Publish listing: " + getFoodItemName();
    }
}
