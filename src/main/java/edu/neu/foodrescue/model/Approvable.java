package edu.neu.foodrescue.model;

/** Anything that an admin can approve or reject. */
public interface Approvable {

    void approve(String note);

    void reject(String reason);
}
