## Dominant Color Detector
App written in Java 21 and Java Swing, that detects dominant colors in image.

### Algorithm parameters
- source file path (path to image whose dominant colors will be detected; supported formats are .jpg and .png)
- dominant colors count (how many dominant colors should programm detect; at least 1, no more than 4)
- similarity threshold (points how large may be difference between each of RGB values to consider two colors similar)
- threads count (on how many threads should algorithm run; limited by workstation resources)
