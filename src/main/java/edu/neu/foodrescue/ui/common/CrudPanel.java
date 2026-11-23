package edu.neu.foodrescue.ui.common;

import edu.neu.foodrescue.util.DataAccessException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

/**
 * Reusable "table on the left, form on the right" screen with Add / Update / Delete / Clear.
 * Template Method pattern: this class runs the workflow (load, select, validate, save, refresh,
 * error handling) and subclasses only supply the entity-specific steps.
 *
 * Subclasses must call init() at the end of their constructor.
 */
public abstract class CrudPanel<T> extends JPanel implements Refreshable {

    private final DefaultTableModel model;
    protected final JTable table;
    private List<T> items = new ArrayList<>();
    private T selected;

    private final JButton addButton = UIStyle.primaryButton("Add");
    private final JButton updateButton = UIStyle.primaryButton("Update");
    private final JButton deleteButton = UIStyle.dangerButton("Delete");
    private final JButton clearButton = UIStyle.secondaryButton("Clear");
    private final String title;

    protected CrudPanel(String title, String[] columns) {
        this.title = title;
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        UIStyle.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ---------- steps supplied by subclasses ----------

    protected abstract List<T> loadItems();

    protected abstract Object[] toRow(T item);

    protected abstract JPanel buildForm();

    protected abstract void fillForm(T item);

    protected abstract void clearForm();

    /**
     * Reads and validates the form. Returns null (after showing a message) if invalid.
     * existing is null when adding, or the selected item when updating.
     */
    protected abstract T readForm(T existing);

    protected abstract void create(T item);

    protected abstract void update(T item);

    protected abstract void delete(T item);

    protected String describe(T item) {
        return item.toString();
    }

    /** Extra warning shown before delete (e.g. what else will be removed). */
    protected String deleteWarning(T item) {
        return "";
    }

    /** Hook: reload combo boxes etc. before the table is reloaded. */
    protected void beforeRefresh() { }

    /** Hook: enable/disable fields when switching between add and edit mode. */
    protected void onModeChanged(boolean editing) { }

    /** Hook: return false to block update/delete for a given item. */
    protected boolean canModify(T item) {
        return true;
    }

    // ---------- template ----------

    protected final void init() {
        setLayout(new BorderLayout(12, 0));
        setBackground(UIStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel left = new JPanel(new BorderLayout(0, 8));
        left.setOpaque(false);
        left.add(UIStyle.subtitle(title), BorderLayout.NORTH);
        left.add(new JScrollPane(table), BorderLayout.CENTER);
        add(left, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(0, 12));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC8E6C9)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        right.setPreferredSize(new Dimension(380, 0));
        right.add(UIStyle.subtitle("Details"), BorderLayout.NORTH);
        right.add(buildForm(), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setOpaque(false);
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        right.add(buttons, BorderLayout.SOUTH);

        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(right, BorderLayout.NORTH);
        add(rightWrap, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onSelect();
            }
        });
        addButton.addActionListener(e -> doAdd());
        updateButton.addActionListener(e -> doUpdate());
        deleteButton.addActionListener(e -> doDelete());
        clearButton.addActionListener(e -> table.clearSelection());

        setSelected(null);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            beforeRefresh();
            items = loadItems();
        } catch (DataAccessException ex) {
            items = new ArrayList<>();
            Dialogs.error(this, ex.getMessage());
        }
        table.clearSelection();
        model.setRowCount(0);
        for (T item : items) {
            model.addRow(toRow(item));
        }
        setSelected(null);
    }

    protected T getSelected() {
        return selected;
    }

    private void onSelect() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            setSelected(null);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        setSelected(modelRow < items.size() ? items.get(modelRow) : null);
    }

    private void setSelected(T item) {
        selected = item;
        if (item == null) {
            clearForm();
        } else {
            fillForm(item);
        }
        boolean editing = item != null;
        boolean modifiable = editing && canModify(item);
        addButton.setEnabled(!editing);
        updateButton.setEnabled(modifiable);
        deleteButton.setEnabled(modifiable);
        onModeChanged(editing);
    }

    private void doAdd() {
        T item = readForm(null);
        if (item == null) {
            return;
        }
        try {
            create(item);
            Dialogs.info(this, "Added: " + describe(item));
            refresh();
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private void doUpdate() {
        if (selected == null) {
            return;
        }
        T item = readForm(selected);
        if (item == null) {
            return;
        }
        try {
            update(item);
            Dialogs.info(this, "Updated: " + describe(item));
            refresh();
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private void doDelete() {
        if (selected == null) {
            return;
        }
        String warning = deleteWarning(selected);
        String msg = "Delete " + describe(selected) + "?" + (warning.isEmpty() ? "" : "\n\n" + warning);
        if (!Dialogs.confirm(this, msg)) {
            return;
        }
        try {
            delete(selected);
            refresh();
        } catch (DataAccessException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }
}
