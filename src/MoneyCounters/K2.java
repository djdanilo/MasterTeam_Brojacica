package MoneyCounters;

import GUI.ButtonListeners;
import GUI.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import net.sourceforge.tess4j.TesseractException;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import static GUI.MainWindow.*;
import static GUI.MainWindow.lb_timeDate;

public class K2 {
    public static Logger log = Logger.getLogger(K2.class.getName());
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
                    log.info("Receiving data from K2.");
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
                    //string for denomination
                    String denomination = "";

                    //Strings that represent the start of the serial number and new lines on each serial number
                    String startSn = "000111010111011000110000";

                    //removing elements from the list
                    int x = 0;
                    String s1 = "";
                    int countBytes = 0;
                    List<Integer> byteList = new ArrayList<>();
                    String substring = "#v0\u0002";
                    String substring2 = "�";
                    String substring3 = "\u0000";
                    String substring4 = "π";
                    String substring5 = "ǀ";

                    //getting InputStream from serial port and then converting it to BufferedStream, so it can be reset
                    //and read two times
                    InputStream in = comPort.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                    bufferedInputStream.mark(1000000000);

                    //trying to show progress of received data from inputStream
                    new ProgressBarFrame();

                    //first reading the input stream with scanner to get count data as Strings
                    Scanner sc = new Scanner(bufferedInputStream);
                    while (sc.hasNextLine()) {
                        countData.add(sc.next());
                        //this is the last string in the InputStream, so I use it to break from the while loop
                        if (countData.get(countData.size() - 1).contains("\u001B@")) {
                            break;
                        }
                        if (countData.contains("RSD") && countData.contains("AUTO") && countData.contains("ARM")) {
                            break;
                        }
                        if (countData.contains("RSD") && countData.size() == 62 && !countData.contains("AUTO")) {
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
                    System.out.println("Out of loop" + countData);
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
                        } else if (countData.contains("EUR")) {
                            MainWindow.lb_currency.setText("EUR");
                            denomination = "EUR";
                            insertEUR(countData, MainWindow.jt_denom);
                        } else {
                            //in case none of the above currencies is chosen, we show the error message on JOptionPane
                            JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana", "Greška!", JOptionPane.ERROR_MESSAGE);
                            ProgressBarFrame.frame.dispose();
                            return;
                        }
                    }
                    ProgressBarFrame.label.setText("Preuzimam serijske brojeve");
                    jt_logs.append(lb_timeDate.getText() + "     Preuzimam serijske brojeve\n");

                    //here I reset the InputStream, so it can be read again to receive the bytes needed to get the image
                    //of serial number
                    bufferedInputStream.reset();

                    while (((x = bufferedInputStream.read()) != 27)) {
                        byteList.add(x);
                        ProgressBarFrame.label.setText("Preuzimam bajtove: " + countBytes);
                        //bytes are converted to binaryStrings, so I can get the binary image with Java Graphics library
                        s1 += String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
                        countBytes++;
                    }

                    //closing input stream
                    bufferedInputStream.close();
                    in.close();


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
                        Font font = new Font("Arial", Font.PLAIN, 1);
                        g2d.setFont(font);
                        int height = g2d.getFontMetrics().getHeight();
                        g2d.dispose();
                        //here we start to create the actual image of the serial number
                        img = new BufferedImage(248, 24, BufferedImage.TYPE_BYTE_BINARY);
                        g2d = img.createGraphics();

                        g2d.setFont(font);
                        g2d.setColor(Color.WHITE);
                        int fontSize = 1;
                        int length = binarySerialNumberArr[i].length();
                        int j = 40;
                        int xH = 0;

                        if ((Collections.indexOfSubList(byteList, Arrays.asList(29, 118, 48, 2, 31)) >= 0))
                            xH = 248;
                        else if ((Collections.indexOfSubList(byteList, Arrays.asList(29, 118, 48, 2, 32)) >= 0))
                            xH = 256;
                        else if ((Collections.indexOfSubList(byteList, Arrays.asList(29, 118, 48, 2, 33)) >= 0))
                            xH = 264;

                        while (j < length) {
                            int end = Math.min(length, j + xH);
                            g2d.drawString(binarySerialNumberArr[i].substring(j, end), 0, height);
                            height += fontSize;
                            j = end;
                        }

                        //creating the image file
                        File tempFile = File.createTempFile("temp"+i, ".png");
                        //tempFile.deleteOnExit();
                        //saving the image to file
                        ImageIO.write(img, "png", tempFile);
                        g2d.dispose();
                        log.info("Creating an image of serial number in file " + tempFile.getAbsolutePath());

                        MainWindow.ocrDenomination.add(ButtonListeners.SerialOcrK2(tempFile));
                        ocrText.add(ButtonListeners.SerialOcrK2(tempFile));
                        log.info("Doing OCR on image files of serial numbers.");
                        //we convert to image to ImageIcon and add it to ImageIcon array
                        serialImage.add(makeIcon(img));
                        log.info("Adding images to an array");

                        ProgressBarFrame.label.setText("Preuzimam serijske brojeve: "
                                + ButtonListeners.SerialOcrK2(tempFile));
                    }
                    for (int i = 0; i < ocrText.size(); i++){
                        model_ocrText.add(i, "apoen " + "+OCR");
                    }
                    MainWindow.jList_ocrText.setModel(model_ocrText);

                    for (int i = 0; i < serialImage.size(); i++){
                        model_serialImage.add(i, serialImage.get(i));
                    }
                    MainWindow.jList_serialImage.setModel(model_serialImage);



                    //after getting all the data to the table in MainWindow, we calculate the total count data
                    ButtonListeners.tableTotalAmountRows(MainWindow.jt_denom);
                    ButtonListeners.tableTotalAmountColumns(MainWindow.jt_denom, denomination);
                    //logs
                    jt_logs.append(lb_timeDate.getText() + "     Podaci primljeni\n");
                    //closing progressBar
                    ProgressBarFrame.frame.dispose();

                } catch (Exception e) {
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
        jt_denom.setValueAt("", 7, 0);
        jt_denom.setValueAt("", 8, 0);
        jt_denom.setValueAt("Ukupno", 9, 0);

        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).equals("Denom")) {
                jt_denom.setValueAt(line.get(i + 4), 0, 1);
                jt_denom.setValueAt(line.get(i + 7), 1, 1);
                jt_denom.setValueAt(line.get(i + 10), 2, 1);
                jt_denom.setValueAt(line.get(i + 13), 3, 1);
                jt_denom.setValueAt(line.get(i + 17), 4, 1);
                jt_denom.setValueAt(line.get(i + 20), 5, 1);
                jt_denom.setValueAt(line.get(i + 23), 6, 1);
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
            if (line.get(i).equals("Denom")) {
                jt_denom.setValueAt(line.get(i + 4), 0, 1);
                jt_denom.setValueAt(line.get(i + 7), 1, 1);
                jt_denom.setValueAt(line.get(i + 10), 2, 1);
                jt_denom.setValueAt(line.get(i + 13), 3, 1);
                jt_denom.setValueAt(line.get(i + 17), 4, 1);
                jt_denom.setValueAt(line.get(i + 20), 5, 1);
                jt_denom.setValueAt(line.get(i + 23), 6, 1);
                jt_denom.setValueAt(line.get(i + 26), 7, 1);
                jt_denom.setValueAt(line.get(i + 29), 8, 1);
            }
        }
    }
    public static ImageIcon makeIcon(BufferedImage img) {
        return new ImageIcon(img);
    }
}
