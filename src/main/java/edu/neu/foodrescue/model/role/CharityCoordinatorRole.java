package edu.neu.foodrescue.model.role;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.charity.CharityCoordinatorWorkArea;
import javax.swing.JPanel;

public class CharityCoordinatorRole extends Role {

    @Override
    public RoleType getType() { return RoleType.CHARITY_COORDINATOR; }

    @Override
    public boolean isAdmin() { return false; }

    @Override
    public JPanel createWorkArea(MainFrame frame, UserAccount user) {
        return new CharityCoordinatorWorkArea(frame, user);
    }
}
