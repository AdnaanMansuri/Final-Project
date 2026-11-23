package edu.neu.foodrescue.ui.supplier;

import edu.neu.foodrescue.dao.FoodItemDao;
import edu.neu.foodrescue.model.FoodItem;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.FoodStatus;
import edu.neu.foodrescue.model.workrequest.FoodClaimRequest;
import edu.neu.foodrescue.service.FoodListingService;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.Refreshable;
import edu.neu.foodrescue.ui.common.UIStyle;
import edu.neu.foodrescue.util.DataAccessException;
import edu.neu.foodrescue.util.Formats;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Store Staff: food that charities have claimed; mark it picked up when the charity collects it. */
public class PickupsPanel extends JPanel implements Refreshable {

    private final UserAccount user;
    private final FoodItemDao foodDao = new FoodItemDao();
    private final FoodListingService service = new FoodListingService();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Item", "Quantity", "Charity", "Pickup time", "Deadline"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JButton pickedUpButton = UIStyle.primaryButton("Mark as Picked Up");
    private List<FoodItem> items = new ArrayList<>();
    private boolean reloading;

    public PickupsPanel(UserAccount user) {
        super(new BorderLayout(0, 8));
        this.user = user;
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        UIStyle.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !reloading) {
                pickedUpButton.setEnabled(selected() != null);
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIStyle.subtitle("Claimed food waiting for pickup"), BorderLayout.WEST);
        JButton reload = UIStyle.secondaryButton("Refresh");
        reload.addActionListener(e -> refresh());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(reload);
        top.add(right, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bottom.setOpaque(false);
        bottom.add(pickedUpButton);
        bottom.add(UIStyle.muted("Confirm when the charity has collected the food. It then counts as rescued."));
        pickedUpButton.addActionListener(e -> markPickedUp());

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        refresh();
    }

    @Override
    public void refresh() {
        reloading = true;
        try {
            table.clearSelection();
            model.setRowCount(0);
            items = new ArrayList<>();
            for (FoodItem f : foodDao.findBySupplierOrg(user.getOrganizationId())) {
                if (f.getStatus() != FoodStatus.CLAIMED) {
                    continue;
                }
                FoodClaimRequest claim = service.findAcceptedClaim(f.getId());
                items.add(f);
                model.addRow(new Object[]{f.getId(), f.getName(), Formats.qty(f.getQuantity()) + " " + f.getUnit(),
                    claim == null || claim.getCharityOrgName() == null ? "-" : claim.getCharityOrgName(),
                    claim == null ? "-" : Formats.dateTime(claim.getPickupTime()),
                    Formats.dateTime(f.getPickupDeadline())});
            }
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        } finally {
            reloading = false;
        }
        pickedUpButton.setEnabled(false);
    }

    private FoodItem selected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0 || viewRow >= table.getRowCount()) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return modelRow < items.size() ? items.get(modelRow) : null;
    }

    private void markPickedUp() {
        FoodItem f = selected();
        if (f == null || !Dialogs.confirm(this, "Confirm that \"" + f.getName() + "\" has been collected?")) {
            return;
        }
        try {
            service.markPickedUp(f);
            Dialogs.info(this, "\"" + f.getName() + "\" marked as picked up. Thank you for rescuing food!");
        } catch (IllegalStateException ex) {
            Dialogs.warn(this, ex.getMessage());
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        }
        refresh();
    }
}
