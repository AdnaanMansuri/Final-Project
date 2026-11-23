package edu.neu.foodrescue.ui.supplier;

import edu.neu.foodrescue.dao.FoodItemDao;
import edu.neu.foodrescue.model.FoodItem;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.FoodCategory;
import edu.neu.foodrescue.model.enums.FoodStatus;
import edu.neu.foodrescue.service.FoodListingService;
import edu.neu.foodrescue.ui.common.CrudPanel;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.FormBuilder;
import edu.neu.foodrescue.ui.common.UIStyle;
import edu.neu.foodrescue.ui.common.UrgencyRowRenderer;
import edu.neu.foodrescue.util.Formats;
import edu.neu.foodrescue.util.Validator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

/**
 * Store Staff: post surplus food (creates WR1 for the manager), edit, delete,
 * and watch the status of every listing from their store.
 */
public class MyListingsPanel extends CrudPanel<FoodItem> {

    private static final String[] UNITS = {"kg", "loaves", "items", "boxes", "trays", "liters", "packs"};

    private final UserAccount user;
    private final FoodItemDao foodDao = new FoodItemDao();
    private final FoodListingService service = new FoodListingService();

    private final JTextField nameField = new JTextField();
    private final JComboBox<FoodCategory> categoryCombo = new JComboBox<>(FoodCategory.values());
    private final JTextField quantityField = new JTextField();
    private final JComboBox<String> unitCombo = new JComboBox<>(UNITS);
    private final JTextField allergensField = new JTextField();
    private final JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
    private final JLabel statusLabel = UIStyle.muted(" ");

    public MyListingsPanel(UserAccount user) {
        super("Surplus food from " + user.getOrganizationName(),
                new String[]{"ID", "Item", "Category", "Quantity", "Allergens", "Pickup by", "Time left", "Status"});
        this.user = user;
        unitCombo.setEditable(true);
        deadlineSpinner.setEditor(new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd HH:mm"));
        table.setDefaultRenderer(Object.class, new UrgencyRowRenderer<>(this::itemAt,
                f -> f.getStatus() == FoodStatus.AVAILABLE || f.getStatus() == FoodStatus.PENDING_APPROVAL));
        init();
    }

    @Override
    protected List<FoodItem> loadItems() {
        return foodDao.findBySupplierOrg(user.getOrganizationId());
    }

    @Override
    protected Object[] toRow(FoodItem f) {
        return new Object[]{f.getId(), f.getName(), f.getCategory(), Formats.qty(f.getQuantity()) + " " + f.getUnit(),
            f.getAllergens() == null ? "-" : f.getAllergens(), Formats.dateTime(f.getPickupDeadline()),
            Formats.timeLeft(f), f.getStatus()};
    }

    @Override
    protected JPanel buildForm() {
        return new FormBuilder()
                .add("Item name *", nameField)
                .add("Category *", categoryCombo)
                .add("Quantity *", quantityField)
                .add("Unit *", unitCombo)
                .add("Allergens", allergensField)
                .add("Pickup by *", deadlineSpinner)
                .addFull(statusLabel)
                .addFull(UIStyle.muted("<html>New and edited listings go to your Supplier Manager "
                        + "for approval before charities can see them.</html>"))
                .build();
    }

    @Override
    protected void fillForm(FoodItem f) {
        nameField.setText(f.getName());
        categoryCombo.setSelectedItem(f.getCategory());
        quantityField.setText(Formats.qty(f.getQuantity()));
        unitCombo.setSelectedItem(f.getUnit());
        allergensField.setText(f.getAllergens() == null ? "" : f.getAllergens());
        deadlineSpinner.setValue(Date.from(f.getPickupDeadline().atZone(ZoneId.systemDefault()).toInstant()));
        statusLabel.setText("Status: " + f.getStatus() + (FoodListingService.isEditable(f) ? "" : "  (locked)"));
    }

    @Override
    protected void clearForm() {
        nameField.setText("");
        categoryCombo.setSelectedIndex(0);
        quantityField.setText("");
        unitCombo.setSelectedIndex(0);
        allergensField.setText("");
        // default: pick up within 4 hours
        deadlineSpinner.setValue(new Date(System.currentTimeMillis() + 4L * 60 * 60 * 1000));
        statusLabel.setText(" ");
    }

    @Override
    protected boolean canModify(FoodItem f) {
        return FoodListingService.isEditable(f);
    }

    @Override
    protected boolean canDelete(FoodItem f) {
        return FoodListingService.isDeletable(f);
    }

    @Override
    protected FoodItem readForm(FoodItem existing) {
        String name = nameField.getText().trim();
        double qty = Validator.parsePositive(quantityField.getText());
        Object unitValue = unitCombo.getEditor().getItem();
        String unit = unitValue == null ? "" : unitValue.toString().trim();
        String allergens = allergensField.getText().trim();
        LocalDateTime deadline = LocalDateTime.ofInstant(((Date) deadlineSpinner.getValue()).toInstant(),
                ZoneId.systemDefault()).withSecond(0).withNano(0);

        if (Validator.isBlank(name) || name.length() > 100) {
            Dialogs.warn(this, "Item name is required (max 100 characters).");
            return null;
        }
        if (qty <= 0 || qty > 100000) {
            Dialogs.warn(this, "Quantity must be a positive number.");
            return null;
        }
        if (Validator.isBlank(unit) || unit.length() > 20) {
            Dialogs.warn(this, "Unit is required (max 20 characters), e.g. kg or loaves.");
            return null;
        }
        if (allergens.length() > 200) {
            Dialogs.warn(this, "Allergens must be 200 characters or fewer.");
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (deadline.isBefore(now.plusMinutes(30))) {
            Dialogs.warn(this, "Pickup deadline must be at least 30 minutes from now.");
            return null;
        }
        if (deadline.isAfter(now.plusDays(7))) {
            Dialogs.warn(this, "This network is for last-minute surplus: the pickup deadline must be within 7 days.");
            return null;
        }

        FoodItem f = existing == null ? new FoodItem() : existing;
        f.setName(name);
        f.setCategory((FoodCategory) categoryCombo.getSelectedItem());
        f.setQuantity(qty);
        f.setUnit(unit);
        f.setAllergens(allergens.isEmpty() ? null : allergens);
        f.setPickupDeadline(deadline);
        return f;
    }

    @Override
    protected void create(FoodItem f) {
        service.postListing(user, f);
    }

    @Override
    protected void update(FoodItem f) {
        service.editListing(user, f);
    }

    @Override
    protected void delete(FoodItem f) {
        service.deleteListing(f);
    }

    @Override
    protected String describe(FoodItem f) {
        return f.getName();
    }

    @Override
    protected String addedMessage(FoodItem f) {
        return "\"" + f.getName() + "\" was posted and sent to your Supplier Manager for approval.";
    }

    @Override
    protected String updatedMessage(FoodItem f) {
        return "\"" + f.getName() + "\" was updated and is waiting for approval again.";
    }
}
