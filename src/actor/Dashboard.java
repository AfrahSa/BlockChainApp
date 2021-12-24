package actor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import blockchain.Ledger;
import blockchain.Block;
import event.Event;
import event.ArrivalEvent;
import event.PositionUpdateEvent;
import event.DepartEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
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
    public ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();

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
                    String FilePath = "C:\\Users\\dell\\IdeaProjects\\BlockChainApp1\\Blockchain.txt";
                    String jsonContent = ledger.toJson();
                    File file = new File(FilePath);

                    try {
                        if (!file.exists())
                            file.createNewFile();
                        FileWriter writer = new FileWriter(file);
                        writer.write(jsonContent);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("Erreur: impossible de créer le fichier '"
                                + FilePath + "'");
                    }

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
        //ObservableList<JSONObject> data = FXCollections.observableArrayList();
        //Platform.runLater(() -> App.instance.hBoxBC.getChildren().addAll(new Label(b.toString())));
        Platform.runLater(() -> {
            JSONArray a = (JSONArray) JSONValue.parse(b.getData());
            for (int i = 0; i < a.size(); i++) {

                JSONObject o = (JSONObject)a.get(i);
                List<StringProperty> list = new ArrayList<>();
                list.add(0,new SimpleStringProperty(o.get("bus").toString()));
                list.add(1,new SimpleStringProperty((String)o.get("station")));
                list.add(2,new SimpleStringProperty(o.get("time").toString()));
                list.add(3,new SimpleStringProperty((String)o.get("type")));
                if(o.get("position") == null)
                    list.add(4,new SimpleStringProperty(""));
                else
                    list.add(4,new SimpleStringProperty (o.get("position").toString()));
                list.add(5,new SimpleStringProperty(b.getHash().substring(0, 4)));
                if(b.getPreviousHash().equals("") || b.getPreviousHash()== null)
                    list.add(6,new SimpleStringProperty(""));
                else
                    list.add(6,new SimpleStringProperty(b.getPreviousHash().substring(0, 4)));
                Long lon= new Long(b.getIndex());
                list.add(7,new SimpleStringProperty(lon.toString()));
                data.add(list);
                App.instance.table.setItems(data);
                Label l =new Label(o.toString());
                l.setTextFill(Color.web("#fff"));
                App.instance.vBoxBC.getChildren().addAll(l);

                long bus = (long)o.get("bus");
                String type = (String)o.get("type");
                long time = (long)o.get("time");
                if (bus == 1) {
                    App.instance.sc.setAnimated(false);
                    App.instance.series.getData().clear();
                    App.instance.series3.getData().clear();
                    App.instance.series2.getData().clear();
                    App.instance.sc.setAnimated(true);
                    App.instance.series2.getData().addAll(App.instance.d1,App.instance.d2,App.instance.d3,App.instance.d4,App.instance.d5,App.instance.d6,App.instance.d7,App.instance.d8,App.instance.d9,App.instance.d10,App.instance.d11);
                    App.instance.series3.getData().addAll(App.instance.S1,App.instance.S2,App.instance.S3,App.instance.S4,App.instance.S5);

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
                        if(station.equals("Bab Ezzouar")){
                            App.instance.sc.setAnimated(false);
                            App.instance.series3.getData().remove(App.instance.S1);
                            App.instance.series.getData().clear();
                            App.instance.sc.setAnimated(true);
                            App.instance.series.getData().add(App.instance.S1);
                        }else if(station.equals("Harrach")){
                            App.instance.sc.setAnimated(false);
                            App.instance.series.getData().clear();
                            App.instance.series3.getData().remove(App.instance.S2);
                            App.instance.sc.setAnimated(true);
                            App.instance.series.getData().add(App.instance.S2);
                        }else if(station.equals("Dar El Beida")){
                            App.instance.sc.setAnimated(false);
                            App.instance.series.getData().clear();
                            App.instance.series3.getData().remove(App.instance.S3);
                            App.instance.sc.setAnimated(true);
                            App.instance.series.getData().add(App.instance.S3);
                        }else if(station.equals("Hammedi")){
                            App.instance.sc.setAnimated(false);
                            App.instance.series.getData().clear();
                            App.instance.series3.getData().remove(App.instance.S4);
                            App.instance.sc.setAnimated(true);
                            App.instance.series.getData().add(App.instance.S4);
                        }else if(station.equals("Bach Djerah")){
                            App.instance.sc.setAnimated(false);
                            App.instance.series.getData().clear();
                            App.instance.series3.getData().remove(App.instance.S5);
                            App.instance.sc.setAnimated(true);
                            App.instance.series.getData().add(App.instance.S5);
                        }
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus1.setText("Bus 1 departed from station " + station + "(" + getStationIndex(station) + ")"
                                + "\nto station " + nextStation(station).getName()
                                + "\nat " + time);
                        busLastDepart.set(0, (int)time);
                    } else if (type.equals("position_update")) {
                       String position = (String)o.get("position");
                        App.instance.labelPosBus1.setText(position);
                         if(position.equals("(4,8)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d9);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d9);
                        }else if(position.equals("(4,3)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d10);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d10);
                        }else if(position.equals("(2,3)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d11);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d11);
                        }else if(position.equals("(6,2)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d1);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d1);
                        }else if(position.equals("(6,4)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d2);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d2);
                        }else if(position.equals("(9,4)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d3);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d3);
                        }else if(position.equals("(9,5)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d4);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d4);
                        }else if(position.equals("(11,5)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d5);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d5);
                        }else if(position.equals("(11,9)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d6);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d6);
                        }else if(position.equals("(7,9)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d7);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d7);
                        }else if(position.equals("(7,8)")){
                             App.instance.sc.setAnimated(false);
                             App.instance.series2.getData().remove(App.instance.d8);
                             App.instance.series.getData().clear();
                             App.instance.sc.setAnimated(true);
                             App.instance.series.getData().add(App.instance.d8);
                        }
                    }
                } else if (bus == 2) {
                    App.instance.sc_2.setAnimated(false);
                    App.instance.series_2.getData().clear();
                    App.instance.series3_2.getData().clear();
                    App.instance.series2_2.getData().clear();
                    App.instance.sc_2.setAnimated(true);
                    App.instance.series2_2.getData().addAll(App.instance.d1_2,App.instance.d2_2,App.instance.d3_2,App.instance.d4_2,App.instance.d5_2,App.instance.d6_2,App.instance.d7_2,App.instance.d8_2,App.instance.d9_2,App.instance.d10_2,App.instance.d11_2);
                    App.instance.series3_2.getData().addAll(App.instance.S1_2,App.instance.S2_2,App.instance.S3_2,App.instance.S4_2,App.instance.S5_2);
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
                        if(station.equals("Bab Ezzouar")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series3_2.getData().remove(App.instance.S1_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.S1_2);
                        }else if(station.equals("Harrach")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series_2.getData().clear();
                            App.instance.series3_2.getData().remove(App.instance.S2_2);
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.S2_2);
                        }else if(station.equals("Dar El Beida")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series_2.getData().clear();
                            App.instance.series3_2.getData().remove(App.instance.S3_2);
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.S3_2);
                        }else if(station.equals("Hammedi")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series_2.getData().clear();
                            App.instance.series3_2.getData().remove(App.instance.S4_2);
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.S4_2);
                        }else if(station.equals("Bach Djerah")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series_2.getData().clear();
                            App.instance.series3_2.getData().remove(App.instance.S5_2);
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.S5_2);
                        }
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus2.setText("Bus 2 departed from station " + station + "(" + getStationIndex(station) + ")"
                                + "\nto station " + nextStation(station).getName()
                                + "\nat " + time);
                        busLastDepart.set(1, (int)time);
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus2.setText(position);

                        if(position.equals("(4,8)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d9_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d9_2);
                        }else if(position.equals("(4,3)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d10_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d10_2);
                        }else if(position.equals("(2,3)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d11_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d11_2);
                        }else if(position.equals("(6,2)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d1_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d1_2);
                        }else if(position.equals("(6,4)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d2_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d2_2);
                        }else if(position.equals("(9,4)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d3_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d3_2);
                        }else if(position.equals("(9,5)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d4_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d4_2);
                        }else if(position.equals("(11,5)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d5_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d5_2);
                        }else if(position.equals("(11,9)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d6_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d6_2);
                        }else if(position.equals("(7,9)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d7_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d7_2);
                        }else if(position.equals("(7,8)")){
                            App.instance.sc_2.setAnimated(false);
                            App.instance.series2_2.getData().remove(App.instance.d8_2);
                            App.instance.series_2.getData().clear();
                            App.instance.sc_2.setAnimated(true);
                            App.instance.series_2.getData().add(App.instance.d8_2);
                        }
                    }
                } else if (bus == 3) {
                    App.instance.sc_3.setAnimated(false);
                    App.instance.series_3.getData().clear();
                    App.instance.series3_3.getData().clear();
                    App.instance.series2_3.getData().clear();
                    App.instance.sc_3.setAnimated(true);
                    App.instance.series2_3.getData().addAll(App.instance.d1_3,App.instance.d2_3,App.instance.d3_3,App.instance.d4_3,App.instance.d5_3,App.instance.d6_3,App.instance.d7_3,App.instance.d8_3,App.instance.d9_3,App.instance.d10_3,App.instance.d11_3);
                    App.instance.series3_3.getData().addAll(App.instance.S1_3,App.instance.S2_3,App.instance.S3_3,App.instance.S4_3,App.instance.S5_3);
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

                        if(station.equals("Bab Ezzouar")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series3_3.getData().remove(App.instance.S1_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.S1_3);
                        }else if(station.equals("Harrach")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series_3.getData().clear();
                            App.instance.series3_3.getData().remove(App.instance.S2_3);
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.S2_3);
                        }else if(station.equals("Dar El Beida")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series_3.getData().clear();
                            App.instance.series3_3.getData().remove(App.instance.S3_3);
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.S3_3);
                        }else if(station.equals("Hammedi")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series_3.getData().clear();
                            App.instance.series3_3.getData().remove(App.instance.S4_3);
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.S4_3);
                        }else if(station.equals("Bach Djerah")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series_3.getData().clear();
                            App.instance.series3_3.getData().remove(App.instance.S5_3);
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.S5_3);
                        }
                    } else if (type.equals("depart")) {
                        String station = (String)o.get("station");
                        App.instance.labelBus3.setText("Bus 3 departed from station " + station + "(" + getStationIndex(station) + ")"
                                + "\nto station " + nextStation(station).getName()
                                + "\nat " + time);
                        busLastDepart.set(2, (int)time);
                    } else if (type.equals("position_update")) {
                        String position = (String)o.get("position");
                        App.instance.labelPosBus3.setText(position);
                        if(position.equals("(4,8)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d9_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d9_3);
                        }else if(position.equals("(4,3)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d10_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d10_3);
                        }else if(position.equals("(2,3)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d11_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d11_3);
                        }else if(position.equals("(6,2)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d1_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d1_3);
                        }else if(position.equals("(6,4)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d2_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d2_3);
                        }else if(position.equals("(9,4)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d3_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d3_3);
                        }else if(position.equals("(9,5)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d4_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d4_3);
                        }else if(position.equals("(11,5)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d5_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d5_3);
                        }else if(position.equals("(11,9)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d6_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d6_3);
                        }else if(position.equals("(7,9)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d7_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d7_3);
                        }else if(position.equals("(7,8)")){
                            App.instance.sc_3.setAnimated(false);
                            App.instance.series2_3.getData().remove(App.instance.d8_3);
                            App.instance.series_3.getData().clear();
                            App.instance.sc_3.setAnimated(true);
                            App.instance.series_3.getData().add(App.instance.d8_3);
                        }
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