package game_logic;
import map.Map;
import map.Tile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameObj{
    public Random rand;
    public boolean alert_mode;
    public Vec2d pos, speed, acc, destination;
    public String type;
    public Set<GameObj> collisions = new HashSet<GameObj>();
    public ArrayList<String> ecollidable = new ArrayList<>();
    public Tile skin;
    public double cattrito, MAXACC, MINSPEED, elasticita;
    public int repulsion_radius, size;
    protected JSONObject prop;

    protected GameObj(String type, Vec2d pos, Vec2d speed, long seed){
        this.type = type;
        rand = new Random(seed);
        this.alert_mode = false;
        this.pos = pos.clone();
        this.speed = speed.clone();
        this.destination = getCenter().clone();
        this.acc = new Vec2d(0,0);
        getObjData();
    }

    private void getObjData(){
        try {
            FileInputStream fis = new FileInputStream("conf/"+type+".json");
            StringBuilder sb = new StringBuilder();
            int ch = fis.read();
            while (ch != -1) {
                sb.append((char)ch);
                ch = fis.read();
            }
            JSONParser parser = new JSONParser();
            prop = (JSONObject)parser.parse(sb.toString());
            cattrito = Double.parseDouble(prop.get("cattrito").toString());
            elasticita = -Double.parseDouble(prop.get("elasticita").toString());
            MAXACC = Double.parseDouble(prop.get("MAXACC").toString());
            MINSPEED = Double.parseDouble(prop.get("MINSPEED").toString());
            repulsion_radius = Integer.parseInt(prop.get("repulsion_radius").toString());
            size = Integer.parseInt(prop.get("size").toString());
            for(Object s: (JSONArray)prop.get("ecollidable"))ecollidable.add(s.toString());
        }catch (Exception e){System.out.println(e);}
    }

    public boolean checkCollisions(Map mappa){
        //Collision with walls
        boolean collided = false;
        Tile t1 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y + size)][mappa.bpadding + mappa.round_bg(pos.x)];
        Tile t2 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y + size)][mappa.bpadding + mappa.round_bg(pos.x + size)];
        if(this.pos.y + size >= mappa.height || (t1 != null && t1.img_path.contains("tree")) || (t2 != null && t2.img_path.contains("tree"))){
            if(this.speed.y > 0)this.speed.y *= elasticita;
            //if(this.acc.y > 0)this.acc.y *= elasticita;
            collided = true;
        }

        t1 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y )][mappa.bpadding +  mappa.round_bg(pos.x )];
        t2 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y )][mappa.bpadding + mappa.round_bg(pos.x + size )];
        if(this.pos.y <= 0 || (t1 != null && t1.img_path.contains("tree")) || (t2 != null && t2.img_path.contains("tree"))){
            if(this.speed.y < 0)this.speed.y *= elasticita;
            //if(this.acc.y < 0)this.acc.y *= elasticita;
            collided = true;
        }

        t1 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y )][mappa.bpadding + mappa.round_bg(pos.x )];
        t2 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y + size )][mappa.bpadding + mappa.round_bg(pos.x )];
        if (this.pos.x <= 0 || (t1 != null && t1.img_path.contains("tree")) || (t2 != null && t2.img_path.contains("tree"))){
            if(this.speed.x < 0)this.speed.x *= elasticita;
            //if(this.acc.x < 0)this.acc.x *= elasticita;
            collided = true;
        }

        t1 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y )][mappa.bpadding + mappa.round_bg(pos.x + size )];
        t2 = mappa.map_bg[mappa.bpadding + mappa.round_bg(pos.y + size ) ][mappa.bpadding + mappa.round_bg(pos.x + size )];
        if(this.pos.x + size >= mappa.width || (t1 != null && t1.img_path.contains("tree")) || (t2 != null && t2.img_path.contains("tree"))){
            if(this.speed.x > 0)this.speed.x *= elasticita;
            //if(this.acc.x > 0)this.acc.x *= elasticita;
            collided = true;
        }
        return collided;
    }

    public Image getSkin(){
        return skin.img;
    }

    public double sigmoid(double x){
        return 1 / (1 + Math.exp(-x));
    }

    public Vec2d getCenter(){
        return new Vec2d(this.pos.x + size/2,this.pos.y + size/2);
    }

    public boolean move(Map map){
        speed.add(acc.normalize(MAXACC));
        Vec2d friction = speed.clone();
        Tile t = map.map_bg[map.bpadding + map.round_bg(getCenter().y)][map.bpadding + map.round_bg(getCenter().x)];
        //if(t == null)return;
        try {
            speed.add(friction.multiply(-t.friction * cattrito));
        }catch (NullPointerException e){speed.add(friction.multiply(-cattrito));}
        if (speed.getModule() < MINSPEED) speed.multiply(0);
        Vec2d temp_speed = speed.clone();
        this.pos.add(temp_speed);
        boolean collided = checkCollisions(map);
        this.pos.add(temp_speed.multiply(-1));
        this.pos.add(speed);
        return collided;
    }
}
