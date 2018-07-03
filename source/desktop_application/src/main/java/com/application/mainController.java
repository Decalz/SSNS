package com.application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.application.bluetooth.Application;
import com.application.bluetooth.DbSave;
import com.application.bluetooth.ProcessMessage;
import com.application.bluetooth.Sensor;
import com.application.bluetooth.Server;
import com.application.bluetooth.Utils;
import com.application.util.FallNotificationService;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class mainController {


	Server sr = Server.getInstance();
	
	public static StringProperty Scan = new SimpleStringProperty(null);
	public static StringProperty status = new SimpleStringProperty(Server.STATUS);
	
    
	
    /**
     * @author Elis
     * 
     *   All IDs ofr mainView elements
     * 
     * TODO: From now one all new elements that you create for mainView 
     *       please add the IDs in this section
     *       
     *       Rule 1: always add a new ID at the end
     * */
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    static NumberAxis accel_accel;
    
    @FXML
    static CategoryAxis accel_time;
    
    @FXML
    static LineChart<Number, String> Accelerometer;

    @FXML
    static CategoryAxis gyro_time;
    
    @FXML
    static NumberAxis gyro_accel;
    
    @FXML
    static LineChart<Number, String> Gyroscope;


    @FXML
    private Button btnClean;   
    
    @FXML
    private MenuItem btnClose;
    
    @FXML
    private Button btnConnect;

    @FXML
    private Button btnDisconnect;
    
    @FXML
    private MenuItem btnSettings;
    
    @FXML
    private Button btnStart;
    
    @FXML
    private Label btnStatus;
    
    @FXML
    private Button btnStop;

    @FXML
    private MenuItem btnUserInfo;
    
    @FXML
    private Label lblConnecting;
    
    @FXML
    private Label lblFallDet;
    
    @FXML
    private Label lblHelpReq;
    
    @FXML
    private Button btnScan;
    
    @FXML
    private ChoiceBox<String> ddlAvSensors;
    
    /* End of IDs of mainView */
    
    /**
     * @author Elis
     * 
     * Actions of main View Buttons f
     * 
     * TODO: From now one all actions that you create for this controller
     *       please add them in this section
     *       
     *       Apply Rule 1
     * */
    MainAppliction main = new MainAppliction();
    
    //LineChart variable initialization
    static int xSeriesData = 0;
    
    static XYChart.Series<Number, String> accel1_series = new XYChart.Series<>();
    
    static XYChart.Series<Number, String> accel2_series = new XYChart.Series<>();
    
    
    static XYChart.Series<Number, String> gyro1_series = new XYChart.Series<>();
    
    
    static XYChart.Series<Number, String> gyro2_series = new XYChart.Series<>();
    
    static ConcurrentLinkedQueue<Number> Q1 = new ConcurrentLinkedQueue<>();    
    static ConcurrentLinkedQueue<Number> Q2 = new ConcurrentLinkedQueue<>();    
    static ConcurrentLinkedQueue<Number> Q3 = new ConcurrentLinkedQueue<>();    
    static ConcurrentLinkedQueue<Number> Q4 = new ConcurrentLinkedQueue<>();
    
    static void setVars() {
    	
    	accel_accel = new NumberAxis(0, 100, 10);
        accel_time = new CategoryAxis();
        
        gyro_accel = new NumberAxis(0, 100, 10);
        gyro_time = new CategoryAxis();
        
        accel1_series.setName("Accel1");
        accel2_series.setName("Accel2");
        gyro1_series.setName("Accel1");
        gyro2_series.setName("Accel1");
        
        Accelerometer = new LineChart<Number, String>(accel_accel, accel_time) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item) {
            }
        };

        Gyroscope = new LineChart<Number, String>(gyro_accel, gyro_time) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item) {
            }
        };
        
        Accelerometer.getData().addAll(accel1_series,accel2_series);
        Gyroscope.getData().addAll(gyro1_series,gyro2_series);
        }

    
    public void setLblHelpReqColor(String color) {
    	 lblHelpReq.setTextFill(Color.web(color));
    }
   
    public void setLblFallDetColor(String color) {
   	 	lblFallDet.setTextFill(Color.web(color));
    }
    
    @FXML
    void Connect(ActionEvent event) {
    	
    	String dev = this.ddlAvSensors.getValue();   	
    	sr.connectTo(Utils.reverseHexString(dev), this);
    	
    }
    
    @FXML
    void Disconnect(ActionEvent event) {
    	sr.WriteToPort("01030C00");
    	//sr.AutoDiscover();
    	//new Application();
    
    }
  
    
    @FXML
    void Clean(ActionEvent event) {
    	 //sr.WriteToPort("01030C00");
     //   new DbSave();
    }
    
    @FXML
    void CloseApp(ActionEvent event) {
   
    }    
    
    @FXML
    void Settings(ActionEvent event) throws Exception {
    	
    	//MainAppliction main = new MainAppliction();
    	main.showSettings();
    }

    @FXML
    void StartReceiving(ActionEvent event) {  	
    	sr.readData();
    	prepareTimeline();
    	// new Application();
    }

    @FXML
    void StopReceiving(ActionEvent event) {
    	
    	for(int i=1;i<100;i++)
    	{
    		Q1.add(Math.random());
    		}
    }
       
    @FXML
    void Userinfo(ActionEvent event) throws Exception {
    	//MainAppliction file = new MainAppliction();
    	main.UserInfo();
    }
    
    @FXML
    void ScanForBluetoothDevices(ActionEvent event) {
    	Server.STATUS="Scanning...";
          	Scan.setValue(null);
          	sr.Scan(this);
    
    }
    
    
    /**End of Actions of main View Buttons */
    
    @FXML
    void initialize() {
    	
    	// set a reference to this controller so that the FallNotificationService can change the colour of labels
    	FallNotificationService.setMain(this);
		sr.DevInit(this);
				
		this.ddlAvSensors.getItems().add("Select a Sensor");
		this.ddlAvSensors.getSelectionModel().selectFirst();
//		this.ddlAvSensors.getSelectionModel().select(1);
		this.lblConnecting.textProperty().bind(status);
        this.btnConnect.disableProperty().bind(BooleanExpression.booleanExpression(Scan.isEmpty()));
        this.btnDisconnect.disableProperty().bind(BooleanExpression.booleanExpression(Scan.isEmpty()));
		//this.btnConnect.disableProperty().bind(observable);
        assert btnConnect != null : "fx:id=\"btnConnect\" was not injected: check your FXML file 'main.fxml'.";       
        assert Accelerometer != null : "fx:id=\"Accelerometer\" was not injected: check your FXML file 'main.fxml'.";
        assert Gyroscope != null : "fx:id=\"Gyroscope\" was not injected: check your FXML file 'main.fxml'.";
        assert btnClean != null : "fx:id=\"btnClean\" was not injected: check your FXML file 'main.fxml'.";
        assert btnDisconnect != null : "fx:id=\"btnDisconnect\" was not injected: check your FXML file 'main.fxml'.";
        assert btnStart != null : "fx:id=\"btnStart\" was not injected: check your FXML file 'main.fxml'.";
        assert btnStatus != null : "fx:id=\"btnStatus\" was not injected: check your FXML file 'main.fxml'.";
        assert btnStop != null : "fx:id=\"btnStop\" was not injected: check your FXML file 'main.fxml'.";
        assert lblConnecting != null : "fx:id=\"lblConnecting\" was not injected: check your FXML file 'main.fxml'.";
        assert lblFallDet != null : "fx:id=\"lblFallDet\" was not injected: check your FXML file 'main.fxml'.";
        assert lblHelpReq != null : "fx:id=\"lblHelpReq\" was not injected: check your FXML file 'main.fxml'.";
        
        setVars();
        prepareTimeline();
        
    }
    
  //Timeline gets called in the JavaFX Main thread
    void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addData();
            }
        }.start();
    }
    
  //use given data to populate the charts
    private void addData() {
    	for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (Q1.isEmpty()) break;
    		accel1_series.getData().add(new XYChart.Data<>(Q1.remove(), ""+ xSeriesData++));
            accel2_series.getData().add(new XYChart.Data<>(Q2.remove(), ""+ xSeriesData++));
            gyro1_series.getData().add(new XYChart.Data<>(Q3.remove(), ""+ xSeriesData++));
            gyro2_series.getData().add(new XYChart.Data<>(Q4.remove(), ""+ xSeriesData++));
        }
    	
    	if (accel1_series.getData().size() > 100) {
            accel1_series.getData().remove(0, accel1_series.getData().size() - 100);
        }
        if (accel2_series.getData().size() > 100) {
            accel2_series.getData().remove(0, accel2_series.getData().size() - 100);
        }
        if (gyro1_series.getData().size() > 100) {
            gyro1_series.getData().remove(0, gyro1_series.getData().size() - 100);
        }
        
        if (gyro2_series.getData().size() > 100) {
            gyro2_series.getData().remove(0, gyro2_series.getData().size() - 100);
        }
        
        // update
        accel_accel.setLowerBound(xSeriesData - 100);
        accel_accel.setUpperBound(xSeriesData - 1);
        gyro_accel.setLowerBound(xSeriesData - 100);
        gyro_accel.setUpperBound(xSeriesData - 1);
    }
    
    /**
     * @author Elis
     * 
     * TODO: Please Use this section t add other functions that you will need for help 
     * 
     *   Apply Rule1
     
     * */
    public Button getbtnConnect()
    {
    	return this.btnConnect;
    }
    public Button getbtnDisconnect()
    {
    	return this.btnDisconnect;
    }

    public void showSensorsFound()
    {
    	this.ddlAvSensors.getItems().addAll(sr.devicesFound);
    }
    
    public static void StatusChanger() {

    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	
            	while(Server.LUNCHPAD_READY)
            	{
            		Thread.sleep(10);
              	  
             	   Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                        	status.setValue(String.valueOf(Server.STATUS));
                        	
                        	if(Server.SCAN_COMPLETE)
                        	{
                        		Scan.setValue("1");
                        	}
                        	
                        }
                    });
             	   
            	}
            	return null;
              
            }
         };
         Thread th = new Thread(task);
         th.setDaemon(true);
         th.start();
      
    }
}
