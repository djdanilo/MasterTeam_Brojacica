package GUI;


import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PDFExportDatabase {

    private static final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);


    public static void createPdfExport(String Id, String time, String user, String client, String file, String[] denomination, String[] serialOcr, String[] serialImage) {
        try {
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(file);
            PdfWriter.getInstance(document, fos);
            document.open();
            addMetaData(document);
            createPdf(document, Id, time, user, new Date(), client, denomination, serialOcr, serialImage);
            document.close();
            fos.close();
            System.out.println("PDF file has been generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addMetaData(Document document) {
        document.addTitle("Izveštaj o transakciji");
        document.addSubject("Izveštaj o transakciji");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Danilo Djurovic");
        document.addCreator("Danilo Djurovic");
    }

    public static void createPdf(Document document, String id, String time, String user, Date date, String client, String[] denomination, String[] serialOcr, String[] serialImage)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph("Izveštaj o transakciji", catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph(
                "Izveštaj generisao: " + user + ", " + date, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        addEmptyLine(preface, 2);

        preface.add(new Paragraph("Id transakcije: " + id, smallBold));
        preface.add(new Paragraph("Vreme transakcije: " + time, smallBold));
        preface.add(new Paragraph("Klijent: " + client, smallBold));

        addEmptyLine(preface, 1);

        preface.add(new Paragraph(
                "Apoenska struktura transakcije",
                smallBold));

        addEmptyLine(preface, 2);

        document.add(preface);
        document.add(createTableDenom(denomination));

        document.add(new Paragraph("\n", smallBold));

        document.add(new Paragraph("Serijski brojevi ", smallBold));
        document.add(new Paragraph("\n", smallBold));
        document.add(new Paragraph("\n", smallBold));

        PdfPTable largeTable = createTableSerial(serialOcr, serialImage);
        List<PdfPRow> rows = largeTable.getRows();
        int rowsPerTable = 20;
        int currentRow = 0;

        while (currentRow < rows.size()) {
            PdfPTable smallTable = new PdfPTable(largeTable.getNumberOfColumns());
            smallTable.setWidthPercentage(100);
            for (int i = currentRow; i < currentRow + rowsPerTable; i++) {
                if (i >= rows.size()) {
                    break;
                }
                PdfPCell[] cells = rows.get(i).getCells();
                for (PdfPCell cell : cells) {
                    smallTable.addCell(cell);
                }
            }
            document.add(smallTable);
            currentRow += rowsPerTable;
        }

    }


    private static PdfPTable createTableDenom(String[] denomination)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        table.setWidthPercentage(100);

        PdfPCell c1 = new PdfPCell(new Phrase("Apoen - " + denomination[0]));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Broj komada"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Vrednost"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(denomination.length - 1);

        if (denomination[0].equals("RSD")) {
            int totalPcs = 0;
            table.addCell("10");
            table.addCell(denomination[1]);
            table.addCell(String.valueOf(10 * Integer.parseInt(denomination[1])));
            table.addCell("20");
            table.addCell(denomination[2]);
            table.addCell(String.valueOf(20 * Integer.parseInt(denomination[2])));
            table.addCell("50");
            table.addCell(denomination[3]);
            table.addCell(String.valueOf(50 * Integer.parseInt(denomination[3])));
            table.addCell("100");
            table.addCell(denomination[4]);
            table.addCell(String.valueOf(100 * Integer.parseInt(denomination[4])));
            table.addCell("200");
            table.addCell(denomination[5]);
            table.addCell(String.valueOf(200 * Integer.parseInt(denomination[5])));
            table.addCell("500");
            table.addCell(denomination[6]);
            table.addCell(String.valueOf(500 * Integer.parseInt(denomination[6])));
            table.addCell("1000");
            table.addCell(denomination[7]);
            table.addCell(String.valueOf(1000 * Integer.parseInt(denomination[7])));
            table.addCell("2000");
            table.addCell(denomination[8]);
            table.addCell(String.valueOf(2000 * Integer.parseInt(denomination[8])));
            table.addCell("5000");
            table.addCell(denomination[9]);
            table.addCell(String.valueOf(5000 * Integer.parseInt(denomination[9])));
            table.addCell("Ukupno:");
            for (int i = 1; i < 10; i++) {
                totalPcs += Integer.parseInt(denomination[i]);
            }
            table.addCell(String.valueOf(totalPcs));
            table.addCell(denomination[10]);
        } else if (denomination[0].equals("EUR")) {
            int totalPcs = 0;
            table.addCell("5");
            table.addCell(denomination[1]);
            table.addCell(String.valueOf(5 * Integer.parseInt(denomination[1])));
            table.addCell("10");
            table.addCell(denomination[2]);
            table.addCell(String.valueOf(10 * Integer.parseInt(denomination[2])));
            table.addCell("20");
            table.addCell(denomination[3]);
            table.addCell(String.valueOf(20 * Integer.parseInt(denomination[3])));
            table.addCell("50");
            table.addCell(denomination[4]);
            table.addCell(String.valueOf(50 * Integer.parseInt(denomination[4])));
            table.addCell("100");
            table.addCell(denomination[5]);
            table.addCell(String.valueOf(100 * Integer.parseInt(denomination[5])));
            table.addCell("200");
            table.addCell(denomination[6]);
            table.addCell(String.valueOf(200 * Integer.parseInt(denomination[6])));
            table.addCell("500");
            table.addCell(denomination[7]);
            table.addCell(String.valueOf(500 * Integer.parseInt(denomination[7])));
            table.addCell("Ukupno:");
            for (int i = 1; i < 8; i++) {
                totalPcs += Integer.parseInt(denomination[i]);
            }
            table.addCell(String.valueOf(totalPcs));
            table.addCell(denomination[8]);
        } else if (denomination[0].equals("USD")) {
            int totalPcs = 0;
            table.addCell("1");
            table.addCell(denomination[1]);
            table.addCell(String.valueOf(Integer.parseInt(denomination[1])));
            table.addCell("2");
            table.addCell(denomination[2]);
            table.addCell(String.valueOf(2 * Integer.parseInt(denomination[2])));
            table.addCell("5");
            table.addCell(denomination[3]);
            table.addCell(String.valueOf(5 * Integer.parseInt(denomination[3])));
            table.addCell("10");
            table.addCell(denomination[4]);
            table.addCell(String.valueOf(10 * Integer.parseInt(denomination[4])));
            table.addCell("20");
            table.addCell(denomination[5]);
            table.addCell(String.valueOf(20 * Integer.parseInt(denomination[5])));
            table.addCell("50");
            table.addCell(denomination[6]);
            table.addCell(String.valueOf(50 * Integer.parseInt(denomination[6])));
            table.addCell("100");
            table.addCell(denomination[7]);
            table.addCell(String.valueOf(100 * Integer.parseInt(denomination[7])));
            table.addCell("Ukupno:");
            for (int i = 1; i < 8; i++) {
                totalPcs += Integer.parseInt(denomination[i]);
            }
            table.addCell(String.valueOf(totalPcs));
            table.addCell(denomination[8]);
        }

        return table;

    }

    private static PdfPTable createTableSerial(String[] serialOcr, String[] serialImage)
            throws BadElementException {
        PdfPTable table = new PdfPTable(3);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        String newLine2 = "01100000110110010001101100010001101000011100001000101";

        PdfPCell c1 = new PdfPCell(new Phrase("Apoen"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Serijski broj"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Slika serijskog broja"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(serialImage.length - 1);

        try {
            int i = 0;
            int j = 1;
            int row = 0;

            while (row < (serialOcr.length + 1) / 2) {
                if (serialImage.length > 1) {
                    table.addCell(serialOcr[i]);
                    table.addCell(serialOcr[i + 1]);

                    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
                    Graphics2D g2d = img.createGraphics();
                    java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN, 2);
                    g2d.setFont(font);
                    int height = g2d.getFontMetrics().getHeight();
                    g2d.dispose();

                    img = new BufferedImage(384, 40, BufferedImage.TYPE_INT_RGB);
                    g2d = img.createGraphics();

                    g2d.setFont(font);
                    g2d.setColor(Color.WHITE);
                    int fontSize = 1;

                    for (String line2 : serialImage[j].split(newLine2)) {

                        g2d.drawString(line2, 0, height);
                        height += fontSize;
                    }

                    File file = new File("images\\TextDB" + j + ".png");

                    ImageIO.write(img, "png", file);

                    g2d.dispose();

                    table.addCell(Image.getInstance(img, null));
                    i += 2;
                    j++;
                    row++;
                } else {
                    table.addCell(serialOcr[i]);
                    table.addCell(serialOcr[i + 1]);
                    table.addCell("/");
                    i += 2;
                    j++;
                    row++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return table;

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
