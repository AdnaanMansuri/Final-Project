package edu.neu.foodrescue.ui.common;

import edu.neu.foodrescue.model.enums.RequestStatus;
import edu.neu.foodrescue.model.workrequest.WorkRequest;
import edu.neu.foodrescue.util.DataAccessException;
import edu.neu.foodrescue.util.Formats;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Reusable inbox for any work request type: a table of requests plus Approve / Reject buttons.
 * The caller supplies how to load requests, what to show in the details column,
 * and what approving / rejecting actually does (usually a service call).
 */
public class RequestInboxPanel extends JPanel implements Refreshable {

    /** What happens when the user approves or rejects a request. */
    @FunctionalInterface
    public interface RequestAction {
        void apply(WorkRequest request, String note);
    }

    private final Supplier<List<WorkRequest>> loader;
    private final Function<WorkRequest, String> details;
    private final RequestAction approveAction;
    private final RequestAction rejectAction;

    private final DefaultTableModel model;
    private final JTable table;
    private final JCheckBox pendingOnly = new JCheckBox("Show pending only", true);
    private final JButton approveButton;
    private final JButton rejectButton = UIStyle.dangerButton("Reject");
    private List<WorkRequest> all = new ArrayList<>();
    private List<WorkRequest> shown = new ArrayList<>();
    private boolean reloading;

    public RequestInboxPanel(String title, String detailsHeader, String approveLabel,
                             Supplier<List<WorkRequest>> loader, Function<WorkRequest, String> details,
                             RequestAction approveAction, RequestAction rejectAction) {
        super(new BorderLayout(0, 8));
        this.loader = loader;
        this.details = details;
        this.approveAction = approveAction;
        this.rejectAction = rejectAction;
        approveButton = UIStyle.primaryButton(approveLabel);

        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        model = new DefaultTableModel(new String[]{"ID", "Request", detailsHeader, "From", "Status", "Sent", "Notes"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIStyle.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !reloading) {
                updateButtons();
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIStyle.subtitle(title), BorderLayout.WEST);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);
        pendingOnly.setOpaque(false);
        pendingOnly.addActionListener(e -> fillTable());
        JButton reload = UIStyle.secondaryButton("Refresh");
        reload.addActionListener(e -> refresh());
        filters.add(pendingOnly);
        filters.add(reload);
        top.add(filters, BorderLayout.EAST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setOpaque(false);
        buttons.add(approveButton);
        buttons.add(rejectButton);
        approveButton.addActionListener(e -> act(true));
        rejectButton.addActionListener(e -> act(false));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            all = loader.get();
        } catch (DataAccessException ex) {
            all = new ArrayList<>();
            Dialogs.error(this, ex.getMessage());
        }
        fillTable();
    }

    private void fillTable() {
        reloading = true;
        try {
            table.clearSelection();
            model.setRowCount(0);
            shown = new ArrayList<>();
            for (WorkRequest r : all) {
                if (pendingOnly.isSelected() && r.getStatus() != RequestStatus.PENDING) {
                    continue;
                }
                shown.add(r);
                model.addRow(new Object[]{r.getId(), r.getSummary(), details.apply(r), r.getSenderName(),
                    r.getStatus(), Formats.dateTime(r.getRequestDate()), r.getMessage() == null ? "" : r.getMessage()});
            }
        } finally {
            reloading = false;
        }
        updateButtons();
    }

    private WorkRequest selected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0 || viewRow >= table.getRowCount()) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return modelRow < shown.size() ? shown.get(modelRow) : null;
    }

    private void updateButtons() {
        WorkRequest r = selected();
        boolean actionable = r != null && r.getStatus() == RequestStatus.PENDING;
        approveButton.setEnabled(actionable);
        rejectButton.setEnabled(actionable);
    }

    private void act(boolean approve) {
        WorkRequest r = selected();
        if (r == null) {
            return;
        }
        String note = Dialogs.askText(this, approve
                ? "Optional note for \"" + r.getSummary() + "\":"
                : "Reason for rejecting \"" + r.getSummary() + "\" (required):");
        if (note == null) {
            return; // cancelled
        }
        if (!approve && note.isBlank()) {
            Dialogs.warn(this, "Please give a reason so the sender knows what to change.");
            return;
        }
        try {
            (approve ? approveAction : rejectAction).apply(r, note.trim());
            Dialogs.info(this, "Request #" + r.getId() + " " + (approve ? "approved." : "rejected."));
        } catch (IllegalStateException ex) {
            Dialogs.warn(this, ex.getMessage());
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        }
        refresh();
    }

    /** Colors the status text so pending items stand out. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
            c.setFont(c.getFont().deriveFont(Font.BOLD));
            if (!sel && value instanceof RequestStatus s) {
                c.setForeground(switch (s) {
                    case PENDING -> new Color(0xE65100);
                    case ACCEPTED, COMPLETED -> UIStyle.PRIMARY_DARK;
                    case REJECTED -> UIStyle.DANGER;
                    case EXPIRED -> UIStyle.TEXT_MUTED;
                });
            }
            return c;
        }
    }
}
