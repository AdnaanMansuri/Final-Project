package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.model.Enterprise;
import edu.neu.foodrescue.model.enums.EnterpriseType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EnterpriseDao extends BaseDao<Enterprise> {

    @Override
    protected Enterprise mapRow(ResultSet rs) throws SQLException {
        return Enterprise.of(rs.getInt("id"), rs.getString("name"),
                EnterpriseType.valueOf(rs.getString("type")));
    }

    @Override
    public int create(Enterprise e) {
        int id = insert("INSERT INTO enterprise (name, type) VALUES (?, ?)", ps -> {
            ps.setString(1, e.getName());
            ps.setString(2, e.getType().name());
        });
        e.setId(id);
        return id;
    }

    @Override
    public Enterprise findById(int id) {
        return queryOne("SELECT * FROM enterprise WHERE id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Enterprise> findAll() {
        return query("SELECT * FROM enterprise ORDER BY name", null);
    }

    public List<Enterprise> findByType(EnterpriseType type) {
        return query("SELECT * FROM enterprise WHERE type = ? ORDER BY name", ps -> ps.setString(1, type.name()));
    }

    public boolean nameExists(String name, int excludeId) {
        return scalar("SELECT COUNT(*) FROM enterprise WHERE LOWER(name) = LOWER(?) AND id <> ?", ps -> {
            ps.setString(1, name.trim());
            ps.setInt(2, excludeId);
        }) > 0;
    }

    /** Only the name can change; the type decides the subclass and stays fixed. */
    @Override
    public boolean update(Enterprise e) {
        return execute("UPDATE enterprise SET name = ? WHERE id = ?", ps -> {
            ps.setString(1, e.getName());
            ps.setInt(2, e.getId());
        }) > 0;
    }

    @Override
    public boolean delete(int id) {
        return execute("DELETE FROM enterprise WHERE id = ?", ps -> ps.setInt(1, id)) > 0;
    }
}
