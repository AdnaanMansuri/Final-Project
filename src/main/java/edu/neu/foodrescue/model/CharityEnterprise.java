package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.EnterpriseType;

/** Food banks and shelters that claim, collect and distribute rescued food. */
public class CharityEnterprise extends Enterprise {

    public CharityEnterprise(int id, String name) {
        super(id, name);
    }

    @Override
    public EnterpriseType getType() { return EnterpriseType.CHARITY; }

    @Override
    public String getContribution() {
        return "Collects rescued food and turns it into meals for people in need.";
    }
}
