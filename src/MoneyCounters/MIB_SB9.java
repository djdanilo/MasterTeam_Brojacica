package MoneyCounters;

import GUI.ButtonListeners;
import GUI.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.sun.tools.javac.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static GUI.TestGuiImageBinary.SerialOcr;

public class MIB_SB9 {


    public static void readingBytesSN(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                try {

                    InputStream in;

                    String startSn2 = "110000010011010001101100100011011000100011010000111000010001010";
                    String newLine2 = "01100000110110010001101100010001101000011100001000101";

                    String startSn = "27434877273598525669";
                    String newLine = "12273598525669";
                    String endOfSn = "12694954545053565052661310";
                    String endOfData = "1227513232131032131032131032131032131027109275132";


                    String s1 = "";
                    String s2 = "";

                    if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }

                    int x = 0;


                    in = comPort.getInputStream();


                    InputStream bufferdInputStream = new BufferedInputStream(in);


                    bufferdInputStream.mark(1000000000);


                    Scanner sc = new Scanner(bufferdInputStream);

                    List<String> line = new ArrayList<>();
                    List<String> ocrText = new ArrayList<>();
                    List<ImageIcon> serialImage = new ArrayList<>();

                    while (sc.hasNextLine()) {

                        line.add(sc.next());
                        if (line.contains("\u001Bm\u001B3")) {
                            break;
                        }
                    }

                    System.out.println(line);

                    bufferdInputStream.reset();


                    while (((x = bufferdInputStream.read()) != 109)) {
                        s1 += String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
                    }

                    String[] snArray = s1.split(startSn2);

                    MainWindow.jt_serialBinary.setText(String.join(", ", snArray));


                    for (int i = 1; i < snArray.length; i++) {

                        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
                        Graphics2D g2d = img.createGraphics();
                        Font font = new Font("Arial", Font.PLAIN, 2);
                        g2d.setFont(font);
                        int height = g2d.getFontMetrics().getHeight();
                        g2d.dispose();

                        img = new BufferedImage(384, 40, BufferedImage.TYPE_INT_RGB);
                        g2d = img.createGraphics();

                        g2d.setFont(font);
                        g2d.setColor(Color.WHITE);
                        int fontSize = 1;

                        for (String line2 : snArray[i].split(newLine2)) {

                            g2d.drawString(line2, 0, height);
                            height += fontSize;
                        }

                        File file = new File("images\\Text" + i + ".png");

                        ImageIO.write(img, "png", file);

                        g2d.dispose();


                        String result = SerialOcr(file);

                        System.out.println(result);

                        ocrText.add(trainOcr(result));


                        serialImage.add(makeIcon(img));
                    }

                    for (int i = 0; i < ocrText.size(); i++) {
                        MainWindow.model_ocrText.add(i, ocrText.get(i));
                    }

                    for (int i = 0; i < serialImage.size(); i++) {
                        MainWindow.model_serialImage.add(i, serialImage.get(i));
                    }


                    for (int i = 0; i < line.size(); i++) {
                        if (line.get(i).equals("RSD")) {
                            insertUSD(line, MainWindow.jt_denom);
                        } else if (line.get(i).equals("USD")) {
                            insertUSD(line, MainWindow.jt_denom);
                        } else if (line.get(i).equals("EUR")) {
                            insertEUR(line, MainWindow.jt_denom);
                        } else {
                            JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana", "Greška!", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    ButtonListeners.tableTotalAmountRows(MainWindow.jt_denom);
                    ButtonListeners.tableTotalAmountColumns(MainWindow.jt_denom);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static ImageIcon makeIcon(BufferedImage img) {

        ImageIcon icon = new ImageIcon(img);

        return icon;
    }

    public static void insertEUR(List<String> line, JTable jt_denom) {

        MainWindow.lb_currency.setText("EUR");
        jt_denom.setValueAt("5", 0, 0);
        jt_denom.setValueAt("10", 1, 0);
        jt_denom.setValueAt("20", 2, 0);
        jt_denom.setValueAt("50", 3, 0);
        jt_denom.setValueAt("100", 4, 0);
        jt_denom.setValueAt("200", 5, 0);
        jt_denom.setValueAt("500", 6, 0);
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("E5")) {
                jt_denom.setValueAt(line.get(i + 1), 0, 1);
            } else if (line.get(i).equals("E10")) {
                jt_denom.setValueAt(line.get(i + 1), 1, 1);
            } else if (line.get(i).equals("E20")) {
                jt_denom.setValueAt(line.get(i + 1), 2, 1);
            } else if (line.get(i).equals("E50")) {
                jt_denom.setValueAt(line.get(i + 1), 3, 1);
            } else if (line.get(i).equals("E100")) {
                jt_denom.setValueAt(line.get(i + 1), 4, 1);
            } else if (line.get(i).equals("E200")) {
                jt_denom.setValueAt(line.get(i + 1), 5, 1);
            } else if (line.get(i).equals("E500")) {
                jt_denom.setValueAt(line.get(i + 1), 6, 1);
            }
        }
    }

    public static void insertRSD(List<String> line, JTable jt_denom) {

        MainWindow.lb_currency.setText("RSD");
        jt_denom.setValueAt("10", 0, 0);
        jt_denom.setValueAt("20", 1, 0);
        jt_denom.setValueAt("50", 2, 0);
        jt_denom.setValueAt("100", 3, 0);
        jt_denom.setValueAt("200", 4, 0);
        jt_denom.setValueAt("500", 5, 0);
        jt_denom.setValueAt("1000", 6, 0);
        jt_denom.setValueAt("2000", 7, 0);
        jt_denom.setValueAt("5000", 8, 0);
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("D10")) {
                jt_denom.setValueAt(line.get(i + 1), 0, 1);
            } else if (line.get(i).equals("D20")) {
                jt_denom.setValueAt(line.get(i + 1), 1, 1);
            } else if (line.get(i).equals("D50")) {
                jt_denom.setValueAt(line.get(i + 1), 2, 1);
            } else if (line.get(i).equals("D100")) {
                jt_denom.setValueAt(line.get(i + 1), 3, 1);
            } else if (line.get(i).equals("D200")) {
                jt_denom.setValueAt(line.get(i + 1), 4, 1);
            } else if (line.get(i).equals("D500")) {
                jt_denom.setValueAt(line.get(i + 1), 5, 1);
            } else if (line.get(i).equals("D1000")) {
                jt_denom.setValueAt(line.get(i + 1), 6, 1);
            } else if (line.get(i).equals("D2000")) {
                jt_denom.setValueAt(line.get(i + 1), 7, 1);
            } else if (line.get(i).equals("D5000")) {
                jt_denom.setValueAt(line.get(i + 1), 8, 1);
            }
        }
    }

    public static void insertUSD(List<String> line, JTable jt_denom) {

        MainWindow.lb_currency.setText("USD");
        jt_denom.setValueAt("1", 0, 0);
        jt_denom.setValueAt("2", 1, 0);
        jt_denom.setValueAt("5", 2, 0);
        jt_denom.setValueAt("10", 3, 0);
        jt_denom.setValueAt("20", 4, 0);
        jt_denom.setValueAt("50", 5, 0);
        jt_denom.setValueAt("100", 6, 0);
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("$1")) {
                jt_denom.setValueAt(line.get(i + 1), 0, 1);
            } else if (line.get(i).equals("$2")) {
                jt_denom.setValueAt(line.get(i + 1), 1, 1);
            } else if (line.get(i).equals("$5")) {
                jt_denom.setValueAt(line.get(i + 1), 2, 1);
            } else if (line.get(i).equals("$10")) {
                jt_denom.setValueAt(line.get(i + 1), 3, 1);
            } else if (line.get(i).equals("$20")) {
                jt_denom.setValueAt(line.get(i + 1), 4, 1);
            } else if (line.get(i).equals("$50")) {
                jt_denom.setValueAt(line.get(i + 1), 5, 1);
            } else if (line.get(i).equals("$100")) {
                jt_denom.setValueAt(line.get(i + 1), 6, 1);
            }
        }
    }

    public static String trainOcr(String result) {

        String fix = "";

        if (result.startsWith("ES ")) {
            fix = result.replaceAll("^ES", "E5");
        } else if (result.startsWith("E18")) {
            fix = result.replaceAll("^E18", "E10");
        } else if (result.startsWith("E28")) {
            fix = result.replaceAll("^E28", "E20");
        } else if (result.startsWith("E58")) {
            fix = result.replaceAll("^E58", "E50");
        } else if (result.startsWith("ESB")) {
            fix = result.replaceAll("^ESB", "E50");
        } else if (result.startsWith("Ese")) {
            fix = result.replaceAll("^Ese", "E50");
        } else if (result.startsWith("E188")) {
            fix = result.replaceAll("^E188", "E100");
        } else if (result.startsWith("E288")) {
            fix = result.replaceAll("^E288", "E200");
        } else if (result.startsWith("E588")) {
            fix = result.replaceAll("^E588", "E500");
        } else {
            fix = result;
        }

        return fix;
    }

    public static void showProgress(InputStream bufferedInputStream, JPanel panel) throws IOException {
        ProgressMonitorInputStream progressMonitorInputStream = new ProgressMonitorInputStream(panel, "Preuzimam podatke", bufferedInputStream);
        ProgressMonitor progressMonitor = progressMonitorInputStream.getProgressMonitor();
        progressMonitor.setMillisToPopup(1);


    }


}
