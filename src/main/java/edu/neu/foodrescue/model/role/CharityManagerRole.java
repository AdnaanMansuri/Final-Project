package edu.neu.foodrescue.model.role;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.charity.CharityManagerWorkArea;
import javax.swing.JPanel;

public class CharityManagerRole extends Role {

    @Override
    public RoleType getType() { return RoleType.CHARITY_MANAGER; }

    @Override
    public boolean isAdmin() { return true; }

    @Override
    public JPanel createWorkArea(MainFrame frame, UserAccount user) {
        return new CharityManagerWorkArea(frame, user);
    }
}
