import javax.swing.*;
import java.awt.*;

public class ColorDetector {
    private final Thread worker;
    private InputData data;

    private final JProgressBar progressBar;
    private final JLabel[] colorLabels;

    private boolean isRunning;

    public ColorDetector(JProgressBar progressBar, JLabel[] colorLabels) {
        this.progressBar = progressBar;
        this.colorLabels = colorLabels;
        this.worker = this.createWorkerThread();
    }

    private Thread createWorkerThread() {
        return new Thread(() -> {
            this.isRunning = true;
            ColorThread[] colorThreads = initializeColorThreads();
            this.waitForColorThreadsToFinishWork(colorThreads);
            int[][][] colors = this.sumColorsFromColorThreads(colorThreads);
            Color[] dominantColors = this.findAllDominantColors(colors);
            this.showDominantColors(dominantColors);
            this.isRunning = false;
        });
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

    private void waitForColorThreadsToFinishWork(ColorThread[] colorThreads) {
        boolean areColorThreadsFinished = false;
        while (!areColorThreadsFinished) {
            areColorThreadsFinished = true;
            int pixelsChecked = 0;
            for (ColorThread colorThread : colorThreads) {
                areColorThreadsFinished &= colorThread.isFinished();
                pixelsChecked += colorThread.getPixelsChecked();
            }
            this.progressBar.setValue(pixelsChecked);
        }
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
//        this.reset();
        this.data = data;
        this.worker.start();
    }

    public void stop() {
        this.worker.interrupt();
    }

//    private void reset() {
//        this.colorThreads.clear();
//        this.dominantColors.clear();
//        this.pixelsChecked = 0;
//        this.isFinished = false;
//    }
}