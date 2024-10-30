package entities;

import game_logic.Vec2d;
import map.Map;
import map.Tile;
import weapons.Bow;
import weapons.Weapon;

import java.util.HashSet;

public class Tank extends Entity{
    Player enemy;
    public Tank(Vec2d pos, int damage, long seed, int spawn_size, Player enemy) {
        super("enemy_tank", pos, new Vec2d(0,0), seed, spawn_size);
        this.enemy = enemy;
        cur_weapon = 0;
        weap_size = 1;
        weapons = new Weapon[weap_size];
        weapons[cur_weapon] = new Bow(this, damage, seed);
        weapons[cur_weapon].reloading_speed = 80;
        weapons[cur_weapon].damage = 2;
        skin = new Tile("texture_packs/player.png", size, size, new int[]{224, 248, 272});
    }

    @Override
    public void action(Map map, HashSet<Entity> entities){
        if(health > 0){
            if (alert_mode || isVisible(enemy, map))pathFinding(enemy);
            else automaticMove(map);
            if(alert_mode)weapons[cur_weapon].fire(enemy.getCenter(), enemy.size);
        }
        super.action(map, entities);
    }
}
