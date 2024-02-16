import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorCounterThread {
    private final int id;
    private final int[][][] colors = new int[256][256][256];
    private int pixelsChecked = 0;
    private boolean isRunning;

    public ColorCounterThread(int id) {
        this.id = id;
    }

    public void start(InputData data) {
        if (this.isRunning) {
            return;
        }
        Thread worker = this.createWorkerThread(data);
        worker.start();
    }

    public void stop() {
        if (!this.isRunning) {
            return;
        }
        this.pixelsChecked = Integer.MAX_VALUE;
    }

    private Thread createWorkerThread(InputData data) {
        return new Thread(() -> {
            this.onWorkerStart();
            this.countColors(data);
            this.onWorkerStop();
        });
    }

    private void onWorkerStart() {
        this.isRunning = true;
    }

    private void onWorkerStop() {
        this.isRunning = false;
    }

    public int[][][] getColors() {
        return this.colors;
    }

    public int getPixelsChecked() {
        return this.pixelsChecked;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    private void countColors(InputData data) {
        BufferedImage image = data.image();
        int totalPixels = image.getHeight() * image.getWidth();
        int pixelsToCheck = totalPixels / data.threadsCount();
        int heightPartSize = image.getHeight() / data.threadsCount();
        int startHeight = this.id * heightPartSize;
        int endHeight = (this.id + 1) * heightPartSize;
        int column = 0;
        int row = startHeight;
        while (this.pixelsChecked < pixelsToCheck) {
            Color color = new Color(image.getRGB(column, row));
            this.addSimilarColors(color, data.similarityThreshold());
            this.pixelsChecked++;
            row++;
            if (row >= endHeight) {
                row = startHeight;
                column++;
            }
        }
    }

    private void addSimilarColors(Color color, int similarityThreshold) {
        int startRed = Math.max(color.getRed() - similarityThreshold, 0);
        int endRed = Math.min(color.getRed() + similarityThreshold, 256);
        int startGreen = Math.max(color.getGreen() - similarityThreshold, 0);
        int endGreen = Math.min(color.getGreen() + similarityThreshold, 256);
        int startBlue = Math.max(color.getBlue() - similarityThreshold, 0);
        int endBlue = Math.min(color.getBlue() + similarityThreshold, 256);
        for (int r = startRed; r < endRed; r++) {
            for (int g = startGreen; g < endGreen; g++) {
                for (int b = startBlue; b < endBlue; b++) {
                    this.colors[r][g][b]++;
                }
            }
        }
    }
}