package MidiKey;

import javax.sound.midi.*;

public class MidiKeyboard {

    public static MidiKeyboard MidiKeyboardSingleton = new MidiKeyboard();

    private static MidiDevice inputDevice;
    private static Sequencer mainSequencer;
    private static RealTimeReceiver realTimeReceiver;

    private MidiKeyboard() {
        realTimeReceiver = new RealTimeReceiver();
    }

    //only run by the front end, needs to pair with the UI so that play is unchecked and all the midi devices are closed
    public MidiDevice.Info[] getListOfMidiDevices() {
        close();

        /*
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            //MidiDevice device = MidiSystem.getMidiDevice(info);
            System.out.println(info.getName());
        }
        //return infos;

         */

        return MidiSystem.getMidiDeviceInfo();
    }

    public void setMidiDevice(MidiDevice.Info info) {
        close();
        try {
            inputDevice = MidiSystem.getMidiDevice(info);
            inputDevice.open();
            inputDevice.getTransmitter().setReceiver(realTimeReceiver);

            mainSequencer = MidiSystem.getSequencer();
            mainSequencer.open();
            System.out.println(info.getName() + " opened");
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.out.println(info.getName() + " failed to open");
        }
    }

    public boolean play(){
        System.out.println("Playing");
        if (inputDevice == null) {
            System.out.println("No midi device selected");
            return false;
        }

        realTimeReceiver.setPlay(true);
        return true;
    }

    public void unplay() {
        realTimeReceiver.setPlay(false);
    }

    public void close() {
        realTimeReceiver.setPlay(false);
        if (mainSequencer != null && mainSequencer.isOpen()) {
            mainSequencer.close();
        }
        if (inputDevice != null && inputDevice.isOpen()) {
            inputDevice.close();
        }
    }



}
