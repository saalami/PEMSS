package ca.ucalgary.sim;

import ca.ucalgary.ui.MapFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMapPanel extends JPanel {
    private final BufferedImage image;

    public ImageMapPanel(BufferedImage img) {
        this.image = img;
        Dimension d = new Dimension(img.getWidth(), img.getHeight());
        this.setPreferredSize(d);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public static void main(String[] args) throws IOException {
        MapFrame frame = new MapFrame();
        final BufferedImage img = ImageIO.read(new File("maps/map.bmp"));
        ImageMapPanel mapPanel = new ImageMapPanel(img);
        frame.add(mapPanel);

        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.printf("(%d, %d)%n", x, y);
                System.out.println(img.getRGB(x, y));
            }
        });

        frame.setVisible(true);
    }
}
