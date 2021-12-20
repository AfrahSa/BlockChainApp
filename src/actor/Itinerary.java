package actor;

import java.util.Vector;
import event.Event;
import event.ArrivalEvent;
import event.DepartEvent;

public class Itinerary {
  private Vector<Point>points;
  public Itinerary(Vector<Point> p){
      this.points=p;
  }
  public Vector<Point> getPoints(){
      return this.points;
  }
}
