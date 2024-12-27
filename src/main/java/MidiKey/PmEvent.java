package MidiKey;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class PmEvent extends Structure {
    public int message;       // 32-bit MIDI message
    public int timestamp;     // 32-bit timestamp

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("message", "timestamp");
    }

    public static class ByReference extends PmEvent implements Structure.ByReference {}
    public static class ByValue extends PmEvent implements Structure.ByValue {}
}
