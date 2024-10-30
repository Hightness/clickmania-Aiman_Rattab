package weapons;
import game_logic.*;
import bullets.*;
import entities.*;

public class Bow extends Weapon {
    public int MAX_BULLET_SPEED = 20;

    public Bow(Entity owner, int damage, long seed){
        super(owner, 5, damage, seed, "bow", 5);
    }

    public void addBullet(Vec2d target){
        Vec2d bullet_speed = owner.getCenter().getDirection(target).getVersor(0).multiply(MAX_BULLET_SPEED);
        Bullet bullet = new Bullet(owner.getCenter(), bullet_speed, this);
        if(this.owner.type == "player"){
            bullet.ecollidable.remove("player");
            bullet.size = 27;
            bullet.skin.height = 27;
            bullet.skin.width = 27;
        }else{
            bullet.ecollidable.clear();
            bullet.ecollidable.add("player");
        }

        bullets.add(bullet);
    }

}
