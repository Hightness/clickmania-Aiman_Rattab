package map;
import game_logic.*;

public class Camera{
    public Vec2d pos;

    public Camera(){
        pos = new Vec2d(0,0);
    }

    public void follow(Vec2d target_center, double SCREEN_HEIGHT, double SCREEN_WIDTH, Map map){
        pos.x = target_center.x - SCREEN_WIDTH/2;//aggiorno posizione in modo da seguire il target
        pos.y = target_center.y - SCREEN_HEIGHT/2;//aggiorno posizione in modo da seguire il target
        if(pos.x < 0)pos.x = 0;
        if(pos.y < 0)pos.y = 0;
        if(pos.x + SCREEN_WIDTH > map.width)pos.x = map.width - SCREEN_WIDTH;
        if(pos.y + SCREEN_HEIGHT > map.height)pos.y = map.height - SCREEN_HEIGHT;
    }
}

