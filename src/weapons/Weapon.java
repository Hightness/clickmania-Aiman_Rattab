package weapons;
import game_logic.*;
import bullets.*;
import entities.*;
import map.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Weapon{
    Random rand;
    public Entity owner;
    public int reloading, reloading_speed, damage, knockback_intensity;
    public String type;
    public long seed;
    public ArrayList<GameObj> bullets;
    public Tile skin;


    Weapon(Entity owner, int reloading_speed, int damage, long seed, String type, int knockback_intensity){
        this.knockback_intensity = knockback_intensity;
        this.type = type;
        this.damage = damage;
        this.reloading_speed = reloading_speed;
        this.seed = seed;
        this.rand = new Random(seed);
        reloading = rand.nextInt(reloading_speed);
        bullets = new ArrayList<>();
        this.owner = owner;
        skin = new Tile("texture_packs/"+ type + ".png");
        skin.height = 50;
        skin.width = 50;
        //skin.animation = new int[]{128, 152, 176};
    }

    public boolean fire(Vec2d bullet_target, int size){

        if(reloading == 0 && owner.getCenter().distance(bullet_target) < owner.attack_area + owner.size/2 + size){
            addBullet(bullet_target);
            reloading++;
            return true;
        }
        return false;
    }

    public void addBullet(Vec2d target){
        Vec2d bullet_dir = owner.getCenter().getDirection(target).getVersor(0);
        Bullet bullet = new Bullet(owner.getCenter(), bullet_dir, this);
        bullet.speed.multiply(bullet.MAXACC);
        bullets.add(bullet);
    }
}
