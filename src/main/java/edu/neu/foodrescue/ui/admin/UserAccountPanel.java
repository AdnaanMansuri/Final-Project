package edu.neu.foodrescue.ui.admin;

import edu.neu.foodrescue.dao.OrganizationDao;
import edu.neu.foodrescue.dao.UserAccountDao;
import edu.neu.foodrescue.model.Organization;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.EnterpriseType;
import edu.neu.foodrescue.model.enums.RoleType;
import edu.neu.foodrescue.ui.common.CrudPanel;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.FormBuilder;
import edu.neu.foodrescue.ui.common.UIStyle;
import edu.neu.foodrescue.util.PasswordUtil;
import edu.neu.foodrescue.util.Validator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Manage user accounts. Reused by several roles:
 *  - System Admin: every role, every organization (enterpriseScope = null)
 *  - Supplier / Charity Manager: only their own enterprise and the roles they are allowed to create
 */
public class UserAccountPanel extends CrudPanel<UserAccount> {

    private final UserAccountDao userDao = new UserAccountDao();
    private final OrganizationDao organizationDao = new OrganizationDao();

    private final UserAccount currentUser;
    private final List<RoleType> allowedRoles;
    private final Integer enterpriseScope;
    private List<Organization> organizations = new ArrayList<>();

    private final JTextField usernameField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<RoleType> roleCombo;
    private final JComboBox<Organization> orgCombo = new JComboBox<>();
    private final JLabel passwordHint = UIStyle.muted("At least 6 characters.");

    public UserAccountPanel(String title, UserAccount currentUser, List<RoleType> allowedRoles, Integer enterpriseScope) {
        super(title, new String[]{"ID", "Username", "Full Name", "Email", "Role", "Organization"});
        this.currentUser = currentUser;
        this.allowedRoles = allowedRoles;
        this.enterpriseScope = enterpriseScope;
        roleCombo = new JComboBox<>(allowedRoles.toArray(new RoleType[0]));
        roleCombo.addActionListener(e -> reloadOrganizations());
        init();
    }

    @Override
    protected void beforeRefresh() {
        List<Organization> all = enterpriseScope == null
                ? organizationDao.findAll()
                : organizationDao.findByEnterprise(enterpriseScope);
        organizations = all;
        reloadOrganizations();
    }

    /** Only organizations whose enterprise type matches the chosen role. */
    private void reloadOrganizations() {
        RoleType role = (RoleType) roleCombo.getSelectedItem();
        Object current = orgCombo.getSelectedItem();
        DefaultComboBoxModel<Organization> m = new DefaultComboBoxModel<>();
        EnterpriseType needed = role == null ? null : role.getEnterpriseType();
        if (needed != null) {
            for (Organization o : organizations) {
                if (o.getType().getEnterpriseType() == needed) {
                    m.addElement(o);
                }
            }
        }
        orgCombo.setModel(m);
        orgCombo.setEnabled(needed != null);
        if (current instanceof Organization co) {
            selectOrganization(co.getId());
        }
    }

    private void selectOrganization(Integer id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < orgCombo.getItemCount(); i++) {
            if (orgCombo.getItemAt(i).getId() == id) {
                orgCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    protected List<UserAccount> loadItems() {
        List<UserAccount> result = new ArrayList<>();
        for (UserAccount u : userDao.findAll()) {
            boolean inScope = enterpriseScope == null || enterpriseScope.equals(u.getEnterpriseId());
            if (inScope && allowedRoles.contains(u.getRoleType())) {
                result.add(u);
            }
        }
        return result;
    }

    @Override
    protected Object[] toRow(UserAccount u) {
        return new Object[]{u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getRoleType(),
            u.getOrganizationName() == null ? "-" : u.getOrganizationName()};
    }

    @Override
    protected JPanel buildForm() {
        return new FormBuilder()
                .add("Username *", usernameField)
                .add("Full name *", fullNameField)
                .add("Email", emailField)
                .add("Password", passwordField)
                .addFull(passwordHint)
                .add("Role *", roleCombo)
                .add("Organization *", orgCombo)
                .build();
    }

    @Override
    protected void fillForm(UserAccount u) {
        usernameField.setText(u.getUsername());
        fullNameField.setText(u.getFullName());
        emailField.setText(u.getEmail() == null ? "" : u.getEmail());
        passwordField.setText("");
        roleCombo.setSelectedItem(u.getRoleType());
        reloadOrganizations();
        selectOrganization(u.getOrganizationId());
    }

    @Override
    protected void clearForm() {
        usernameField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedIndex(0);
        reloadOrganizations();
    }

    @Override
    protected void onModeChanged(boolean editing) {
        passwordHint.setText(editing ? "Leave blank to keep the current password." : "Required, at least 6 characters.");
    }

    /** Nobody can delete the account they are logged in with. */
    @Override
    protected boolean canDelete(UserAccount u) {
        return u.getId() != currentUser.getId();
    }

    @Override
    protected UserAccount readForm(UserAccount existing) {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        RoleType role = (RoleType) roleCombo.getSelectedItem();

        if (!Validator.isValidUsername(username)) {
            Dialogs.warn(this, "Username must be 3-30 characters: letters, numbers or underscore.");
            return null;
        }
        int id = existing == null ? 0 : existing.getId();
        if (userDao.usernameExists(username, id)) {
            Dialogs.warn(this, "Username \"" + username + "\" is already taken.");
            return null;
        }
        if (Validator.isBlank(fullName) || fullName.length() > 100) {
            Dialogs.warn(this, "Full name is required (max 100 characters).");
            return null;
        }
        if (!email.isEmpty() && !Validator.isValidEmail(email)) {
            Dialogs.warn(this, "Please enter a valid email address, or leave it blank.");
            return null;
        }
        boolean passwordRequired = existing == null;
        if ((passwordRequired || !password.isEmpty()) && password.length() < 6) {
            Dialogs.warn(this, "Password must be at least 6 characters.");
            return null;
        }
        if (role == null) {
            Dialogs.warn(this, "Please choose a role.");
            return null;
        }
        Integer orgId = null;
        if (role.getEnterpriseType() != null) {
            if (!(orgCombo.getSelectedItem() instanceof Organization org)) {
                Dialogs.warn(this, "Please choose an organization. Create one first if the list is empty.");
                return null;
            }
            orgId = org.getId();
        }
        if (existing != null && existing.getId() == currentUser.getId() && role != currentUser.getRoleType()) {
            Dialogs.warn(this, "You cannot change your own role.");
            return null;
        }

        UserAccount u = existing == null ? new UserAccount() : existing;
        u.setUsername(username);
        u.setFullName(fullName);
        u.setEmail(email.isEmpty() ? null : email);
        u.setRoleType(role);
        u.setOrganizationId(orgId);
        u.setPasswordHash(password.isEmpty() ? null : PasswordUtil.hash(password));
        return u;
    }

    @Override
    protected void create(UserAccount u) {
        userDao.create(u);
    }

    @Override
    protected void update(UserAccount u) {
        userDao.update(u);
    }

    @Override
    protected void delete(UserAccount u) {
        userDao.delete(u.getId());
    }

    @Override
    protected String describe(UserAccount u) {
        return u.getFullName() + " (" + u.getUsername() + ")";
    }

    @Override
    protected String deleteWarning(UserAccount u) {
        return "Food listings and requests created by this user will also be deleted.";
    }
}
