package map;

import java.util.Random;

public class Tree extends Tile{
    public int typex, typey;
    public Tree(int size) {
        super("texture_packs/trees.png");
        this.friction = 1;
        height = width = size;
        Random rand = new Random();
        typex = rand.nextInt(4)*16;
        typey = rand.nextInt(2)*32;
        this.img = bimg.getSubimage(typex, typey, 16, 32);
    }

    public Tree(int size, int typex, int typey){
        super("texture_packs/trees.png");
        this.friction = 1;
        height = width = size;
        this.img = bimg.getSubimage(typex, typey + 16, 16, 16);
    }
}
