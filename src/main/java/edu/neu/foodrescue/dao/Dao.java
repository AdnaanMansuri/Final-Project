package edu.neu.foodrescue.dao;

import java.util.List;

/**
 * Generic CRUD contract implemented by every DAO.
 * Methods throw DataAccessException (unchecked) if the database call fails.
 */
public interface Dao<T> {

    /** Inserts the object and returns the generated id. */
    int create(T t);

    /** Returns the object or null if not found. */
    T findById(int id);

    List<T> findAll();

    boolean update(T t);

    boolean delete(int id);
}
