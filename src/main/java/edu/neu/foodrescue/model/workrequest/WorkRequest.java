package edu.neu.foodrescue.model.workrequest;

import edu.neu.foodrescue.model.Approvable;
import edu.neu.foodrescue.model.enums.RequestStatus;
import edu.neu.foodrescue.model.enums.RequestType;
import java.time.LocalDateTime;

/**
 * Base class for the 4 work requests that move between users, organizations and enterprises.
 * Lifecycle: PENDING -> ACCEPTED -> COMPLETED, or REJECTED / EXPIRED.
 */
public abstract class WorkRequest implements Approvable {

    private int id;
    private int senderId;
    private String senderName;
    private Integer receiverId;          // null = any admin of the target enterprise/org
    private String receiverName;
    private Integer targetEnterpriseId;
    private RequestStatus status = RequestStatus.PENDING;
    private String message;
    private LocalDateTime requestDate;
    private LocalDateTime resolveDate;

    public abstract RequestType getType();

    /** One-line description shown in request tables. */
    public abstract String getSummary();

    /** True if the request crosses from one enterprise to another. */
    public abstract boolean isInterEnterprise();

    @Override
    public void approve(String note) {
        requireStatus(RequestStatus.PENDING);
        status = RequestStatus.ACCEPTED;
        appendNote(note);
        resolveDate = LocalDateTime.now();
    }

    @Override
    public void reject(String reason) {
        requireStatus(RequestStatus.PENDING);
        status = RequestStatus.REJECTED;
        appendNote(reason);
        resolveDate = LocalDateTime.now();
    }

    public void complete(String note) {
        if (!status.isOpen()) {
            throw new IllegalStateException("Request is already " + status);
        }
        status = RequestStatus.COMPLETED;
        appendNote(note);
        resolveDate = LocalDateTime.now();
    }

    private void requireStatus(RequestStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Request is " + status + ", expected " + expected);
        }
    }

    private void appendNote(String note) {
        if (note != null && !note.isBlank()) {
            message = (message == null || message.isBlank()) ? note.trim() : message + " | " + note.trim();
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public Integer getTargetEnterpriseId() { return targetEnterpriseId; }
    public void setTargetEnterpriseId(Integer targetEnterpriseId) { this.targetEnterpriseId = targetEnterpriseId; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    public LocalDateTime getResolveDate() { return resolveDate; }
    public void setResolveDate(LocalDateTime resolveDate) { this.resolveDate = resolveDate; }

    /** Factory used by the DAO to create the right subclass from the type column. */
    public static WorkRequest of(RequestType type) {
        return switch (type) {
            case DONATION_LISTING -> new DonationListingRequest();
            case CLAIM_APPROVAL -> new ClaimApprovalRequest();
            case FOOD_CLAIM -> new FoodClaimRequest();
            case FOOD_NEED -> new FoodNeedRequest();
        };
    }
}
