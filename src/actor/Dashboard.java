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
    private Vector<Long> durations;
    private Vector<Integer> busLastStation;
    private Vector<Integer> busLastDepart;

    public Dashboard(Vector<Station> stations, Vector<Bus> buses, Vector<Long> durations) {
        this.stations = stations;
        this.buses = buses;
        this.transactions = new Vector<JSONObject>();
        this.ledger = new Ledger();
        this.durations = durations;
        this.busLastStation = new Vector<Integer>();
        this.busLastStation.add(-1);
        this.busLastStation.add(-1);
        this.busLastStation.add(-1);
        this.busLastDepart = new Vector<Integer>();
        this.busLastDepart.add(-1);
        this.busLastDepart.add(-1);
        this.busLastDepart.add(-1);
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
            transactions.clear();
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
                long time = (long)o.get("time");
                if (bus == 1) {
                    if (type.equals("arrival")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus1.setText("Bus 1 in station " + station + "(" + getStationIndex(station) + ")"
                                + "\nat " + time);
                        System.out.println("[ Dashboard             ] : Bus 1 last " + busLastStation.get(0));
                        if (busLastStation.get(0) != -1) {
                            long delay = calculateDelay((long)busLastDepart.get(0), time, busLastStation.get(0));
                            System.out.println("[ Dashboard             ] : Bus 1 delay " + delay);
                            if (delay > 0)
                                App.instance.labelDelayBus1.setText("Delay of " + delay);
                            else
                                App.instance.labelDelayBus1.setText("");
                        }
                        busLastStation.set(0, getStationIndex(station));
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus1.setText("Bus 1 departed from station " + station + "(" + getStationIndex(station) + ")"
                                + "\nto station " + nextStation(station).getName()
                                + "\nat " + time);
                        busLastDepart.set(0, (int)time);
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus1.setText(position);
                    }
                } else if (bus == 2) {
                    if (type.equals("arrival")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus2.setText("Bus 2 in station " + station + "(" + getStationIndex(station) + ")"
                                + "\nat " + time);
                        System.out.println("[ Dashboard             ] : Bus 2 last " + busLastStation.get(1));
                        if (busLastStation.get(1) != -1) {
                            long delay = calculateDelay((long)busLastDepart.get(1), time, busLastStation.get(1));
                            System.out.println("[ Dashboard             ] : Bus 2 delay " + delay);
                            if (delay > 0)
                                App.instance.labelDelayBus2.setText("Delay of " + delay);
                            else
                                App.instance.labelDelayBus2.setText("");
                        }
                        busLastStation.set(1, getStationIndex(station));
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus2.setText("Bus 2 departed from station " + station + "(" + getStationIndex(station) + ")"
                                + "\nto station " + nextStation(station).getName()
                                + "\nat " + time);
                        busLastDepart.set(1, (int)time);
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus2.setText(position);
                    }
                } else if (bus == 3) {
                    if (type.equals("arrival")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus3.setText("Bus 3 in station " + station + "(" + getStationIndex(station) + ")"
                                + "\nat " + time);
                        System.out.println("[ Dashboard             ] : Bus 1 last " + busLastStation.get(2));
                        if (busLastStation.get(2) != -1) {
                            long delay = calculateDelay((long)busLastDepart.get(2), time, busLastStation.get(2));
                            System.out.println("[ Dashboard             ] : Bus 3 delay " + delay);
                            if (delay > 0)
                                App.instance.labelDelayBus3.setText("Delay of " + delay);
                            else
                                App.instance.labelDelayBus3.setText("");
                        }
                        busLastStation.set(2, getStationIndex(station));
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus3.setText("Bus 3 departed from station " + station + "(" + getStationIndex(station) + ")"
                                + "\nto station " + nextStation(station).getName()
                                + "\nat " + time);
                        busLastDepart.set(2, (int)time);
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

    private int getStationIndex(String station) {
        for (int i = 0; i < stations.size(); ++i) {
            if (stations.get(i).getName().equals(station))
                return i;
        }
        return -1;
    }

    private long calculateDelay(long lastDepart, long arrivalTime, int station){
        long exprectedArrivalTime = lastDepart + durations.get(station);
        return (arrivalTime / 1000) - (exprectedArrivalTime / 1000);
    }
}