package edu.neu.foodrescue.ui.common;

import java.awt.Component;
import javax.swing.JOptionPane;

/** Consistent message boxes. Every caught error goes through here, so the app never crashes silently. */
public final class Dialogs {

    private Dialogs() { }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Food Rescue Network", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Asks for an optional note/reason. Returns null if the user cancelled. */
    public static String askText(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message, "Food Rescue Network", JOptionPane.QUESTION_MESSAGE);
    }
}
