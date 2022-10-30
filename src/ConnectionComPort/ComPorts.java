package ConnectionComPort;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ComPorts {

    public ArrayList<String> listSerials() {
        ArrayList<String> portList = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            portList.add(port.getSystemPortName());
        }
        return portList;
    }



    public SerialPort openSerialPort(final SerialPort serialPort,
                                     final long timeout, String baudRate) {

        serialPort.openPort(0);
        serialPort.setComPortParameters(Integer.parseInt(baudRate), 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, (int) timeout, (int) timeout);

        System.out.println("Successfully connected to " + serialPort.getSystemPortName());
        System.out.println(serialPort.isOpen());

        if (!serialPort.openPort()) {
            throw new IllegalStateException("Failed to open serial port: "
                    + serialPort.getSystemPortName());
        }


        return serialPort;
    }


    public static void closePort(SerialPort serialPort) {
        // Check to make sure serial port has reference to avoid a NPE
        if (serialPort != null) {
                // Close the port.
            serialPort.removeDataListener();
            serialPort.closePort();


            System.out.println("Serial port " + serialPort.getSystemPortName() + " closed");

            System.out.println(serialPort.isOpen());

        }
    }
}





