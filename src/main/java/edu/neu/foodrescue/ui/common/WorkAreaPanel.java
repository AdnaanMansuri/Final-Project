package edu.neu.foodrescue.ui.common;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.ui.MainFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Common layout for every role: green header (title, who is logged in, logout) and tabs below.
 * Subclasses add their tabs in their constructor with addTab(...).
 * A tab that implements Refreshable is reloaded each time it is selected.
 */
public abstract class WorkAreaPanel extends JPanel {

    protected final MainFrame frame;
    protected final UserAccount user;
    private final JTabbedPane tabs = new JTabbedPane();

    protected WorkAreaPanel(MainFrame frame, UserAccount user, String title) {
        this.frame = frame;
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(UIStyle.BACKGROUND);
        add(buildHeader(title), BorderLayout.NORTH);

        tabs.setFont(UIStyle.BODY.deriveFont(Font.BOLD));
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() instanceof Refreshable r) {
                r.refresh();
            }
        });
        add(tabs, BorderLayout.CENTER);
    }

    protected void addTab(String title, JComponent content) {
        tabs.addTab(title, content);
    }

    private JPanel buildHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.TITLE);
        titleLabel.setForeground(Color.WHITE);

        StringBuilder who = new StringBuilder(user.getFullName()).append("  •  ").append(user.getRoleType());
        if (user.getOrganizationName() != null) {
            who.append("  •  ").append(user.getOrganizationName());
        }
        if (user.getEnterpriseName() != null) {
            who.append(" (").append(user.getEnterpriseName()).append(")");
        }
        JLabel whoLabel = new JLabel(who.toString());
        whoLabel.setFont(UIStyle.BODY);
        whoLabel.setForeground(new Color(0xE8F5E9));

        text.add(titleLabel);
        text.add(whoLabel);

        JButton logout = UIStyle.secondaryButton("Log out");
        logout.addActionListener(e -> frame.logout());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        right.setOpaque(false);
        right.add(logout);

        header.add(text, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }
}
