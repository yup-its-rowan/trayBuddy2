package MidiKey;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class PortMidiReceiver {
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

        // Read MIDI input
        Pointer buffer = new Pointer(0);
        while (true) {
            int readResult = pm.Pm_Read(stream, buffer, 1); // Read one event
            if (readResult > 0) {
                System.out.println("MIDI Event Received!");
            } else if (readResult != PMError.pmNoError) {
                System.err.println("Error reading MIDI input: " + PMError.getErrorMessage(readResult));
                break;
            }
        }

        // Close the stream and terminate PortMidi
        pm.Pm_Close(stream);
        pm.Pm_Terminate();
    }
}
