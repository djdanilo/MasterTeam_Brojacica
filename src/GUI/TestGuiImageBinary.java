package GUI;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TestGuiImageBinary {

    public static void main(String[] args) throws IOException {

        SerialPort comPort = SerialPort.getCommPort("COM4");
        comPort.openPort();
        comPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        readingBytes(comPort);

        //invokeDevice();
        //readWriteImage();
    }

    private static void invokeDevice() throws IOException {
        String comPort = "COM4";
        System.out.println(String.format("Connecting to %s", comPort));
        SerialPort sp = SerialPort.getCommPort(comPort);
        sp.setBaudRate(115200);
        sp.setRTS();
        sp.openPort();
        sp.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[sp.bytesAvailable()];
                sp.readBytes(newData, newData.length);
                try (FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\danilodjurovic\\Desktop\\MasterTeam Brojacica\\output.txt"), true)) {
                    fos.write(newData);
                    fos.write("\n===============Start=============".getBytes());
                    for (byte b : newData) {
                        fos.write(10);
                        int i = b;
                        String s = String.valueOf(i);
                        fos.write(s.getBytes());
                    }
                    fos.write("\n===============Stop=============".getBytes());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    private static void readWriteImage() throws IOException {
        BufferedImage bImage = ImageIO.read(new File("C:\\Users\\danilodjurovic\\Desktop\\MasterTeam Brojacica\\src\\sample.jpg"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte[] data = bos.toByteArray();

        //System.out.println(Arrays.toString(data));

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File("output.jpg"));
        System.out.println("image created");
    }

    private static void readingBytes(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

                ArrayList<String> list = new ArrayList<>();
                String s1 = new String();

                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                //System.out.println("Read " + numRead + " bytes.");

                for (Byte b : readBuffer) {

                    if (numRead <= 500) {
                        break;
                    }

                    s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');


                    if (s1.equals("01000101")) {
                        System.out.println();
                        continue;
                    }

                    System.out.print(s1);
                    list.add(s1);

                    try {
                        File f = new File("output.txt");
                        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
                        Scanner sc = new Scanner(s1);
                        while (sc.hasNextLine()){
                            String line = sc.nextLine();
                            writer.append(line);
                        }
                        sc.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                //System.out.println(list);
            }
        });
    }
}

