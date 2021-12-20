package actor;


public class Point {
    private int x;
    private int y;
    public Point(int x,int y){
        this.x=x;
        this.y=y;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void setX(int x){
         this.x=x;
    }
    public void setY(int y){
         this.y=y;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || this.getClass() != obj.getClass())
            return false;

        Point p = (Point) obj;

        return (this.x == p.x && this.y==p.y);
    }
    public int Distance(Point p){
        return (int)Math.sqrt(Math.pow(this.getX()-p.getX(),2)+Math.pow(this.getY()-p.getY(),2));

    }
}
