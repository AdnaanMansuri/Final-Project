package edu.neu.foodrescue.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** A small "big number + label" tile used on overview and dashboard screens. */
public class StatCard extends JPanel {

    private final JLabel valueLabel = new JLabel("-");

    public StatCard(String label, Color accent) {
        super(new BorderLayout(0, 4));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accent);
        JLabel name = UIStyle.muted(label);
        name.setFont(UIStyle.BODY);
        add(valueLabel, BorderLayout.CENTER);
        add(name, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
