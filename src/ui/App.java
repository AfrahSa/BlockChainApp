package ui;

import actor.Bus;
import actor.Dashboard;
import actor.Manager;
import blockchain.Block;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import actor.Bus;
import actor.Station;
import actor.Dashboard;
import org.json.simple.JSONObject;

public class App extends Application {
    public VBox vBoxBC;
    public VBox vboxBlocs;
    public ScrollPane scrollPane;
    public Label label1;
    public Label labelBus1;
    public Label labelBus2;
    public Label labelBus3;
    public Label labelDelayBus1;
    public Label labelDelayBus2;
    public Label labelDelayBus3;
    public Label labelPosBus1;
    public Label labelPosBus2;
    public Label labelPosBus3;
    public HBox hbox1;
    public HBox hBoxBuses;
    public HBox hBoxDelayBuses;
    public HBox hBoxPosBuses;

    public TableView<List<StringProperty>> table;
    public TableColumn<List<StringProperty>, String> BusCol;
    public TableColumn<List<StringProperty>, String> StationCol;
    public TableColumn<List<StringProperty>, String> TimeCol;
    public TableColumn<List<StringProperty>, String> TypeCol;
    public TableColumn<List<StringProperty>, String> Position;
    public ScatterChart<Number,Number> sc;

    public XYChart.Series series;
    public XYChart.Series series2;
    public XYChart.Series series3;

    public XYChart.Data S1;
    public XYChart.Data S5;
    public XYChart.Data d9;
    public XYChart.Data d10;
    public XYChart.Data d11;
    public XYChart.Data d1;
    public XYChart.Data d2;
    public XYChart.Data S2;
    public XYChart.Data d3;
    public XYChart.Data d4;
    public XYChart.Data d5;
    public XYChart.Data S3;
    public XYChart.Data d6;
    public XYChart.Data S4;
    public XYChart.Data d7;
    public XYChart.Data d8;

    public static App instance;

    public static Manager manager;
    public static Thread threadManager;

    @Override
    public void start(Stage stage) throws IOException {

        vBoxBC = new VBox();
        vBoxBC.setAlignment(Pos.CENTER_LEFT);
        vBoxBC.setSpacing(10);
        vBoxBC.setStyle("-fx-background-color: #889EAF;");
        vBoxBC.setMinWidth(320);
        //vBoxBC.setMaxWidth(320);


        Label lb = new Label("Blocks");
        lb.setFont(new Font("Segoe UI Light", 20));
        Image img = new Image("ui/images/blockchain (3).png");
        ImageView view = new ImageView(img);
        view.setPreserveRatio(true);
        lb.setGraphic(view);

        HBox hb = new HBox(lb);
        hb.setAlignment(Pos.CENTER);
        hb.setSpacing(100);
        hb.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");

        table = new TableView<List<StringProperty>>();
        //table.setPrefSize( 300, 700 );
        table.setPrefHeight(700);

        BusCol = new TableColumn("Bus");
        BusCol.setPrefWidth(50);
        BusCol.setCellValueFactory(data -> data.getValue().get(0));

        StationCol = new TableColumn("Station");
        StationCol.setPrefWidth(120);
        StationCol.setCellValueFactory(data -> data.getValue().get(1));

        TimeCol = new TableColumn("Time");
        TimeCol.setPrefWidth(80);
        TimeCol.setCellValueFactory(data -> data.getValue().get(2));

        TypeCol = new TableColumn("Event Type");
        TypeCol.setPrefWidth(100);
        TypeCol.setCellValueFactory(data -> data.getValue().get(3));

        Position = new TableColumn("Position");
        Position.setPrefWidth(80);
        Position.setCellValueFactory(data -> data.getValue().get(4));

        table.getColumns().addAll(BusCol,StationCol,TimeCol,TypeCol,Position);

        vboxBlocs = new VBox(hb,table);
        vboxBlocs.setAlignment(Pos.TOP_CENTER);
        vBoxBC.setSpacing(10);
        //vBoxBC.setStyle("-fx-background-color: #889EAF;");


        labelBus1 = new Label("Bus1");
        labelBus2 = new Label("Bus2");
        labelBus3 = new Label("Bus3");

        hBoxBuses = new HBox(labelBus1, labelBus2, labelBus3);
        hBoxBuses.setAlignment(Pos.CENTER);
        hBoxBuses.setSpacing(100);

        labelDelayBus1 = new Label("");
        labelDelayBus2 = new Label("");
        labelDelayBus3 = new Label("");

        hBoxDelayBuses = new HBox(labelDelayBus1, labelDelayBus2, labelDelayBus3);
        hBoxDelayBuses.setAlignment(Pos.CENTER);
        hBoxDelayBuses.setSpacing(100);

        labelPosBus1 = new Label("PosBus1");
        labelPosBus2 = new Label("PosBus2");
        labelPosBus3 = new Label("PosBus3");

        label1 = new Label("Gestion des Stations de bus");
        label1.setFont(new Font("Segoe UI Light", 30));
        Image img1 = new Image("ui/images/bus-station.png");
        ImageView view1 = new ImageView(img1);
        view1.setPreserveRatio(true);
        label1.setGraphic(view1);

        hbox1 = new HBox(label1);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");
        hbox1.setMinHeight(40);
        hbox1.setPadding(new Insets(15, 12, 15, 12));
        BorderPane root = new BorderPane();
        root.setTop(hbox1);
        root.setMargin(hbox1, new Insets(10));

        //Defining the x axis
        final NumberAxis xAxis = new NumberAxis(0, 12, 1);
        xAxis.setVisible(false);

        //Defining the y axis
        final NumberAxis yAxis = new NumberAxis(0, 10, 1);

        sc = new ScatterChart<>(xAxis,yAxis);
        sc.setPrefSize(400, 400);
        sc.setMinSize(400, 400);
        sc.setMaxSize(600, 600);

        series = new XYChart.Series();
        series2= new XYChart.Series();
        series3= new XYChart.Series();

        S1 = new  XYChart.Data(2,2);
        S5 = new XYChart.Data(5, 8);
        d9 = new XYChart.Data(4, 8);
        d10 = new XYChart.Data(4, 3);
        d11 = new XYChart.Data(2, 3);
        d1 = new XYChart.Data(6, 2);
        d2 = new XYChart.Data(6, 4);
        S2 = new XYChart.Data(8, 4);
        d3 = new XYChart.Data(9, 4);
        d4 = new XYChart.Data(9, 5);
        d5 = new XYChart.Data(11, 5);
        S3 = new XYChart.Data(11, 7);
        d6 = new XYChart.Data(11, 9);
        S4 = new XYChart.Data(9, 9);
        d7 = new XYChart.Data(7, 9);
        d8 = new XYChart.Data(7, 8);



        sc.getData().add(series2); //d
        sc.getData().add(series3); //station
        sc.getData().add(series);  //bus


        //Prepare XYChart.Series objects by setting data


        //Setting the data to Line chart


        hBoxPosBuses = new HBox(labelPosBus1, labelPosBus2, labelPosBus3);
        hBoxPosBuses.setAlignment(Pos.CENTER);
        hBoxPosBuses.setSpacing(100);

        VBox vBoxGlobal = new VBox(sc, hBoxBuses, hBoxDelayBuses, hBoxPosBuses);
        vBoxGlobal.setAlignment(Pos.CENTER_LEFT);
        vBoxGlobal.setSpacing(100);
        root.setLeft(vboxBlocs);
        root.setCenter(vBoxGlobal);
        root.setPrefSize(400, 400);
        root.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #316B83, #6D8299);");



        Scene scene = new Scene(root,1000,700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
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