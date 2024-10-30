package map;
import entities.Entity;
import game_logic.*;
import entities.Player;
import java.util.*;


public class Map{
    public int padding, bpadding;
	public GameObj[][] map;
    public Tile[][] map_bg;
    public int height, width, tile_size, seed;
    private  double lava_friction, water_friction;
    Random rand;

    public Map(int width, int height, int padding, long seed, int tile_size, double lv, double wf){
        this.tile_size = tile_size;
        rand = new Random(seed);
        this.lava_friction = lv;
        this.water_friction = wf;
        this.padding = padding;
        this.bpadding = 50;
        this.height = height;
        this.width = width;

        map = new GameObj[height + padding*2][width + padding*2];
        map_bg = new Tile[height/tile_size + bpadding*2][width/tile_size + bpadding*2];
        mapGeneration();
    }

    private void mapGeneration(){
        System.out.println("inizio mapGeneration");
        Tile[] tiles = new Tile[3];
        tiles[0] = new Water(tile_size, water_friction);
        tiles[1] = new Lava(tile_size, lava_friction);
        tiles[2] = new Pavement(tile_size);
        int max_depth = rand.nextInt(Math.min(height, width)/200)+Math.min(height, width)/200;

        for(int i = bpadding; i < map_bg.length - bpadding; i ++)
            for(int j = bpadding; j < map_bg[0].length - bpadding; j ++) {
                int random_num = rand.nextInt(Math.min(height, width)/100 + 10);
                map_bg[i][j] = tiles[random_num > 1 ? 2 : random_num];
                if(random_num <= 1)expand(new Vec2d(j-2, i-2), new Vec2d(j, i), max_depth, tiles[random_num]);
            }

        int num_alberi = Math.max(width, height)/20 - 40;
        for(int i = 0; i < num_alberi; i++) {
            int rx = rand.nextInt(height / tile_size - 2) + bpadding;
            int ry = rand.nextInt(width / tile_size - 1) + bpadding;
            map_bg[rx][ry] = new Tree(tile_size);
            map_bg[rx][ry].img = map_bg[rx][ry].bimg.getSubimage(0, 0, 16, 16);
            map_bg[rx + 1][ry] = new Tree(tile_size, ((Tree)map_bg[rx][ry]).typex, ((Tree)map_bg[rx][ry]).typey);
        }

        System.out.println("fine mapGeneration");
    }
    private void expand(Vec2d pp, Vec2d cp, int max_depth, Tile t){
        Vec2d rp;

        do rp = new Vec2d(rand.nextInt(3) - 1, rand.nextInt(3) - 1);
        while(rp.getModule() == 0 || (rp.x == pp.x && rp.y == pp.y));

        cp.add(rp);
        if(0 == max_depth || cp.y < bpadding || cp.x < bpadding || cp.y > map_bg.length - bpadding || cp.x > map_bg[0].length - bpadding)return;
        map_bg[(int)cp.y][(int)cp.x] = t;
        Vec2d cp_c = cp.clone();
        expand(cp.add(rp.multiply(-1)).clone(), cp_c, max_depth - 1, t);
    }

    public int round_bg(double n){
        return (int)(n - n%tile_size)/tile_size;
    }

    public boolean insert(GameObj entity){

        Tile tlc = map_bg[bpadding + round_bg(entity.pos.y)][bpadding + round_bg(entity.pos.x)];
        Tile trc = map_bg[bpadding + round_bg(entity.pos.y)][bpadding + round_bg(entity.pos.x + entity.size)];
        Tile blc = map_bg[bpadding + round_bg(entity.pos.y + entity.size)][bpadding + round_bg(entity.pos.x)];
        Tile brc = map_bg[bpadding + round_bg(entity.pos.y + entity.size)][bpadding + round_bg(entity.pos.x + entity.size)];
        if((tlc != null && tlc.img_path.contains("tree")) || (trc != null && trc.img_path.contains("tree")) || (blc != null && blc.img_path.contains("tree")) || (brc!=null && brc.img_path.contains("tree")))return false;
        map[padding + (int)entity.pos.y][padding + (int)entity.pos.x] = entity;
        map[padding + (int)entity.pos.y][padding + (int)entity.pos.x + entity.size] = entity;
        map[padding + (int)entity.pos.y + entity.size][padding + (int)entity.pos.x] = entity;
        map[padding + (int)entity.pos.y + entity.size][padding + (int)entity.pos.x + entity.size] = entity;
        return true;
    }

    public void delete(GameObj entity){

        map[padding + (int)entity.pos.y][padding + (int)entity.pos.x] = null;
        map[padding + (int)entity.pos.y][padding + (int)entity.pos.x + entity.size] = null;
        map[padding + (int)entity.pos.y + entity.size][padding + (int)entity.pos.x] = null;
        map[padding + (int)entity.pos.y + entity.size][padding + (int)entity.pos.x + entity.size] = null;

    }

    public void checkCollisions(GameObj entity){
        Vec2d top_left_corner = new Vec2d(padding - entity.repulsion_radius + (int)entity.pos.x,padding - entity.repulsion_radius + (int)entity.pos.y);
        Vec2d bottom_right_corner = new Vec2d(padding + entity.repulsion_radius + (int)entity.pos.x + entity.size,padding + entity.repulsion_radius + (int)entity.pos.y + entity.size);
        for (int i = Math.max((int)top_left_corner.y, 0); i <= Math.min(bottom_right_corner.y, height); i ++){
            for (int j = Math.max((int)top_left_corner.x, 0); j <= Math.min(bottom_right_corner.x, width); j ++){
                GameObj ecollided = map[i][j];
                if (ecollided != null){

                    if(ecollided.type == "item" && entity.type == "player" && ((Item) ecollided).getWeapon() != null) {
                        ((Item) ecollided).getWeapon().owner = (Entity)entity;
                        ((Player) entity).weapons[((Player) entity).weap_size % 10] = (((Item) ecollided).getWeapon());
                        ((Player) entity).weap_size ++;
                        ((Item) ecollided).usage -- ;
                        if(((Item) ecollided).usage <= 0) delete(ecollided);
                    }

                    if(entity.ecollidable.contains(ecollided.type))entity.collisions.add(ecollided);
                    if(ecollided.ecollidable.contains(entity.type))ecollided.collisions.add(entity);
                }
            }
        }
    }
}