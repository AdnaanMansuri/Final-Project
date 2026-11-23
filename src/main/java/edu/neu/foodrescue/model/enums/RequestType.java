package edu.neu.foodrescue.model.enums;

public enum RequestType {
    DONATION_LISTING("Donation Listing"),
    CLAIM_APPROVAL("Claim Approval"),
    FOOD_CLAIM("Food Claim"),
    FOOD_NEED("Food Need");

    private final String label;

    RequestType(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
