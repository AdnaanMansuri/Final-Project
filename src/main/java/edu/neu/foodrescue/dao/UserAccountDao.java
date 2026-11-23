package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.RoleType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserAccountDao extends BaseDao<UserAccount> {

    private static final String SELECT =
            "SELECT u.*, o.name AS org_name, e.id AS ent_id, e.name AS ent_name "
            + "FROM user_account u "
            + "LEFT JOIN organization o ON o.id = u.organization_id "
            + "LEFT JOIN enterprise e ON e.id = o.enterprise_id ";

    @Override
    protected UserAccount mapRow(ResultSet rs) throws SQLException {
        UserAccount u = new UserAccount();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRoleType(RoleType.valueOf(rs.getString("role")));
        u.setOrganizationId(getNullableInt(rs, "organization_id"));
        u.setOrganizationName(rs.getString("org_name"));
        u.setEnterpriseId(getNullableInt(rs, "ent_id"));
        u.setEnterpriseName(rs.getString("ent_name"));
        return u;
    }

    @Override
    public int create(UserAccount u) {
        int id = insert("INSERT INTO user_account (username, password_hash, full_name, email, role, organization_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)", ps -> {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getRoleType().name());
            setNullableInt(ps, 6, u.getOrganizationId());
        });
        u.setId(id);
        return id;
    }

    @Override
    public UserAccount findById(int id) {
        return queryOne(SELECT + "WHERE u.id = ?", ps -> ps.setInt(1, id));
    }

    public UserAccount findByUsername(String username) {
        return queryOne(SELECT + "WHERE u.username = ?", ps -> ps.setString(1, username));
    }

    @Override
    public List<UserAccount> findAll() {
        return query(SELECT + "ORDER BY u.role, u.full_name", null);
    }

    public List<UserAccount> findByOrganization(int orgId) {
        return query(SELECT + "WHERE u.organization_id = ? ORDER BY u.full_name", ps -> ps.setInt(1, orgId));
    }

    public List<UserAccount> findByEnterpriseAndRole(int enterpriseId, RoleType role) {
        return query(SELECT + "WHERE e.id = ? AND u.role = ? ORDER BY u.full_name", ps -> {
            ps.setInt(1, enterpriseId);
            ps.setString(2, role.name());
        });
    }

    public List<UserAccount> findByRole(RoleType role) {
        return query(SELECT + "WHERE u.role = ? ORDER BY u.full_name", ps -> ps.setString(1, role.name()));
    }

    public boolean usernameExists(String username, int excludeId) {
        return scalar("SELECT COUNT(*) FROM user_account WHERE username = ? AND id <> ?", ps -> {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
        }) > 0;
    }

    /** Updates profile fields; the password is only changed when passwordHash is set. */
    @Override
    public boolean update(UserAccount u) {
        return execute("UPDATE user_account SET username = ?, full_name = ?, email = ?, role = ?, organization_id = ?, "
                + "password_hash = COALESCE(?, password_hash) WHERE id = ?", ps -> {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getFullName());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRoleType().name());
            setNullableInt(ps, 5, u.getOrganizationId());
            ps.setString(6, u.getPasswordHash());
            ps.setInt(7, u.getId());
        }) > 0;
    }

    @Override
    public boolean delete(int id) {
        return execute("DELETE FROM user_account WHERE id = ?", ps -> ps.setInt(1, id)) > 0;
    }
}
