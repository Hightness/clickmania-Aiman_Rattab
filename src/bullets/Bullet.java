package bullets;
import game_logic.*;
import map.*;
import entities.*;

import weapons.Weapon;

public class Bullet extends GameObj {
    public Weapon owner;

	public Bullet(Vec2d pos, Vec2d speed, Weapon owner){
        super("bullet", pos, speed, owner.seed);
        this.owner = owner;
        skin = new Tile("texture_packs/bullet.png", size, size);
	}

    //controlla collisioni proiettile e lo elimina se collide
    public boolean checkCollisions(Map mappa){
        mappa.checkCollisions(this);

        if(!collisions.isEmpty() || super.checkCollisions(mappa) || speed.getModule() < 5){
            mappa.delete(this);
            owner.bullets.remove(this);

            for(GameObj e : collisions) {
                Entity entity_hit = (Entity) e;
                entity_hit.health -= owner.damage;

                //knockBack
                Vec2d speed_knockback = this.getCenter().getDirection(e.getCenter()).getVersor(0).multiply(owner.knockback_intensity);//calcolo la direzione del knockback da applicare all'entita'
                entity_hit.speed.add(speed_knockback); //aggiungo alla velocita' dell'entita' colpita il knockback vector
                entity_hit.alert_mode = true;
            }
            return true;
        }
        return false;
    }
}