import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class Main {

    private static long currentMIDIProcessID = -1;

    public static void main(String[] args) throws IOException {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        } //"src/com/company/images/icon.png"
        //System.out.println("Working Directory = " + System.getProperty("user.dir"));
        //Image image = ImageIO.read(new File("images/icon.png"));

        InputStream inputStream = Main.class.getResourceAsStream("/icon.png");
        if (inputStream == null){
            System.out.println("inputStream is null");
            return;
        }
        Image image = ImageIO.read(inputStream);
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(image, "traybuddy2");
        final SystemTray tray = SystemTray.getSystemTray();
        trayIcon.setImageAutoSize(true);
        Desktop desktop = Desktop.getDesktop();

        //setup for how menu should look
        Menu shortcutsMenu = new Menu("Shortcuts");
            MenuItem emailItem = new MenuItem("Email");
            //MenuItem schoolItem = new MenuItem("School");
            MenuItem tf2Item = new MenuItem("TF2");
        Menu midiMenu = new Menu("Midikey");
            CheckboxMenuItem loudMidiCheckboxItem = new CheckboxMenuItem("Loud", true);
            CheckboxMenuItem patternMidiCheckboxItem = new CheckboxMenuItem("Pattern", true);
        MenuItem piBoardItem = new MenuItem("PiBoard");
        MenuItem exitItem = new MenuItem("Exit");

        //email listener and stuff
        ActionListener emailListener = e -> {
            try {
                desktop.browse(new URI("https://mail.google.com/mail/u/0/#inbox"));
                desktop.browse(new URI("https://mail.yahoo.com/d/folders/1"));
            } catch (IOException | URISyntaxException ioException) {
                ioException.printStackTrace();
            }

        };
        emailItem.addActionListener(emailListener);

        //tf2 listener and stuff
        ActionListener tf2Listener = e -> {
            try {
                desktop.browse(new URI("https://uncletopia.com/servers"));
                desktop.browse(new URI("steam://rungameid/440"));
            } catch (IOException | URISyntaxException ioException) {
                ioException.printStackTrace();
            }
        };
        tf2Item.addActionListener(tf2Listener);

        //midi input devices listener and stuff
        ProcessBuilder midiBothExe = tempMIDIFile("/rustPiano/both.exe", "both");
        ProcessBuilder midiLoudExe = tempMIDIFile("/rustPiano/loud.exe", "loud");
        ProcessBuilder midiPatternExe = tempMIDIFile("/rustPiano/pattern.exe", "pattern");



        loudMidiCheckboxItem.addItemListener(e -> {
            if (loudMidiCheckboxItem.getState() && patternMidiCheckboxItem.getState()) {
                changeMIDIProcess(midiBothExe);
            } else if (loudMidiCheckboxItem.getState()) {
                changeMIDIProcess(midiLoudExe);
            } else if (patternMidiCheckboxItem.getState()) {
                changeMIDIProcess(midiPatternExe);
            } else {
                stopCurrentMIDIProcess();
            }
        });

        patternMidiCheckboxItem.addItemListener(e -> {
            if (loudMidiCheckboxItem.getState() && patternMidiCheckboxItem.getState()) {
                changeMIDIProcess(midiBothExe);
            } else if (loudMidiCheckboxItem.getState()) {
                changeMIDIProcess(midiLoudExe);
            } else if (patternMidiCheckboxItem.getState()) {
                changeMIDIProcess(midiPatternExe);
            } else {
                stopCurrentMIDIProcess();
            }
        });

        //pi board opener and stuff
        ActionListener piBoardItemListener = e -> {
            PiBoard.PiBoardSingleton.show();
        };
        piBoardItem.addActionListener(piBoardItemListener);

        //exit listener and stuff
        ActionListener exitListener = e -> {
            PiBoard.PiBoardSingleton.exit();
            stopCurrentMIDIProcess();
            tray.remove(trayIcon);
            System.exit(0);
        };
        exitItem.addActionListener(exitListener);

        shortcutsMenu.add(emailItem);
        //shortcutsMenu.add(schoolItem);
        shortcutsMenu.add(tf2Item);

        midiMenu.add(loudMidiCheckboxItem);
        midiMenu.add(patternMidiCheckboxItem);

        popup.add(shortcutsMenu);
        popup.add(midiMenu);
        popup.add(piBoardItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

        //onStartThings
        changeMIDIProcess(midiBothExe);
        System.out.println("Traybuddy started");
    }

    private static ProcessBuilder tempMIDIFile(String path, String tempFileName) throws IOException {
        InputStream inputStream = Main.class.getResourceAsStream(path);
        if (inputStream == null){
            System.out.println("inputStream is null");
            return null;
        }
        File tempFile = File.createTempFile(tempFileName, ".exe");
        tempFile.deleteOnExit();

        Files.copy(inputStream, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return new ProcessBuilder(tempFile.getAbsolutePath());
    }

    private static void stopCurrentMIDIProcess(){
        if (currentMIDIProcessID != -1) {
            try {
                Process process = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(currentMIDIProcessID)).start();
                process.waitFor();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
            currentMIDIProcessID = -1;
        }
    }

    private static void changeMIDIProcess(ProcessBuilder processBuilder) {
        stopCurrentMIDIProcess();
        try {
            Process process = processBuilder.start();
            currentMIDIProcessID = process.pid();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
