package actor;

import java.util.Vector;
import blockchain.Ledger;
import blockchain.Block;
import event.Event;
import event.ArrivalEvent;
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

                    //System.out.println("[ Dashboard             ] :\n" + ledger);
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
                String station = (String)o.get("station");
                if (bus == 1) {
                    if (type.equals("arrival")) {
                        App.instance.labelBus1.setText("Bus 1 in station " + station);
                    } else {
                        App.instance.labelBus1.setText("Bus 1 departed from station " + station + "\nto station " + nextStation(station));
                    }
                } else if (bus == 2) {
                    if (type.equals("arrival")) {
                        App.instance.labelBus2.setText("Bus 2 in station " + station);
                    } else {
                        App.instance.labelBus2.setText("Bus 2 departed from station " + station + "\nto station " + nextStation(station));
                    }
                } else if (bus == 3) {
                    if (type.equals("arrival")) {
                        App.instance.labelBus3.setText("Bus 3 in station " + station);
                    } else {
                        App.instance.labelBus3.setText("Bus 3 departed from station " + station + "\nto station " + nextStation(station));
                    }
                }
            }
        });
    }

    public void createTransaction(Event event) {
        JSONObject transaction = new JSONObject();
        if (event instanceof ArrivalEvent) {
            transaction.put("type", "arrival");
        } else if (event instanceof DepartEvent) {
            transaction.put("type", "depart");
        }
        transaction.put("bus", event.getBus().getNumber());
        transaction.put("station", event.getStation().getName());
        transaction.put("time", event.getTimeStamp());

        transactions.add(transaction);
    }

    public String transactionsToJson() {
        JSONArray transactionsJson = new JSONArray();
        for (int i = 0; i < transactions.size(); i++)
            transactionsJson.add(transactions.get(i));

        return transactionsJson.toString();
    }

    public String nextStation(String s) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getName().equals(s))
                return stations.get((i + 1) % stations.size()).getName();
        }
        return "";
    }
}