package MoneyCounters;

import GUI.ButtonListeners;
import GUI.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ML2F {
    public static Logger log = Logger.getLogger(ML2F.class.getName());

    public static void readingData(SerialPort comPort) {

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                log.info("Receiving data from ML-2F");
                //clearing count data and serial numbers before that start of next data transfer
                MainWindow.model_ocrText.removeAllElements();
                MainWindow.model_serialImage.removeAllElements();
                ButtonListeners.clearTable(MainWindow.jt_denom);
                //showing progress of receiving data
                new ProgressBarFrame();

                try {
                    Scanner sc = new Scanner(comPort.getInputStream());
                    List<String> countData = new ArrayList<>();
                    while (sc.hasNextLine()) {
                        countData.add(sc.next());
                        if (countData.contains("Signature")) {
                            break;
                        }
                    }
                    System.out.println(countData);
                    log.info("Receiving: " + countData);
                    //inserting count data to the table in gui
                    if (countData.contains("RSD")) {
                        insertRSD(countData, MainWindow.jt_denom);
                        log.info("Receiving data for RSD");
                    } else if (countData.contains("EUR")) {
                        insertEUR(countData, MainWindow.jt_denom);
                        log.info("Receiving data for EUR");
                    } else if (countData.contains("USD")) {
                        insertUSD(countData, MainWindow.jt_denom);
                        log.info("Receiving data for USD");
                    } else {
                        //in case none of the above currencies is chosen, we show the error message on JOptionPane
                        JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana", "Greška!", JOptionPane.ERROR_MESSAGE);
                    }

                    //inserting serial numbers in table in gui
                    ProgressBarFrame.label.setText("Preuzimam serijske brojeve");
                    getSerialOcrRSD(countData, MainWindow.model_ocrText, MainWindow.model_serialImage);

                    //calculating values in count data table
                    ButtonListeners.tableTotalAmountRows(MainWindow.jt_denom);
                    ButtonListeners.tableTotalAmountColumns(MainWindow.jt_denom);

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
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
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            switch (line.get(i)) {
                case "E5" -> jt_denom.setValueAt(line.get(i + 1), 0, 1);
                case "E10" -> jt_denom.setValueAt(line.get(i + 1), 1, 1);
                case "E20" -> jt_denom.setValueAt(line.get(i + 1), 2, 1);
                case "E50" -> jt_denom.setValueAt(line.get(i + 1), 3, 1);
                case "E100" -> jt_denom.setValueAt(line.get(i + 1), 4, 1);
                case "E200" -> jt_denom.setValueAt(line.get(i + 1), 5, 1);
                case "E500" -> jt_denom.setValueAt(line.get(i + 1), 6, 1);
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
            switch (line.get(i)) {
                case "$1" -> jt_denom.setValueAt(line.get(i + 1), 0, 1);
                case "$2" -> jt_denom.setValueAt(line.get(i + 1), 1, 1);
                case "$5" -> jt_denom.setValueAt(line.get(i + 1), 2, 1);
                case "$10" -> jt_denom.setValueAt(line.get(i + 1), 3, 1);
                case "$20" -> jt_denom.setValueAt(line.get(i + 1), 4, 1);
                case "$50" -> jt_denom.setValueAt(line.get(i + 1), 5, 1);
                case "$100" -> jt_denom.setValueAt(line.get(i + 1), 6, 1);
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
            if (line.get(i).equals("RSD") && line.get(i + 1).equals("10")) {
                jt_denom.setValueAt(line.get(i + 3), 0, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("20")) {
                jt_denom.setValueAt(line.get(i + 3), 1, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("50")) {
                jt_denom.setValueAt(line.get(i + 3), 2, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("100")) {
                jt_denom.setValueAt(line.get(i + 3), 3, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("200")) {
                jt_denom.setValueAt(line.get(i + 3), 4, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("500")) {
                jt_denom.setValueAt(line.get(i + 3), 5, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("1,000")) {
                jt_denom.setValueAt(line.get(i + 3), 6, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("2,000")) {
                jt_denom.setValueAt(line.get(i + 3), 7, 1);
            } else if (line.get(i).equals("RSD") && line.get(i + 1).equals("5,000")) {
                jt_denom.setValueAt(line.get(i + 3), 8, 1);
            }
        }
    }

    public static void getSerialOcrRSD(List<String> line, DefaultListModel<String> ocrModel, DefaultListModel<ImageIcon> serialImage) {
        String serial = "";
        int j = 0;
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("N")) {
                serial = "D" + line.get(i + 1) + " " + line.get(i + 2) + line.get(i + 3);
                ocrModel.add(j, serial);
                System.out.println(serial);
                serialImage.add(j, createTransparentIcon(384, 40));
                j++;
            }
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







