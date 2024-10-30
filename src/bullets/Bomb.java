package bullets;
import weapons.*;
import game_logic.*;
import map.*;
import entities.*;
import java.awt.*;

public class Bomb extends GameObj {
    public Weapon owner;
    public int detonation_animation;
    public Vec2d destination;

    public Bomb(Vec2d pos, Vec2d speed, Weapon owner, Vec2d destination, long seed){
        super("bomb", pos, speed, seed);
        this.destination = destination;//la bomba, al contrario dei proiettili, non ha una direzione da seguire ma un punto in cui arrivare
        if(owner != null) this.owner = owner;
        skin = new Tile("texture_packs/bomb.png", size, size);
        detonation_animation = 20;
    }

    //restituisce skin della bomba se non e' ancora detonata, altrimenti avvia animazione della skin detonazione
    public Image getSkin(){
        return type != "detonated" ? skin.img : skin.bimg.getSubimage(skin.animation[(2*(20 - detonation_animation + 1)/20)], 160, 32, 32);
    }

    //innesca esplosione se bomba colpisce qualcosa o arriva al traguardo
    public boolean checkCollisions(Map mappa){//se ci sono collisioni restituisce true
        if(this.type == "detonated"){
            detonation_animation--;
            if (this.detonation_animation == 0){
                mappa.delete(this);
                owner.bullets.remove(this);
                return true;
            }
            for(GameObj e : collisions) {
                Entity entity_hit = (Entity)e;
                //knockback
                Vec2d speed_knockback = this.getCenter().getDirection(e.getCenter()).getVersor(0).multiply(owner.knockback_intensity*detonation_animation);//calcolo la direzione del knockback da applicare all'entita'
                entity_hit.speed.add(speed_knockback); //aggiungo alla velocita' dell'entita' colpita il knockback vector
            }
        }else{
            int temp = this.repulsion_radius;
            this.repulsion_radius = 0;
            boolean collided = super.checkCollisions(mappa);
            this.repulsion_radius = temp;

            if (collided || this.getCenter().getDirection(destination).getModule() < size|| speed.getModule() < 1){//se sono giunto a destinazione
                mappa.checkCollisions(this);
                for(GameObj e : collisions) {
                    Entity entity_hit = (Entity)e;
                    entity_hit.alert_mode = true;
                    entity_hit.health -= owner.damage;
                    //knockback
                    Vec2d speed_knockback = this.getCenter().getDirection(e.getCenter()).getVersor(0).multiply(owner.knockback_intensity);//calcolo la direzione del knockback da applicare all'entita'
                    entity_hit.speed.add(speed_knockback); //aggiungo alla velocita' dell'entita' colpita il knockback vector
                }
                type = "detonated";
                skin = new Tile("texture_packs/detonation.png");
                skin.animation = new int[]{224, 256, 288, 320};
                skin.height = skin.width = repulsion_radius;
                skin.animation_index = 0;
            }
        }
        return false;
    }
}