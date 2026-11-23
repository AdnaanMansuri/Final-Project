package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.EnterpriseType;
import edu.neu.foodrescue.model.enums.OrganizationType;

/**
 * An enterprise in the Food Rescue Network (e.g. a group of bakeries/stores, or a charity network).
 * Concrete subclasses decide which organization types they may contain.
 */
public abstract class Enterprise {

    private int id;
    private String name;

    protected Enterprise(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract EnterpriseType getType();

    /** What this enterprise contributes to the network (shown on dashboards / admin screens). */
    public abstract String getContribution();

    /** Polymorphic rule: only matching organization types can join this enterprise. */
    public boolean accepts(OrganizationType orgType) {
        return orgType.getEnterpriseType() == getType();
    }

    /** Factory: builds the right subclass for a type loaded from the database. */
    public static Enterprise of(int id, String name, EnterpriseType type) {
        return switch (type) {
            case SUPPLIER -> new SupplierEnterprise(id, name);
            case CHARITY -> new CharityEnterprise(id, name);
        };
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
