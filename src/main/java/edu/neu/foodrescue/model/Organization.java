package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.OrganizationType;

/** A single bakery, grocery store, food bank or shelter inside an enterprise. */
public class Organization {

    private int id;
    private int enterpriseId;
    private String enterpriseName;
    private String name;
    private OrganizationType type;
    private String address;

    public Organization() { }

    public Organization(int enterpriseId, String name, OrganizationType type, String address) {
        this.enterpriseId = enterpriseId;
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(int enterpriseId) { this.enterpriseId = enterpriseId; }
    public String getEnterpriseName() { return enterpriseName; }
    public void setEnterpriseName(String enterpriseName) { this.enterpriseName = enterpriseName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public OrganizationType getType() { return type; }
    public void setType(OrganizationType type) { this.type = type; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() { return name; }
}
