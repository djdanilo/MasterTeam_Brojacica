package GUI;

import MoneyCounters.SB9;

import javax.swing.*;
import java.util.List;

public class ProgressBar extends JFrame {
    private JProgressBar progressBar;
    private MySwingWorker worker;
    private List<String> list;

    public ProgressBar() {
        //create progress bar
        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        add(progressBar);

        //create and start worker
        worker = new MySwingWorker();
        worker.execute();
    }

    public static void main(String[] args) {
        ProgressBar frame = new ProgressBar();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    class MySwingWorker extends SwingWorker<Void, Integer> {
        @Override
        protected Void doInBackground() throws Exception {
            list = SB9.countData;
            for (int i = 0; i < list.size(); i++) {
                Thread.sleep(50);
                i++;
                publish(i);
            }
            return null;
        }

        @Override
        protected void process(java.util.List<Integer> chunks) {
            for (int i : chunks) {
                progressBar.setValue((i+1)*100/list.size());
            }
        }
    }
}
