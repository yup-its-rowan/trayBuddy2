package MidiKey;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

public class PortMidiReceiver {

    private static Synthesizer synthesizer;
    public static void main(String[] args) {
        PortMidiLibrary pm = PortMidiLibrary.INSTANCE;

        // Initialize PortMidi
        int initResult = pm.Pm_Initialize();
        if (initResult != PMError.pmNoError) {
            System.err.println("Failed to initialize PortMidi: " + PMError.getErrorMessage(initResult));
            return;
        }

        // Get the default input device ID
        int inputDevice = pm.Pm_GetDefaultInputDeviceID();
        if (inputDevice < 0) {
            System.err.println("No default input device found.");
            pm.Pm_Terminate();
            return;
        }

        // Open the input stream
        PointerByReference streamRef = new PointerByReference();
        int openResult = pm.Pm_OpenInput(streamRef, inputDevice, null, 0, null, null);
        if (openResult != PMError.pmNoError) {
            System.err.println("Failed to open MIDI input: " + PMError.getErrorMessage(openResult));
            pm.Pm_Terminate();
            return;
        }

        System.out.println("MIDI input opened successfully.");

        Pointer stream = streamRef.getValue();

        // Allocate buffer for one PmEvent
        PmEvent.ByReference buffer = new PmEvent.ByReference();



        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
        } catch (Exception e) {
            System.err.println("Failed to get synthesizer: " + e.getMessage());
            return;
        }

        // Read MIDI input
        while (true) {
            int readResult = pm.Pm_Read(stream, buffer, 1); // Read one event
            if (readResult > 0) {
                //System.out.printf("MIDI Event Received: Message=0x%08X, Timestamp=%d%n", buffer.message, buffer.timestamp);
                interpreter(buffer.message);

            } else if (readResult != PMError.pmNoError) {
                System.err.println("Error reading MIDI input: " + PMError.getErrorMessage(readResult));
                break;
            }
        }

        // Close the stream and terminate PortMidi
        pm.Pm_Close(stream);
        pm.Pm_Terminate();
    }

    public static void interpreter(int message) {
        int status = message & 0xFF;
        int data1 = (message >> 8) & 0xFF;
        int data2 = (message >> 16) & 0xFF;

        if (status == 0x90 && data2 > 0) {
            synthesizer.getChannels()[0].noteOn(data1, data2);
            //System.out.printf("Note On: Note=%d, Velocity=%d%n", data1, data2);
        } else if (status == 0x80 || (status == 0x90 && data2 == 0)) {
            synthesizer.getChannels()[0].noteOff(data1);
            //System.out.printf("Note Off: Note=%d, Velocity=%d%n", data1, data2);
        }
    }
}
