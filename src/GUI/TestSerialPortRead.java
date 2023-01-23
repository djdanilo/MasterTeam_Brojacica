package GUI;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class TestSerialPortRead {

    public static List<String> countData;

    public static void main(String[] args) throws IOException, TesseractException {


        countData = new ArrayList<>();
        for (int i = 0; i < 50; i++){
            countData.add("test");
        }




        SerialPort comPort = SerialPort.getCommPort("COM8");
        comPort.openPort();
        comPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        readingBytesSN(comPort);



    }

    public static void readingBytesSN(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

                String startSn2 = "0000110100001010";
                String newLine2 = "0000101000001101";

                String startSn = "27434877273598525669";
                String newLine = "483232120323232323232324832";
                String endOfSn = "12694954545053565052661310";
                String endOfData = "1227513232131032131032131032131032131027109275132";


                String s1 = "";

                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }

                try {

                    InputStream in = comPort.getInputStream();

                    List<Integer> x = new ArrayList<>();


                } catch (Exception e) {
                    e.printStackTrace();
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

                while (sc.hasNextLine()) {
                    line.add(sc.next());
                    if (line.contains("\u001Bm\u001B3")) {
                        break;
                    }
                }

                System.out.println(line);

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

    public static String SerialOcr(File file) throws TesseractException {

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(7);
        tesseract.setOcrEngineMode(1);

        return tesseract.doOCR(file);
    }

}







