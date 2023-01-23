package MoneyCounters;

import GUI.ButtonListeners;
import GUI.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static GUI.MainWindow.model_ocrText;
import static GUI.MainWindow.model_serialImage;

public class SB9 {
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
                    //here I save the text that is OCRed from the images
                    List<String> ocrText = new ArrayList<>();
                    //here I save the images from the machine
                    List<ImageIcon> serialImage = new ArrayList<>();

                    //Strings that represent the start of the serial number and new lines on each serial number
                    String startSn = "110000010011010001101100100011011000100011010000111000010001010";
                    String newLine = "01100000110110010001101100010001101000011100001000101";

                    int x = 0;
                    int countBytes = 0;
                    String s1 = "";
                    String substring = "#b48E"; // the substring you want to remove
                    String substring2 = "�";
                    String substring3 = "\u0000";
                    String substring4 = "π";

                    //getting InputStream from serial port and then converting it to BufferedStream, so it can be reset
                    //and read two times
                    InputStream in = comPort.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                    bufferedInputStream.mark(1000000000);

                    //trying to show progress of received data from inputStream
                    JFrame frame = new JFrame("Pažnja!");
                    JLabel label = new JLabel("Preuzimam apoensku strukturu.");
                    frame.setSize(300, 150);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    frame.setLayout(new FlowLayout());
                    frame.setVisible(true);
                    frame.add(label);

                    //first reading the input stream with scanner to get count data as Strings
                    Scanner sc = new Scanner(bufferedInputStream);
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
                                || str.contains(substring3) || str.contains(substring4)) {
                            countData.remove(i);
                            i--; // decrease the index by 1 to compensate for the removed element
                        }
                    }
                    log.info("Receiving and modifying count data +" + countData);

                    //checking what currency is counted and processing data accordingly
                    if (countData.get(10).equals("RSD") || countData.get(11).equals("RSD")) {
                        MainWindow.lb_currency.setText("RSD");
                        insertRSD(countData, MainWindow.jt_denom);
                        log.info("Received data for RSD currency. Saving to table.");
                    } else {
                        log.info("Received data for EUR or USD currency.");
                        //checking to see what currency is the data from the machine, and building the gui table accordingly
                        MainWindow.lb_currency.setText(countData.get(6));
                        if (countData.get(6).equals("USD"))
                            insertUSD(countData, MainWindow.jt_denom);
                        else if (countData.get(6).equals("EUR"))
                            insertEUR(countData, MainWindow.jt_denom);
                        else
                            //in case none of the above currencies is chosen, we show the error message on JOptionPane
                            JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana", "Greška!", JOptionPane.ERROR_MESSAGE);

                        label.setText("Preuzimam serijske brojeve");

                        //here I reset the InputStream, so it can be read again to receive the bytes needed to get the image
                        //of serial number
                        bufferedInputStream.reset();

                        while (((x = bufferedInputStream.read()) != 109)) {
                            label.setText("Preuzimam bajtove: " + countBytes);
                            //bytes are converted to binaryStrings, so I can get the binary image with Java Graphics library
                            s1 += String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
                            countBytes++;
                        }

                        //bytes are stored in the String array, they are split by String for the start of serial number
                        String[] binarySerialNumberArr = s1.split(startSn);
                        log.info("Storing binary data of serial numbers to array.");

                        //here I immediately write the binaryString to the gui of MainWindow, so it can be saved to the database
                        MainWindow.jt_serialBinary.setText(String.join(", ", binarySerialNumberArr));
                        log.info("Storing binary data of serial numbers to GUI table.");

                        //here we take each element of String array and convert it from BinaryString to image
                        for (int i = 1; i < binarySerialNumberArr.length; i++) {
                            //first we create an empty image
                            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = img.createGraphics();
                            Font font = new Font("Arial", Font.PLAIN, 2);
                            g2d.setFont(font);
                            int height = g2d.getFontMetrics().getHeight();
                            g2d.dispose();
                            //here we start to create the actual image of the serial number
                            img = new BufferedImage(384, 40, BufferedImage.TYPE_INT_RGB);
                            g2d = img.createGraphics();

                            g2d.setFont(font);
                            g2d.setColor(Color.WHITE);
                            int fontSize = 1;

                            for (String s : binarySerialNumberArr[i].split(newLine)) {
                                //we draw each string and use the newLine String to split the array element
                                //each 0 is represented as white, and 1 as black
                                g2d.drawString(s, 0, height);
                                height += fontSize;
                            }
                            //creating the image file
                            File file = new File("images\\Text" + i + ".png");
                            //saving the image to file
                            ImageIO.write(img, "png", file);
                            g2d.dispose();
                            log.info("Creating an image of serial number in file " + file.getAbsolutePath());

                            //here we fix some mistakes that Tesseracts does when doing OCR on images and add the data to OCR string array
                            MainWindow.ocrDenomination.add(trainOcr(ButtonListeners.SerialOcr(file)));
                            ocrText.add(trainOcr(ButtonListeners.SerialOcr(file)));
                            log.info("Doing OCR on image files of serial numbers.");
                            //we convert to image to ImageIcon and add it to ImageIcon array
                            serialImage.add(makeIcon(img));
                            log.info("Adding images to an array");

                            label.setText("Preuzimam slike: " + file.getName());
                        }
                        //here we add ocrText to gui MainWindow
                        try {
                            DefaultListModel<String> model1 = new DefaultListModel<>();
                            int j = 0;
                            boolean valid = true;
                            for (int i = 51; i < countData.size(); i++) {
                                if (countData.get(i).equals("\u001B3")) {
                                    valid = false;
                                    break;
                                }
                                if (valid) {
                                    model_ocrText.add(j, ocrText.get(j) + " " + countData.get(i));
                                    j++;
                                }
                            }
                            MainWindow.jList_ocrText.setModel(model_ocrText);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                        }
                        DefaultListModel<ImageIcon> model2 = new DefaultListModel<>();
                        //here we add images to gui MainWindow
                        for (int i = 0; i < serialImage.size(); i++) {
                            model_serialImage.add(i, serialImage.get(i));
                        }
                        MainWindow.jList_serialImage.setModel(model_serialImage);

                    }
                    //after getting all the data to the gui table in MainWindow, we calculate the total count data
                    ButtonListeners.tableTotalAmountRows(MainWindow.jt_denom);
                    ButtonListeners.tableTotalAmountColumns(MainWindow.jt_denom);

                    frame.dispose();

                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();

                }
            }
        });
    }

    public static ImageIcon makeIcon(BufferedImage img) {
        return new ImageIcon(img);
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

    public static String trainOcr(String result) {
        int index = result.indexOf(" ");// index of first blank space
        String firstWord = "";
        if (index != -1) {
            firstWord = result.substring(0, index); // get first word
            firstWord = firstWord.replaceAll("[8|B]", "0").replaceAll("[S,s]", "5");
        }
        return firstWord;
    }

}
