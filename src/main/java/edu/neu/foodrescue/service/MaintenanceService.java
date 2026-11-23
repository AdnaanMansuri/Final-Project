package edu.neu.foodrescue.service;

import edu.neu.foodrescue.dao.FoodItemDao;
import edu.neu.foodrescue.dao.WorkRequestDao;

/** Keeps data consistent with the clock: listings past their pickup deadline become EXPIRED. */
public class MaintenanceService {

    private final FoodItemDao foodDao = new FoodItemDao();
    private final WorkRequestDao requestDao = new WorkRequestDao();

    /** Returns the number of food items that were expired. */
    public int expireOverdueItems() {
        int expired = foodDao.expireOverdue();
        requestDao.expireForExpiredFood();
        return expired;
    }
}
