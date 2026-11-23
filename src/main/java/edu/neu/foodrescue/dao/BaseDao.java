package edu.neu.foodrescue.dao;

import edu.neu.foodrescue.util.DBConnection;
import edu.neu.foodrescue.util.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared JDBC plumbing so each DAO only writes its SQL and row mapping.
 * Every query uses PreparedStatement (prevents SQL injection) and try-with-resources.
 */
public abstract class BaseDao<T> implements Dao<T> {

    /** Converts one result-set row into a model object. */
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    @FunctionalInterface
    protected interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    protected Connection connection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    protected List<T> query(String sql, Binder binder) {
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load data: " + e.getMessage(), e);
        }
    }

    protected T queryOne(String sql, Binder binder) {
        List<T> list = query(sql, binder);
        return list.isEmpty() ? null : list.get(0);
    }

    /** INSERT that returns the auto-generated key. */
    protected int insert(String sql, Binder binder) {
        try (Connection c = connection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            binder.bind(ps);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save: " + e.getMessage(), e);
        }
    }

    /** UPDATE / DELETE; returns number of affected rows. */
    protected int execute(String sql, Binder binder) {
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update: " + e.getMessage(), e);
        }
    }

    /** Runs a single aggregate query (COUNT/SUM) and returns the first column as a double. */
    protected double scalar(String sql, Binder binder) {
        try (Connection c = connection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load totals: " + e.getMessage(), e);
        }
    }

    // ---------- null-safe helpers ----------

    protected static void setNullableInt(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) {
            ps.setNull(idx, Types.INTEGER);
        } else {
            ps.setInt(idx, v);
        }
    }

    protected static void setNullableTime(PreparedStatement ps, int idx, LocalDateTime v) throws SQLException {
        if (v == null) {
            ps.setNull(idx, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(idx, Timestamp.valueOf(v));
        }
    }

    protected static Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    protected static LocalDateTime getTime(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toLocalDateTime();
    }
}
