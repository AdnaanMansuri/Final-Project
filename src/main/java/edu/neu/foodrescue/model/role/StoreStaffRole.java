package edu.neu.foodrescue.model.role;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.supplier.StoreStaffWorkArea;
import javax.swing.JPanel;

public class StoreStaffRole extends Role {

    @Override
    public RoleType getType() { return RoleType.STORE_STAFF; }

    @Override
    public boolean isAdmin() { return false; }

    @Override
    public JPanel createWorkArea(MainFrame frame, UserAccount user) {
        return new StoreStaffWorkArea(frame, user);
    }
}
