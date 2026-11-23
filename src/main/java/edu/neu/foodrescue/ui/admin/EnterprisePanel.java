package edu.neu.foodrescue.ui.admin;

import edu.neu.foodrescue.dao.EnterpriseDao;
import edu.neu.foodrescue.dao.OrganizationDao;
import edu.neu.foodrescue.model.Enterprise;
import edu.neu.foodrescue.model.Organization;
import edu.neu.foodrescue.model.enums.EnterpriseType;
import edu.neu.foodrescue.ui.common.CrudPanel;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.FormBuilder;
import edu.neu.foodrescue.ui.common.UIStyle;
import edu.neu.foodrescue.util.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** System Admin: create, view, rename and delete enterprises. */
public class EnterprisePanel extends CrudPanel<Enterprise> {

    private final EnterpriseDao enterpriseDao = new EnterpriseDao();
    private final OrganizationDao organizationDao = new OrganizationDao();

    private final JTextField nameField = new JTextField();
    private final JComboBox<EnterpriseType> typeCombo = new JComboBox<>(EnterpriseType.values());
    private final JLabel contributionLabel = UIStyle.muted(" ");
    private Map<Integer, Integer> orgCounts = new HashMap<>();

    public EnterprisePanel() {
        super("Enterprises", new String[]{"ID", "Name", "Type", "Organizations", "Role in the network"});
        typeCombo.addActionListener(e -> showContribution());
        init();
    }

    @Override
    protected List<Enterprise> loadItems() {
        orgCounts = new HashMap<>();
        for (Organization o : organizationDao.findAll()) {
            orgCounts.merge(o.getEnterpriseId(), 1, Integer::sum);
        }
        return enterpriseDao.findAll();
    }

    @Override
    protected Object[] toRow(Enterprise e) {
        return new Object[]{e.getId(), e.getName(), e.getType(),
            orgCounts.getOrDefault(e.getId(), 0), e.getContribution()};
    }

    @Override
    protected JPanel buildForm() {
        return new FormBuilder()
                .add("Name *", nameField)
                .add("Type *", typeCombo)
                .addFull(contributionLabel)
                .addFull(UIStyle.muted("<html>The type cannot change after creation, because it decides "
                        + "which organizations and roles belong to the enterprise.</html>"))
                .build();
    }

    private void showContribution() {
        EnterpriseType t = (EnterpriseType) typeCombo.getSelectedItem();
        if (t != null) {
            contributionLabel.setText("<html><i>" + Enterprise.of(0, "", t).getContribution() + "</i></html>");
        }
    }

    @Override
    protected void fillForm(Enterprise e) {
        nameField.setText(e.getName());
        typeCombo.setSelectedItem(e.getType());
    }

    @Override
    protected void clearForm() {
        nameField.setText("");
        typeCombo.setSelectedIndex(0);
    }

    @Override
    protected void onModeChanged(boolean editing) {
        typeCombo.setEnabled(!editing);
    }

    @Override
    protected Enterprise readForm(Enterprise existing) {
        String name = nameField.getText().trim();
        if (Validator.isBlank(name)) {
            Dialogs.warn(this, "Enterprise name is required.");
            return null;
        }
        if (name.length() > 100) {
            Dialogs.warn(this, "Enterprise name must be 100 characters or fewer.");
            return null;
        }
        int id = existing == null ? 0 : existing.getId();
        if (enterpriseDao.nameExists(name, id)) {
            Dialogs.warn(this, "An enterprise named \"" + name + "\" already exists.");
            return null;
        }
        if (existing == null) {
            return Enterprise.of(0, name, (EnterpriseType) typeCombo.getSelectedItem());
        }
        existing.setName(name);
        return existing;
    }

    @Override
    protected void create(Enterprise e) {
        enterpriseDao.create(e);
    }

    @Override
    protected void update(Enterprise e) {
        enterpriseDao.update(e);
    }

    @Override
    protected void delete(Enterprise e) {
        enterpriseDao.delete(e.getId());
    }

    @Override
    protected String describe(Enterprise e) {
        return e.getName();
    }

    @Override
    protected String deleteWarning(Enterprise e) {
        int count = orgCounts.getOrDefault(e.getId(), 0);
        return count == 0 ? "" : "This also deletes its " + count
                + " organization(s), their users, food listings and requests.";
    }
}
