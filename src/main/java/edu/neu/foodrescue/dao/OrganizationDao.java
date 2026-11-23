package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.model.Organization;
import edu.neu.foodrescue.model.enums.OrganizationType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrganizationDao extends BaseDao<Organization> {

    private static final String SELECT =
            "SELECT o.*, e.name AS enterprise_name FROM organization o "
            + "JOIN enterprise e ON e.id = o.enterprise_id ";

    @Override
    protected Organization mapRow(ResultSet rs) throws SQLException {
        Organization o = new Organization();
        o.setId(rs.getInt("id"));
        o.setEnterpriseId(rs.getInt("enterprise_id"));
        o.setEnterpriseName(rs.getString("enterprise_name"));
        o.setName(rs.getString("name"));
        o.setType(OrganizationType.valueOf(rs.getString("type")));
        o.setAddress(rs.getString("address"));
        return o;
    }

    @Override
    public int create(Organization o) {
        int id = insert("INSERT INTO organization (enterprise_id, name, type, address) VALUES (?, ?, ?, ?)", ps -> {
            ps.setInt(1, o.getEnterpriseId());
            ps.setString(2, o.getName());
            ps.setString(3, o.getType().name());
            ps.setString(4, o.getAddress());
        });
        o.setId(id);
        return id;
    }

    @Override
    public Organization findById(int id) {
        return queryOne(SELECT + "WHERE o.id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<Organization> findAll() {
        return query(SELECT + "ORDER BY e.name, o.name", null);
    }

    public List<Organization> findByEnterprise(int enterpriseId) {
        return query(SELECT + "WHERE o.enterprise_id = ? ORDER BY o.name", ps -> ps.setInt(1, enterpriseId));
    }

    @Override
    public boolean update(Organization o) {
        return execute("UPDATE organization SET name = ?, type = ?, address = ? WHERE id = ?", ps -> {
            ps.setString(1, o.getName());
            ps.setString(2, o.getType().name());
            ps.setString(3, o.getAddress());
            ps.setInt(4, o.getId());
        }) > 0;
    }

    @Override
    public boolean delete(int id) {
        return execute("DELETE FROM organization WHERE id = ?", ps -> ps.setInt(1, id)) > 0;
    }
}
