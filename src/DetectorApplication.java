import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class DetectorApplication {
    private final JFrame applicationFrame = new JFrame();

    /// buttons
    private final JButton selectSourceImageButton = new JButton();
    private final JButton startButton = new JButton();
    private final JButton stopButton = new JButton();
    private final JButton exitButton = new JButton();

    /// image labels
    private final JLabel sourceImageLabel = new JLabel();
    private final JLabel firstColorLabel = new JLabel();
    private final JLabel secondColorLabel = new JLabel();
    private final JLabel thirdColorLabel = new JLabel();
    private final JLabel fourthColorLabel = new JLabel();

    /// input labels
    private final JLabel dominantColorsCountLabel = new JLabel();
    private final JLabel similarityThresholdLabel = new JLabel();
    private final JLabel threadsCountLabel = new JLabel();

    /// inputs
    private final JTextField sourceImagePathTextField = new JTextField();
    private final JTextField dominantColorsCountTextField = new JTextField();
    private final JTextField similarityThresholdTextField = new JTextField();
    private final JTextField threadsCountTextField = new JTextField();

    /// other
    private final JFileChooser imageFileChooser = new JFileChooser();
    private final JProgressBar progressBar = new JProgressBar();

    private BufferedImage sourceImage = null;
    private final BufferedImage loadingScreenImage;
    private final ColorDetector detector;

    public DetectorApplication() {
        this.configureFrame();
        this.configureButtons();
        this.configureImageLabels();
        this.configureInputLabels();
        this.configureInputs();
        this.configureOther();
        this.applicationFrame.pack();

        this.loadingScreenImage = ImageLoader.loadImageFromFile("loading-screen.png");
        this.detector = new ColorDetector(progressBar, new JLabel[] { this.firstColorLabel, this.secondColorLabel, this.thirdColorLabel, this.fourthColorLabel });
    }

    private void configureFrame() {
        this.applicationFrame.getContentPane().setPreferredSize(new Dimension(820, 610));
        this.applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.applicationFrame.setLayout(null);
        this.applicationFrame.setLocationRelativeTo(null);
        this.applicationFrame.setResizable(false);
        this.applicationFrame.setTitle("\"DOMINANT COLORS DETECTOR\" by Maciej Moryń");
        this.applicationFrame.setVisible(true);
    }

    private void configureButtons() {
        this.selectSourceImageButton.addActionListener(this::selectSourceImageButtonClick);
        this.selectSourceImageButton.setBounds(10, 10, 200, 40);
        this.selectSourceImageButton.setText("SELECT SOURCE IMAGE");
        this.applicationFrame.add(this.selectSourceImageButton);

        this.startButton.addActionListener(this::startButtonClick);
        this.startButton.setBounds(10, 460, 200, 40);
        this.startButton.setText("START");
        this.applicationFrame.add(this.startButton);

        this.stopButton.addActionListener(this::stopButtonClick);
        this.stopButton.setBounds(10, 510, 200, 40);
        this.stopButton.setText("STOP");
        this.applicationFrame.add(this.stopButton);

        this.exitButton.addActionListener(this::exitButtonClick);
        this.exitButton.setBackground(Color.RED);
        this.exitButton.setBounds(10, 560, 200, 40);
        this.exitButton.setText("EXIT");
        this.applicationFrame.add(this.exitButton);
    }

    private void configureImageLabels() {
        this.sourceImageLabel.setBackground(Color.BLACK);
        this.sourceImageLabel.setBounds(220, 10, 590, 440);
        this.sourceImageLabel.setHorizontalAlignment(JLabel.CENTER);
        this.sourceImageLabel.setVerticalAlignment(JLabel.CENTER);
        this.applicationFrame.add(this.sourceImageLabel);

        this.firstColorLabel.setBackground(Color.BLACK);
        this.firstColorLabel.setBounds(220, 460, 140, 140);
        this.firstColorLabel.setVisible(false);
        this.applicationFrame.add(this.firstColorLabel);

        this.secondColorLabel.setBackground(Color.BLACK);
        this.secondColorLabel.setBounds(370, 460, 140, 140);
        this.secondColorLabel.setVisible(false);
        this.applicationFrame.add(this.secondColorLabel);

        this.thirdColorLabel.setBackground(Color.BLACK);
        this.thirdColorLabel.setBounds(520, 460, 140, 140);
        this.thirdColorLabel.setVisible(false);
        this.applicationFrame.add(this.thirdColorLabel);

        this.fourthColorLabel.setBackground(Color.BLACK);
        this.fourthColorLabel.setBounds(670, 460, 140, 140);
        this.fourthColorLabel.setVisible(false);
        this.applicationFrame.add(this.fourthColorLabel);
    }

    private void configureInputLabels() {
        this.dominantColorsCountLabel.setBounds(10, 110, 200, 40);
        this.dominantColorsCountLabel.setHorizontalAlignment(JLabel.CENTER);
        this.dominantColorsCountLabel.setText("DOMINANT COLORS COUNT");
        this.applicationFrame.add(this.dominantColorsCountLabel);

        this.similarityThresholdLabel.setBounds(10, 210, 200, 40);
        this.similarityThresholdLabel.setHorizontalAlignment(JLabel.CENTER);
        this.similarityThresholdLabel.setText("SIMILARITY THRESHOLD");
        this.applicationFrame.add(this.similarityThresholdLabel);

        this.threadsCountLabel.setBounds(10, 310, 200, 40);
        this.threadsCountLabel.setHorizontalAlignment(JLabel.CENTER);
        this.threadsCountLabel.setText("THREADS COUNT");
        this.applicationFrame.add(this.threadsCountLabel);
    }

    private void configureInputs() {
        this.sourceImagePathTextField.setBounds(10, 60, 200, 40);
        this.sourceImagePathTextField.setEditable(false);
        this.sourceImagePathTextField.setHorizontalAlignment(JTextField.LEFT);
        this.applicationFrame.add(this.sourceImagePathTextField);

        this.dominantColorsCountTextField.setBounds(10, 160, 200, 40);
        this.dominantColorsCountTextField.setHorizontalAlignment(JTextField.CENTER);
        this.applicationFrame.add(this.dominantColorsCountTextField);

        this.similarityThresholdTextField.setBounds(10, 260, 200, 40);
        this.similarityThresholdTextField.setHorizontalAlignment(JTextField.CENTER);
        this.applicationFrame.add(this.similarityThresholdTextField);

        this.threadsCountTextField.setBounds(10, 360, 200, 40);
        this.threadsCountTextField.setHorizontalAlignment(JTextField.CENTER);
        this.applicationFrame.add(this.threadsCountTextField);
    }

    private void configureOther() {
        this.configureImageFileChooser();
        this.configureProgressBar();
    }

    private void configureImageFileChooser() {
        String[] extensions = new String[] { "jpg", "png" };
        for (String extension : extensions) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("image file (." + extension + ")", extension);
            this.imageFileChooser.addChoosableFileFilter(filter);
        }
        this.imageFileChooser.setAcceptAllFileFilterUsed(false);
    }

    private void configureProgressBar() {
        this.progressBar.setBackground(Color.DARK_GRAY);
        this.progressBar.setBounds(10, 410, 200, 40);
        this.progressBar.setForeground(Color.GREEN);
        this.progressBar.setMinimum(0);
        this.progressBar.setMaximum(1);
        this.progressBar.setValue(0);
        this.applicationFrame.add(this.progressBar);
    }

    private void selectSourceImageButtonClick(ActionEvent event) {
        int dialogResult = this.imageFileChooser.showDialog(null, "Select");
        if (dialogResult == JFileChooser.APPROVE_OPTION) {
            new Thread(() -> {
                this.selectSourceImageButton.setEnabled(false);
                ImageLoader.loadImageIntoLabel(this.loadingScreenImage, this.sourceImageLabel);
                String filePath = this.imageFileChooser.getSelectedFile().getAbsolutePath();
                this.sourceImagePathTextField.setText(filePath);
                BufferedImage image = ImageLoader.loadImageFromFile(filePath);
                this.sourceImage = image;
                if (image == null) {
                    this.showError("Error occurred while loading image " + filePath + "!");
                    this.selectSourceImageButton.setEnabled(true);
                    return;
                }
                ImageLoader.loadImageIntoLabel(image, this.sourceImageLabel);
                this.selectSourceImageButton.setEnabled(true);
            }).start();
        }
    }

    private InputData validateInputs() {
        String error = "";

        if (this.sourceImage == null) {
            error += "Source image hasn't been selected!";
        }

        Integer dominantColorsCount = InputValidator.validateNumericInput(this.dominantColorsCountTextField.getText().trim(), 1, 4);
        if (dominantColorsCount == null) {
            error += "Invalid dominant colors count value! Please enter an integer number between 1 and 4 (inclusive)!\n";
        }

        Integer similarityThreshold = InputValidator.validateNumericInput(this.similarityThresholdTextField.getText().trim(), 0, 255);
        if (similarityThreshold == null) {
            error += "Invalid similarity threshold value! Please enter an integer number between 0 and 255 (inclusive)!\n";
        }

        int coresNumber = Runtime.getRuntime().availableProcessors();
        Integer threadsCount = InputValidator.validateNumericInput(this.threadsCountTextField.getText().trim(), 1, coresNumber);
        if (threadsCount == null) {
            error += "Invalid threads count value! Please enter an integer number between 1 and " + coresNumber + " (inclusive)!";
        }

        if (!error.isBlank()) {
            this.showError(error);
            return null;
        }

        // noinspection DataFlowIssue
        return new InputData(this.sourceImage, dominantColorsCount, similarityThreshold, threadsCount);
    }

    private void startButtonClick(ActionEvent event) {
        InputData data = this.validateInputs();
        if (data == null) {
            return;
        }
        this.detector.start(data);
    }

    private void makeColorLabelsInvisible() {
        this.firstColorLabel.setVisible(false);
        this.secondColorLabel.setVisible(false);
        this.thirdColorLabel.setVisible(false);
        this.fourthColorLabel.setVisible(false);
    }

    private void resetProgressBar() {
        this.progressBar.setValue(0);
        this.progressBar.setMaximum(this.sourceImage.getHeight() * this.sourceImage.getWidth());
    }

    private void stopButtonClick(ActionEvent event) {
        this.detector.stop();
    }

    private void exitButtonClick(ActionEvent event) {
        this.applicationFrame.dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}