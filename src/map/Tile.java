package map;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Tile{
    public Image img, last_used;
    public BufferedImage bimg;
    public int height, width, direction;
    public String img_path;
    public double  animation_index = 0, friction;
    public int[] animation;

    public Tile(String img_path) {
        this.img_path = img_path;
        img = TileMemory.getImg(img_path);
        bimg = (BufferedImage) img;
    }

    public Tile(String img_path, int height, int width) {
        this.img_path = img_path;
        img = TileMemory.getImg(img_path);
        bimg = (BufferedImage) img;
        this.height = height;
        this.width = width;
    }

    public Tile(String img_path, int height, int width, int[] animation) {
        this.img_path = img_path;
        img = TileMemory.getImg(img_path);
        bimg = (BufferedImage) img;
        this.height = height;
        this.width = width;
        this.animation = animation.clone();
    }
}
