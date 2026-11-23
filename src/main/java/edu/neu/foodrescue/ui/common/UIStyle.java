package edu.neu.foodrescue.ui.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

/** One place for colors, fonts and component styling so every screen looks consistent. */
public final class UIStyle {

    public static final Color PRIMARY = new Color(0x2E7D32);
    public static final Color PRIMARY_DARK = new Color(0x1B5E20);
    public static final Color PRIMARY_LIGHT = new Color(0xE8F5E9);
    public static final Color DANGER = new Color(0xC62828);
    public static final Color AMBER = new Color(0xF9A825);
    public static final Color BACKGROUND = new Color(0xF5F7F5);
    public static final Color TEXT_MUTED = new Color(0x616161);

    public static final Color URGENT_RED_BG = new Color(0xFFEBEE);
    public static final Color URGENT_AMBER_BG = new Color(0xFFF8E1);
    public static final Color URGENT_GREEN_BG = new Color(0xE8F5E9);

    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private UIStyle() { }

    /** Uses FlatLaf if it is on the classpath, otherwise Nimbus, otherwise the default look. */
    public static void installLookAndFeel() {
        String[] candidates = {
            "com.formdev.flatlaf.FlatLightLaf",
            "javax.swing.plaf.nimbus.NimbusLookAndFeel"
        };
        for (String laf : candidates) {
            try {
                UIManager.setLookAndFeel(laf);
                break;
            } catch (Exception ignored) {
                // try next
            }
        }
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
    }

    public static JButton primaryButton(String text) {
        return button(text, PRIMARY, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return button(text, DANGER, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return button(text, Color.WHITE, PRIMARY_DARK);
    }

    private static JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(BODY.deriveFont(Font.BOLD));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.equals(Color.WHITE) ? PRIMARY : bg),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        b.setOpaque(true);
        return b;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(TITLE);
        l.setForeground(PRIMARY_DARK);
        return l;
    }

    public static JLabel subtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SUBTITLE);
        l.setForeground(PRIMARY_DARK);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(SMALL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static void styleTable(JTable table) {
        table.setFont(BODY);
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xE0E0E0));
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(BODY.deriveFont(Font.BOLD));
        header.setBackground(PRIMARY_LIGHT);
        header.setForeground(PRIMARY_DARK);
        header.setReorderingAllowed(false);
    }
}
