package edu.neu.foodrescue.service;

import edu.neu.foodrescue.dao.UserAccountDao;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.util.PasswordUtil;

/** Checks credentials against the user_account table. */
public class AuthService {

    private final UserAccountDao userDao = new UserAccountDao();

    /** Returns the matching user, or null if the username/password is wrong. */
    public UserAccount login(String username, char[] password) {
        if (username == null || username.isBlank() || password == null || password.length == 0) {
            return null;
        }
        UserAccount user = userDao.findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        String hash = PasswordUtil.hash(new String(password));
        return hash.equals(user.getPasswordHash()) ? user : null;
    }
}
