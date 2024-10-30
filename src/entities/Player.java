package entities;
import weapons.*;
import game_logic.*;
import map.*;
import java.util.HashSet;

public class Player extends Entity {
	public int P_up, P_down, P_left, P_right;
	public Player(Vec2d pos, long seed, int spawn_size){
        super("player", pos, new Vec2d(0, 0), seed, spawn_size);

		P_up = P_down = P_left = P_right = 0;
		weapons = new Weapon[10];
		//weapon = new GrenadeLauncher(this, 1, seed);
		cur_weapon = 0;
		weap_size = 0;
		skin = new Tile("texture_packs/player.png", size, size, new int[]{32, 56, 80});
	}

	public void action(Map map, HashSet<Entity> entities){
		this.acc.x = P_right - P_left;
		this.acc.y = P_down - P_up;
		this.vision_direction = this.acc.clone();
		super.action(map, entities);
		Tile t = map.map_bg[map.bpadding + map.round_bg(getCenter().y)][map.bpadding + map.round_bg(getCenter().x)];
		if(t!=null && t.img_path.contains("lava"))health--;
	}
}