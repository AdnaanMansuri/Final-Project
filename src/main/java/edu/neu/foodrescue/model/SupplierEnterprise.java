package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.EnterpriseType;

/** Bakeries and grocery stores that donate surplus food. */
public class SupplierEnterprise extends Enterprise {

    public SupplierEnterprise(int id, String name) {
        super(id, name);
    }

    @Override
    public EnterpriseType getType() { return EnterpriseType.SUPPLIER; }

    @Override
    public String getContribution() {
        return "Provides surplus food before it goes to waste.";
    }
}
