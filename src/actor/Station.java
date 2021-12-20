package actor;

import java.util.LinkedList;
import event.Event;
import event.ArrivalEvent;
import event.DepartEvent;

public class Station implements Runnable {
    private String name;
    private boolean exit;

    private LinkedList<Event> events;
    private Dashboard dashboard;

    public Station(String name) {
        this.name = name;
        this.exit = false;
        this.events = new LinkedList<Event>();
    }

    @Override 
    public void run() {
        synchronized (this) {
            while (!exit) {
                try {
                    this.wait();

                    while (!events.isEmpty()) {
                        Event event = events.poll();
                        if (event instanceof ArrivalEvent) {
                            ArrivalEvent e = (ArrivalEvent) event;
                            System.out.println(String.format("[ Station %-13s ] : Arrival of Bus %-3d at %d", name, e.getBus().getNumber(), e.getTimeStamp()));
                            synchronized (e.getBus()) {
                                e.getBus().notify();
                            }
                        } else if (event instanceof DepartEvent) {
                            DepartEvent e = (DepartEvent) event;
                            System.out.println(String.format("[ Station %-13s ] : Depart of  Bus %-3d at %d", name, e.getBus().getNumber(), e.getTimeStamp()));
                            synchronized (e.getBus()) {
                                e.getBus().notify();
                            }
                        }
                        synchronized (dashboard) {
                            dashboard.createTransaction(event);
                            dashboard.notify();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(String.format("[ Station %-13s ] : exit", name));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || this.getClass() != obj.getClass())
            return false;

        Station s = (Station)obj;

        return this.name.equals(s.name);
    }

    public synchronized void stop() {
        exit = true;
    }

    public String getName() {
        return this.name;
    }

    public synchronized void enqueueEvent(Event event) {
        events.add(event);
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
