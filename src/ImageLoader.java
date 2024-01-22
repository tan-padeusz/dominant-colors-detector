import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {
    public static BufferedImage loadImageFromFile(String imagePath) {
        try {
            return ImageIO.read(new File(imagePath));
        } catch (IOException exception) {
            return null;
        }
    }

    public static void loadImageIntoLabel(BufferedImage image, JLabel label) {
        Image scaledImage = ImageLoader.getScaledImage(image, label);
        ImageIcon icon = new ImageIcon(scaledImage);
        label.setIcon(icon);
    }

    private static Image getScaledImage(BufferedImage image, JLabel label) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        int boundaryWidth = label.getWidth();
        int boundaryHeight = label.getHeight();
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > boundaryWidth) {
            newWidth = boundaryWidth;
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        if (newHeight > boundaryHeight) {
            newHeight = boundaryHeight;
            newWidth = (newHeight * originalWidth) / originalHeight;
        }

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
    }

    public static void loadColorIntoLabel(Color color, JLabel label) {
        int width = label.getWidth();
        int height = label.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                image.setRGB(column, row, color.getRGB());
            }
        }
        ImageIcon icon = new ImageIcon(image);
        label.setIcon(icon);
    }
}