package GUI;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ButtonListeners {

    public static void tableTotalAmountRows(JTable table) {

        try {
            for (int i = 0; i < table.getRowCount() - 1; i++) {
                String denom = table.getValueAt(i, 0).toString();
                int denom2 = Integer.parseInt(denom);
                String totalPcs = table.getValueAt(i, 1).toString();
                int totalPcs2 = Integer.parseInt(totalPcs);
                int total = totalPcs2 * denom2;
                table.setValueAt(String.valueOf(total), i, 2);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void tableTotalAmountColumns(JTable table){
        int totalPcs2 = 0;
        int denom2 = 0;
        try{
            for (int i = 0; i < table.getRowCount(); i++){
                String totalPcs = table.getValueAt(i, 1).toString();
                totalPcs2 = totalPcs2 + Integer.parseInt(totalPcs);
                String denom = table.getValueAt(i, 2).toString();
                denom2 = denom2 + Integer.parseInt(denom);
            }
            table.setValueAt(String.valueOf(totalPcs2), 9,1);
            table.setValueAt(String.valueOf(denom2), 9,2);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    public void PDFPrinter(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fis.close();
            fc.close();
            PDFFile pdfFile = new PDFFile(bb); // Create PDF Print Page
            PDFPrintPage pages = new PDFPrintPage(pdfFile);

            // Create Print Job
            PrinterJob pjob = PrinterJob.getPrinterJob();
            PageFormat pf = PrinterJob.getPrinterJob().defaultPage();
            Paper a4paper = new Paper();
            double paperWidth = 8.26;
            double paperHeight = 11.69;
            a4paper.setSize(paperWidth * 72.0, paperHeight * 72.0);

            /*
             * set the margins respectively the imageable area
             */
            double leftMargin = 0.3;
            double rightMargin = 0.3;
            double topMargin = 0.5;
            double bottomMargin = 0.5;

            a4paper.setImageableArea(leftMargin * 72.0, topMargin * 72.0,
                    (paperWidth - leftMargin - rightMargin) * 72.0,
                    (paperHeight - topMargin - bottomMargin) * 72.0);
            pf.setPaper(a4paper);

            pjob.setJobName(file.getName());
            Book book = new Book();
            book.append(pages, pf, pdfFile.getNumPages());
            pjob.setPageable(book);

            // Send print job to default printer
            if (pjob.printDialog()) {
                pjob.print();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(null, "Printing Error: "
                            + e.getMessage(), "Print Aborted",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    class PDFPrintPage implements Printable {
        private PDFFile file;

        PDFPrintPage(PDFFile file) {
            this.file = file;
        }

        public int print(Graphics g, PageFormat format, int index)
                throws PrinterException {
            int pagenum = index + 1;

            // don't bother if the page number is out of range.
            if ((pagenum >= 1) && (pagenum <= file.getNumPages())) {
                // fit the PDFPage into the printing area
                Graphics2D g2 = (Graphics2D) g;
                PDFPage page = file.getPage(pagenum);
                double pwidth = format.getImageableWidth();
                double pheight = format.getImageableHeight();

                double aspect = page.getAspectRatio();
                double paperaspect = pwidth / pheight;

                Rectangle imgbounds;

                if (aspect > paperaspect) {
                    // paper is too tall / pdfpage is too wide
                    int height = (int) (pwidth / aspect);
                    imgbounds = new Rectangle(
                            (int) format.getImageableX(),
                            (int) (format.getImageableY() + ((pheight - height) / 2)),
                            (int) pwidth, height);
                } else {
                    // paper is too wide / pdfpage is too tall
                    int width = (int) (pheight * aspect);
                    imgbounds = new Rectangle(
                            (int) (format.getImageableX() + ((pwidth - width) / 2)),
                            (int) format.getImageableY(), width, (int) pheight);
                }

                // render the page
                PDFRenderer pgs = new PDFRenderer(page, g2, imgbounds, null,
                        null);
                try {
                    page.waitForFinish();
                    pgs.run();
                } catch (InterruptedException ie) {
                }

                return PAGE_EXISTS;
            } else {
                return NO_SUCH_PAGE;
            }
        }
    }

}
