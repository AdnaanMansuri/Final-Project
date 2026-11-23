package edu.neu.foodrescue.ui;

import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.service.AuthService;
import edu.neu.foodrescue.ui.common.UIStyle;
import edu.neu.foodrescue.util.DataAccessException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Login screen shown at startup and after logout. */
public class LoginPanel extends JPanel {

    private final MainFrame frame;
    private final AuthService auth = new AuthService();
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel errorLabel = new JLabel(" ");

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBackground(UIStyle.PRIMARY_LIGHT);
        add(buildCard());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8E6C9)),
                BorderFactory.createEmptyBorder(32, 40, 32, 40)));
        card.setPreferredSize(new Dimension(440, 420));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 0, 4, 0);

        JLabel title = UIStyle.title("Food Rescue Network");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel tagline = UIStyle.muted("Surplus food from local stores to people who need it");
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField.setFont(UIStyle.BODY);
        passwordField.setFont(UIStyle.BODY);
        errorLabel.setForeground(UIStyle.DANGER);
        errorLabel.setFont(UIStyle.SMALL);

        JButton loginButton = UIStyle.primaryButton("Log in");
        loginButton.addActionListener(e -> doLogin());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> doLogin());

        int y = 0;
        g.gridy = y++; card.add(title, g);
        g.gridy = y++; g.insets = new Insets(0, 0, 24, 0); card.add(tagline, g);
        g.insets = new Insets(4, 0, 4, 0);
        g.gridy = y++; card.add(new JLabel("Username"), g);
        g.gridy = y++; card.add(usernameField, g);
        g.gridy = y++; card.add(new JLabel("Password"), g);
        g.gridy = y++; card.add(passwordField, g);
        g.gridy = y++; card.add(errorLabel, g);
        g.gridy = y++; g.insets = new Insets(12, 0, 4, 0); card.add(loginButton, g);
        g.gridy = y; g.insets = new Insets(16, 0, 0, 0);
        JLabel hint = UIStyle.muted("Demo users: admin, smanager, staff1, cmanager, coord1  (password123)");
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(hint, g);
        return card;
    }

    private void doLogin() {
        char[] password = passwordField.getPassword();
        try {
            if (usernameField.getText().isBlank() || password.length == 0) {
                errorLabel.setText("Please enter your username and password.");
                return;
            }
            UserAccount user = auth.login(usernameField.getText(), password);
            if (user == null) {
                errorLabel.setText("Wrong username or password.");
                passwordField.setText("");
                return;
            }
            frame.showWorkArea(user);
        } catch (DataAccessException e) {
            errorLabel.setText("<html>Cannot reach the database. Is Docker running?<br>"
                    + "Run: docker compose up -d</html>");
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        usernameField.requestFocusInWindow();
    }
}
