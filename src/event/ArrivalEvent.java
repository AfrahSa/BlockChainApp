package event;

import actor.Bus;
import actor.Station;

public class ArrivalEvent extends Event {
    private Bus bus;
    private Station station;
    private long timeStamp;
    public ArrivalEvent(Bus bus, Station station, long timeStamp) {
        this.bus = bus;
        this.station = station;
        this.timeStamp = timeStamp;
    }

    public Bus getBus() {
        return this.bus;
    }

    public Station getStation() {
        return this.station;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

}
