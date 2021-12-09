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
            //updateStationsLedger();
        }
    }

    public synchronized void updateStationsLedger() {
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
}
