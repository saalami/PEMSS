package ca.ucalgary.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Soheila on 2015-04-27.
 */
public class MapFrame extends JFrame {
    public MapFrame() throws HeadlessException {
        super("Evacuation");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setExtendedState(MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
    }
}
