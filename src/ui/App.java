package ui;

import actor.Bus;
import actor.Dashboard;
import actor.Manager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Vector;

import actor.Bus;
import actor.Station;
import actor.Dashboard;

public class App extends Application {
    public HBox hBoxBC;
    public ScrollPane scrollPane;
    public Label labelBus1;
    public Label labelBus2;
    public Label labelBus3;
    public HBox hBoxBuses;

    public static App instance;

    public static Manager manager;
    public static Thread threadManager;

    @Override
    public void start(Stage stage) throws IOException {
        Label l = new Label("Debut du ledger");

        hBoxBC = new HBox(l);
        hBoxBC.setAlignment(Pos.CENTER);
        hBoxBC.setSpacing(100);

        scrollPane = new ScrollPane(hBoxBC);

        labelBus1 = new Label("Bus1");
        labelBus2 = new Label("Bus2");
        labelBus3 = new Label("Bus3");

        hBoxBuses = new HBox(labelBus1, labelBus2, labelBus3);
        hBoxBuses.setAlignment(Pos.CENTER);
        hBoxBuses.setSpacing(100);

        VBox vBoxGlobal = new VBox(scrollPane, hBoxBuses);
        vBoxGlobal.setAlignment(Pos.CENTER);
        vBoxGlobal.setSpacing(100);

        Scene scene = new Scene(vBoxGlobal);
        stage.setScene(scene);
        stage.setTitle("Test window");
        stage.show();

        instance = this;

        try {
            manager = new Manager();
            threadManager = new Thread(manager);
            threadManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
        try {
            synchronized (manager) {
                manager.notify();
            }
            threadManager.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}