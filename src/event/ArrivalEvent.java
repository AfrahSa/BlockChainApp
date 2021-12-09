package event;

import actor.Bus;
import actor.Station;

public class ArrivalEvent extends Event {
    public ArrivalEvent(Bus bus, Station station, long timeStamp) {
        super(bus, station, timeStamp);
    }
}
