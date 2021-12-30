package actor;

import java.security.*;
import java.util.Vector;
import java.util.Random;
import event.Event;
import event.ArrivalEvent;
import event.DepartEvent;
import event.PositionUpdateEvent;
import java.lang.Math;
import actor.Manager;
import ui.App;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

//import static actor.Dashboard.decrypt;
//import static actor.Dashboard.getPublicKey;

public class Bus implements Runnable {
    private int number;
    private boolean exit;
    private Point point;
    private Vector<Itinerary> itineraries;
    private Vector<Station> stations;
    private int start;
    private Dashboard dashboard;

    private PrivateKey privateKey;
    private PublicKey publicKey;


    public Bus(int number, Vector<Station> stations, int start,Vector<Itinerary>I) throws NoSuchAlgorithmException {
        this.number = number;
        this.exit = false;
        this.stations = stations;
        this.start = start;
        this.itineraries=I;
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    @Override
    public void run() {
        synchronized (this) {
            //Creating a Signature object
            Signature sign = null;
            try {
                sign = Signature.getInstance("SHA256withRSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            //Initializing the signature
            try {
                sign.initSign(this.privateKey);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            byte[] bytes = "Signature".getBytes();

            //Adding data to the signature
            try {
                sign.update(bytes);
            } catch (SignatureException e) {
                e.printStackTrace();
            }

            byte[] signature = new byte[0];
            //Calculating the signature
            try {
                signature = sign.sign();
            } catch (SignatureException e) {
                e.printStackTrace();
            }
            try {
                synchronized (dashboard) {
                    //send the signature to the dashboard
                    dashboard.getBusesSignatures().put(this.number, signature);
                    dashboard.notify();
                }
                this.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    Random r = new Random();
                    int delay = 0;
                    for (int j = 0; j < itineraries.get(i).getPoints().size()-1; j++) {
                        if (r.nextInt(10) == 0) {
                            delay = r.nextInt(5) + 5;
                        } else
                            delay = 0;
                        Thread.sleep((3000 * itineraries.get(i).getPoints().get(j+1).Distance(itineraries.get(i).getPoints().get(j))) + (delay * 1000));
                        synchronized (dashboard) {
                            dashboard.createTransaction((Event)(new PositionUpdateEvent(this, itineraries.get(i).getPoints().get(j+1), System.currentTimeMillis() - Manager.startTime)));
                            dashboard.notify();
                        }
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
    public void setItineraries(Vector<Itinerary> I) {
        this.itineraries=I;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
