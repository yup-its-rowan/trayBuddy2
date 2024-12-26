package MidiKey;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LowLevelMidiKeyboard {
    public static void main(String[] args) {
        Context context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to initialize libusb.", result);

        // List devices
        DeviceList deviceList = new DeviceList();
        result = LibUsb.getDeviceList(context, deviceList);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        Device chosenDevice = null;

        try {
            for (Device device : deviceList) {
                // Retrieve the device descriptor
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    System.err.println("Failed to read device descriptor: " + LibUsb.strError(result));
                    continue;
                }

                System.out.println("Found Device:");
                System.out.println("  Vendor ID: 0x" + Integer.toHexString(descriptor.idVendor()));
                System.out.println("  Product ID: 0x" + Integer.toHexString(descriptor.idProduct()));

                // Open the device to get a handle
                DeviceHandle handle = new DeviceHandle();
                result = LibUsb.open(device, handle);
                if (result != LibUsb.SUCCESS) {
                    System.err.println("Unable to open device: " + LibUsb.strError(result));
                    continue;
                }

                // Retrieve the product name using StringBuffer
                String productName = getStringDescriptor(handle, descriptor.iProduct());
                if (productName != null) {
                    System.out.println("  Product Name: " + productName);
                } else {
                    System.out.println("  Product Name: Not available");
                }

                // Close the handle
                LibUsb.close(handle);
            }
        } finally {
            // Free the device list and deinitialize the library
            LibUsb.freeDeviceList(deviceList, true);
            LibUsb.exit(context);
        }

        if (chosenDevice == null) {
            System.out.println("Device not found");
            return;
        }

        DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(chosenDevice, handle);
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to open device.", result);


        LibUsb.exit(context);
    }

    private static String getStringDescriptor(DeviceHandle handle, int index) {
        if (index == 0) {
            return null; // No string descriptor available
        }

        // Create a StringBuffer for the string descriptor
        StringBuffer stringBuffer = new StringBuffer(256);

        // Retrieve the string descriptor
        int result = LibUsb.getStringDescriptorAscii(handle, (byte) 0, stringBuffer);
        if (result < 0) {
            System.err.println("Error retrieving string descriptor: " + LibUsb.strError(result));
            return null;
        }

        // Convert the StringBuffer to a string
        return stringBuffer.toString();
    }

    public Device findDevice(short vendorId, short productId)
    {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        try
        {
            // Iterate over all devices and scan for the right one
            for (Device device: list)
            {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) return device;
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

    public void openDevice(short vendorId, short productId) {
        Context context = new Context();
        LibUsb.init(context);

        DeviceHandle handle = LibUsb.openDeviceWithVidPid(context, vendorId, productId);
        if (handle == null) {
            throw new RuntimeException("Device not found");
        }

        // Claim the interface
        int interfaceNumber = 0; // Adjust based on your device
        int result = LibUsb.claimInterface(handle, interfaceNumber);
        if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to initialize libusb.", result);

        System.out.println("Device opened and interface claimed!");
    }

    public void readMidiData(DeviceHandle handle) {
        byte[] data = new byte[64]; // Adjust size based on your device's endpoint
        int[] transferred = new int[1];

        while (true) {
            //int result = LibUsb.interruptTransfer(handle, (byte) 0x81, data, transferred, 5000);
            int result = 0;
            if (result == LibUsb.SUCCESS) {
                System.out.println("Received MIDI data: " + bytesToHex(data, transferred[0]));
            } else if (result == LibUsb.ERROR_TIMEOUT) {
                System.out.println("Timeout waiting for MIDI data.");
            } else {
                System.err.println("Error during transfer: " + LibUsb.strError(result));
                break;
            }
        }
    }

    private String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString().trim();
    }
}
