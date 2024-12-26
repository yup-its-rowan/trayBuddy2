package MidiKey;

import javax.sound.midi.*;
import java.io.IOException;

public class MidiInputExample {

    public static void main(String[] args) throws MidiUnavailableException {
        try {
            // Get the default MIDI receiver
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            MidiDevice inputDevice = null;
            for (MidiDevice.Info info : infos) {
                if (info.getName().equals("Your MIDI Device Name")) { // Replace with your device name
                    inputDevice = MidiSystem.getMidiDevice(info);
                    break;
                }
            }

            if (inputDevice == null) {
                System.out.println("No MIDI input device found.");
                return;
            }

            // Open the device
            inputDevice.open();

            // Create a receiver to handle incoming MIDI messages
            Receiver receiver = new Receiver() {
                @Override
                public void send(MidiMessage message, long timeStamp) {
                    // Process the MIDI message
                    System.out.println("Received MIDI message: " + message);
                }

                @Override
                public void close() {
                    // Handle device closing
                }
            };

            // Set the receiver for the input device
            inputDevice.getTransmitter().setReceiver(receiver);

            // Keep the program running to receive MIDI input
            System.out.println("Listening for MIDI input. Press Enter to exit.");
            System.in.read();

            // Close the device when finished
            inputDevice.close();

        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}