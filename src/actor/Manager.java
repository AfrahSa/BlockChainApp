package actor;

import java.util.Vector;

public class Manager implements Runnable {
    public static long startTime;
    public void run() {
        try {
            // Creation of stations
            Station s1 = new Station("Bajarah");
            Station s2 = new Station("Bab Ezzouar");
            Station s3 = new Station("Harrach");
            Station s4 = new Station("Dar El Beida");
            Station s5 = new Station("Hammedi");

            Vector<Station> stations = new Vector<Station>();
            stations.add(s1);
            stations.add(s2);
            stations.add(s3);
            stations.add(s4);
            stations.add(s5);

            Vector<Integer> durations = new Vector<Integer>();
            durations.add(15000);
            durations.add(10000);
            durations.add(20000);
            durations.add(5000);
            durations.add(25000);

            // Creation of buses
            Bus b1 = new Bus(1, stations, 0, durations);
            Bus b2 = new Bus(2, stations, 2, durations);
            Bus b3 = new Bus(3, stations, 3, durations);

            Vector<Bus> buses = new Vector<Bus>();
            buses.add(b1);
            buses.add(b2);
            buses.add(b3);

            Dashboard dashboard = new Dashboard(stations, buses);

            for (Station s : stations)
                s.setDashboard(dashboard);


            // Creation of threads
            Vector<Thread> threadsStations = new Vector<Thread>();
            for (Station s : stations)
                threadsStations.add(new Thread(s));

            Vector<Thread> threadsBuses = new Vector<Thread>();
            for (Bus b : buses)
                threadsBuses.add(new Thread(b));

            Thread threadDashboard = new Thread(dashboard);

            startTime = System.currentTimeMillis();

            // Launching threads
            threadDashboard.start();
            for (Thread t : threadsStations)
                t.start();

            for (Thread t : threadsBuses)
                t.start();

            synchronized (this) {
                this.wait();
            }

            // Stopping threads 
            for (Station s : stations)
                synchronized (s) {
                    s.stop();
                    s.notify();
                }

            for (Bus b : buses)
                synchronized (b) {
                    b.stop();
                    b.notify();
                }
            synchronized (dashboard) {
                dashboard.stop();
                dashboard.notify();
            }

            // Joining threads
            for (Thread t : threadsStations)
                t.join();

            for (Thread t : threadsBuses)
                t.join();

            threadDashboard.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[ Manager               ] : exit");
    }
}
