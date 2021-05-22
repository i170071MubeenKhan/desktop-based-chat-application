package application;


import Client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class popUpInComingController  {

	public Client client;
    @FXML
    public Label CallerName;

    @FXML
    public void accept(MouseEvent e){
    	System.out.println("accept the call");
    	client.incomingCallPicked=true;
    	client.incomingCall=false;
    }
    @FXML
    public void decline(MouseEvent e){
    	System.out.println("decline the call");
    	client.EndAudioCall();
    	 Platform.exit();
    	//client.incomingCallPicked=false;
    	//client.incomingCall=false;
    }





}