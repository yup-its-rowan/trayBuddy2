package MidiKey;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class FreddyPopup {
    private JFrame frame = new JFrame("Freddy");
    public FreddyPopup() {
        InputStream freddyInputStream = FreddyPopup.class.getResourceAsStream("/freddy.jpg");
        Image freddyImage;
        try {
            freddyImage = ImageIO.read(freddyInputStream);
        } catch (IOException e) {
            System.out.println("bruh image broke");
            throw new RuntimeException(e);
        }
        JLabel freddyLabel = new JLabel(new ImageIcon(freddyImage));
        frame.add(freddyLabel);
        frame.setResizable(false);
        frame.pack();
        frame.setIconImage(freddyImage);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double boundsXMin, boundsXMax, boundsYMin, boundsYMax, xLocationMax, yLocationMax;
        boundsXMin = 0.1;
        boundsXMax = 0.7;
        boundsYMin = 0.1;
        boundsYMax = 0.5;
        xLocationMax = (screenSize.getWidth() * (boundsXMax - boundsXMin) + boundsXMin);
        yLocationMax = (screenSize.getHeight() * (boundsYMax - boundsYMin) + boundsYMin);
        Random random = new Random();
        frame.setLocation((int) (random.nextDouble() * xLocationMax), (int) (random.nextDouble() * yLocationMax));
        frame.setAlwaysOnTop(true);
    }

    public void showPopup() {
        this.frame.setVisible(true);
    }


}
