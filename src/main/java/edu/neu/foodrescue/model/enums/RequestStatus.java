package edu.neu.foodrescue.model.enums;

public enum RequestStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    EXPIRED("Expired");

    private final String label;

    RequestStatus(String label) { this.label = label; }

    public boolean isOpen() { return this == PENDING || this == ACCEPTED; }

    @Override
    public String toString() { return label; }
}
