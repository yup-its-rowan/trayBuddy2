package MidiKey;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Popup {
    private JFrame frame;
    public Popup(String urlToImage, String jFramename) {
        frame = new JFrame(jFramename);
        JPanel mainPanel = new JPanel(new BorderLayout()); //for a better centering or somethin
        InputStream inputStream = Popup.class.getResourceAsStream(urlToImage); //so that it can be read in a .jar file
        ImageIcon finalIcon = null;

        //do this so you can get the icon on the taskbar
        Image image;
        try {
            image = ImageIO.read(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.out.println("bruh image broke");
            throw new RuntimeException(e);
        }

        //need to read again because the first time it is read it is consumed
        inputStream = Popup.class.getResourceAsStream(urlToImage);

        //cannot handle files with extensions longer than 3 characters
        String filetype = urlToImage.substring(urlToImage.length() - 3);
        if (filetype.equals("jpg") || filetype.equals("png")) {
            finalIcon = new ImageIcon(image);
        } else if (filetype.equals("gif")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            try {
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                finalIcon = new ImageIcon(buffer.toByteArray());
                buffer.close();
            } catch (IOException e) {
                System.out.println("bruh gif broke");
                throw new RuntimeException(e);
            }
        }


        try {
            inputStream.close(); //close the stream after done
        } catch (IOException e) {
            System.out.println("bruh close broke");
            throw new RuntimeException(e);
        }

        if (finalIcon == null) {
            throw new RuntimeException("finalIcon is null");
        }

        JLabel imageLabel = new JLabel(finalIcon);
        Dimension imageSize = new Dimension(finalIcon.getIconWidth() + 10, finalIcon.getIconHeight() + 10);
        imageLabel.setPreferredSize(imageSize);

        mainPanel.add(imageLabel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setMinimumSize(new Dimension(200, 200));
        frame.setIconImage(image);
        frame.setAlwaysOnTop(true);

        //the following section is to place the frame randomly on the screen but still have most of it visible
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double boundsXMin = 0.1, boundsXMax = 0.7, boundsYMin = 0.1, boundsYMax = 0.5;
        double xLocationMax = (screenSize.getWidth() * (boundsXMax - boundsXMin) + boundsXMin), yLocationMax = (screenSize.getHeight() * (boundsYMax - boundsYMin) + boundsYMin);
        Random random = new Random();
        frame.setLocation((int) (random.nextDouble() * xLocationMax), (int) (random.nextDouble() * yLocationMax));
    }

    public void showPopup() {
        this.frame.setVisible(true);
    }
}