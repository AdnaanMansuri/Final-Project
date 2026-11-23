package edu.neu.foodrescue.model;

import java.time.Duration;
import java.time.LocalDateTime;

/** Anything with a deadline. Provides the urgency logic used for the color-coded board. */
public interface Expirable {

    LocalDateTime getDeadline();

    default boolean isExpired() {
        return getDeadline() != null && LocalDateTime.now().isAfter(getDeadline());
    }

    default long minutesLeft() {
        if (getDeadline() == null) {
            return Long.MAX_VALUE;
        }
        return Math.max(0, Duration.between(LocalDateTime.now(), getDeadline()).toMinutes());
    }

    /** RED under 2 hours, AMBER under 6 hours, otherwise GREEN. */
    default Urgency getUrgency() {
        long m = minutesLeft();
        if (isExpired() || m < 120) {
            return Urgency.RED;
        }
        return m < 360 ? Urgency.AMBER : Urgency.GREEN;
    }

    enum Urgency { GREEN, AMBER, RED }
}
