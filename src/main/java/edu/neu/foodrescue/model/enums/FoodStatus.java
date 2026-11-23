package edu.neu.foodrescue.model.enums;

public enum FoodStatus {
    PENDING_APPROVAL("Pending Approval"),
    AVAILABLE("Available"),
    CLAIMED("Claimed"),
    PICKED_UP("Picked Up"),
    EXPIRED("Expired"),
    REJECTED("Rejected");

    private final String label;

    FoodStatus(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
