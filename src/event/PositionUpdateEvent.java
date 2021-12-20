package event;

import actor.Bus;
import actor.Point;

public class PositionUpdateEvent extends Event {
    private Bus bus;
    private Point position;
    private long timeStamp;
    public PositionUpdateEvent(Bus bus, Point position, long timeStamp) {
        this.bus = bus;
        this.position = position;
        this.timeStamp = timeStamp;
    }

    public Bus getBus() {
        return this.bus;
    }

    public Point getPosition() {
        return this.position;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }
}
