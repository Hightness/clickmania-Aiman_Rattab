package map;

public class Water extends Tile{
    public Water(int size, double f) {
        super("texture_packs/water.png");
        this.friction = f;
        height = width = size;
    }
}
