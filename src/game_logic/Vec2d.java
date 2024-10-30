package game_logic;

public class Vec2d {
    public double x;
    public double y;

	public Vec2d(double x, double y){
        this.x = x;
        this.y = y;
	}

    public double distance(Vec2d vector){// trova distanza x  y 
        return Math.sqrt((vector.x-this.x)*(vector.x-this.x) + (vector.y-this.y)*(vector.y-this.y));
    }

    public Vec2d clone(){
        return new Vec2d(this.x, this.y);
    }

    public Vec2d multiply(double a){
        this.x *= a; 
        this.y *= a;
        return this;
    }
    
    public Vec2d add(Vec2d vector){
        this.x += vector.x;
        this.y += vector.y;
        return this;
    }

    public Vec2d getDirection(Vec2d target){//vettore freccia che punta verso target
        return new Vec2d(target.x - this.x, target.y - this.y);
    }

    public double getModule(){
        return Math.sqrt(this.x*this.x + this.y*this.y);
    }

    public Vec2d getVersor(double MINSPEED){
        double modulo = getModule();
        if (modulo < MINSPEED)
            return new Vec2d(0, 0);
        return new Vec2d(this.x/modulo, this.y/modulo);
    }

    public Vec2d normalize(double MAXSPEED){
        double l = getModule();
        if (l > MAXSPEED){
            double n = MAXSPEED/l;
            this.x*=n;
            this.y*=n;
        }
        return this;
    }

    public double getAngle(Vec2d target){
        return Math.atan2(target.y*this.x - target.x*this.y, target.x*this.x + target.y*this.y);
    }

    public Vec2d rotate(double angle){
        double temp = this.x;
        this.x = Math.cos(angle)*this.x - Math.sin(angle)*this.y;
        this.y = Math.sin(angle)*temp + Math.cos(angle)*this.y;
        return this;
    }
}
