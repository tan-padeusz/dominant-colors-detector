import java.awt.image.BufferedImage;

public record InputData(BufferedImage image, int dominantColorsCount, int similarityThreshold, int threadsCount) {

}