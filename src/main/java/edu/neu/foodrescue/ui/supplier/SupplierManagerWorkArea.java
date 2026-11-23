package edu.neu.foodrescue.ui.supplier;

import edu.neu.foodrescue.dao.WorkRequestDao;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RequestType;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.model.workrequest.FoodItemRequest;
import edu.neu.foodrescue.service.FoodListingService;
import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.admin.UserAccountPanel;
import edu.neu.foodrescue.ui.common.PlaceholderPanel;
import edu.neu.foodrescue.ui.common.RequestInboxPanel;
import edu.neu.foodrescue.ui.common.WorkAreaPanel;
import edu.neu.foodrescue.util.Formats;
import java.util.List;

public class SupplierManagerWorkArea extends WorkAreaPanel {

    public SupplierManagerWorkArea(MainFrame frame, UserAccount user) {
        super(frame, user, "Supplier Manager");
        WorkRequestDao requestDao = new WorkRequestDao();
        FoodListingService listingService = new FoodListingService();

        // WR1: Store Staff -> Supplier Manager (intra-enterprise)
        addTab("Listing Approvals", new RequestInboxPanel(
                "Donation listings waiting for approval (WR1)", "Food details", "Approve",
                () -> requestDao.findInbox(user.getId(), user.getEnterpriseId(), RequestType.DONATION_LISTING),
                r -> r instanceof FoodItemRequest fr
                        ? fr.getFoodAmount() + " from " + fr.getSupplierOrgName()
                          + ", pickup by " + Formats.dateTime(fr.getFoodDeadline())
                        : "",
                (r, note) -> listingService.approveListing(user, r, note),
                (r, note) -> listingService.rejectListing(user, r, note)));

        addTab("Food Claims", new PlaceholderPanel("Accept charity claims (WR3)"));
        addTab("Food Needs", new PlaceholderPanel("Respond to food needs (WR4)"));

        addTab("Staff Accounts", new UserAccountPanel("Store Staff in " + user.getEnterpriseName(), user,
                List.of(RoleType.STORE_STAFF), user.getEnterpriseId()));
    }
}
