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
    public VBox layout;
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
    public Label Bus1Auth;
    public Label Bus2Auth;
    public Label Bus3Auth;
    public Label StationsBabAuth;
    public Label StationsBachAuth;
    public Label StationsDarAuth;
    public Label StationsHarrachAuth;
    public Label StationsHammediAuth;
    public HBox hbox1;
    public HBox hBoxBuses;
    public HBox hBoxDelayBuses;
    public HBox hBoxPosBuses;
    public Button buttonAuth;

    public TableView<List<StringProperty>> table;
    public TableColumn<List<StringProperty>, String> BusCol;
    public TableColumn<List<StringProperty>, String> StationCol;
    public TableColumn<List<StringProperty>, String> TimeCol;
    public TableColumn<List<StringProperty>, String> TypeCol;
    public TableColumn<List<StringProperty>, String> Position;
    public TableColumn<List<StringProperty>, String> HashCol;
    public TableColumn<List<StringProperty>, String> PrHashCol;
    public TableColumn<List<StringProperty>, String> BlockCol;

    public ScatterChart<Number,Number> sc;
    public ScatterChart<Number,Number> sc_2;
    public ScatterChart<Number,Number> sc_3;

    public XYChart.Series series;
    public XYChart.Series series2;
    public XYChart.Series series3;

    public XYChart.Series series_2;
    public XYChart.Series series2_2;
    public XYChart.Series series3_2;

    public XYChart.Series series_3;
    public XYChart.Series series2_3;
    public XYChart.Series series3_3;

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

    public XYChart.Data S1_2;
    public XYChart.Data S5_2;
    public XYChart.Data d9_2;
    public XYChart.Data d10_2;
    public XYChart.Data d11_2;
    public XYChart.Data d1_2;
    public XYChart.Data d2_2;
    public XYChart.Data S2_2;
    public XYChart.Data d3_2;
    public XYChart.Data d4_2;
    public XYChart.Data d5_2;
    public XYChart.Data S3_2;
    public XYChart.Data d6_2;
    public XYChart.Data S4_2;
    public XYChart.Data d7_2;
    public XYChart.Data d8_2;

    public XYChart.Data S1_3;
    public XYChart.Data S5_3;
    public XYChart.Data d9_3;
    public XYChart.Data d10_3;
    public XYChart.Data d11_3;
    public XYChart.Data d1_3;
    public XYChart.Data d2_3;
    public XYChart.Data S2_3;
    public XYChart.Data d3_3;
    public XYChart.Data d4_3;
    public XYChart.Data d5_3;
    public XYChart.Data S3_3;
    public XYChart.Data d6_3;
    public XYChart.Data S4_3;
    public XYChart.Data d7_3;
    public XYChart.Data d8_3;

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
        lb.setFont(new Font("Segoe UI Semibold", 20));
        lb.setTextFill(Color.web("#000000"));
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
        table.setPrefHeight(610);

        BusCol = new TableColumn("Bus");
        BusCol.setPrefWidth(40);
        BusCol.setCellValueFactory(data -> data.getValue().get(0));

        StationCol = new TableColumn("Station");
        StationCol.setPrefWidth(90);
        StationCol.setCellValueFactory(data -> data.getValue().get(1));

        TimeCol = new TableColumn("Time");
        TimeCol.setPrefWidth(40);
        TimeCol.setCellValueFactory(data -> data.getValue().get(2));

        TypeCol = new TableColumn("Event Type");
        TypeCol.setPrefWidth(95);
        TypeCol.setCellValueFactory(data -> data.getValue().get(3));

        Position = new TableColumn("Position");
        Position.setPrefWidth(70);
        Position.setCellValueFactory(data -> data.getValue().get(4));

        HashCol = new TableColumn("Hash");
        HashCol.setPrefWidth(70);
        HashCol.setCellValueFactory(data -> data.getValue().get(5));

        PrHashCol = new TableColumn("Previous Hash");
        PrHashCol.setPrefWidth(100);
        PrHashCol.setCellValueFactory(data -> data.getValue().get(6));

        BlockCol = new TableColumn("N° Bloc");
        BlockCol.setPrefWidth(50);
        BlockCol.setCellValueFactory(data -> data.getValue().get(7));

        table.getColumns().addAll(BlockCol,HashCol,PrHashCol,BusCol,StationCol,TimeCol,TypeCol,Position);

        vboxBlocs = new VBox(hb,table);
        vboxBlocs.setAlignment(Pos.TOP_CENTER);
        vBoxBC.setSpacing(10);



        labelBus1 = new Label("Bus1");
        labelBus2 = new Label("Bus2");
        labelBus3 = new Label("Bus3");

        labelBus1.setPadding(new Insets(20));
        labelBus1.setFont(new Font("Segoe UI Light", 14));
        labelBus1.setTextFill(Color.web("#000000"));

        labelBus2.setPadding(new Insets(20));
        labelBus2.setFont(new Font("Segoe UI Light", 14));
        labelBus2.setTextFill(Color.web("#000000"));

        labelBus3.setPadding(new Insets(20));
        labelBus3.setFont(new Font("Segoe UI Light", 14));
        labelBus3.setTextFill(Color.web("#000000"));

        hBoxBuses = new HBox(labelBus1, labelBus2, labelBus3);
        hBoxBuses.setAlignment(Pos.CENTER);
        hBoxBuses.setSpacing(100);

        labelDelayBus1 = new Label("");
        labelDelayBus1.setFont(new Font("Segoe UI Light", 14));
        labelDelayBus1.setTextFill(Color.web("#F90716"));

        labelDelayBus2 = new Label("");
        labelDelayBus2.setFont(new Font("Segoe UI Light", 14));
        labelDelayBus2.setTextFill(Color.web("#F90716"));

        labelDelayBus3 = new Label("");
        labelDelayBus3.setFont(new Font("Segoe UI Light", 14));
        labelDelayBus3.setTextFill(Color.web("#F90716"));

        hBoxDelayBuses = new HBox(labelDelayBus1, labelDelayBus2, labelDelayBus3);
        hBoxDelayBuses.setAlignment(Pos.CENTER);
        hBoxDelayBuses.setSpacing(100);

        labelPosBus1 = new Label("PosBus1");
        labelPosBus2 = new Label("PosBus2");
        labelPosBus3 = new Label("PosBus3");

        label1 = new Label("Bus Stations Management");
        label1.setFont(new Font("Segoe UI Light", 26));
        label1.setTextFill(Color.web("#000000"));
        Image img1 = new Image("ui/images/bus-stop-pointer (1).png");
        ImageView view1 = new ImageView(img1);
        view1.setPreserveRatio(true);
        label1.setGraphic(view1);

        hbox1 = new HBox(label1);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");
        hbox1.setMinHeight(40);
        hbox1.setPadding(new Insets(7, 5, 7, 5));
        BorderPane root = new BorderPane();
        root.setTop(hbox1);
        root.setMargin(hbox1, new Insets(5));


        final NumberAxis xAxis = new NumberAxis(0, 12, 1);
        final NumberAxis yAxis = new NumberAxis(0, 10, 1);

        final NumberAxis xAxis2 = new NumberAxis(0, 12, 1);
        final NumberAxis yAxis2 = new NumberAxis(0, 10, 1);

        final NumberAxis xAxis3 = new NumberAxis(0, 12, 1);
        final NumberAxis yAxis3 = new NumberAxis(0, 10, 1);

        sc = new ScatterChart<>(xAxis,yAxis);
        sc.setPrefSize(400, 250);
        sc.setMinSize(400, 200);
        sc.setMaxSize(400, 200);
        sc.setLegendVisible(false);

        sc_2 = new ScatterChart<>(xAxis2,yAxis2);
        sc_2.setPrefSize(400, 200);
        sc_2.setMinSize(400, 200);
        sc_2.setMaxSize(400, 200);
        sc_2.setLegendVisible(false);

        sc_3 = new ScatterChart<>(xAxis3,yAxis3);
        sc_3.setPrefSize(400, 200);
        sc_3.setMinSize(400, 200);
        sc_3.setMaxSize(400, 200);
        sc_3.setLegendVisible(false);

        series = new XYChart.Series();
        series2= new XYChart.Series();
        series3= new XYChart.Series();

        series_2 = new XYChart.Series();
        series2_2= new XYChart.Series();
        series3_2= new XYChart.Series();

        series_3 = new XYChart.Series();
        series2_3= new XYChart.Series();
        series3_3= new XYChart.Series();

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

        S1_2 = new  XYChart.Data(2,2);
        S5_2 = new XYChart.Data(5, 8);
        d9_2 = new XYChart.Data(4, 8);
        d10_2 = new XYChart.Data(4, 3);
        d11_2 = new XYChart.Data(2, 3);
        d1_2 = new XYChart.Data(6, 2);
        d2_2 = new XYChart.Data(6, 4);
        S2_2 = new XYChart.Data(8, 4);
        d3_2 = new XYChart.Data(9, 4);
        d4_2 = new XYChart.Data(9, 5);
        d5_2 = new XYChart.Data(11, 5);
        S3_2 = new XYChart.Data(11, 7);
        d6_2 = new XYChart.Data(11, 9);
        S4_2 = new XYChart.Data(9, 9);
        d7_2 = new XYChart.Data(7, 9);
        d8_2 = new XYChart.Data(7, 8);

        S1_3 = new  XYChart.Data(2,2);
        S5_3 = new XYChart.Data(5, 8);
        d9_3 = new XYChart.Data(4, 8);
        d10_3 = new XYChart.Data(4, 3);
        d11_3 = new XYChart.Data(2, 3);
        d1_3 = new XYChart.Data(6, 2);
        d2_3 = new XYChart.Data(6, 4);
        S2_3 = new XYChart.Data(8, 4);
        d3_3 = new XYChart.Data(9, 4);
        d4_3 = new XYChart.Data(9, 5);
        d5_3 = new XYChart.Data(11, 5);
        S3_3 = new XYChart.Data(11, 7);
        d6_3 = new XYChart.Data(11, 9);
        S4_3 = new XYChart.Data(9, 9);
        d7_3 = new XYChart.Data(7, 9);
        d8_3 = new XYChart.Data(7, 8);


        sc.getData().add(series2); //d
        sc.getData().add(series3); //station
        sc.getData().add(series);  //bus

        sc_2.getData().add(series2_2); //d
        sc_2.getData().add(series3_2); //station
        sc_2.getData().add(series_2);  //bus

        sc_3.getData().add(series2_3); //d
        sc_3.getData().add(series3_3); //station
        sc_3.getData().add(series_3);  //bus

        NumberAxis xAxisL = new NumberAxis(0, 12, 1);
        NumberAxis yAxisL = new NumberAxis(0, 10, 1);
        final LineChart<Number,Number> linchart = new LineChart<>(xAxisL,yAxisL);
        linchart.setOpacity(0.3);
        linchart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        linchart.setLegendVisible(false);


        XYChart.Series serieL1 = new XYChart.Series();
        serieL1.getData().add(new XYChart.Data(2,2));
        serieL1.getData().add(new XYChart.Data(6,2));
        serieL1.getData().add(new XYChart.Data(6,4));
        serieL1.getData().add(new XYChart.Data(9,4));
        serieL1.getData().add(new XYChart.Data(9,5));
        serieL1.getData().add(new XYChart.Data(11,5));
        serieL1.getData().add(new XYChart.Data(11,9));

        XYChart.Series serieL2 = new XYChart.Series();
        serieL2.getData().add(new XYChart.Data(2,2));
        serieL2.getData().add(new XYChart.Data(2,3));
        serieL2.getData().add(new XYChart.Data(4,3));
        serieL2.getData().add(new XYChart.Data(4,8));
        serieL2.getData().add(new XYChart.Data(7,8));
        serieL2.getData().add(new XYChart.Data(7,9));
        serieL2.getData().add(new XYChart.Data(11,9));

        linchart.setAnimated(false);
        linchart.setCreateSymbols(false);
        linchart.getData().addAll(serieL1, serieL2);

        NumberAxis xAxisL22 = new NumberAxis(0, 12, 1);
        NumberAxis yAxisL22 = new NumberAxis(0, 10, 1);
        final LineChart<Number,Number> linchart2 = new LineChart<>(xAxisL22,new NumberAxis(0, 10, 1));
        linchart2.setOpacity(0.3);
        linchart2.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        linchart2.setLegendVisible(false);


        XYChart.Series serieL12 = new XYChart.Series();
        serieL12.getData().add(new XYChart.Data(2,2));
        serieL12.getData().add(new XYChart.Data(6,2));
        serieL12.getData().add(new XYChart.Data(6,4));
        serieL12.getData().add(new XYChart.Data(9,4));
        serieL12.getData().add(new XYChart.Data(9,5));
        serieL12.getData().add(new XYChart.Data(11,5));
        serieL12.getData().add(new XYChart.Data(11,9));

        XYChart.Series serieL22 = new XYChart.Series();
        serieL22.getData().add(new XYChart.Data(2,2));
        serieL22.getData().add(new XYChart.Data(2,3));
        serieL22.getData().add(new XYChart.Data(4,3));
        serieL22.getData().add(new XYChart.Data(4,8));
        serieL22.getData().add(new XYChart.Data(7,8));
        serieL22.getData().add(new XYChart.Data(7,9));
        serieL22.getData().add(new XYChart.Data(11,9));

        linchart2.setAnimated(false);
        linchart2.setCreateSymbols(false);
        linchart2.getData().addAll(serieL12, serieL22);

        NumberAxis xAxisL23 = new NumberAxis(0, 12, 1);
        NumberAxis yAxisL23 = new NumberAxis(0, 10, 1);
        final LineChart<Number,Number> linchart3 = new LineChart<>(xAxisL23,yAxisL23);
        linchart3.setOpacity(0.3);
        linchart3.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        linchart3.setLegendVisible(false);


        XYChart.Series serieL13 = new XYChart.Series();
        serieL13.getData().add(new XYChart.Data(2,2));
        serieL13.getData().add(new XYChart.Data(6,2));
        serieL13.getData().add(new XYChart.Data(6,4));
        serieL13.getData().add(new XYChart.Data(9,4));
        serieL13.getData().add(new XYChart.Data(9,5));
        serieL13.getData().add(new XYChart.Data(11,5));
        serieL13.getData().add(new XYChart.Data(11,9));

        XYChart.Series serieL23 = new XYChart.Series();
        serieL23.getData().add(new XYChart.Data(2,2));
        serieL23.getData().add(new XYChart.Data(2,3));
        serieL23.getData().add(new XYChart.Data(4,3));
        serieL23.getData().add(new XYChart.Data(4,8));
        serieL23.getData().add(new XYChart.Data(7,8));
        serieL23.getData().add(new XYChart.Data(7,9));
        serieL23.getData().add(new XYChart.Data(11,9));

        linchart3.setAnimated(false);
        linchart3.setCreateSymbols(false);
        linchart3.getData().addAll(serieL13, serieL23);

        StackPane pane=new StackPane();
        pane.getChildren().add(linchart);
        pane.getChildren().add(sc);
        pane.setPrefSize(400, 250);
        pane.setMinSize(400, 200);
        pane.setMaxSize(400, 200);
        pane.setAlignment(Pos.TOP_LEFT);

        StackPane pane2=new StackPane();
        pane2.getChildren().add(linchart2);
        pane2.getChildren().add(sc_2);
        pane2.setPrefSize(400, 250);
        pane2.setMinSize(400, 200);
        pane2.setMaxSize(400, 200);
        pane2.setAlignment(Pos.TOP_LEFT);

        StackPane pane3=new StackPane();
        pane3.getChildren().add(linchart3);
        pane3.getChildren().add(sc_3);
        pane3.setPrefSize(400, 250);
        pane3.setMinSize(400, 200);
        pane3.setMaxSize(400, 200);
        pane3.setAlignment(Pos.TOP_LEFT);


        Label lbus1= new Label("Bus 1");
        lbus1.setFont(new Font("Segoe UI Semibold", 20));
        lbus1.setTextFill(Color.web("#000000"));
        lbus1.setPadding(new Insets(10));

        Label lbus2= new Label("Bus 2");
        lbus2.setFont(new Font("Segoe UI Semibold", 20));
        lbus2.setTextFill(Color.web("#000000"));
        lbus2.setPadding(new Insets(10));

        Label lbus3= new Label("Bus 3");
        lbus3.setFont(new Font("Segoe UI Semibold", 20));
        lbus3.setTextFill(Color.web("#000000"));
        lbus3.setPadding(new Insets(10));

        VBox v1 = new VBox(lbus1,labelBus1,labelDelayBus1);
        v1.setAlignment(Pos.TOP_CENTER);
        v1.setSpacing(2);
        v1.setMinWidth(300);
        v1.setMaxWidth(300);
        v1.setMinHeight(210);
        v1.setMaxHeight(210);
        v1.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");

        VBox v2 = new VBox(lbus2,labelBus2,labelDelayBus2);
        v2.setAlignment(Pos.TOP_CENTER);
        v2.setSpacing(2);
        v2.setMinWidth(300);
        v2.setMaxWidth(300);
        v2.setMinHeight(210);
        v2.setMaxHeight(210);
        v2.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");

        VBox v3 = new VBox(lbus3,labelBus3,labelDelayBus3);
        v3.setAlignment(Pos.TOP_CENTER);
        v3.setSpacing(2);
        v3.setMinWidth(300);
        v3.setMaxWidth(300);
        v3.setMinHeight(210);
        v3.setMaxHeight(210);
        v3.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");


        HBox hboxBus = new HBox(pane);
        hboxBus.setAlignment(Pos.TOP_LEFT);
        hboxBus.setSpacing(10);
        hboxBus.setMinWidth(500);
        hboxBus.setMaxWidth(520);
        hboxBus.setMinHeight(210);
        hboxBus.setMaxHeight(210);
        hboxBus.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");

        HBox hboxBus2 = new HBox(pane2);
        hboxBus2.setAlignment(Pos.TOP_LEFT);
        hboxBus2.setSpacing(10);
        hboxBus2.setMinWidth(500);
        hboxBus2.setMaxWidth(520);
        hboxBus2.setMinHeight(210);
        hboxBus2.setMaxHeight(210);
        hboxBus2.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");

        HBox hboxBus3 = new HBox(pane3);
        hboxBus3.setAlignment(Pos.TOP_LEFT);
        hboxBus3.setSpacing(10);
        hboxBus3.setMinWidth(500);
        hboxBus3.setMaxWidth(520);
        hboxBus3.setMinHeight(210);
        hboxBus3.setMaxHeight(210);
        hboxBus3.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");

        VBox Vcenter = new VBox(hboxBus,hboxBus2,hboxBus3);
        Vcenter.setAlignment(Pos.TOP_LEFT);
        Vcenter.setSpacing(5);

        hBoxPosBuses = new HBox(labelPosBus1, labelPosBus2, labelPosBus3);
        hBoxPosBuses.setAlignment(Pos.CENTER);
        hBoxPosBuses.setSpacing(100);

        VBox vBoxGlobal = new VBox( v1,v2,v3);
        vBoxGlobal.setAlignment(Pos.TOP_LEFT);
        vBoxGlobal.setSpacing(5);
        root.setRight(vboxBlocs);
        root.setLeft(vBoxGlobal);
        root.setCenter(Vcenter);
        root.setPrefSize(400, 400);
        root.setStyle("-fx-background-color: linear-gradient(from 30% 30% to 100% 100%, #35589A, #FFF1BD);");
        root.setMargin(vBoxGlobal, new Insets(3));
        root.setMargin(Vcenter, new Insets(3));
        root.setMargin(vboxBlocs, new Insets(3));

        ScrollPane scroller = new ScrollPane(root);
        scroller.setFitToWidth(true);


        layout = new VBox();
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 300);
        Scene scene2 = new Scene(root,1350,700);
        scene2.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Label busStat= new Label("Bus Authentication state:");
        Bus1Auth = new Label("Bus 1 : not connected");
        Bus2Auth = new Label("Bus 2 : not connected");
        Bus3Auth = new Label("Bus 3 : not connected");

        Label stationStat= new Label("Stations Authentication state:");
        StationsBachAuth = new Label("Stations Bach Djerah : not connected");
        StationsBabAuth= new Label("Stations Bab Ezzouar : not connected");
        StationsDarAuth = new Label("Stations Dar El Beida : not connected");
        StationsHammediAuth = new Label("Stations Hammedi : not connected");
        StationsHarrachAuth = new Label("Stations Harrach : not connected");
        buttonAuth = new Button("OK");
        buttonAuth.setVisible(false);
        buttonAuth.setOnAction(e -> stage.setScene(scene2));
        layout.getChildren().addAll(busStat,Bus1Auth,Bus2Auth,Bus3Auth,stationStat,StationsBachAuth,StationsBabAuth,StationsDarAuth,StationsHarrachAuth,StationsHammediAuth, buttonAuth);



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