import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorThread {
    private final int id;
    private final InputData data;
    private final Thread thread;

    private int[][][] colors = new int[256][256][256];
    private int pixelsChecked = 0;
    private boolean isFinished = true;

    public ColorThread(int id, InputData data) {
        this.id = id;
        this.data = data;
        this.thread = new Thread(() -> {
            this.countColors(data.getImage(), data.getSimilarityThreshold(), this.data.getThreadsCount());
            this.isFinished = true;
        });
    }

    public int[][][] getColors() {
        return this.colors;
    }

    public int getPixelsChecked() {
        return this.pixelsChecked;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public void start() {
        if (!this.isFinished) {
            return;
        }
        this.reset();
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
    }

    private void reset() {
        this.colors = new int[256][256][256];
        this.pixelsChecked = 0;
        this.isFinished = false;
    }

    private void countColors(BufferedImage image, int similarityThreshold, int threadsCount) {
        int heightPartSize = image.getHeight() / threadsCount;
        int startHeight = this.id * heightPartSize;
        int endHeight = (this.id + 1) * heightPartSize;
        for (int colum = 0; colum < image.getWidth(); colum++) {
            for (int row = startHeight; row < endHeight; row++) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                Color color = new Color(image.getRGB(colum, row));
                this.addSimilarColors(color, similarityThreshold);
                this.pixelsChecked++;
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