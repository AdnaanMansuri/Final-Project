package edu.neu.foodrescue.ui.common;

import edu.neu.foodrescue.model.Expirable;
import java.awt.Color;
import java.awt.Component;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Colors whole table rows by urgency: red under 2 hours, amber under 6 hours, green otherwise.
 * Only rows matching the "highlight" rule are colored (e.g. open listings, not finished ones).
 */
public class UrgencyRowRenderer<T extends Expirable> extends DefaultTableCellRenderer {

    private final IntFunction<T> itemAtModelRow;
    private final Predicate<T> highlight;

    public UrgencyRowRenderer(IntFunction<T> itemAtModelRow, Predicate<T> highlight) {
        this.itemAtModelRow = itemAtModelRow;
        this.highlight = highlight;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (isSelected) {
            return c;
        }
        Color bg = Color.WHITE;
        if (row < table.getRowCount()) {
            T item = itemAtModelRow.apply(table.convertRowIndexToModel(row));
            if (item != null && highlight.test(item)) {
                bg = switch (item.getUrgency()) {
                    case RED -> UIStyle.URGENT_RED_BG;
                    case AMBER -> UIStyle.URGENT_AMBER_BG;
                    case GREEN -> UIStyle.URGENT_GREEN_BG;
                };
            }
        }
        c.setBackground(bg);
        c.setForeground(Color.BLACK);
        return c;
    }
}
