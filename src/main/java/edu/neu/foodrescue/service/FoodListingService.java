package edu.neu.foodrescue.service;

import edu.neu.foodrescue.dao.FoodItemDao;
import edu.neu.foodrescue.dao.WorkRequestDao;
import edu.neu.foodrescue.model.FoodItem;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.FoodStatus;
import edu.neu.foodrescue.model.enums.RequestStatus;
import edu.neu.foodrescue.model.enums.RequestType;
import edu.neu.foodrescue.model.workrequest.DonationListingRequest;
import edu.neu.foodrescue.model.workrequest.FoodClaimRequest;
import edu.neu.foodrescue.model.workrequest.WorkRequest;
import edu.neu.foodrescue.util.DataAccessException;
import java.time.LocalDateTime;

/**
 * Business rules for surplus food listings and WR1 (Donation Listing).
 * Rule violations throw IllegalStateException with a message meant for the user.
 */
public class FoodListingService {

    private final FoodItemDao foodDao = new FoodItemDao();
    private final WorkRequestDao requestDao = new WorkRequestDao();

    public static boolean isEditable(FoodItem f) {
        return f.getStatus() == FoodStatus.PENDING_APPROVAL
                || f.getStatus() == FoodStatus.AVAILABLE
                || f.getStatus() == FoodStatus.REJECTED;
    }

    public static boolean isDeletable(FoodItem f) {
        return f.getStatus() != FoodStatus.CLAIMED && f.getStatus() != FoodStatus.PICKED_UP;
    }

    /** Store Staff posts an item: it is saved as PENDING_APPROVAL and WR1 goes to the Supplier Managers. */
    public void postListing(UserAccount staff, FoodItem item) {
        item.setSupplierOrgId(staff.getOrganizationId());
        item.setPostedById(staff.getId());
        item.setStatus(FoodStatus.PENDING_APPROVAL);
        foodDao.create(item);
        try {
            submitForApproval(staff, item, "New listing");
        } catch (DataAccessException e) {
            foodDao.delete(item.getId()); // keep data consistent if the request could not be saved
            throw e;
        }
    }

    /**
     * Editing an approved or rejected listing sends it back for approval (new WR1),
     * so nothing reaches charities without a manager seeing the final version.
     */
    public void editListing(UserAccount staff, FoodItem item) {
        if (!isEditable(item)) {
            throw new IllegalStateException("A " + item.getStatus() + " listing can no longer be changed.");
        }
        boolean resubmit = item.getStatus() != FoodStatus.PENDING_APPROVAL;
        item.setStatus(FoodStatus.PENDING_APPROVAL);
        foodDao.update(item);
        if (resubmit) {
            submitForApproval(staff, item, "Resubmitted after changes");
        }
    }

    public void deleteListing(FoodItem item) {
        if (!isDeletable(item)) {
            throw new IllegalStateException("A " + item.getStatus() + " listing cannot be deleted; a charity is relying on it.");
        }
        foodDao.delete(item.getId()); // its work requests are removed by ON DELETE CASCADE
    }

    private void submitForApproval(UserAccount staff, FoodItem item, String message) {
        DonationListingRequest r = new DonationListingRequest();
        r.setSenderId(staff.getId());
        r.setTargetEnterpriseId(staff.getEnterpriseId());
        r.setFoodItemId(item.getId());
        r.setMessage(message);
        requestDao.create(r);
    }

    /** Supplier Manager approves WR1: the item becomes visible to charities. */
    public void approveListing(UserAccount manager, WorkRequest request, String note) {
        FoodItem item = loadItem(request);
        if (item.isExpired()) {
            foodDao.updateStatus(item.getId(), FoodStatus.EXPIRED);
            request.setStatus(RequestStatus.EXPIRED);
            request.setResolveDate(LocalDateTime.now());
            requestDao.update(request);
            throw new IllegalStateException("The pickup deadline has already passed, so the listing was marked expired.");
        }
        request.approve(note);
        request.setReceiverId(manager.getId());
        requestDao.update(request);
        foodDao.updateStatus(item.getId(), FoodStatus.AVAILABLE);
    }

    /** Supplier Manager rejects WR1: staff can edit and resubmit. */
    public void rejectListing(UserAccount manager, WorkRequest request, String reason) {
        FoodItem item = loadItem(request);
        request.reject(reason);
        request.setReceiverId(manager.getId());
        requestDao.update(request);
        foodDao.updateStatus(item.getId(), FoodStatus.REJECTED);
    }

    /** Store Staff hands the food to the charity: WR3 is completed and the item counts as rescued. */
    public void markPickedUp(FoodItem item) {
        if (item.getStatus() != FoodStatus.CLAIMED) {
            throw new IllegalStateException("Only claimed items can be marked as picked up.");
        }
        FoodClaimRequest claim = findAcceptedClaim(item.getId());
        if (claim == null) {
            throw new IllegalStateException("No accepted claim was found for this item.");
        }
        claim.complete("Picked up");
        requestDao.update(claim);
        foodDao.updateStatus(item.getId(), FoodStatus.PICKED_UP);
    }

    public FoodClaimRequest findAcceptedClaim(int foodItemId) {
        for (WorkRequest r : requestDao.findByFoodItem(foodItemId, RequestType.FOOD_CLAIM)) {
            if (r.getStatus() == RequestStatus.ACCEPTED && r instanceof FoodClaimRequest fc) {
                return fc;
            }
        }
        return null;
    }

    private FoodItem loadItem(WorkRequest request) {
        if (!(request instanceof DonationListingRequest dl) || dl.getFoodItemId() == null) {
            throw new IllegalStateException("This is not a donation listing request.");
        }
        FoodItem item = foodDao.findById(dl.getFoodItemId());
        if (item == null) {
            throw new IllegalStateException("The listing no longer exists.");
        }
        return item;
    }
}
