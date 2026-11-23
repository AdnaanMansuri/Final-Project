package edu.neu.foodrescue.ui.admin;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;
import java.util.Arrays;

public class SystemAdminWorkArea extends WorkAreaPanel {

    public SystemAdminWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "System Administration");
        addTab("Network Overview", new NetworkOverviewPanel());
        addTab("Enterprises", new EnterprisePanel());
        addTab("Organizations", new OrganizationPanel());
        addTab("User Accounts", new UserAccountPanel("All User Accounts", user,
                Arrays.asList(RoleType.values()), null));
    }
}
