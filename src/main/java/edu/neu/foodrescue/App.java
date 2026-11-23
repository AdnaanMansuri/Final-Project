package edu.neu.foodrescue;

import edu.neu.foodrescue.ui.MainFrame;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.UIStyle;
import javax.swing.SwingUtilities;

/** Entry point. Run this class from NetBeans (or: mvn exec:java). */
public class App {

    public static void main(String[] args) {
        // Last line of defence: any unexpected error shows a message instead of breaking the app.
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> Dialogs.error(null,
                    "Unexpected error: " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage())));
        });

        SwingUtilities.invokeLater(() -> {
            UIStyle.installLookAndFeel();
            new MainFrame().setVisible(true);
        });
    }
}
