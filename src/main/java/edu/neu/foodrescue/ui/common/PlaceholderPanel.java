package edu.neu.foodrescue.ui.common;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Temporary tab content until the feature is built. */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String feature) {
        super(new GridBagLayout());
        setBackground(UIStyle.BACKGROUND);
        JLabel l = UIStyle.muted(feature + " - coming soon");
        l.setFont(UIStyle.SUBTITLE);
        add(l);
    }
}
