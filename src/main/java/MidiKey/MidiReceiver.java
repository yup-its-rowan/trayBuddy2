package MidiKey;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MidiReceiver {
    public static void main(String[] args) {
        RtMidiLibrary rtMidi = RtMidiLibrary.INSTANCE;

        // Create a MIDI input object
        Pointer midiIn = rtMidi.rtmidi_in_create_default();

        if (midiIn == null) {
            System.err.println("Failed to create MIDI input object.");
            return;
        }

        try {
            // List available MIDI input ports
            int portCount = rtMidi.rtmidi_get_port_count(midiIn);
            if (portCount == 0) {
                System.out.println("No MIDI input ports available.");
                return;
            }

            System.out.println("Available MIDI input ports:");
            for (int i = 0; i < portCount; i++) {
                // Allocate memory for the port name
                int bufLen = 256;  // Arbitrary buffer size, should be enough
                Memory bufOut = new Memory(bufLen);
                IntByReference lenRef = new IntByReference(bufLen);

                // Retrieve the port name
                int result = rtMidi.rtmidi_get_port_name(midiIn, i, bufOut, lenRef);
                if (result == 0) {
                    String portName = new String(bufOut.getByteArray(0, lenRef.getValue()), StandardCharsets.UTF_8);
                    System.out.printf("%d: %s%n", i, portName);
                } else {
                    System.out.printf("Port %d: Error retrieving name%n", i);
                }
            }

            // Ask user to select a port
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter port number to connect to: ");
            int selectedPort = scanner.nextInt();

            if (selectedPort < 0 || selectedPort >= portCount) {
                System.out.println("Invalid port number.");
                return;
            }

            // Get the selected port's name
            int bufLen = 256;  // Arbitrary buffer size
            Memory bufOut = new Memory(bufLen);
            IntByReference lenRef = new IntByReference(bufLen);
            rtMidi.rtmidi_get_port_name(midiIn, selectedPort, bufOut, lenRef);
            String portName = new String(bufOut.getByteArray(0, lenRef.getValue()), StandardCharsets.UTF_8);
            System.out.printf("Connected to port: %s%n", portName);

            // Open the selected port
            rtMidi.rtmidi_open_port(midiIn, selectedPort, portName);

            // Ignore system-exclusive, timing, and sensing messages
            rtMidi.rtmidi_in_ignore_types(midiIn, true, true, true);

            // Set callback for incoming messages

            rtMidi.rtmidi_in_set_callback(midiIn, (timestamp, message, size, userData) -> {
                // Read the message from the pointer
                byte[] midiMessage = message.getByteArray(0, size.getValue());
                System.out.printf("MIDI Message (size %d): ", size.getValue());
                for (byte b : midiMessage) {
                    System.out.printf("%02X ", b);
                }
                System.out.println();
            }, null);

            System.out.println("Listening for MIDI messages... Press Enter to quit.");
            scanner.nextLine(); // Wait for user to press Enter
            scanner.nextLine(); // Wait for user to press Enter

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cleanup
            if (midiIn != null) {
                rtMidi.rtmidi_close_port(midiIn);
                rtMidi.rtmidi_in_free(midiIn);
                System.out.println("MIDI input closed.");
            }
        }
    }
}


interface RtMidiLibrary extends Library {
    RtMidiLibrary INSTANCE = Native.load("rtmidi", RtMidiLibrary.class);

    //native methods from the RtMIDI library
    Pointer rtmidi_in_create_default();
    int rtmidi_get_port_count(Pointer midi);
    int rtmidi_get_port_name(Pointer midi, int portNumber, Memory buffer, IntByReference bufferSize);

    void rtmidi_in_free(Pointer midiIn);
    void rtmidi_open_port(Pointer midiIn, int portNumber, String portName);
    void rtmidi_close_port(Pointer midiIn);
    void rtmidi_in_set_callback(Pointer midiIn, MidiCallback callback, Pointer userData);
    void rtmidi_in_ignore_types(Pointer midiIn, boolean midiSysex, boolean midiTime, boolean midiSense);
}

interface MidiCallback extends com.sun.jna.Callback {
    void invoke(double timestamp, Pointer message, IntByReference size, Pointer userData);
}