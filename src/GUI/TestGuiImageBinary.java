package GUI;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.tc33.jheatchart.HeatChart;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TestGuiImageBinary {

    public static void main(String[] args) throws IOException {

        SerialPort comPort = SerialPort.getCommPort("COM8");
        comPort.openPort();
        comPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        readingBytesSN(comPort);
        //readingBytes2(comPort);

        //toImage();

    }

    private static void readingBytesSN(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

                ArrayList<String> list = new ArrayList<>();
                String s1 = "";
                StringBuffer sb = new StringBuffer();

                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                //System.out.println("Read " + numRead + " bytes.");

                for (Byte b : readBuffer) {

                    if (numRead <= 1200) {
                        break;
                    }else {

                        s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');

                        list.add(s1);
                    }
                }

                String sn = fixedLengthString(sb.toString(), 290);


                String newLine = "01100000110110010001101100010001101000011100001000101";

                BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                Font font = new Font("Arial", Font.PLAIN, 1);
                g2d.setFont(font);
                int height = g2d.getFontMetrics().getHeight();
                g2d.dispose();

                img = new BufferedImage(384, 50, BufferedImage.TYPE_INT_RGB);
                g2d = img.createGraphics();

                g2d.setFont(font);
                g2d.setColor(Color.WHITE);
                int fontSize = 1;

                for (String line : sn.split(newLine)) {
                    g2d.drawString(line, 0, height);
                    height += fontSize;
                    System.out.println(line);
                }
                g2d.dispose();

                try {
                    ImageIO.write(img, "png", new File("Text.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


            }
        });

    }


    private static void readingBytesDENOM(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return 1;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

                Scanner sc = new Scanner(comPort.getInputStream());

                List<String> line = new ArrayList<>();
                File file = new File("output.txt");
                try {
                    FileWriter fw = new FileWriter(file);
                    while (sc.hasNextLine()) {
                        line.add(sc.next());
                        if (line.contains("\u001Bm\u001B3")) {
                            break;
                        }
                    }
                    fw.write(String.valueOf(line));
                    fw.flush();
                    fw.close();
                    System.out.println(line);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public static void toImage() throws IOException {

        File txt = new File("serNo.txt");
        StringBuffer result = new StringBuffer();
        Scanner sc = new Scanner(txt);

        while (sc.hasNextLine()) {
            result.append(sc.nextLine() + "\n");
        }

        String serialNumber = new String(result);

        String newLine = "011000001101100100011011000100011010000111000";

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 1);
        g2d.setFont(font);
        int height = g2d.getFontMetrics().getHeight();
        g2d.dispose();

        img = new BufferedImage(325, 35, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();

        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        int fontSize = 1;

        int y = 0;

        for (String line : serialNumber.split(newLine)) {
            g2d.drawString(line, 0, height);
            height += fontSize;
        }
        g2d.dispose();

        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }


}







