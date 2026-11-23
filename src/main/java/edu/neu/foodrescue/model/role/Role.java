package edu.neu.foodrescue.model.role;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.MainFrame;
import javax.swing.JPanel;

/**
 * Base class for every role. Each subclass builds its own work area,
 * so after login the app simply calls user.getRole().createWorkArea(...) (polymorphism).
 */
public abstract class Role {

    public abstract RoleType getType();

    public abstract boolean isAdmin();

    public abstract JPanel createWorkArea(MainFrame frame, UserAccount user);

    public String getName() {
        return getType().toString();
    }

    /** Factory: maps the role stored in the database to its Role object. */
    public static Role of(RoleType type) {
        return switch (type) {
            case SYSTEM_ADMIN -> new SystemAdminRole();
            case SUPPLIER_MANAGER -> new SupplierManagerRole();
            case STORE_STAFF -> new StoreStaffRole();
            case CHARITY_MANAGER -> new CharityManagerRole();
            case CHARITY_COORDINATOR -> new CharityCoordinatorRole();
        };
    }

    @Override
    public String toString() { return getName(); }
}
