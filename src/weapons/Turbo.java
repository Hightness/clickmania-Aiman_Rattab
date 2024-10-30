package weapons;
import game_logic.*;
import bullets.*;
import entities.*;

public class Turbo extends Weapon {
    private int ndir;

    public Turbo(Entity owner, int ndir, int damage, long seed){
        super(owner, 10*ndir, damage, seed, "turbo" + ndir, 10);
        this.ndir = ndir;
    }

    public void addBullet(Vec2d target){
        Vec2d bullet_speed = owner.getCenter().getDirection(target).getVersor(0);
        double step = (Math.PI/(2*ndir));

        for (int i = 0; i <= ndir/2; i++) {
            bullet_speed.rotate(step*i);
            bullets.add(craftBullet(bullet_speed.clone()));

            bullet_speed.rotate(-i*2*step);
            if(i > 0)bullets.add(craftBullet(bullet_speed.clone()));
            bullet_speed.rotate(step*i);
        }
    }

    private Bullet craftBullet(Vec2d bullet_speed){
        Bullet bullet = new Bullet(owner.getCenter(), bullet_speed, this);
        bullet.speed.multiply(bullet.MAXACC);
        bullet.skin.height = 27;
        bullet.skin.width = 27;
        if(this.owner.type == "player")bullet.ecollidable.remove("player");
        else{
            bullet.ecollidable.clear();
            bullet.ecollidable.add("player");
        }
        return bullet;
    }

}
