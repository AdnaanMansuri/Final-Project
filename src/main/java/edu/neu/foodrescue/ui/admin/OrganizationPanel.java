package edu.neu.foodrescue.ui.admin;

import edu.neu.foodrescue.dao.EnterpriseDao;
import edu.neu.foodrescue.dao.OrganizationDao;
import edu.neu.foodrescue.dao.UserAccountDao;
import edu.neu.foodrescue.model.Enterprise;
import edu.neu.foodrescue.model.Organization;
import edu.neu.foodrescue.model.enums.OrganizationType;
import edu.neu.foodrescue.ui.common.CrudPanel;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.FormBuilder;
import edu.neu.foodrescue.util.Validator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** System Admin: manage the bakeries, stores, food banks and shelters inside each enterprise. */
public class OrganizationPanel extends CrudPanel<Organization> {

    private final OrganizationDao organizationDao = new OrganizationDao();
    private final EnterpriseDao enterpriseDao = new EnterpriseDao();
    private final UserAccountDao userDao = new UserAccountDao();

    private final JComboBox<Enterprise> enterpriseCombo = new JComboBox<>();
    private final JComboBox<OrganizationType> typeCombo = new JComboBox<>();
    private final JTextField nameField = new JTextField();
    private final JTextField addressField = new JTextField();

    public OrganizationPanel() {
        super("Organizations", new String[]{"ID", "Name", "Type", "Enterprise", "Address"});
        enterpriseCombo.addActionListener(e -> reloadTypes());
        init();
    }

    @Override
    protected void beforeRefresh() {
        Object current = enterpriseCombo.getSelectedItem();
        enterpriseCombo.setModel(new DefaultComboBoxModel<>(enterpriseDao.findAll().toArray(new Enterprise[0])));
        if (current instanceof Enterprise ce) {
            selectEnterprise(ce.getId());
        }
        reloadTypes();
    }

    /** Only organization types that the chosen enterprise accepts (polymorphic Enterprise.accepts). */
    private void reloadTypes() {
        Object currentType = typeCombo.getSelectedItem();
        DefaultComboBoxModel<OrganizationType> m = new DefaultComboBoxModel<>();
        if (enterpriseCombo.getSelectedItem() instanceof Enterprise ent) {
            for (OrganizationType t : OrganizationType.values()) {
                if (ent.accepts(t)) {
                    m.addElement(t);
                }
            }
        }
        typeCombo.setModel(m);
        if (currentType != null) {
            typeCombo.setSelectedItem(currentType);
        }
    }

    private void selectEnterprise(int id) {
        for (int i = 0; i < enterpriseCombo.getItemCount(); i++) {
            if (enterpriseCombo.getItemAt(i).getId() == id) {
                enterpriseCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    protected List<Organization> loadItems() {
        return organizationDao.findAll();
    }

    @Override
    protected Object[] toRow(Organization o) {
        return new Object[]{o.getId(), o.getName(), o.getType(), o.getEnterpriseName(), o.getAddress()};
    }

    @Override
    protected JPanel buildForm() {
        return new FormBuilder()
                .add("Enterprise *", enterpriseCombo)
                .add("Type *", typeCombo)
                .add("Name *", nameField)
                .add("Address", addressField)
                .build();
    }

    @Override
    protected void fillForm(Organization o) {
        selectEnterprise(o.getEnterpriseId());
        reloadTypes();
        typeCombo.setSelectedItem(o.getType());
        nameField.setText(o.getName());
        addressField.setText(o.getAddress() == null ? "" : o.getAddress());
    }

    @Override
    protected void clearForm() {
        if (enterpriseCombo.getItemCount() > 0) {
            enterpriseCombo.setSelectedIndex(0);
        }
        reloadTypes();
        nameField.setText("");
        addressField.setText("");
    }

    /** Moving an organization to another enterprise would orphan its users' roles, so it is locked on edit. */
    @Override
    protected void onModeChanged(boolean editing) {
        enterpriseCombo.setEnabled(!editing);
    }

    @Override
    protected Organization readForm(Organization existing) {
        if (!(enterpriseCombo.getSelectedItem() instanceof Enterprise ent)) {
            Dialogs.warn(this, "Create an enterprise first.");
            return null;
        }
        OrganizationType type = (OrganizationType) typeCombo.getSelectedItem();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        if (type == null) {
            Dialogs.warn(this, "Please choose an organization type.");
            return null;
        }
        if (Validator.isBlank(name)) {
            Dialogs.warn(this, "Organization name is required.");
            return null;
        }
        if (name.length() > 100 || address.length() > 200) {
            Dialogs.warn(this, "Name must be at most 100 characters and address at most 200.");
            return null;
        }
        Organization o = existing == null ? new Organization() : existing;
        o.setEnterpriseId(ent.getId());
        o.setType(type);
        o.setName(name);
        o.setAddress(address.isEmpty() ? null : address);
        return o;
    }

    @Override
    protected void create(Organization o) {
        organizationDao.create(o);
    }

    @Override
    protected void update(Organization o) {
        organizationDao.update(o);
    }

    @Override
    protected void delete(Organization o) {
        organizationDao.delete(o.getId());
    }

    @Override
    protected String describe(Organization o) {
        return o.getName();
    }

    @Override
    protected String deleteWarning(Organization o) {
        int users = userDao.findByOrganization(o.getId()).size();
        return users == 0 ? "" : "This also deletes its " + users + " user account(s) and their listings/requests.";
    }
}
