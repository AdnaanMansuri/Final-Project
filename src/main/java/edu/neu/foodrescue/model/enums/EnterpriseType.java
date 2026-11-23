package edu.neu.foodrescue.model.enums;

public enum EnterpriseType {
    SUPPLIER("Food Supplier"),
    CHARITY("Charity / Community");

    private final String label;

    EnterpriseType(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
