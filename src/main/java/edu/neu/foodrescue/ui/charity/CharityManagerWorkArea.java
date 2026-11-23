package edu.neu.foodrescue.ui.charity;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.PlaceholderPanel;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;

public class CharityManagerWorkArea extends WorkAreaPanel {

    public CharityManagerWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "Charity Manager");
        addTab("Claim Approvals", new PlaceholderPanel("Approve coordinator claims (WR2)"));
        addTab("Impact Dashboard", new PlaceholderPanel("Impact dashboard"));
        addTab("Coordinator Accounts", new PlaceholderPanel("Manage coordinators"));
    }
}
