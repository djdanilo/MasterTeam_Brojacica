package GUI;

import javax.swing.*;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class SerialReaderWorker extends SwingWorker<String, Void> {
    private InputStream inputStream;
    private JProgressBar progressBar;

    public SerialReaderWorker(InputStream inputStream, JProgressBar progressBar) {
        this.inputStream = inputStream;
        this.progressBar = progressBar;
    }

    @Override
    protected String doInBackground() throws Exception {
        int totalBytes = inputStream.available();
        int bytesRead = 0;
        byte[] buffer = new byte[1024];

        while (bytesRead < totalBytes) {
            int n = inputStream.read(buffer);
            bytesRead += n;
            setProgress((int) (100 * ((double) bytesRead / totalBytes)));
        }

        return new String(buffer);
    }

    @Override
    protected void done() {
        try {
            String result = get();
            // update the user interface with the result
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
