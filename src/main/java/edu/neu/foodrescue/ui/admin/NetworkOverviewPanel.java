package edu.neu.foodrescue.ui.admin;

import edu.neu.foodrescue.dao.EnterpriseDao;
import edu.neu.foodrescue.dao.FoodItemDao;
import edu.neu.foodrescue.dao.OrganizationDao;
import edu.neu.foodrescue.dao.UserAccountDao;
import edu.neu.foodrescue.dao.WorkRequestDao;
import edu.neu.foodrescue.model.Enterprise;
import edu.neu.foodrescue.model.FoodItem;
import edu.neu.foodrescue.model.Organization;
import edu.neu.foodrescue.model.UserAccount;
import edu.neu.foodrescue.model.enums.FoodStatus;
import edu.neu.foodrescue.model.enums.RequestStatus;
import edu.neu.foodrescue.ui.common.Dialogs;
import edu.neu.foodrescue.ui.common.Refreshable;
import edu.neu.foodrescue.ui.common.StatCard;
import edu.neu.foodrescue.ui.common.UIStyle;
import edu.neu.foodrescue.util.DataAccessException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** System Admin: read-only summary of the whole network and each enterprise. */
public class NetworkOverviewPanel extends JPanel implements Refreshable {

    private final StatCard enterprisesCard = new StatCard("Enterprises", UIStyle.PRIMARY);
    private final StatCard orgsCard = new StatCard("Organizations", UIStyle.PRIMARY);
    private final StatCard usersCard = new StatCard("User accounts", UIStyle.PRIMARY);
    private final StatCard availableCard = new StatCard("Listings available now", UIStyle.AMBER);
    private final StatCard rescuedCard = new StatCard("Units of food rescued", UIStyle.PRIMARY_DARK);
    private final StatCard pendingCard = new StatCard("Pending work requests", UIStyle.DANGER);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Enterprise", "Type", "Organizations", "Users", "Food listings", "Units rescued"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    public NetworkOverviewPanel() {
        super(new BorderLayout(0, 12));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIStyle.subtitle("Network Overview"), BorderLayout.WEST);
        JButton reload = UIStyle.secondaryButton("Refresh");
        reload.addActionListener(e -> refresh());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(reload);
        top.add(right, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(2, 3, 12, 12));
        cards.setOpaque(false);
        cards.add(enterprisesCard);
        cards.add(orgsCard);
        cards.add(usersCard);
        cards.add(availableCard);
        cards.add(rescuedCard);
        cards.add(pendingCard);

        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(cards, BorderLayout.CENTER);

        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(UIStyle.subtitle("By enterprise"), BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            List<Enterprise> enterprises = new EnterpriseDao().findAll();
            List<Organization> orgs = new OrganizationDao().findAll();
            List<UserAccount> users = new UserAccountDao().findAll();
            List<FoodItem> food = new FoodItemDao().findAll();
            WorkRequestDao requestDao = new WorkRequestDao();

            Map<Integer, Integer> orgToEnterprise = new HashMap<>();
            Map<Integer, Integer> orgCount = new HashMap<>();
            for (Organization o : orgs) {
                orgToEnterprise.put(o.getId(), o.getEnterpriseId());
                orgCount.merge(o.getEnterpriseId(), 1, Integer::sum);
            }
            Map<Integer, Integer> userCount = new HashMap<>();
            for (UserAccount u : users) {
                if (u.getEnterpriseId() != null) {
                    userCount.merge(u.getEnterpriseId(), 1, Integer::sum);
                }
            }
            Map<Integer, Integer> listingCount = new HashMap<>();
            Map<Integer, Double> rescued = new HashMap<>();
            int available = 0;
            double rescuedTotal = 0;
            for (FoodItem f : food) {
                Integer ent = orgToEnterprise.get(f.getSupplierOrgId());
                listingCount.merge(ent, 1, Integer::sum);
                if (f.getStatus() == FoodStatus.AVAILABLE && !f.isExpired()) {
                    available++;
                }
                if (f.getStatus() == FoodStatus.PICKED_UP) {
                    rescued.merge(ent, f.getQuantity(), Double::sum);
                    rescuedTotal += f.getQuantity();
                }
            }

            enterprisesCard.setValue(String.valueOf(enterprises.size()));
            orgsCard.setValue(String.valueOf(orgs.size()));
            usersCard.setValue(String.valueOf(users.size()));
            availableCard.setValue(String.valueOf(available));
            rescuedCard.setValue(String.format("%.0f", rescuedTotal));
            pendingCard.setValue(String.valueOf(requestDao.countByStatus(RequestStatus.PENDING)));

            model.setRowCount(0);
            for (Enterprise e : enterprises) {
                model.addRow(new Object[]{e.getName(), e.getType(),
                    orgCount.getOrDefault(e.getId(), 0),
                    userCount.getOrDefault(e.getId(), 0),
                    listingCount.getOrDefault(e.getId(), 0),
                    String.format("%.0f", rescued.getOrDefault(e.getId(), 0.0))});
            }
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}
