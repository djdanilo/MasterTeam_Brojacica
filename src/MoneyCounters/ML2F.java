package MoneyCounters;

import GUI.ButtonListeners;
import GUI.MainWindow;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static GUI.TestSerialPortRead.SerialOcr;

public class ML2F {


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

                    //here I save the count data in the string array
                    List<String> countData = new ArrayList<>();
                    //here I save the text that is OCRed from the images
                    List<String> ocrText = new ArrayList<>();
                    //here I save the images from the machine
                    List<ImageIcon> serialImage = new ArrayList<>();

                    //trying to decode non UTF-8 strings from an array of Strings, only for TEST purposes
                    CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
                    decoder.onMalformedInput(CodingErrorAction.IGNORE);

                    //Strings that represent the start of the serial number and new lines on each serial number
                    String startSn = "110000010011010001101100100011011000100011010000111000010001010";
                    String newLine = "01100000110110010001101100010001101000011100001000101";

                    int x = 0;
                    String s1 = "";

                    //getting InputStream from serial port and then converting it to BufferedStream, so it can be reset
                    //and read two times
                    InputStream in = comPort.getInputStream();
                    InputStream bufferdInputStream = new BufferedInputStream(in);
                    bufferdInputStream.mark(1000000000);

                    //I try to show progress of receiving data from InputStream
                    //showProgress(bufferedInputStream, MainWindow.frame);

                    //first reading the input stream with scanner to get count data as Strings
                    Scanner sc = new Scanner(bufferdInputStream);

                    while (sc.hasNextLine()) {

                        countData.add(sc.next());
                        //this is the last string in the InputStream, so I use it to break from the while loop
                        if (countData.contains("\u001Bm\u001B3")) {
                            break;
                        }
                        System.out.println(countData);
                    }

                    System.out.println(countData);

                    //here I reset the InputStream, so it can be read again to receive the bytes needed to get the image
                    //of serial number
                    bufferdInputStream.reset();

                    while (((x = bufferdInputStream.read()) != 109)) {
                        //bytes are converted to binaryStrings, so I can get the binary image with Java Graphics library
                        s1 += String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
                    }

                    //bytes are stored in the String array, they are split by String for the start of serial number
                    String[] binarySerialNumberArr = s1.split(startSn);

                    //here I immediately write the binaryString to the gui of MainWindow, so it can be saved to the database
                    MainWindow.jt_serialBinary.setText(String.join(", ", binarySerialNumberArr));


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

                        //here we use the Tesseracts OCR library to get OCR text from image file
                        String ocrString = SerialOcr(file);
                        //here we fix some mistakes that OCR library does when doing OCR on images and add the data to
                        //OCR string array
                        ocrText.add(trainOcr(ocrString));
                        //we convert to image to ImageIcon and add it to ImageIcon array
                        serialImage.add(makeIcon(img));

                        System.out.println(ocrString);
                    }

                    //here we add ocrText to gui MainWindow
                    for (int i = 0; i < ocrText.size(); i++) {
                        MainWindow.model_ocrText.add(i, ocrText.get(i));
                    }
                    //here we add images to gui MainWindow
                    for (int i = 0; i < serialImage.size(); i++) {
                        MainWindow.model_serialImage.add(i, serialImage.get(i));
                    }

                    //here I try to ignore non UTF-8 string, so I can get the OCR values from the machine also
                    //this is a TEST
                    ArrayList<String> validData = new ArrayList<>();

                    for (int i = 0; i < countData.size(); i++) {
                        decoder.decode(java.nio.ByteBuffer.wrap(countData.get(i).getBytes()));
                        validData.add(countData.get(i));
                    }

                    System.out.println("Decoded data: " + validData);


                    //checking to see what currency is the data from the machine, and building the gui table accordingly
                    for (int i = 0; i < countData.size(); i++) {
                        switch (countData.get(i)) {
                            case "RSD" -> insertRSD(countData, MainWindow.jt_denom);
                            case "USD" -> insertUSD(countData, MainWindow.jt_denom);
                            case "EUR" -> insertEUR(countData, MainWindow.jt_denom);
                            default ->
                                //in case none of the above currencies is chosen, we show the error message on JOptionPane
                                    JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana", "Greška!", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    //after getting all the data to the gui table in MainWindow, we calculate the total count data
                    ButtonListeners.tableTotalAmountRows(MainWindow.jt_denom);
                    ButtonListeners.tableTotalAmountColumns(MainWindow.jt_denom);

                } catch (Exception e) {
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

    public static void showProgress(InputStream bufferedInputStream, JFrame frame) throws IOException {
        JOptionPane pane = new JOptionPane();
        pane.setMessage("Receiving data...");
        JProgressBar jProgressBar = new JProgressBar(1, bufferedInputStream.available());
        jProgressBar.setValue(bufferedInputStream.read());
        pane.add(jProgressBar,1);
        JDialog dialog = pane.createDialog(frame, "Information message");
        dialog.setVisible(true);
        dialog.dispose();
    }


}
