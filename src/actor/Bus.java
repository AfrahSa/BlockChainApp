package actor;

import java.util.Vector;
import event.Event;
import event.ArrivalEvent;
import event.DepartEvent;
import actor.Manager;
import ui.App;

public class Bus implements Runnable {
    private int number;
    private boolean exit;

    private Vector<Station> stations;
    private Vector<Integer> durations;
    private int start;

    public Bus(int number, Vector<Station> stations, int start, Vector<Integer>durations) {
        this.number = number;
        this.exit = false;
        this.stations = stations;
        this.durations = durations;
        this.start = start;
    }

    @Override
    public void run() {
        synchronized (this) {
            int i = start;
            while (!exit) {
                try {
                    synchronized (stations.get(i)) {
                        stations.get(i).enqueueEvent((Event)(new ArrivalEvent(this, stations.get(i), System.currentTimeMillis() - Manager.startTime)));
                        stations.get(i).notify();
                    }

                    this.wait();
                    if (exit) break;
                    //System.out.println(String.format("[ Bus %-17d ] : waiting...", number));
                    Thread.sleep(3000); 

                    synchronized (stations.get(i)) {
                        stations.get(i).enqueueEvent((Event)(new DepartEvent(this, stations.get(i), System.currentTimeMillis() - Manager.startTime)));
                        stations.get(i).notify();
                    }

                    this.wait();
                    //System.out.println(String.format("[ Bus %-17d ] : transit...", number));
                    Thread.sleep(durations.get(i));

                    i = (i + 1) % stations.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(String.format("[ Bus %-17d ] : exit", number));
        }
    }

    public synchronized void stop() {
        exit = true;
    }

    public void setStations(Vector<Station> stations) {
        this.stations = stations;
    }

    public int getNumber() {
        return this.number;
    }
}
