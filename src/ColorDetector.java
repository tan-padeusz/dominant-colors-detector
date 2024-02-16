import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorDetector {
    private final JProgressBar progressBar;
    private final JLabel[] colorLabels;
    private boolean isRunning;
    private boolean hasCancellationPending;

    public ColorDetector(JProgressBar progressBar, JLabel[] colorLabels) {
        this.progressBar = progressBar;
        this.colorLabels = colorLabels;
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
        this.hasCancellationPending = true;
    }

    private Thread createWorkerThread(InputData data) {
        return new Thread(() -> {
            this.onWorkerStart(data);
            ColorThread[] threads = ColorDetector.initializeColorThreads(data);
            this.waitForColorThreadsToFinishWork(threads);
            if (!this.hasCancellationPending) {
                int[][][] rgbColors = ColorDetector.sumColorsFromColorThreads(threads);
                Color[] dominantColors = ColorDetector.findAllDominantColors(data, rgbColors);
                this.showDominantColors(dominantColors);
                this.onWorkerStop(false);
            }
            this.onWorkerStop(this.hasCancellationPending);
        });
    }

    private void onWorkerStart(InputData data) {
        this.isRunning = true;
        this.hasCancellationPending = false;
        this.showColorLabels(false);
        this.progressBar.setValue(0);
        BufferedImage image = data.image();
        this.progressBar.setMaximum(image.getHeight() * image.getWidth());
    }

    private void onWorkerStop(boolean forcedStop) {
        this.showColorLabels(!forcedStop);
        this.hasCancellationPending = false;
        this.isRunning = false;
    }

    private void showColorLabels(boolean show) {
        for (JLabel label : this.colorLabels) {
            label.setVisible(show);
        }
    }

    private void waitForColorThreadsToFinishWork(ColorThread[] threads) {
        boolean areColorThreadsRunning = true;
        while (areColorThreadsRunning) {
            if (this.hasCancellationPending) {
                for (ColorThread ct : threads) {
                    ct.stop();
                }
                return;
            }
            areColorThreadsRunning = false;
            int pixelsChecked = 0;
            for (ColorThread colorThread : threads) {
                areColorThreadsRunning |= colorThread.isRunning();
                pixelsChecked += colorThread.getPixelsChecked();
            }
            this.progressBar.setValue(pixelsChecked);
        }
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

    private static ColorThread[] initializeColorThreads(InputData data) {
        ColorThread[] threads = new ColorThread[data.threadsCount()];
        for (int index = 0; index < data.threadsCount(); index++) {
            threads[index] = new ColorThread(index, data);
            threads[index].start();
        }
        return threads;
    }

    private static int[][][] sumColorsFromColorThreads(ColorThread[] threads) {
        int[][][] colors = new int[256][256][256];
        for (ColorThread colorThread : threads) {
            ColorDetector.sumColorArrays(colors, colorThread.getColors());
        }
        return colors;
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

    private static Color[] findAllDominantColors(InputData data, int[][][] colors) {
        Color[] dominantColors = new Color[data.dominantColorsCount()];
        for (int index = 0; index < dominantColors.length; index++) {
            Color dominantColor = ColorDetector.findSingleDominantColor(colors);
            ColorDetector.removeSimilarColors(colors, dominantColor, data.similarityThreshold());
            dominantColors[index] = dominantColor;
        }
        return dominantColors;
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
}