package actor;

import java.util.Vector;
import event.Event;
import event.ArrivalEvent;
import event.DepartEvent;
import java.lang.Math;
import actor.Manager;
import ui.App;

public class Bus implements Runnable {
    private int number;
    private boolean exit;
    private Point point;
    private Vector<Itinerary> itineraries;
    private Vector<Station> stations;
    // Vector<Integer> durations;
    private int start;


    public Bus(int number, Vector<Station> stations, int start,Vector<Itinerary>I) {
        this.number = number;
        this.exit = false;
        this.stations = stations;
        this.start = start;
        this.itineraries=I;
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
                    for(int j=0; j<itineraries.get(i).getPoints().size()-1;j++){
                        Thread.sleep(3000*itineraries.get(i).getPoints().get(j+1).Distance(itineraries.get(i).getPoints().get(j)));
                        System.out.println("Bus "+this.number+" in point("+itineraries.get(i).getPoints().get(j).getX()+", "+itineraries.get(i).getPoints().get(j).getY()+")");
                    }


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
    public void setItineraries(Vector<Itinerary> I){ this.itineraries=I;}
}
