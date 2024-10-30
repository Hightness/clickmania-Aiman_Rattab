package game_logic;
import weapons.Weapon;

public class Item extends GameObj {

    private Weapon weapon;
    public int usage;
    public Item(Vec2d spawn_point,long seed, Weapon weapon) {
        super("item", spawn_point, new Vec2d(0,0), seed);

        usage = 1;
        this.weapon = weapon;
        skin = weapon.skin;
        //skin = new Tile("texture_packs/"+ weapon.type + ".png");
        //skin.height = size;
        //skin.width = size;
        //skin.animation = new int[]{128, 152, 176};
    }

    public Weapon getWeapon() {
        return usage <= 0 ? null : weapon;
    }

}
