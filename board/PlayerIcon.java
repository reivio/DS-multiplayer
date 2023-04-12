package board;

import java.awt.Image;
import javax.swing.ImageIcon;

public class PlayerIcon {
    public static ImageIcon getScaledImage(String path, int squareSize) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(squareSize, squareSize, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
