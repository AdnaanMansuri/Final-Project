package edu.neu.foodrescue.model;

import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.model.role.Role;

/** A person who logs in. Their Role decides which work area they see. */
public class UserAccount {

    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private RoleType roleType;
    private Integer organizationId;   // null for System Admin
    private String organizationName;
    private Integer enterpriseId;     // derived from organization
    private String enterpriseName;

    public UserAccount() { }

    /** Polymorphic role object built from the stored role type. */
    public Role getRole() {
        return Role.of(roleType);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }
    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public Integer getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Integer enterpriseId) { this.enterpriseId = enterpriseId; }
    public String getEnterpriseName() { return enterpriseName; }
    public void setEnterpriseName(String enterpriseName) { this.enterpriseName = enterpriseName; }

    @Override
    public String toString() { return fullName + " (" + username + ")"; }
}
