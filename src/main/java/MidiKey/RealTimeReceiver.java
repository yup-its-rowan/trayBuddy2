package MidiKey;

import javax.sound.midi.*;

import static MidiKey.PatternInterpreter.PatternInterpreterSingleton;

public class RealTimeReceiver implements Receiver {
    private Synthesizer synthesizer;
    private boolean play = false;
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
        //System.out.println("Receiver created");
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
            //System.out.println("Note " + key + " on at " + velocity);
            if (play){
                synthesizer.getChannels()[0].noteOn(key, velocity);
            }
            PatternInterpreterSingleton.interpretNote(key);
        } else if (command == ShortMessage.NOTE_OFF) {
            if (play) {
                synthesizer.getChannels()[0].noteOff(key);
            }
            //System.out.println("Note " + key + " off");
        }
    }

    public void setPlay(boolean play) {
        System.out.println("Play set to " + play);
        this.play = play;
    }
}
