import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.*;

public class PiBoard {
    public static String password;
    public static String[] apiEndpoints = new String[6];
    public static PiBoard PiBoardSingleton = new PiBoard();

    private JFrame frame = new JFrame("PiBoard");
    private JPanel boardOnlinePanel = new JPanel();
    private JPanel serverOnlinePanel = new JPanel();
    private JPanel bufferRightOnlinePanel = new JPanel();
    private JPanel bufferLeftOnlinePanel = new JPanel();
    private JPanel settingsPanel = new JPanel(new BorderLayout());
    private JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    private FlowLayout tileBack = new FlowLayout(FlowLayout.CENTER);
    private JPanel tileBackgroundPanel = new JPanel();

    private JLabel boardOnLabel = new JLabel("board");
    private JLabel serverOnLabel = new JLabel("server");

    private final Color settingsC = new Color(58, 56, 52);
    private final Color titleBackC = new Color(58, 56, 52);
    private final Color tileBackgroundC = new Color(58, 56, 52);
    private final Color titleC = new Color(255, 219, 155);

    private final Color purpleDiagonal = new Color(194, 68, 120);
    private final Color tanDiagonal = new Color(214, 142, 78);
    private final Color offDiagonal = new Color(239, 180, 88);

    private final Color labelInactiveColor = Color.RED;
    private final Color labelActiveColor = Color.GREEN;

    private final Font titleFont = new Font("Monospaced", Font.PLAIN, 70);
    private final Font buttonFont = new Font("Monospaced", Font.PLAIN, 15);
    private final Font labelFont = new Font("Monospaced", Font.PLAIN, 11);

    private final int settingsV = 50;
    private final int titleV = 90;
    private final int spacerV = 30;
    private final int tileV = 100;
    private final int exitV = 50;

    private final int spacerH = 50;
    private final int tileH = 140;
    private final int midH = 30;
    private final int fileSQ = 30;
    private final int panelCheckW = 52;

    private final int windowWidth = spacerH + tileH + midH + tileH + spacerH;
    private final int windowHeight = settingsV + titleV + spacerV + tileV + spacerV + tileV + spacerV + exitV + spacerV + spacerV ;
    private final int windowBottomBuffer = 150;

    private String videoDowngradeCheck = "true";

    private PiBoard (){
        password = readPasswordFile();
        apiEndpoints = readAPIFile();

        InputStream inputStream = Main.class.getResourceAsStream("/icon2.png");
        if (inputStream == null){
            return;
        }
        Image image;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            System.out.println("bruh image broke");
            throw new RuntimeException(e);
        }


        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int boardX = (int) ((dimension.getWidth() - windowWidth - windowBottomBuffer));
        int boardY = (int) ((dimension.getHeight() - windowHeight - windowBottomBuffer));


        JLabel titleLabel = new JLabel("PiBoard");

        JButton offButton = new JButton("Turn Off");
        JButton picButton = new JButton("Picture");
        JButton slideshowButton = new JButton("Slideshow");
        JButton vidButton = new JButton("Video");
        JButton paintButton = new JButton("Paint");

        JButton pushPic = new JButton("...");
        JButton pushSlide = new JButton("...");
        JButton pushVid = new JButton("...");

        JButton optionsMenu = new JButton("...");
        optionsMenu.setPreferredSize(new Dimension(fileSQ, fileSQ));

        JPopupMenu optionsMenuFull = new JPopupMenu("Settings");
            JMenuItem slideClearingItem = new JMenuItem("Clear Slides");
            JCheckBoxMenuItem downgradeVideoItem = new JCheckBoxMenuItem("Downgrade Video?");
            downgradeVideoItem.setState(true);
        optionsMenuFull.add(slideClearingItem);
        optionsMenuFull.add(downgradeVideoItem);

        slideClearingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("clear slides");
                clearSlides();
            }
        });
        downgradeVideoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("downgrade video");
                if (downgradeVideoItem.getState() == true){
                    videoDowngradeCheck = "true";
                } else {
                    videoDowngradeCheck = "false";
                }
            }
        });
        optionsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("got to optionMenu");
                showPopup(e, optionsMenuFull);
            }
        });

        optionsMenu.setFocusPainted(false);
        pushPic.setFocusPainted(false);
        pushVid.setFocusPainted(false);
        pushSlide.setFocusPainted(false);
        picButton.setFocusPainted(false);
        vidButton.setFocusPainted(false);
        slideshowButton.setFocusPainted(false);
        paintButton.setFocusPainted(false);
        offButton.setFocusPainted(false);

        //offButton.setBackground(tileC);
        picButton.setBackground(purpleDiagonal);
        slideshowButton.setBackground(tanDiagonal);
        vidButton.setBackground(tanDiagonal);
        paintButton.setBackground(purpleDiagonal);
        offButton.setBackground(offDiagonal);

        picButton.setFont(buttonFont);
        slideshowButton.setFont(buttonFont);
        vidButton.setFont(buttonFont);
        paintButton.setFont(buttonFont);
        offButton.setFont(buttonFont);

        offButton.setBorderPainted(false);
        picButton.setBorderPainted(false);
        slideshowButton.setBorderPainted(false);
        vidButton.setBorderPainted(false);
        paintButton.setBorderPainted(false);

        addStateChangerListener(offButton, "off");
        addStateChangerListener(picButton, "staticPicture");
        addStateChangerListener(slideshowButton, "slideshow");
        addStateChangerListener(vidButton, "vid");
        addStateChangerListener(paintButton, "paint");

        pushPic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pushPic();
            }
        });
        pushSlide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pushSlideshow();
            }
        });
        pushVid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pushVideo();
            }
        });

        pushPic.setBackground(tileBackgroundC);
        pushVid.setBackground(tileBackgroundC);
        pushSlide.setBackground(tileBackgroundC);
        optionsMenu.setBackground(tileBackgroundC);

        pushPic.setForeground(titleC);
        pushVid.setForeground(titleC);
        pushSlide.setForeground(titleC);
        optionsMenu.setForeground(titleC);

        optionsMenu.setBorder(BorderFactory.createLineBorder(titleC));
        pushPic.setBorder(BorderFactory.createLineBorder(purpleDiagonal));
        pushVid.setBorder(BorderFactory.createLineBorder(tanDiagonal));
        pushSlide.setBorder(BorderFactory.createLineBorder(tanDiagonal));


        //This is for some online checking things
        bufferRightOnlinePanel.setBackground(settingsC);
        bufferRightOnlinePanel.setPreferredSize(new Dimension(10, 5));
        bufferLeftOnlinePanel.setBackground(settingsC);
        bufferLeftOnlinePanel.setPreferredSize(new Dimension(10, 5));

        serverOnLabel.setFont(labelFont);
        boardOnLabel.setFont(labelFont);
        serverOnLabel.setForeground(labelInactiveColor);
        boardOnLabel.setForeground(labelInactiveColor);

        serverOnlinePanel.setPreferredSize(new Dimension(panelCheckW, fileSQ));
        boardOnlinePanel.setPreferredSize(new Dimension(panelCheckW, fileSQ));

        serverOnlinePanel.setBackground(tileBackgroundC);
        boardOnlinePanel.setBackground(tileBackgroundC);
        serverOnlinePanel.setBorder(BorderFactory.createLineBorder(labelInactiveColor));
        boardOnlinePanel.setBorder(BorderFactory.createLineBorder(labelInactiveColor));
        serverOnlinePanel.add(serverOnLabel);
        boardOnlinePanel.add(boardOnLabel);

        settingsPanel.setPreferredSize(new Dimension(windowWidth, settingsV));
        //settingsPanel.setBounds(0, 0, windowWidth, settingsV);
        titlePanel.setPreferredSize(new Dimension(windowWidth, titleV));
        //titlePanel.setBounds(0, settingsV, windowWidth, titleV);
        tileBack.setVgap(spacerV);
        tileBack.setHgap(midH);
        tileBackgroundPanel.setLayout(tileBack);
        tileBackgroundPanel.setPreferredSize(new Dimension(windowWidth, windowHeight-settingsV-tileV));
        //tileBackgroundPanel.setBounds(0, settingsV + titleV, windowWidth, windowHeight - settingsV - titleV);
        settingsPanel.setBackground(settingsC);
        titlePanel.setBackground(titleBackC);
        tileBackgroundPanel.setBackground(tileBackgroundC);

        pushPic.setBounds(tileH - fileSQ, 0, fileSQ, fileSQ);
        pushSlide.setBounds(tileH - fileSQ, 0, fileSQ, fileSQ);
        pushVid.setBounds(tileH - fileSQ, 0, fileSQ, fileSQ);

        picButton.setSize(new Dimension(tileH, tileV));
        slideshowButton.setSize(new Dimension(tileH, tileV));
        vidButton.setSize(new Dimension(tileH, tileV));
        paintButton.setPreferredSize(new Dimension(tileH, tileV));
        offButton.setPreferredSize(new Dimension(tileH + tileH + midH, exitV));

        JPanel settingsRightWrapper = new JPanel(new FlowLayout());
        settingsRightWrapper.setBackground(settingsC);
        settingsRightWrapper.add(serverOnlinePanel);
        settingsRightWrapper.add(boardOnlinePanel);
        settingsRightWrapper.add(bufferRightOnlinePanel);

        JPanel settingsLeftWrapper = new JPanel(new FlowLayout());
        settingsLeftWrapper.setBackground(settingsC);
        settingsLeftWrapper.add(bufferLeftOnlinePanel);
        settingsLeftWrapper.add(optionsMenu);

        settingsPanel.add(settingsRightWrapper, BorderLayout.LINE_END);
        settingsPanel.add(settingsLeftWrapper, BorderLayout.LINE_START);


        titleLabel.setFont(titleFont);
        titleLabel.setForeground(titleC);
        titlePanel.add(titleLabel);

        JLayeredPane picPushPane = new JLayeredPane();
        picPushPane.add(pushPic, JLayeredPane.POPUP_LAYER);
        picPushPane.add(picButton, JLayeredPane.DEFAULT_LAYER);
        picPushPane.setPreferredSize(new Dimension(tileH, tileV));

        JLayeredPane slidePushPane = new JLayeredPane();
        slidePushPane.add(pushSlide, JLayeredPane.POPUP_LAYER);
        slidePushPane.add(slideshowButton, JLayeredPane.DEFAULT_LAYER);
        slidePushPane.setPreferredSize(new Dimension(tileH, tileV));

        JLayeredPane vidPushPane = new JLayeredPane();
        vidPushPane.add(pushVid, JLayeredPane.POPUP_LAYER);
        vidPushPane.add(vidButton, JLayeredPane.DEFAULT_LAYER);
        vidPushPane.setPreferredSize(new Dimension(tileH, tileV));

        tileBackgroundPanel.add(picPushPane);
        tileBackgroundPanel.add(slidePushPane);
        tileBackgroundPanel.add(vidPushPane);
        tileBackgroundPanel.add(paintButton);
        tileBackgroundPanel.add(offButton);

        frame.add(settingsPanel);
        frame.add(titlePanel);
        frame.add(tileBackgroundPanel);


        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        frame.setSize(windowWidth, windowHeight);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        frame.setLayout(flowLayout);
        frame.setResizable(false);
        frame.setLocation(boardX,boardY);
        frame.setIconImage(image);
        frame.setVisible(true);
    }

    public void exit(){
        frame.dispose();
    }

    public String stateCheck() {
        try {
            URL stateURL = new URL(apiEndpoints[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) stateURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(2000);

            int responseCode = urlConnection.getResponseCode();
            System.out.println("response code is : " + responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK){
                System.out.println("both up");
                serverOnLabel.setForeground(labelActiveColor);
                boardOnLabel.setForeground(labelActiveColor);
                serverOnlinePanel.setBorder(BorderFactory.createLineBorder(labelActiveColor));
                boardOnlinePanel.setBorder(BorderFactory.createLineBorder(labelActiveColor));
                return "both up";
            } else if ( responseCode == HttpsURLConnection.HTTP_UNAVAILABLE){
                System.out.println("server up");
                serverOnLabel.setForeground(labelActiveColor);
                boardOnLabel.setForeground(labelInactiveColor);
                serverOnlinePanel.setBorder(BorderFactory.createLineBorder(labelActiveColor));
                boardOnlinePanel.setBorder(BorderFactory.createLineBorder(labelInactiveColor));
                return "server up";
            } else {
                System.out.println("this should never happen");
                serverOnLabel.setForeground(labelInactiveColor);
                boardOnLabel.setForeground(labelInactiveColor);
                serverOnlinePanel.setBorder(BorderFactory.createLineBorder(labelInactiveColor));
                boardOnlinePanel.setBorder(BorderFactory.createLineBorder(labelInactiveColor));
                return "how did you get here???";
            }

        } catch (Exception e) {
            System.out.println("both down");
            return "both down";
        }
    }

    public void startState(String state){
        ArrayList<String> possibilities = new ArrayList<>(Arrays.asList("off", "vid", "slideshow", "staticPicture", "paint"));
        if (!possibilities.contains(state)){
            return;
        }
        try {
            URL stateURL = new URL(apiEndpoints[1]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) stateURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            String jsonInputString = "{\"state\": \"" + state + "\", \"pass\": \"" + password + "\"}";
            //System.out.println(jsonInputString);
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (IOException e) {
            System.out.println("uhh idk");
            throw new RuntimeException(e);
        }
    }
    public void clearSlides(){
        try {
            URL stateURL = new URL(apiEndpoints[2]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) stateURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(3000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            String jsonInputString = "{\"pass\": \"" + password + "\"}";
            //System.out.println(jsonInputString);
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (IOException e) {
            System.out.println("uhh idk");
            throw new RuntimeException(e);
        }
    }

    public void pushPic(){
        try {
            // Set header
            FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setFile("*.jpg;*.png;*.jpeg");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            String file = dialog.getDirectory() + dialog.getFile();

            if (dialog.getFile() == null){
                System.out.println("its null");
                dialog.dispose();
                return;
            }
            dialog.dispose();
            System.out.println(file + " chosen.");

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            HttpPostMultipart multipart = new HttpPostMultipart(apiEndpoints[3], "utf-8", headers);
            // Add form field
            multipart.addFormField("pass", password);
            // Add file
            multipart.addFilePart("myFile", new File(file));
            // Print result
            String response = multipart.finish();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushSlideshow(){
        try {
            // Set header
            FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setFile("*.jpg;*.png;*.jpeg");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            String file = dialog.getDirectory() + dialog.getFile();

            if (dialog.getFile() == null){
                System.out.println("its null");
                dialog.dispose();
                return;
            }

            dialog.dispose();
            System.out.println(file + " chosen.");

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            HttpPostMultipart multipart = new HttpPostMultipart(apiEndpoints[4], "utf-8", headers);
            // Add form field
            multipart.addFormField("pass", password);
            // Add file
            multipart.addFilePart("myFile", new File(file));
            // Print result
            String response = multipart.finish();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushVideo(){
        try {
            // Set header
            FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
            dialog.setFile("*.mp4");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            String file = dialog.getDirectory() + dialog.getFile();

            if (dialog.getFile() == null){
                System.out.println("its null");
                dialog.dispose();
                return;
            }

            dialog.dispose();
            System.out.println(file + " chosen.");

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            HttpPostMultipart multipart = new HttpPostMultipart(apiEndpoints[5], "utf-8", headers);
            // Add form field
            multipart.addFormField("pass", password);
            multipart.addFormField("videoDowngrade", videoDowngradeCheck);
            // Add file
            multipart.addFilePart("myFile", new File(file));
            // Print result
            String response = multipart.finish();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(){
        this.frame.setVisible(true);
        this.frame.repaint();
        stateCheck();
        //startState("off");
    }

    private void addStateChangerListener(JButton jbutton, String correctState){
        jbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startState(correctState);
            }
        });
    }
    private String readPasswordFile(){
        String data = "oopsy";
        InputStream inputStream = Main.class.getResourceAsStream("/pass.txt");
        Scanner myReader = new Scanner(inputStream);
        data = myReader.nextLine();

        myReader.close();
        //System.out.println(data);
        return data;
    }

    private String[] readAPIFile(){
        String[] data = new String[6];
        InputStream inputStream = Main.class.getResourceAsStream("/api.txt");
        Scanner myReader = new Scanner(inputStream);
        data[0] = myReader.nextLine();
        data[1] = myReader.nextLine();
        data[2] = myReader.nextLine();
        data[3] = myReader.nextLine();
        data[4] = myReader.nextLine();
        data[5] = myReader.nextLine();

        myReader.close();
        //System.out.println(data);
        return data;
    }
    private void showPopup(ActionEvent ae, JPopupMenu menu)
    {
        // Get the event source
        Component b=(Component)ae.getSource();

        // Get the location of the point 'on the screen'
        Point p=b.getLocationOnScreen();

        // Show the JPopupMenu via program

        // Parameter desc
        // ----------------
        // this - represents current frame
        // 0,0 is the coordinate where the popup
        // is shown
        menu.show(frame,0,0);

        // Now set the location of the JPopupMenu
        // This location is relative to the screen
        menu.setLocation(p.x,p.y+b.getHeight());
    }
}
