package event;

import actor.Bus;
import actor.Station;

public class DepartEvent extends Event {
    private Bus bus;
    private Station station;
    private long timeStamp;
    private int expectedArrivalTime;
    public DepartEvent(Bus bus, Station station, long timeStamp) {
        this.bus = bus;
        this.station = station;
        this.timeStamp = timeStamp;
        this.expectedArrivalTime=bus.expectedArrivalTime(station);
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

    public int getExpectedArrivalTime(){ return this.expectedArrivalTime;}
}
