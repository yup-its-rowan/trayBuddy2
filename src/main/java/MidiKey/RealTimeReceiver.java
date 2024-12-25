package MidiKey;

import javax.sound.midi.*;

import static MidiKey.PatternInterpreter.PatternInterpreterSingleton;

public class RealTimeReceiver implements Receiver {
    Synthesizer synthesizer;
    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage shortMessage) {
            int command = shortMessage.getCommand();
            int channel = shortMessage.getChannel();
            int data1 = shortMessage.getData1();
            int data2 = shortMessage.getData2();
            MIDIinterpreter(command, channel, data1, data2);
        }
    }
    public RealTimeReceiver () {
        System.out.println("Receiver created");
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        System.out.println("Receiver closed");
    }

    public void MIDIinterpreter(int command, int channel, int key, int velocity) {
        //System.out.println("Command: " + command + " Channel: " + channel + " Key: " + key + " Velocity: " + velocity);
        if (command == ShortMessage.NOTE_ON) {
            System.out.println("Note " + key + " on at " + velocity);
            synthesizer.getChannels()[0].noteOn(key, velocity);
            PatternInterpreterSingleton.interpretNote(key);
        } else if (command == ShortMessage.NOTE_OFF) {
            System.out.println("Note " + key + " off");
            synthesizer.getChannels()[0].noteOff(key);
        }
    }
}
