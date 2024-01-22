import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorDetector {
    private Thread worker;
    private InputData data;

    private final JProgressBar progressBar;
    private final JLabel[] colorLabels;

    private boolean isRunning;

    public ColorDetector(JProgressBar progressBar, JLabel[] colorLabels) {
        this.progressBar = progressBar;
        this.colorLabels = colorLabels;
    }

    private void initializeWorkerThread() {
        this.worker = new Thread(() -> {
            this.isRunning = true;
            this.reset();
            ColorThread[] colorThreads = initializeColorThreads();
            boolean result = this.waitForColorThreadsToFinishWork(colorThreads);
            if (!result) {
                this.reset();
                this.isRunning = false;
                return;
            }
            int[][][] colors = this.sumColorsFromColorThreads(colorThreads);
            Color[] dominantColors = this.findAllDominantColors(colors);
            this.showDominantColors(dominantColors);
            this.isRunning = false;
        });
        this.worker.start();
    }

    private void reset() {
        this.progressBar.setValue(0);
        BufferedImage image = this.data.getImage();
        this.progressBar.setMaximum(image.getHeight() * image.getWidth());
        for (JLabel label : this.colorLabels) {
            label.setVisible(false);
        }
    }

    private ColorThread[] initializeColorThreads() {
        ColorThread[] colorThreads = new ColorThread[this.data.getThreadsCount()];
        for (int index = 0; index < colorThreads.length; index++) {
            ColorThread colorThread = new ColorThread(index, this.data);
            colorThread.start();
            colorThreads[index] = colorThread;
        }
        return colorThreads;
    }

    private boolean waitForColorThreadsToFinishWork(ColorThread[] colorThreads) {
        boolean areColorThreadsRunning = true;
        while (areColorThreadsRunning) {
            if (this.worker.isInterrupted()) {
                for (ColorThread ct : colorThreads) {
                    ct.stop();
                }
                return false;
            }
            areColorThreadsRunning = false;
            int pixelsChecked = 0;
            for (ColorThread colorThread : colorThreads) {
                areColorThreadsRunning |= colorThread.isRunning();
                pixelsChecked += colorThread.getPixelsChecked();
            }
            this.progressBar.setValue(pixelsChecked);
        }
        return true;
    }

    private int[][][] sumColorsFromColorThreads(ColorThread[] colorThreads) {
        int[][][] colors = new int[256][256][256];
        for (ColorThread colorThread : colorThreads) {
            ColorDetector.sumColorArrays(colors, colorThread.getColors());
        }
        return colors;
    }

    private Color[] findAllDominantColors(int[][][] colors) {
        Color[] dominantColors = new Color[this.data.getDominantColorsCount()];
        for (int index = 0; index < dominantColors.length; index++) {
            Color dominantColor = ColorDetector.findSingleDominantColor(colors);
            ColorDetector.removeSimilarColors(colors, dominantColor, this.data.getSimilarityThreshold());
            dominantColors[index] = dominantColor;
        }
        return dominantColors;
    }

    private void showDominantColors(Color[] dominantColors) {
        for (int index = 0; index < dominantColors.length; index++) {
            Color dominantColor = dominantColors[index];
            JLabel colorLabel = this.colorLabels[index];
            ImageLoader.loadColorIntoLabel(dominantColor, colorLabel);
            colorLabel.setOpaque(true);
            colorLabel.setVisible(true);
        }
    }

    private static void sumColorArrays(int[][][] target, int[][][] source) {
        for (int r = 0; r < 256; r++) {
            for (int g = 0; g < 256; g++) {
                for (int b = 0; b < 256; b++) {
                    target[r][g][b] += source[r][g][b];
                }
            }
        }
    }

    private static Color findSingleDominantColor(int[][][] colors) {
        int redValue = 0;
        int greenValue = 0;
        int blueValue = 0;
        int maxColorCount = colors[0][0][0];
        for (int r = 0; r < 256; r++) {
            for (int g = 0; g < 256; g++) {
                for (int b = 0; b < 256; b++) {
                    int colorCount = colors[r][g][b];
                    if (colorCount > maxColorCount) {
                        redValue = r;
                        greenValue = g;
                        blueValue = b;
                        maxColorCount = colorCount;
                    }
                }
            }
        }
        return new Color(redValue, greenValue, blueValue);
    }

    private static void removeSimilarColors(int[][][] colors, Color color, int similarityThreshold) {
        int startRed = Math.max(color.getRed() - similarityThreshold, 0);
        int endRed = Math.min(color.getRed() + similarityThreshold, 256);
        int startGreen = Math.max(color.getGreen() - similarityThreshold, 0);
        int endGreen = Math.min(color.getGreen() + similarityThreshold, 256);
        int startBlue = Math.max(color.getBlue() - similarityThreshold, 0);
        int endBlue = Math.min(color.getBlue() + similarityThreshold, 256);
        for (int r = startRed; r < endRed; r++) {
            for (int g = startGreen; g < endGreen; g++) {
                for (int b = startBlue; b < endBlue; b++) {
                    colors[r][g][b] = 0;
                }
            }
        }
    }

    public void start(InputData data) {
        if (this.isRunning) {
            return;
        }
        this.data = data;
        this.initializeWorkerThread();
    }

    public void stop() {
        this.worker.interrupt();
    }
}