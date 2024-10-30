package map;

public class Pavement extends Tile{

    public Pavement(int size) {
        super("texture_packs/texture.png");
        this.friction = 1;
        height = width = size;
    }
}
