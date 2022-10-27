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

        SerialPort comPort = SerialPort.getCommPort("COM4");
        comPort.openPort();
        comPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        //readingBytes(comPort);
        //readingBytes2(comPort);

        toImage();

        //invokeDevice();
        //readWriteImage();
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


    private static void readingBytes2(SerialPort comPort) {

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

        while(sc.hasNextLine()){
            result.append(sc.nextLine() + "\n");
        }

        String serialNumber = new String(result);
        StringTokenizer str = new StringTokenizer(serialNumber, "\n\r");

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(serialNumber);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);

        while (str.hasMoreTokens()) {
            String token = str.nextToken();
            g2d.drawString(token, 0, height + fm.getAscent());
            height += (fm.getAscent() + fm.getLeading());
        }

        g2d.dispose();

        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }

}







