package edu.neu.foodrescue.ui.admin;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.PlaceholderPanel;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;

public class SystemAdminWorkArea extends WorkAreaPanel {

    public SystemAdminWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "System Administration");
        addTab("Enterprises", new PlaceholderPanel("Manage enterprises"));
        addTab("Organizations", new PlaceholderPanel("Manage organizations"));
        addTab("User Accounts", new PlaceholderPanel("Manage admin accounts"));
        addTab("Network Overview", new PlaceholderPanel("Network overview"));
    }
}
