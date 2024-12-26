package MidiKey;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class MidiReceiver {
    public static void main(String[] args) {
        RtMidiLibrary rtMidi = RtMidiLibrary.INSTANCE;

        // Create MIDI input object
        Pointer midiIn = rtMidi.rtmidi_in_create_default();
        rtMidi.rtmidi_open_port(midiIn, 0, "Java MIDI Input");

        // Process MIDI events (this will depend on RtMIDI's API)
        // Example: listen for messages in a loop or with callbacks.

        // Cleanup
        rtMidi.rtmidi_close_port(midiIn);
        rtMidi.rtmidi_in_free(midiIn);
    }
}


interface RtMidiLibrary extends Library {
    RtMidiLibrary INSTANCE = Native.load("rtmidi", RtMidiLibrary.class);

    // Define native methods from the RtMIDI library
    Pointer rtmidi_in_create_default();
    void rtmidi_in_free(Pointer midiIn);
    void rtmidi_open_port(Pointer midiIn, int portNumber, String portName);
    void rtmidi_close_port(Pointer midiIn);
    // Add other methods as needed
}