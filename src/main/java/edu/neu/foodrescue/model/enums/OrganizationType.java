package edu.neu.foodrescue.model.enums;

public enum OrganizationType {
    BAKERY("Bakery", EnterpriseType.SUPPLIER),
    GROCERY_STORE("Grocery Store", EnterpriseType.SUPPLIER),
    FOOD_BANK("Food Bank", EnterpriseType.CHARITY),
    SHELTER("Shelter", EnterpriseType.CHARITY);

    private final String label;
    private final EnterpriseType enterpriseType;

    OrganizationType(String label, EnterpriseType enterpriseType) {
        this.label = label;
        this.enterpriseType = enterpriseType;
    }

    /** Which kind of enterprise this organization type may belong to. */
    public EnterpriseType getEnterpriseType() { return enterpriseType; }

    @Override
    public String toString() { return label; }
}
