package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.model.enums.FoodCategory;
import edu.neu.foodrescue.model.enums.RequestStatus;
import edu.neu.foodrescue.model.enums.RequestType;
import edu.neu.foodrescue.model.workrequest.ClaimApprovalRequest;
import edu.neu.foodrescue.model.workrequest.FoodClaimRequest;
import edu.neu.foodrescue.model.workrequest.FoodItemRequest;
import edu.neu.foodrescue.model.workrequest.FoodNeedRequest;
import edu.neu.foodrescue.model.workrequest.WorkRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * All 4 request types live in one table (single-table inheritance).
 * The type column tells the DAO which subclass to build.
 */
public class WorkRequestDao extends BaseDao<WorkRequest> {

    private static final String SELECT =
            "SELECT w.*, s.full_name AS sender_name, r.full_name AS receiver_name, "
            + "f.name AS food_name, f.quantity AS food_qty, f.unit AS food_unit, "
            + "f.pickup_deadline AS food_deadline, so.name AS supplier_name, "
            + "n.category AS need_category, n.quantity AS need_qty, "
            + "c.name AS charity_name "
            + "FROM work_request w "
            + "JOIN user_account s ON s.id = w.sender_id "
            + "LEFT JOIN user_account r ON r.id = w.receiver_id "
            + "LEFT JOIN food_item f ON f.id = w.food_item_id "
            + "LEFT JOIN organization so ON so.id = f.supplier_org_id "
            + "LEFT JOIN food_need n ON n.id = w.food_need_id "
            + "LEFT JOIN organization c ON c.id = w.charity_org_id ";

    @Override
    protected WorkRequest mapRow(ResultSet rs) throws SQLException {
        WorkRequest w = WorkRequest.of(RequestType.valueOf(rs.getString("type")));
        w.setId(rs.getInt("id"));
        w.setSenderId(rs.getInt("sender_id"));
        w.setSenderName(rs.getString("sender_name"));
        w.setReceiverId(getNullableInt(rs, "receiver_id"));
        w.setReceiverName(rs.getString("receiver_name"));
        w.setTargetEnterpriseId(getNullableInt(rs, "target_enterprise_id"));
        w.setStatus(RequestStatus.valueOf(rs.getString("status")));
        w.setMessage(rs.getString("message"));
        w.setRequestDate(getTime(rs, "request_date"));
        w.setResolveDate(getTime(rs, "resolve_date"));

        if (w instanceof FoodItemRequest fr) {
            fr.setFoodItemId(getNullableInt(rs, "food_item_id"));
            fr.setFoodItemName(rs.getString("food_name"));
            fr.setFoodQuantity(rs.getDouble("food_qty"));
            fr.setFoodUnit(rs.getString("food_unit"));
            fr.setFoodDeadline(getTime(rs, "food_deadline"));
            fr.setSupplierOrgName(rs.getString("supplier_name"));
        }
        if (w instanceof ClaimApprovalRequest ca) {
            ca.setCharityOrgId(getNullableInt(rs, "charity_org_id"));
            ca.setPickupTime(getTime(rs, "pickup_time"));
        } else if (w instanceof FoodClaimRequest fc) {
            fc.setCharityOrgId(getNullableInt(rs, "charity_org_id"));
            fc.setCharityOrgName(rs.getString("charity_name"));
            fc.setPickupTime(getTime(rs, "pickup_time"));
        } else if (w instanceof FoodNeedRequest fn) {
            fn.setFoodNeedId(getNullableInt(rs, "food_need_id"));
            fn.setCharityOrgId(getNullableInt(rs, "charity_org_id"));
            fn.setCharityOrgName(rs.getString("charity_name"));
            String cat = rs.getString("need_category");
            if (cat != null) {
                fn.setFoodNeedSummary(rs.getDouble("need_qty") + " x " + FoodCategory.valueOf(cat));
            }
        }
        return w;
    }

    /** Writes the subclass-specific columns (positions 4-8 in INSERT, 1-5 in UPDATE prefix). */
    private static void bindSpecific(PreparedStatement ps, int start, WorkRequest w) throws SQLException {
        Integer foodItemId = null;
        Integer foodNeedId = null;
        Integer charityOrgId = null;
        java.time.LocalDateTime pickup = null;

        if (w instanceof FoodItemRequest fr) {
            foodItemId = fr.getFoodItemId();
        }
        if (w instanceof ClaimApprovalRequest ca) {
            charityOrgId = ca.getCharityOrgId();
            pickup = ca.getPickupTime();
        } else if (w instanceof FoodClaimRequest fc) {
            charityOrgId = fc.getCharityOrgId();
            pickup = fc.getPickupTime();
        } else if (w instanceof FoodNeedRequest fn) {
            foodNeedId = fn.getFoodNeedId();
            charityOrgId = fn.getCharityOrgId();
        }
        setNullableInt(ps, start, foodItemId);
        setNullableInt(ps, start + 1, foodNeedId);
        setNullableInt(ps, start + 2, charityOrgId);
        setNullableTime(ps, start + 3, pickup);
    }

    @Override
    public int create(WorkRequest w) {
        int id = insert("INSERT INTO work_request (type, sender_id, receiver_id, target_enterprise_id, "
                + "food_item_id, food_need_id, charity_org_id, pickup_time, status, message) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", ps -> {
            ps.setString(1, w.getType().name());
            ps.setInt(2, w.getSenderId());
            setNullableInt(ps, 3, w.getReceiverId());
            setNullableInt(ps, 4, w.getTargetEnterpriseId());
            bindSpecific(ps, 5, w);
            ps.setString(9, w.getStatus().name());
            ps.setString(10, w.getMessage());
        });
        w.setId(id);
        return id;
    }

    @Override
    public WorkRequest findById(int id) {
        return queryOne(SELECT + "WHERE w.id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<WorkRequest> findAll() {
        return query(SELECT + "ORDER BY w.request_date DESC", null);
    }

    /** Requests this user sent (their "My Requests" tab). */
    public List<WorkRequest> findBySender(int userId) {
        return query(SELECT + "WHERE w.sender_id = ? ORDER BY w.request_date DESC", ps -> ps.setInt(1, userId));
    }

    /**
     * Inbox for an admin: requests addressed to them directly, plus requests of the given type
     * addressed to their whole enterprise (receiver_id NULL).
     */
    public List<WorkRequest> findInbox(int userId, int enterpriseId, RequestType type) {
        return query(SELECT + "WHERE w.type = ? AND (w.receiver_id = ? "
                + "OR (w.receiver_id IS NULL AND w.target_enterprise_id = ?)) "
                + "ORDER BY FIELD(w.status, 'PENDING', 'ACCEPTED') DESC, w.request_date DESC", ps -> {
            ps.setString(1, type.name());
            ps.setInt(2, userId);
            ps.setInt(3, enterpriseId);
        });
    }

    public List<WorkRequest> findByFoodItem(int foodItemId, RequestType type) {
        return query(SELECT + "WHERE w.food_item_id = ? AND w.type = ? ORDER BY w.request_date DESC", ps -> {
            ps.setInt(1, foodItemId);
            ps.setString(2, type.name());
        });
    }

    public List<WorkRequest> findByCharityOrg(int orgId) {
        return query(SELECT + "WHERE w.charity_org_id = ? ORDER BY w.request_date DESC", ps -> ps.setInt(1, orgId));
    }

    @Override
    public boolean update(WorkRequest w) {
        return execute("UPDATE work_request SET food_item_id = ?, food_need_id = ?, charity_org_id = ?, "
                + "pickup_time = ?, receiver_id = ?, status = ?, message = ?, resolve_date = ? WHERE id = ?", ps -> {
            bindSpecific(ps, 1, w);
            setNullableInt(ps, 5, w.getReceiverId());
            ps.setString(6, w.getStatus().name());
            ps.setString(7, w.getMessage());
            setNullableTime(ps, 8, w.getResolveDate());
            ps.setInt(9, w.getId());
        }) > 0;
    }

    /** Expires open requests whose food item has expired. */
    public int expireForExpiredFood() {
        return execute("UPDATE work_request w JOIN food_item f ON f.id = w.food_item_id "
                + "SET w.status = 'EXPIRED', w.resolve_date = NOW() "
                + "WHERE f.status = 'EXPIRED' AND w.status IN ('PENDING', 'ACCEPTED')", null);
    }

    @Override
    public boolean delete(int id) {
        return execute("DELETE FROM work_request WHERE id = ?", ps -> ps.setInt(1, id)) > 0;
    }

    public int countByStatus(RequestStatus status) {
        return (int) scalar("SELECT COUNT(*) FROM work_request WHERE status = ?", ps -> ps.setString(1, status.name()));
    }
}
