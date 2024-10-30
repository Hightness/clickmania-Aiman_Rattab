package entities;
import game_logic.*;
import java.awt.*;
import java.util.*;
import map.Map;
import weapons.Weapon;

public class Entity extends GameObj {
	public boolean counter_clockwise, collided;
	public Vec2d spawn_point, vision_direction;
	public int attack_area, vision_radius,awareness_radius, death_animation, spawn_size, cur_weapon, weap_size;
	public double cone_of_vision, health, max_health;
	public Weapon[] weapons;

	Entity(String type, Vec2d spawn_point, Vec2d speed, long seed, int spawn_size){
		super(type, spawn_point, speed, seed);
		this.spawn_size = spawn_size;
		counter_clockwise = rand.nextBoolean();
		this.spawn_point = spawn_point.clone();
		vision_direction = speed.clone();
		getObjData();
    }

	public Image getSkin(){
		double w = skin.img.getWidth(null);

		if(speed.getModule() < MINSPEED*2){
			skin.last_used = skin.bimg.getSubimage((int)(w/2), skin.animation[1], 16, 16);
			skin.animation_index = 2;
			return skin.last_used;
		}
		double theta = new Vec2d(0, -1).getAngle(vision_direction);
		if(theta < 0)theta+=Math.PI*2;
		int direction = (int)(theta*(4/Math.PI))%8;
		skin.direction = direction;
		skin.last_used = skin.bimg.getSubimage((int)(w*skin.direction/8), skin.animation[(int)skin.animation_index], 16, 16);
		skin.animation_index = (skin.animation_index + 0.005*speed.getModule()) % skin.animation.length;
		return skin.last_used;
	}

	public void action(Map map, HashSet<Entity> entities){
		if(cur_weapon<weap_size)weapons[cur_weapon].reloading = weapons[cur_weapon].reloading == 0 ? 0 : (weapons[cur_weapon].reloading + 1) % weapons[cur_weapon].reloading_speed;
		if(health <= 0)kill(entities, map);
		else do{
			map.delete(this);
			setRepulsions(map);
			collided = move(map);
			counter_clockwise = (!counter_clockwise && collided) || (counter_clockwise && !collided);
			collisions.clear();
		} while(!map.insert(this));
	}

	private void getObjData(){
		health = Double.parseDouble(prop.get("health").toString());
		max_health = health;
		attack_area = Integer.parseInt(prop.get("attack_area").toString());
		vision_radius = Integer.parseInt(prop.get("vision_radius").toString());
		awareness_radius = Integer.parseInt(prop.get("awareness_radius").toString());
		death_animation = Integer.parseInt(prop.get("death_animation").toString());
		cone_of_vision = Double.parseDouble(prop.get("cone_of_vision").toString());
	}

	public void kill(HashSet<Entity> entities, Map map){//chiamata quando entita' e' programmata per morire
		type = "dead";
		death_animation--;
		acc = new Vec2d(0, 0);
		if(death_animation == 159){
			alert_mode = false;
			skin.animation = new int[]{0, 0, 0};
		}else if(death_animation == 0){
			map.delete(this);
			entities.remove(this);
		}
	}

	public boolean isVisible(GameObj p, Map map){//returns true if p is visible to this entity
		Vec2d player_dir = this.getCenter().getDirection(p.getCenter());
		//for(int i = (int)getCenter().y; i < vision_direction.y*vision_radius + 1; i++)
			//for(int j = (int)getCenter().x; j < vision_direction.x*vision_radius + 1; j++){
				//Tile t = map.map_bg[map.bpadding + map.round_bg(i)][map.bpadding + map.round_bg(j)];
				//if(t!=null && t.img_path.contains("wall"))return false;
			//}
		return player_dir.getModule() <= vision_radius && Math.abs(vision_direction.getAngle(player_dir)) <= cone_of_vision/2;
	}

	public void automaticMove(Map mappa){
		vision_direction = acc.clone();
		Vec2d new_direction_versor = getCenter().getDirection(destination);
		if(new_direction_versor.getModule() < size || super.checkCollisions(mappa) || collided) {
			do destination = spawn_point.clone().add(new Vec2d(rand.nextInt(spawn_size*2) - spawn_size, rand.nextInt(spawn_size*2) - spawn_size));
			while(destination.y >= mappa.height || destination.y <= 0 || destination.x <= 0 || destination.x >= mappa.width);
		}
		new_direction_versor = getCenter().getDirection(destination).getVersor(0).multiply(MAXACC/2);
		this.acc.add(new_direction_versor);
	}

	public void pathFinding(Player target){
		double	distanza_target = getCenter().distance(target.getCenter()) - size/2 - target.size/2;
		this.alert_mode = distanza_target <= awareness_radius;

		double player_direction_weight = 2 * (sigmoid(Math.pow(distanza_target / (attack_area - size*4), 2)) - 0.5);//peso del vettore direzione player
		double pd_rotated_weight = 1 - player_direction_weight;//peso del vettore rotazione attorno al player
		double player_opposition_weight = 2 * (sigmoid(Math.pow(attack_area / (distanza_target + size), 2)) - 0.5);//eso del vettore opposizione

		destination = getCenter().clone();
		vision_direction = this.getCenter().getDirection(target.getCenter()).getVersor(0);//prendo direzione del player

		//prendo direzione ortogonale a direzione player (si ha un effetto di rotazione attorno al player)
		Vec2d pd_rotated_versor = vision_direction.clone().rotate(counter_clockwise ? Math.PI/2 : -Math.PI/2).multiply(pd_rotated_weight * MAXACC);
		Vec2d player_opposition_vector = target.getCenter().getDirection(getCenter()).getVersor(0).multiply(player_opposition_weight * MAXACC);//vettore opposizione al player in caso si e' troppo vicini

		//aggiungo tutti i vettori pesati al vettore accelerazione
		Vec2d target_reached = pd_rotated_versor.add(player_opposition_vector).multiply((1 - player_direction_weight) * MAXACC);
		this.acc.add(vision_direction.multiply(player_direction_weight * MAXACC)).add(target_reached);
	}

	public void setRepulsions(Map mappa){
		Vec2d new_dir = new Vec2d(0, 0);

		//Collision with entities
		mappa.checkCollisions(this);
		for(GameObj entity : this.collisions){
			double distanza_nemici = getCenter().distance(entity.getCenter()) - size/2 - entity.size/2;
			double repulsion_vector_weight = 2*(sigmoid(Math.abs(repulsion_radius/distanza_nemici)) - 0.5);
			Vec2d repulsion_vector = entity.getCenter().getDirection(getCenter()).getVersor(0);
			double cosine_vel = (1 + Math.cos(repulsion_vector.getAngle(entity.speed)))/2;
			new_dir.add(repulsion_vector.multiply((repulsion_vector_weight+cosine_vel)*MAXACC));
			alert_mode = entity.type == "player" ? true : alert_mode;
		}
		this.acc.add(new_dir).normalize(MAXACC);
	}
}