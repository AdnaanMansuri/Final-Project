package edu.neu.foodrescue.ui.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Builds a two-column "Label | Field" form with GridBagLayout. */
public class FormBuilder {

    private final JPanel panel = new JPanel(new GridBagLayout());
    private int row = 0;

    public FormBuilder() {
        panel.setOpaque(false);
    }

    /** Adds a labelled row. Put a * at the end of the label for required fields. */
    public FormBuilder add(String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0;
        l.gridy = row;
        l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(6, 0, 6, 12);
        JLabel jl = new JLabel(label);
        jl.setFont(UIStyle.BODY);
        panel.add(jl, l);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1;
        f.gridy = row++;
        f.weightx = 1;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(6, 0, 6, 0);
        field.setFont(UIStyle.BODY);
        panel.add(field, f);
        return this;
    }

    /** Adds a component spanning both columns (e.g. a hint label). */
    public FormBuilder addFull(JComponent c) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = row++;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 0, 4, 0);
        panel.add(c, g);
        return this;
    }

    public JPanel build() {
        return panel;
    }
}
