package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.model.FoodNeed;
import edu.neu.foodrescue.model.enums.FoodCategory;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FoodNeedDao extends BaseDao<FoodNeed> {

    private static final String SELECT =
            "SELECT n.*, o.name AS org_name FROM food_need n JOIN organization o ON o.id = n.charity_org_id ";

    @Override
    protected FoodNeed mapRow(ResultSet rs) throws SQLException {
        FoodNeed n = new FoodNeed();
        n.setId(rs.getInt("id"));
        n.setCharityOrgId(rs.getInt("charity_org_id"));
        n.setCharityOrgName(rs.getString("org_name"));
        n.setCategory(FoodCategory.valueOf(rs.getString("category")));
        n.setQuantity(rs.getDouble("quantity"));
        n.setDescription(rs.getString("description"));
        Date d = rs.getDate("needed_by");
        n.setNeededBy(d == null ? null : d.toLocalDate());
        return n;
    }

    @Override
    public int create(FoodNeed n) {
        int id = insert("INSERT INTO food_need (charity_org_id, category, quantity, description, needed_by) "
                + "VALUES (?, ?, ?, ?, ?)", ps -> {
            ps.setInt(1, n.getCharityOrgId());
            ps.setString(2, n.getCategory().name());
            ps.setDouble(3, n.getQuantity());
            ps.setString(4, n.getDescription());
            ps.setDate(5, Date.valueOf(n.getNeededBy()));
        });
        n.setId(id);
        return id;
    }

    @Override
    public FoodNeed findById(int id) {
        return queryOne(SELECT + "WHERE n.id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<FoodNeed> findAll() {
        return query(SELECT + "ORDER BY n.needed_by", null);
    }

    public List<FoodNeed> findByCharityOrg(int orgId) {
        return query(SELECT + "WHERE n.charity_org_id = ? ORDER BY n.needed_by", ps -> ps.setInt(1, orgId));
    }

    @Override
    public boolean update(FoodNeed n) {
        return execute("UPDATE food_need SET category = ?, quantity = ?, description = ?, needed_by = ? WHERE id = ?",
                ps -> {
                    ps.setString(1, n.getCategory().name());
                    ps.setDouble(2, n.getQuantity());
                    ps.setString(3, n.getDescription());
                    ps.setDate(4, Date.valueOf(n.getNeededBy()));
                    ps.setInt(5, n.getId());
                }) > 0;
    }

    @Override
    public boolean delete(int id) {
        return execute("DELETE FROM food_need WHERE id = ?", ps -> ps.setInt(1, id)) > 0;
    }
}
