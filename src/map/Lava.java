package map;

public class Lava extends Tile{

    public Lava(int size, double f) {
        super("texture_packs/lava.png");
        this.friction = f;
        height = width = size;
    }
}
