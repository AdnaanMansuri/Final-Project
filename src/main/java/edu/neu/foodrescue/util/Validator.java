package edu.neu.foodrescue.util;

import java.util.regex.Pattern;

/** Input validation helpers shared by all forms. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");

    private Validator() { }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s.trim()).matches();
    }

    public static boolean isValidUsername(String s) {
        return s != null && USERNAME.matcher(s.trim()).matches();
    }

    /** Parses a positive number; returns -1 if invalid. */
    public static double parsePositive(String s) {
        try {
            double v = Double.parseDouble(s.trim());
            return v > 0 ? v : -1;
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }
}
