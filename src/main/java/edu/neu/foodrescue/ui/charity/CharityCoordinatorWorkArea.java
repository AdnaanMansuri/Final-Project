package edu.neu.foodrescue.ui.charity;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.PlaceholderPanel;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;

public class CharityCoordinatorWorkArea extends WorkAreaPanel {

    public CharityCoordinatorWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "Charity Coordinator");
        addTab("Available Food", new PlaceholderPanel("Browse and claim food"));
        addTab("Food Needs", new PlaceholderPanel("Post food needs (WR4)"));
        addTab("My Requests", new PlaceholderPanel("Track my requests"));
    }
}
