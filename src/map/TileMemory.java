package map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class TileMemory {
    private static ArrayList<Image> imgs = new ArrayList<>();
    private static ArrayList<String> paths = new ArrayList<>();

    public static void addTile(Image i, String path){
        imgs.add(i);
        paths.add(path);
    }

    public static Image getImg(String path) {
        if(paths.contains(path))return imgs.get(paths.indexOf(path));
        try {
            File f = new File(path);
            Image i = ImageIO.read(f);
            addTile(i, path);
            return i;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
