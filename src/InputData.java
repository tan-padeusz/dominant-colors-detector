import java.awt.image.BufferedImage;

public class InputData {
    private final BufferedImage image;
    private final int dominantColorsCount;
    private final int similarityThreshold;
    private final int threadsCount;

    public InputData(BufferedImage image, int dominantColorsCount, int similarityThreshold, int threadsCount) {
        this.image = image;
        this.dominantColorsCount = dominantColorsCount;
        this.similarityThreshold = similarityThreshold;
        this.threadsCount = threadsCount;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public int getDominantColorsCount() {
        return this.dominantColorsCount;
    }

    public int getSimilarityThreshold() {
        return this.similarityThreshold;
    }

    public int getThreadsCount() {
        return this.threadsCount;
    }
}