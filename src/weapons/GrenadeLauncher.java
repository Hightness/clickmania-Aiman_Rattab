package weapons;
import game_logic.*;
import entities.*;
import bullets.Bomb;



public class GrenadeLauncher extends Weapon {
    public int MAX_BULLET_SPEED = 20;

    public GrenadeLauncher(Entity owner, int damage, long seed){
        super(owner, 50, damage, seed, "grenadelauncher", 1);
    }

    public void addBullet(Vec2d target){
        Vec2d bomb_dir = owner.getCenter().getDirection(target).getVersor(0);//prendo vetore direzione target
        Bomb bomb = new Bomb(owner.getCenter(), bomb_dir, this, target, seed);
        bomb.speed.multiply(MAX_BULLET_SPEED);
        if(this.owner.type == "player")bomb.ecollidable.remove("player");
        else{
            bomb.ecollidable.clear();
            bomb.ecollidable.add("player");
        }
        bullets.add(bomb);
    }

}
