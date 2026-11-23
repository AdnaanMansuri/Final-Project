package edu.neu.foodrescue.ui.supplier;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.PlaceholderPanel;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;

public class SupplierManagerWorkArea extends WorkAreaPanel {

    public SupplierManagerWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "Supplier Manager");
        addTab("Listing Approvals", new PlaceholderPanel("Approve donation listings (WR1)"));
        addTab("Food Claims", new PlaceholderPanel("Accept charity claims (WR3)"));
        addTab("Food Needs", new PlaceholderPanel("Respond to food needs (WR4)"));
        addTab("Staff Accounts", new PlaceholderPanel("Manage store staff"));
    }
}
