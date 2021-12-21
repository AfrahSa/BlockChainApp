package actor;

import java.util.Vector;
import blockchain.Ledger;
import blockchain.Block;
import event.Event;
import event.ArrivalEvent;
import event.PositionUpdateEvent;
import event.DepartEvent;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import javafx.application.Platform;
import javafx.scene.control.Label;
import ui.App;

public class Dashboard implements Runnable {
    private boolean exit;

    private Vector<Station> stations;
    private Vector<Bus> buses;
    private Vector<JSONObject> transactions;
    private Ledger ledger;

    public Dashboard(Vector<Station> stations, Vector<Bus> buses) {
        this.stations = stations;
        this.buses = buses;
        this.transactions = new Vector<JSONObject>();
        this.ledger = new Ledger();
    }

    @Override
    public void run() {
        synchronized (this) {
            while(!exit) {
                try {
                    this.wait();

                    handleTransactions();

                    System.out.println("[ Dashboard             ] :\n" + ledger);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("[ Dashboard             ] : exit");
        }
    }

    public synchronized void stop() {
        exit = true;
    }

    public void handleTransactions() {
        if (!transactions.isEmpty()) {
            ledger.addBlockToLedger(new Block(transactionsToJson(), ledger.getLastBlockHash(), ledger.getSize()));
            updateUI();
        }
    }

    public synchronized void updateUI() {
        Block b = ledger.getLastBlock();
        //Platform.runLater(() -> App.instance.hBoxBC.getChildren().addAll(new Label(b.toString())));
        Platform.runLater(() -> {
            JSONArray a = (JSONArray) JSONValue.parse(b.getData());
            for (int i = 0; i < a.size(); i++) {
                JSONObject o = (JSONObject)a.get(i);
                App.instance.hBoxBC.getChildren().addAll(new Label(o.toString()));
                long bus = (long)o.get("bus");
                String type = (String)o.get("type");
                if (bus == 1) {
                    if (type.equals("arrival")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus1.setText("Bus 1 in station " + station);


                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus1.setText("Bus 1 departed from station " + station + "\nto station " + nextStation(station).getName());
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus1.setText(position);
                    }
                } else if (bus == 2) {
                    if (type.equals("arrival")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus2.setText("Bus 2 in station " + station);
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus2.setText("Bus 2 departed from station " + station + "\nto station " + nextStation(station).getName());
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus2.setText(position);
                    }
                } else if (bus == 3) {
                    if (type.equals("arrival")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus3.setText("Bus 3 in station " + station);
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus3.setText("Bus 3 departed from station " + station + "\nto station " + nextStation(station).getName());
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus3.setText(position);
                    }
                }
            }
        });
    }

    public void createTransaction(Event event) {
        JSONObject transaction = new JSONObject();
        if (event instanceof ArrivalEvent) {
            ArrivalEvent e = (ArrivalEvent)event;
            transaction.put("type", "arrival");
            transaction.put("bus", e.getBus().getNumber());
            transaction.put("station", e.getStation().getName());
            transaction.put("time", e.getTimeStamp());

        } else if (event instanceof DepartEvent) {
            DepartEvent e = (DepartEvent)event;
            transaction.put("type", "depart");
            transaction.put("bus", e.getBus().getNumber());
            transaction.put("station", e.getStation().getName());
            transaction.put("time", e.getTimeStamp());
            transaction.put("expectedArrivalTime", e.getExpectedArrivalTime());
        } else if (event instanceof PositionUpdateEvent) {
            PositionUpdateEvent e = (PositionUpdateEvent)event;
            transaction.put("type", "position_update");
            transaction.put("bus", e.getBus().getNumber());
            transaction.put("position", "(" + e.getPosition().getX() + "," + e.getPosition().getY() + ")");
            transaction.put("time", e.getTimeStamp());
        }
        transactions.add(transaction);
    }

    public String transactionsToJson() {
        JSONArray transactionsJson = new JSONArray();
        for (int i = 0; i < transactions.size(); i++)
            transactionsJson.add(transactions.get(i));

        return transactionsJson.toString();
    }

    public Station nextStation(String s) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getName().equals(s))
                return stations.get((i + 1) % stations.size());
        }
        return stations.get(1);
    }
    public int IsLate(int ibus,int arrivalTime,String station){
        int n=ledger.getSize();
        Block b;
        String previousStation="";
        for(Station s: stations){
            if(nextStation(s.getName()).getName()==station){
                previousStation=s.getName();
                break;
            }
        }
        while (n>=0){
            b=ledger.getLedger().get(n);
            JSONArray a = (JSONArray) JSONValue.parse(b.getData());
            for (int i = 0; i < a.size(); i++) {
                JSONObject o = (JSONObject)a.get(i);
                int bus = (int)o.get("bus");
                String type = (String)o.get("type");
                String st=(String)o.get("station");
                if (bus == ibus && type.equals("depart") && st.equals(previousStation)) {
                    int expectedTime=(int)o.get("expectedArrivalTime");
                    if(expectedTime==arrivalTime)
                        return 0;
                }
            }
            n--;
        }
        return 0;
    }
}