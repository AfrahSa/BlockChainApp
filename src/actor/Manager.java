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

            //Vector<Integer> durations = new Vector<Integer>();
            //durations.add(60000);
            //durations.add(40000);
            //durations.add(30000);
            //durations.add(4000);
            //durations.add(55000);


            // Creation of itinerary
            Vector<Point> V5= new Vector<Point>();
            Point p16 = new Point(3,6);
            Point p17 = new Point(2,6);
            Point p18 = new Point(2,1);
            Point p19 = new Point(0,1);
            Point p20 = new Point(0,0);
            V5.add(p16);
            V5.add(p17);
            V5.add(p18);
            V5.add(p19);
            V5.add(p20);
            Itinerary i5 = new Itinerary(V5);

            Vector<Point> V1= new Vector<Point>();
            Point p1 = new Point(4,0);
            Point p2 = new Point(4,2);
            Point p3 = new Point(6,2);
            V1.add(p1);
            V1.add(p2);
            V1.add(p3);
            Itinerary i1 = new Itinerary(V1);

            Vector<Point> V2= new Vector<Point>();
            Point p4 = new Point(6,2);
            Point p5 = new Point(7,2);
            Point p6 = new Point(7,3);
            Point p7 = new Point(9,3);
            Point p8 = new Point(9,5);
            V2.add(p4);
            V2.add(p5);
            V2.add(p6);
            V2.add(p7);
            V2.add(p8);
            Itinerary i2 = new Itinerary(V2);

            Vector<Point> V3= new Vector<Point>();
            Point p9 = new Point(9,5);
            Point p10 = new Point(9,7);
            Point p11 = new Point(7,7);

            V3.add(p9);
            V3.add(p10);
            V3.add(p11);
            Itinerary i3 = new Itinerary(V3);

            Vector<Point> V4= new Vector<Point>();
            Point p12 = new Point(7,7);
            Point p13 = new Point(5,7);
            Point p14 = new Point(5,6);
            Point p15 = new Point(3,6);
            V4.add(p12);
            V4.add(p13);
            V4.add(p14);
            V4.add(p15);
            Itinerary i4 = new Itinerary(V4);



            Vector<Itinerary> itineraries=new Vector<Itinerary>();
            itineraries.add(i5);
            itineraries.add(i1);
            itineraries.add(i2);
            itineraries.add(i3);
            itineraries.add(i4);

            // Creation of buses
            Bus b1 = new Bus(1, stations, 0,itineraries);
            Bus b2 = new Bus(2, stations, 2,itineraries);
            Bus b3 = new Bus(3, stations, 3,itineraries);

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
