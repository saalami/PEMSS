package ca.ucalgary.ui;

import ca.ucalgary.*;
import ca.ucalgary.algorithms.Distributor;
import ca.ucalgary.algorithms.MaxFlowAlgorithm;
import ca.ucalgary.algorithms.ShortestPathAlgorithm;
import ca.ucalgary.algorithms.TimeExpandedAlgorithm;
import ca.ucalgary.mapgraph.*;
import ca.ucalgary.sim.Zone;
import ca.ucalgary.sim.SimpleDemandEstimator;
import ca.ucalgary.sim.Simulator;
import com.camick.WrapLayout;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void createAndShowGUI() throws ParserConfigurationException, SAXException, IOException {

        final MapGraph mapGraph = new MapParser().parse(new File("maps/map.xml"));
        System.out.println(mapGraph.getBounds());
        MapFrame frame = new MapFrame();
        final MapPanel mapPanel = new MapPanel(mapGraph);
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        frame.add(scrollPane);

        // ----
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new WrapLayout());

        WayType[] wayTypes = WayType.values();
        for (WayType type:wayTypes) {
            JCheckBox checkBox = new JCheckBox(type.name());

            checkBox.addActionListener(e -> {
                JCheckBox cb = (JCheckBox) e.getSource();
                mapPanel.setShowWay(WayType.valueOf(cb.getText()), cb.isSelected());
                SwingUtilities.invokeLater(() -> mapPanel.repaint());
            });

            selectionPanel.add(checkBox);
            if (type.getCapacity() > 0) {
                checkBox.setSelected(true);
                mapPanel.setShowWay(type, true);
            }
        }

        JButton addSafeZoneBtn = new JButton("Add Safe Zone");
        JButton addHotZoneBtn = new JButton("Add Hot Zone");
        JButton cancelZoneBtn = new JButton("Cancel Zone");
        JButton maxFlowBtn = new JButton("Max Flow");
        JButton shortestPathBtn = new JButton("Shortest Path");
        JButton timeExpandedBtn = new JButton("Augmented Graph");

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);
        JTextField numOfEvacueesTxt = new JFormattedTextField(decimalFormat);
        numOfEvacueesTxt.setColumns(6);
        numOfEvacueesTxt.setText("1000");

        addSafeZoneBtn.addActionListener(e -> mapPanel.startMakeZone(Zone.SAFE));
        selectionPanel.add(addSafeZoneBtn);


        addHotZoneBtn.addActionListener(e -> mapPanel.startMakeZone(Zone.HOT));
        selectionPanel.add(addHotZoneBtn);


        cancelZoneBtn.addActionListener(e -> mapPanel.cancelZone());
        selectionPanel.add(cancelZoneBtn);

        maxFlowBtn.addActionListener(e -> {
            MaxFlowAlgorithm algorithm = new MaxFlowAlgorithm();
            Map<Intersection, ? extends Distributor> distributors = algorithm.solve(mapGraph, mapPanel.getHotZones(), mapPanel.getSafeZones());
            Simulator simulator = new Simulator(mapGraph, distributors, mapPanel.getSafeZones(), mapPanel.getHotZones(), Integer.parseInt(numOfEvacueesTxt.getText()));
            JOptionPane.showMessageDialog(mapPanel, "Max Flow Time = " + simulator.run());
        });
        selectionPanel.add(maxFlowBtn);

        shortestPathBtn.addActionListener(e -> {
            ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm();
            Map<Intersection, ? extends Distributor> distributors = algorithm.solve(mapGraph, mapPanel.getHotZones(), mapPanel.getSafeZones());
            Simulator simulator = new Simulator(mapGraph, distributors, mapPanel.getSafeZones(), mapPanel.getHotZones(), Integer.parseInt(numOfEvacueesTxt.getText()));
            JOptionPane.showMessageDialog(mapPanel, "Shortest Path Time = " + simulator.run());
        });
        selectionPanel.add(shortestPathBtn);

        timeExpandedBtn.addActionListener(e -> {
            TimeExpandedAlgorithm algorithm = new TimeExpandedAlgorithm();
            Map<Intersection, ? extends Distributor> distributors = algorithm.solve(mapGraph, mapPanel.getHotZones(), mapPanel.getSafeZones());
            Simulator simulator = new Simulator(mapGraph, distributors, mapPanel.getSafeZones(), mapPanel.getHotZones(), Integer.parseInt(numOfEvacueesTxt.getText()));
            JOptionPane.showMessageDialog(mapPanel, "Augmented Graph Time = " + simulator.run());
        });
        selectionPanel.add(timeExpandedBtn);

        selectionPanel.add(numOfEvacueesTxt);

        frame.add(selectionPanel, BorderLayout.SOUTH);
        // ----
        frame.setVisible(true);
    }

}
