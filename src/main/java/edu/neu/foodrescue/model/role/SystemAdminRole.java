package edu.neu.foodrescue.model.role;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.admin.SystemAdminWorkArea;
import javax.swing.JPanel;

public class SystemAdminRole extends Role {

    @Override
    public RoleType getType() { return RoleType.SYSTEM_ADMIN; }

    @Override
    public boolean isAdmin() { return true; }

    @Override
    public JPanel createWorkArea(MainFrame frame, UserAccount user) {
        return new SystemAdminWorkArea(frame, user);
    }
}
