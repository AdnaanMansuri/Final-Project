package edu.neu.foodrescue.ui.supplier;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;

public class StoreStaffWorkArea extends WorkAreaPanel {

    public StoreStaffWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "Store Staff");
        addTab("My Listings", new MyListingsPanel(user));
        addTab("Pickups", new PickupsPanel(user));
    }
}
