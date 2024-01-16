public class InputValidator {
    public static String validateSourceImageFilePath(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".png")) {
            return filePath;
        } else {
            return null;
        }
    }

    public static Integer validateNumericInput(String text, int min, int max) {
        if (text.isBlank() || InputValidator.isStringNotInteger(text)) {
            return null;
        }
        int value = Integer.parseInt(text);
        if (InputValidator.isIntegerNotInRange(value, min, max)) {
            return null;
        }
        return value;
    }

    private static boolean isStringNotInteger(String text) {
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (!InputValidator.isCharDigit(character)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCharDigit(char character) {
        return !InputValidator.isIntegerNotInRange(character, '0', '9');
    }

    private static boolean isIntegerNotInRange(int value, int min, int max) {
        return value < min || value > max;
    }
}