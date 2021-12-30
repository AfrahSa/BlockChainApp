package actor;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import event.Event;
import event.ArrivalEvent;
import event.DepartEvent;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Station implements Runnable {
    private String name;
    private boolean exit;

    private LinkedList<Event> events;
    private Dashboard dashboard;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Station(String name) throws NoSuchAlgorithmException {
        this.name = name;
        this.exit = false;
        this.events = new LinkedList<Event>();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    @Override 
    public void run() {
        //Creating a Signature object
        Signature sign = null;
        try {
            sign = Signature.getInstance("SHA256withDSA");
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

        //send the signature to the dashboard
        dashboard.getStationsSignatures().put(this.name,signature);

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

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
