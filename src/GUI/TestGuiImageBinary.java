package GUI;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class TestGuiImageBinary {

    public static void main(String[] args) throws IOException, TesseractException {

        //SerialPort comPort = SerialPort.getCommPort("COM8");
        //comPort.openPort();
        //comPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        //comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        //readingBytesSN(comPort);
        //readingBytes2(comPort);

        //toImage();

        File image = new File("text1.png");
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);
        String result = tesseract.doOCR(image);
        System.out.println(result);

    }

    private static void readingBytesSN(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {

                InputStream in;
                long startTime = System.currentTimeMillis();

                String startSn2 = "110000010011010001101100100011011000100011010000111000010001010";
                String newLine2 = "011000001101100100011011000100011010000111000";

                String startSn = "27434877273598525669";
                String newLine = "12273598525669";
                String endOfSn = "12694954545053565052661310";
                String endOfData = "1227513232131032131032131032131032131027109275132";


                String s1 = "";
                String s2 = "";

                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                //byte[] readBuffer = new byte[comPort.bytesAvailable()];
                //int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                //System.out.println("Read " + numRead + " bytes.");

                int x = 0;
                try {
                    in = comPort.getInputStream();

                    while (((x = in.read()) != 109)) {
                        s1 += String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                String[] snArray = s1.split(startSn2);

                System.out.println(Arrays.toString(snArray));


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

                for (int i = 1; i < snArray.length; i++) {
                    for (String line : snArray[i].split(newLine2)) {

                        g2d.drawString(line, 0, height);
                        height += fontSize;
                        //System.out.println("Serial number: " + line);
                    }
                    g2d.dispose();
                    try {
                        ImageIO.write(img, "png", new File("Text" + i + ".png"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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




}







