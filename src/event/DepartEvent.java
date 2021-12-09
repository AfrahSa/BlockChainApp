package event;

import actor.Bus;
import actor.Station;

public class DepartEvent extends Event {
    public DepartEvent(Bus bus, Station station, long timeStamp) {
        super(bus, station, timeStamp);
    }
}
