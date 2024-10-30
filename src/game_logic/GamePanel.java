package game_logic;
import entities.*;
import map.*;
import weapons.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


//dungeon master
public class GamePanel extends JPanel implements ActionListener {
	public JSONObject prop;
	private int MAP_WIDTH, MAP_HEIGHT, MAP_PADDING, DELAY, num_enemy_archers, num_enemy_tanks, num_enemy_creepers, fadeCounter, num_items;
	private double player_health;
	private GameFrame.Callback callback;
	private long seed;
	Random rand;
	Timer timer;
	Player player;
	Map map;
	Camera camera;
	HashSet<Entity> visible_entities, entities;

	GamePanel(GameFrame.Callback cb) {
		this.callback = cb;
		this.setFocusable(true);
		player_health = 0;
		getData();
	}

	public void getData() {
		try {
			FileInputStream fis = new FileInputStream("conf/game_settings.json");
			StringBuilder sb = new StringBuilder();
			int ch = fis.read();
			while (ch != -1) {
				sb.append((char) ch);
				ch = fis.read();
			}
			JSONParser parser = new JSONParser();
			prop = (JSONObject) parser.parse(sb.toString());
			MAP_WIDTH = Integer.parseInt(prop.get("MAP_WIDTH").toString());
			MAP_HEIGHT = Integer.parseInt(prop.get("MAP_HEIGHT").toString());
			MAP_PADDING = Integer.parseInt(prop.get("MAP_PADDING").toString());
			DELAY = Integer.parseInt(prop.get("DELAY").toString());
			num_items = Integer.parseInt(prop.get("num_items").toString());
			num_enemy_archers = Integer.parseInt(prop.get("num_enemy_archers").toString());
			num_enemy_creepers = Integer.parseInt(prop.get("num_enemy_creepers").toString());
			num_enemy_tanks = Integer.parseInt(prop.get("num_enemy_tanks").toString());
			seed = Long.parseLong(prop.get("seed").toString());
			camera = new Camera();
			timer = new Timer(DELAY, this);
			fadeCounter = 0;
		} catch (Exception e) {
			System.out.println(e);
		}
		initializeGame();
	}

	public ArrayList<Vec2d> addSpawn(int nspawns, int size_spawn) {
		ArrayList<Vec2d> spawns = new ArrayList<>();
		Vec2d new_spawn;
		while (spawns.size() < nspawns) {
			new_spawn = new Vec2d(rand.nextInt(map.width - size_spawn * 2) + size_spawn, rand.nextInt(map.height - size_spawn * 2) + size_spawn);
			boolean collides = false;
			for (Vec2d s : spawns) if (s.distance(new_spawn) < size_spawn) collides = true;
			if (!collides) spawns.add(new_spawn);
		}
		//map.spiazza_spawn(spawns, size_spawn);
		return spawns;
	}

	public void placeItems() {
		for (int i = 0; i < num_items; i++) {
			Item item = null;
			switch (rand.nextInt(4)) {
				case 0:
					item = new Item(new Vec2d(0, 0), seed, new Bow(null, 1, seed));
					break;
				case 1:
					item = new Item(new Vec2d(0, 0), seed, new Turbo(null, 3, 1, seed));
					break;
				case 2:
					item = new Item(new Vec2d(0, 0), seed, new GrenadeLauncher(null, 2, seed));
					break;
				case 3:
					item = new Item(new Vec2d(0, 0), seed, new Turbo(null, 6, 1, seed));
					break;
			}
			do {
				item.pos = new Vec2d(rand.nextInt(map.width - item.size), rand.nextInt(map.height - item.size));
			} while (!map.insert(item));
		}
	}

	public void initializeGame() {
		System.out.println("initializing game");
		rand = new Random(seed);
		visible_entities = new HashSet<>();
		entities = new HashSet<>();
		map = new Map(MAP_WIDTH, MAP_HEIGHT, MAP_PADDING, seed, 50, Double.parseDouble(prop.get("lava_friction").toString()), Double.parseDouble(prop.get("water_friction").toString()));
		player = new Player(new Vec2d(0, 0), seed, 300);
		if(player_health > 0)player.health = player_health;
		do player.pos = new Vec2d(rand.nextInt(map.width - player.size), rand.nextInt(map.height - player.size));
		while (!map.insert(player));
		int spawn_size = Math.min(MAP_HEIGHT, MAP_WIDTH)/10 + 100;
		ArrayList<Vec2d> spawnpoints_archers = addSpawn(num_enemy_archers / 7 + 1, spawn_size);
		ArrayList<Vec2d> spawnpoints_tanks = addSpawn(num_enemy_tanks / 7 + 1, spawn_size);
		ArrayList<Vec2d> spawnpoints_creepers = addSpawn(num_enemy_creepers / 7 + 1, spawn_size);
		placeItems();
		Vec2d rand_deviation, start_pos;

		System.out.println("generating entities");
		for (int i = 0; i < num_enemy_tanks; i++) {
			start_pos = spawnpoints_tanks.get(rand.nextInt(spawnpoints_tanks.size()));
			Tank e = new Tank(start_pos, 1, seed + i, spawn_size, player);
			do {
				rand_deviation = new Vec2d(rand.nextInt(spawn_size * 2 - e.size) - spawn_size, rand.nextInt(spawn_size * 2 - e.size) - spawn_size);
				e.pos = rand_deviation.add(start_pos);
			} while (!map.insert(e));
			entities.add(e);
		}

		for (int i = 0; i < num_enemy_creepers; i++) {
			start_pos = spawnpoints_creepers.get(rand.nextInt(spawnpoints_creepers.size()));
			Creeper e = new Creeper(start_pos, 2, seed/2 + i, spawn_size, player);
			do {
				rand_deviation = new Vec2d(rand.nextInt(spawn_size * 2 - e.size) - spawn_size, rand.nextInt(spawn_size * 2 - e.size) - spawn_size);
				e.pos = rand_deviation.add(start_pos);
			} while (!map.insert(e));
			entities.add(e);
		}

		for (int i = 0; i < num_enemy_archers; i++) {
			start_pos = spawnpoints_archers.get(rand.nextInt(spawnpoints_archers.size()));
			Archer e = new Archer(start_pos, 3, seed/3 + i, spawn_size, player);
			do {
				rand_deviation = new Vec2d(rand.nextInt(spawn_size * 2 - e.size) - spawn_size, rand.nextInt(spawn_size * 2 - e.size) - spawn_size);
				e.pos = rand_deviation.add(start_pos);
			} while (!map.insert(e));
			entities.add(e);
		}
		System.out.println("fine generating entities");
		timer.start();
		System.out.println("initializing game");
	}

	public void updateEntitiesPos() {
		for (Entity e : visible_entities){
			if (e.weap_size > e.cur_weapon){
				for (int i = e.weapons[e.cur_weapon].bullets.size() - 1; i >= 0; i--) {
					GameObj b = e.weapons[e.cur_weapon].bullets.get(i);
					map.delete(b);
					if (!b.checkCollisions(map) && !b.move(map))map.insert(b);
				}
			}
			e.action(map, entities);
	}
		camera.follow(player.getCenter(), getHeight(), getWidth(), map);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for(int i = map.round_bg(camera.pos.y); i < map.round_bg(getHeight() + camera.pos.y + map.tile_size); i++) {
			for (int j = map.round_bg(camera.pos.x); j < map.round_bg(getWidth() + camera.pos.x + map.tile_size); j++) {
				Tile tile = map.map_bg[map.bpadding + i][map.bpadding + j];
				if(tile != null){
					if(tile.img_path.contains("tree"))g.drawImage(new Pavement(map.tile_size).img,j*map.tile_size - (int)camera.pos.x, i*map.tile_size - (int)camera.pos.y, tile.width, tile.height, this);
					g.drawImage(tile.img,j*map.tile_size - (int)camera.pos.x, i*map.tile_size - (int)camera.pos.y, tile.width, tile.height, this);
				}
			}
		}

		ArrayList<GameObj> drawn_obj = new ArrayList<>();
		drawn_obj.add(null);
        for(int i = (int)camera.pos.y; i < Math.min((int)camera.pos.y + getHeight(), map.height); i++){
        	for(int j = (int)camera.pos.x; j < Math.min((int)camera.pos.x + getWidth(), map.width); j++){
        		GameObj obj = map.map[i + map.padding][j + map.padding];
				if(!drawn_obj.contains(obj)){
					g.drawImage(obj.getSkin(),(int)obj.getCenter().x - obj.skin.width/2 - (int)camera.pos.x, (int)obj.getCenter().y - obj.skin.height/2 - (int)camera.pos.y, obj.skin.width, obj.skin.height, this);
					drawn_obj.add(obj);
				}
        	}
        }
		drawn_obj.remove(null);
		for(GameObj obj : drawn_obj) try{visible_entities.add((Entity)obj);}catch (Exception e){}
		drawHealthBar(g, player);
		//drawWeaponChargeBar(g, player);
		for(Entity e: visible_entities){
			if(e.health > 0){
				drawHealthBar(g, e);
				if(e.weap_size > e.cur_weapon)drawWeaponChargeBar(g, e);
				if(e.alert_mode)drawAlertSymbol(g, e);
			}
		}

		for (int i = 0; i < 10; i++) {
			g.setColor(Color.gray);
			if(i == player.cur_weapon && i < player.weap_size)g.setColor(Color.cyan);
			g.fillRect((getWidth()/2 - 300) + i * 60, getHeight()*4/5 + 30, 60, 60); // Assumendo slot di 32x32 pixel
			if(player.weap_size > i)g.drawImage(player.weapons[i].skin.img,(getWidth()/2 - 295) + i*60, getHeight()*4/5 + 35, player.weapons[i].skin.width, player.weapons[i].skin.height,  this);
		}
	}

	private void drawAlertSymbol(Graphics g, Entity e){
		g.setColor(Color.CYAN);
		g.fillOval((int)e.pos.x - (int)camera.pos.x + e.size, (int)e.pos.y - (int)camera.pos.y - 20, 10, 10);
	}

	private void drawHealthBar(Graphics g, Entity e) {
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		((Graphics2D)g).setComposite(alpha);
		g.setColor(Color.orange);
		g.fillRect( (int)e.pos.x - 20 - (int)camera.pos.x,(int)e.pos.y - 70 - (int)camera.pos.y, 10, 80);
		g.setColor(Color.green);
		g.fillRect( (int)e.pos.x - 20 - (int)camera.pos.x,((int)((e.max_health-e.health)*80/e.max_health)) + (int)e.pos.y - 70 - (int)camera.pos.y, 10, (int)(e.health*80/e.max_health));
	}

	private void drawWeaponChargeBar(Graphics g, Entity e){
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		((Graphics2D)g).setComposite(alpha);
		g.setColor(Color.white);
		g.fillRect( (int)e.pos.x - 10 - (int)camera.pos.x,(int)e.pos.y - 70 - (int)camera.pos.y, 10, 80);
		g.setColor(Color.cyan);
		g.fillRect( (int)e.pos.x - 10 - (int)camera.pos.x,
				((e.weapons[e.cur_weapon].reloading)*80/e.weapons[e.cur_weapon].reloading_speed) + (int)e.pos.y - 70 - (int)camera.pos.y,
				10, (e.weapons[e.cur_weapon].reloading_speed - e.weapons[e.cur_weapon].reloading)*80/e.weapons[e.cur_weapon].reloading_speed);

	}

	private void fadeScreen(Graphics g){
		// Calcola l'opacità in base al contatore
		float alpha = (float) fadeCounter / 1000;

		// Crea un oggetto Graphics2D per gestire l'opacità
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		fadeCounter = (fadeCounter + 1)%1000;

		if(fadeCounter > 150){
			timer.stop();
			player_health = 0;
			callback.callback(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(entities.size() == 0){//ucciso tutti
			seed ++;
			num_enemy_archers += 7;
			num_enemy_creepers += 2;
			num_enemy_tanks += 10;
			num_items += 3;
			MAP_WIDTH += 500;
			MAP_HEIGHT += 500;
			timer.stop();
			player_health = player.health;
			initializeGame();
		}
		if(player.health > 0){
			updateEntitiesPos();
			repaint();
		}
		else fadeScreen(getGraphics());
	}


}