package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.model.FoodItem;
import edu.neu.foodrescue.model.enums.FoodCategory;
import edu.neu.foodrescue.model.enums.FoodStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class FoodItemDao extends BaseDao<FoodItem> {

    private static final String SELECT =
            "SELECT f.*, o.name AS org_name, u.full_name AS posted_name FROM food_item f "
            + "JOIN organization o ON o.id = f.supplier_org_id "
            + "JOIN user_account u ON u.id = f.posted_by ";

    @Override
    protected FoodItem mapRow(ResultSet rs) throws SQLException {
        FoodItem f = new FoodItem();
        f.setId(rs.getInt("id"));
        f.setSupplierOrgId(rs.getInt("supplier_org_id"));
        f.setSupplierOrgName(rs.getString("org_name"));
        f.setPostedById(rs.getInt("posted_by"));
        f.setPostedByName(rs.getString("posted_name"));
        f.setName(rs.getString("name"));
        f.setCategory(FoodCategory.valueOf(rs.getString("category")));
        f.setQuantity(rs.getDouble("quantity"));
        f.setUnit(rs.getString("unit"));
        f.setAllergens(rs.getString("allergens"));
        f.setPickupDeadline(getTime(rs, "pickup_deadline"));
        f.setStatus(FoodStatus.valueOf(rs.getString("status")));
        f.setCreatedAt(getTime(rs, "created_at"));
        return f;
    }

    @Override
    public int create(FoodItem f) {
        int id = insert("INSERT INTO food_item (supplier_org_id, posted_by, name, category, quantity, unit, "
                + "allergens, pickup_deadline, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", ps -> {
            ps.setInt(1, f.getSupplierOrgId());
            ps.setInt(2, f.getPostedById());
            ps.setString(3, f.getName());
            ps.setString(4, f.getCategory().name());
            ps.setDouble(5, f.getQuantity());
            ps.setString(6, f.getUnit());
            ps.setString(7, f.getAllergens());
            ps.setTimestamp(8, Timestamp.valueOf(f.getPickupDeadline()));
            ps.setString(9, f.getStatus().name());
            ps.setTimestamp(10, now());
        });
        f.setId(id);
        return id;
    }

    @Override
    public FoodItem findById(int id) {
        return queryOne(SELECT + "WHERE f.id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<FoodItem> findAll() {
        return query(SELECT + "ORDER BY f.pickup_deadline", null);
    }

    public List<FoodItem> findBySupplierOrg(int orgId) {
        return query(SELECT + "WHERE f.supplier_org_id = ? ORDER BY f.created_at DESC", ps -> ps.setInt(1, orgId));
    }

    public List<FoodItem> findBySupplierEnterprise(int enterpriseId) {
        return query(SELECT + "WHERE o.enterprise_id = ? ORDER BY f.created_at DESC", ps -> ps.setInt(1, enterpriseId));
    }

    /** Listings a charity can claim right now, most urgent first. */
    public List<FoodItem> findAvailable() {
        return query(SELECT + "WHERE f.status = 'AVAILABLE' AND f.pickup_deadline > ? "
                + "ORDER BY f.pickup_deadline", ps -> ps.setTimestamp(1, now()));
    }

    @Override
    public boolean update(FoodItem f) {
        return execute("UPDATE food_item SET name = ?, category = ?, quantity = ?, unit = ?, allergens = ?, "
                + "pickup_deadline = ?, status = ? WHERE id = ?", ps -> {
            ps.setString(1, f.getName());
            ps.setString(2, f.getCategory().name());
            ps.setDouble(3, f.getQuantity());
            ps.setString(4, f.getUnit());
            ps.setString(5, f.getAllergens());
            ps.setTimestamp(6, Timestamp.valueOf(f.getPickupDeadline()));
            ps.setString(7, f.getStatus().name());
            ps.setInt(8, f.getId());
        }) > 0;
    }

    public boolean updateStatus(int id, FoodStatus status) {
        return execute("UPDATE food_item SET status = ? WHERE id = ?", ps -> {
            ps.setString(1, status.name());
            ps.setInt(2, id);
        }) > 0;
    }

    /** Marks every open listing whose deadline has passed as EXPIRED. Returns how many changed. */
    public int expireOverdue() {
        return execute("UPDATE food_item SET status = 'EXPIRED' WHERE pickup_deadline < ? "
                + "AND status IN ('PENDING_APPROVAL', 'AVAILABLE', 'CLAIMED')", ps -> ps.setTimestamp(1, now()));
    }

    @Override
    public boolean delete(int id) {
        return execute("DELETE FROM food_item WHERE id = ?", ps -> ps.setInt(1, id)) > 0;
    }

    // ---------- impact dashboard ----------

    public double totalRescuedQuantity(Integer enterpriseId) {
        String sql = "SELECT COALESCE(SUM(f.quantity), 0) FROM food_item f "
                + "JOIN organization o ON o.id = f.supplier_org_id WHERE f.status = 'PICKED_UP'"
                + (enterpriseId == null ? "" : " AND o.enterprise_id = ?");
        return scalar(sql, enterpriseId == null ? null : ps -> ps.setInt(1, enterpriseId));
    }

    public int countByStatus(FoodStatus status) {
        return (int) scalar("SELECT COUNT(*) FROM food_item WHERE status = ?", ps -> ps.setString(1, status.name()));
    }
}
