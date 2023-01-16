package GUI;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.concurrent.ExecutionException;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class SerialReaderWorker extends SwingWorker<Void, Void> {

    private JProgressBar progressBar;
    private BufferedInputStream bis;

    public SerialReaderWorker(JProgressBar progressBar, BufferedInputStream bis) {
        this.progressBar = progressBar;
        this.bis = bis;
    }

    @Override
    protected Void doInBackground() throws Exception {
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = bis.read(buffer)) != -1) {
            bytesRead += bytesRead;
            setProgress((int) (((double) bytesRead / Integer.MAX_VALUE) * 100));
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
