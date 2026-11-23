package edu.neu.foodrescue.ui;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.service.MaintenanceService;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.util.DataAccessException;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The single application window. Uses CardLayout to swap between the login screen
 * and the logged-in user's work area.
 */
public class MainFrame extends JFrame {

    private static final String LOGIN = "login";
    private static final String WORK = "work";

    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    private final LoginPanel loginPanel;
    private JPanel currentWorkArea;

    public MainFrame() {
        super("Last-Minute Food Rescue Network");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(this);
        container.add(loginPanel, LOGIN);
        setContentPane(container);
        cards.show(container, LOGIN);
    }

    /** Called after a successful login: builds the work area for the user's role (polymorphism). */
    public void showWorkArea(UserAccount user) {
        try {
            new MaintenanceService().expireOverdueItems();
        } catch (DataAccessException e) {
            Dialogs.warn(this, "Could not update expired listings: " + e.getMessage());
        }
        if (currentWorkArea != null) {
            container.remove(currentWorkArea);
        }
        currentWorkArea = user.getRole().createWorkArea(this, user);
        container.add(currentWorkArea, WORK);
        cards.show(container, WORK);
        container.revalidate();
    }

    public void logout() {
        if (currentWorkArea != null) {
            container.remove(currentWorkArea);
            currentWorkArea = null;
        }
        loginPanel.reset();
        cards.show(container, LOGIN);
    }
}
