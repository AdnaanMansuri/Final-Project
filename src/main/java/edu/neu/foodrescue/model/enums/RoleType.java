package edu.neu.foodrescue.model.enums;

public enum RoleType {
    SYSTEM_ADMIN("System Admin", null),
    SUPPLIER_MANAGER("Supplier Manager", EnterpriseType.SUPPLIER),
    STORE_STAFF("Store Staff", EnterpriseType.SUPPLIER),
    CHARITY_MANAGER("Charity Manager", EnterpriseType.CHARITY),
    CHARITY_COORDINATOR("Charity Coordinator", EnterpriseType.CHARITY);

    private final String label;
    private final EnterpriseType enterpriseType;

    RoleType(String label, EnterpriseType enterpriseType) {
        this.label = label;
        this.enterpriseType = enterpriseType;
    }

    /** Enterprise type this role works in; null for the platform-level System Admin. */
    public EnterpriseType getEnterpriseType() { return enterpriseType; }

    @Override
    public String toString() { return label; }
}
