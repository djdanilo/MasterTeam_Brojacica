package MoneyCounters;

import GUI.ButtonListeners;
import GUI.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static GUI.MainWindow.*;

public class MIB9 {
    public static Logger log = Logger.getLogger(SB9.class.getName());
    public static List<String> countData;

    public static void readingData(SerialPort comPort) {
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                try {
                    if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        return;
                    }
                    log.info("Receiving data from SB-9.");
                    //clearing count data and serial numbers before that start of next data transfer
                    model_ocrText.removeAllElements();
                    MainWindow.model_serialImage.removeAllElements();
                    ButtonListeners.clearTable(MainWindow.jt_denom);

                    //here I save the count data in the string array
                    countData = new ArrayList<>();
                    //string for denomination
                    String denomination = "";

                    String substring = "#b48E"; // the substring you want to remove
                    String substring2 = "�";
                    String substring3 = "\u0000";
                    String substring4 = "π";
                    String substring5 = "ǀ";
                    //getting InputStream from serial port and then converting it to BufferedStream, so it can be reset
                    //and read two times
                    InputStream in = comPort.getInputStream();
                    //trying to show progress of received data from inputStream
                    new ProgressBarFrame();
                    //first reading the input stream with scanner to get count data as Strings
                    Scanner sc = new Scanner(in);
                    while (sc.hasNextLine()) {
                        countData.add(sc.next());
                        //this is the last string in the InputStream, so I use it to break from the while loop
                        if (countData.contains("\u001Bm\u001B3")) {
                            break;
                        }
                    }
                    //removing elements from the array that are not needed
                    for (int i = 0; i < countData.size(); i++) {
                        String str = countData.get(i);
                        if (str.contains(substring) || str.contains(substring2)
                                || str.contains(substring3) || str.contains(substring4) || str.contains(substring5)) {
                            countData.remove(i);
                            i--; // decrease the index by 1 to compensate for the removed element
                        }
                    }
                    List<String> serialNumberList = new ArrayList<>();
                    boolean start = false;
                    for (String s : countData) {
                        if (s.contains("No.")) {
                            start = true;
                        }
                        if (start) {
                            serialNumberList.add(s);
                        }
                    }
                    serialNumberList.remove("No.");
                    serialNumberList.remove("--------------------------------");
                    serialNumberList.remove("\u001B3");
                    serialNumberList.remove("\u001Bm\u001B3");
                    for (int i = 0; i < serialNumberList.size(); i++) {
                        String str = serialNumberList.get(i);
                        if (str.contains("\u000F\u0001") || str.contains("\u000F\u0003")) {
                            serialNumberList.remove(i);
                            i--; //decrease the index by 1 to compensate for the removed element
                        }
                    }
                    //logs
                    log.info("Receiving and modifying count data +" + countData);
                    jt_logs.append(lb_timeDate.getText() + "     Preuzeta apoenska struktura\n");

                    //checking what currency is counted and processing data accordingly
                    if (countData.contains("RSD")) {
                        MainWindow.lb_currency.setText("RSD");
                        denomination = "RSD";
                        insertRSD(countData, MainWindow.jt_denom);
                        log.info("Received data for RSD currency. Saving to table.");
                    } else {
                        log.info("Received data for EUR or USD currency.");
                        //checking to see what currency is the data from the machine, and building the gui table accordingly
                        if (countData.contains("USD")) {
                            denomination = "USD";
                            insertUSD(countData, MainWindow.jt_denom);
                            getSerialOcrUSD(MainWindow.jList_ocrText, MainWindow.jList_serialImage, serialNumberList, MainWindow.model_ocrText, MainWindow.model_serialImage);
                        } else if (countData.contains("EUR")) {
                            MainWindow.lb_currency.setText("EUR");
                            denomination = "EUR";
                            insertEUR(countData, MainWindow.jt_denom);
                            getSerialOcrEUR(MainWindow.jList_ocrText, MainWindow.jList_serialImage, serialNumberList, MainWindow.model_ocrText, MainWindow.model_serialImage);
                        } else {
                            //in case none of the above currencies is chosen, we show the error message on JOptionPane
                            JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana", "Greška!", JOptionPane.ERROR_MESSAGE);
                            ProgressBarFrame.frame.dispose();
                            return;
                        }
                        jt_logs.append(lb_timeDate.getText() + "     Preuzimam serijske brojeve\n");
                        ProgressBarFrame.label.setText("Preuzimam serijske brojeve");
                        in.close();
                    }
                    //after getting all the data to the table in MainWindow, we calculate the total count data
                    ButtonListeners.tableTotalAmountRows(MainWindow.jt_denom);
                    ButtonListeners.tableTotalAmountColumns(MainWindow.jt_denom, denomination);
                    //logs
                    jt_logs.append(lb_timeDate.getText() + "     Podaci primljeni\n");
                    //closing progressBar
                    ProgressBarFrame.frame.dispose();

                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
        jt_denom.setValueAt("", 7, 0);
        jt_denom.setValueAt("", 8, 0);
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("E5") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 0, 1);
            } else if (line.get(i).equals("E10") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 1, 1);
            } else if (line.get(i).equals("E20") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 2, 1);
            } else if (line.get(i).equals("E50") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 3, 1);
            } else if (line.get(i).equals("E100") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 4, 1);
            } else if (line.get(i).equals("E200") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 5, 1);
            } else if (line.get(i).equals("E500") && line.get(i + 2).equals("E")) {
                jt_denom.setValueAt(line.get(i + 1), 6, 1);
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
        jt_denom.setValueAt("", 7, 0);
        jt_denom.setValueAt("", 8, 0);
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("$1") && line.get(i + 2).equals("$")) {
                jt_denom.setValueAt(line.get(i + 1), 0, 1);
            } else if (line.get(i).equals("$2") && line.get(i + 2).equals("$")) {
                jt_denom.setValueAt(line.get(i + 1), 1, 1);
            } else if (line.get(i).equals("$5") && line.get(i + 2).equals("$")) {
                jt_denom.setValueAt(line.get(i + 1), 2, 1);
            } else if (line.get(i).equals("$10") && line.get(i + 2).equals("$")) {
                jt_denom.setValueAt(line.get(i + 1), 3, 1);
            } else if (line.get(i).equals("$20") && line.get(i + 2).equals("$")) {
                jt_denom.setValueAt(line.get(i + 1), 4, 1);
            } else if (line.get(i).equals("$50") && line.get(i + 2).equals("$")) {
                jt_denom.setValueAt(line.get(i + 1), 5, 1);
            } else if (line.get(i).equals("$100") && line.get(i + 2).equals("$")) {
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
            switch (line.get(i)) {
                case "D10" -> jt_denom.setValueAt(line.get(i + 1), 0, 1);
                case "D20" -> jt_denom.setValueAt(line.get(i + 1), 1, 1);
                case "D50" -> jt_denom.setValueAt(line.get(i + 1), 2, 1);
                case "D100" -> jt_denom.setValueAt(line.get(i + 1), 3, 1);
                case "D200" -> jt_denom.setValueAt(line.get(i + 1), 4, 1);
                case "D500" -> jt_denom.setValueAt(line.get(i + 1), 5, 1);
                case "D1000" -> jt_denom.setValueAt(line.get(i + 1), 6, 1);
                case "D2000" -> jt_denom.setValueAt(line.get(i + 1), 7, 1);
                case "D5000" -> jt_denom.setValueAt(line.get(i + 1), 8, 1);
            }
        }
    }

    public static void getSerialOcrUSD(JList jList, JList jlist2, List<String> line, DefaultListModel<String> ocrModel, DefaultListModel<ImageIcon> serialImage) {
        String serial = "";
        int j = 0;
        for (int i = 0; i < line.size(); i++) {
            serial = line.get(i + 1) + " " + line.get(i);
            ocrModel.add(j, serial);
            jList.setModel(ocrModel);
            serialImage.add(j, createTransparentIcon(384, 40));
            jlist2.setModel(serialImage);
            j++;
            i++;
        }
    }
    public static void getSerialOcrEUR(JList jList, JList jlist2, List<String> line, DefaultListModel<String> ocrModel, DefaultListModel<ImageIcon> serialImage) {
        String serial = "";
        int j = 0;
        for (int i = 0; i < line.size(); i++) {
            serial = line.get(i + 1) + " " + line.get(i);
            ocrModel.add(j, serial);
            jList.setModel(ocrModel);
            serialImage.add(j, createTransparentIcon(384, 40));
            jlist2.setModel(serialImage);
            j++;
            i++;
        }
    }

    public static BufferedImage createTransparentImage(final int width, final int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw a black outline around the image
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width - 1, height - 1);

        // Clean up
        g2d.dispose();
        return img;
    }

    public static ImageIcon createTransparentIcon(final int width, final int height) {
        return new ImageIcon(createTransparentImage(width, height));
    }
}
