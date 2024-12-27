package MidiKey;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public interface PortMidiLibrary extends Library {
    PortMidiLibrary INSTANCE = Native.load("PortMidi", PortMidiLibrary.class);

    int Pm_Initialize();
    int Pm_Terminate();
    int Pm_GetDefaultInputDeviceID();
    //int Pm_OpenInput(Pointer stream, Pointer inputDevice, Pointer buffer, int bufferSize, Pointer timeProc, Pointer timeInfo);
    int Pm_OpenInput(PointerByReference stream, int inputDevice, Pointer inputBuffer, int bufferSize, Pointer timeProc, Pointer timeInfo);
    int Pm_Read(Pointer stream, Pointer buffer, int length);
    int Pm_Close(Pointer stream);
}

class PMError {
    public static final int pmNoError = 0;
    public static final int pmHostError = -10000;
    public static final int pmInvalidDeviceId = -9999;
    public static final int pmInsufficientMemory = -9998;
    public static final int pmBufferTooSmall = -9997;
    public static final int pmBufferOverflow = -9996;
    public static final int pmBadPointer = -9995;
    public static final int pmBadData = -9994;
    public static final int pmInternalError = -9993;
    public static final int pmBufferMaxSize = -9992;

    public static String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case pmNoError: return "No error.";
            case pmHostError: return "A host error has occurred.";
            case pmInvalidDeviceId: return "Invalid device ID.";
            case pmInsufficientMemory: return "Insufficient memory.";
            case pmBufferTooSmall: return "Buffer too small.";
            case pmBufferOverflow: return "Buffer overflow.";
            case pmBadPointer: return "Bad pointer.";
            case pmBadData: return "Bad data.";
            case pmInternalError: return "Internal PortMidi error.";
            case pmBufferMaxSize: return "Buffer exceeded maximum size.";
            default: return "Unknown error code: " + errorCode;
        }
    }
}