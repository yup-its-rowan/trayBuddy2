import javax.swing.*;

public class TestFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Base JFrame Example");

        // Set the size of the JFrame
        frame.setSize(400, 300);

        // Specify what happens when the close button is clicked
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the JFrame visible
        frame.setVisible(true);
    }
}
