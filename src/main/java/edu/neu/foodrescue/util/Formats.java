package edu.neu.foodrescue.util;

import edu.neu.foodrescue.model.Expirable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Human-friendly formatting for tables and labels. */
public final class Formats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, h:mm a");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private Formats() { }

    public static String dateTime(LocalDateTime t) {
        return t == null ? "-" : t.format(DATE_TIME);
    }

    public static String date(LocalDate d) {
        return d == null ? "-" : d.format(DATE);
    }

    /** "Expired", "45m", "3h 20m" or "2d 4h". */
    public static String timeLeft(Expirable e) {
        if (e == null || e.getDeadline() == null) {
            return "-";
        }
        if (e.isExpired()) {
            return "Expired";
        }
        long m = e.minutesLeft();
        if (m < 60) {
            return m + "m";
        }
        if (m < 24 * 60) {
            return (m / 60) + "h " + (m % 60) + "m";
        }
        return (m / (24 * 60)) + "d " + ((m / 60) % 24) + "h";
    }

    /** 5.0 -> "5", 2.5 -> "2.5". */
    public static String qty(double q) {
        return q == Math.rint(q) ? String.valueOf((long) q) : String.valueOf(q);
    }
}
