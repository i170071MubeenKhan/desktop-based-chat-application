package application;


import Client.Client;
import Shared.PersonalChat_Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class popUpCallingController  {

	public Client client;
	public int receiverChatIndex;
    @FXML
    public Label receiverName;
    public String name;

    @FXML
    public Label setResponse;
    public void initialize(){
    	//setName(name);
    	;
    	new Thread() {
    		int a=-1;
            // runnable for that thread
            public void run() {
            	System.out.println("running thread");
            	a=client.SendAudioCallRequest(((PersonalChat_Client)(client.user.allChats.get(receiverChatIndex))).getPersonalChat_Client());
            	if (a==1){//accept
            		setResponse.setText("ongoing call");
            	}else{//reject
            		setResponse.setText("call ended");
            	}
            }
        }.start();

    }

    public void setName(String a){
    	receiverName.setText(a);
    }

    public void setClient(Client client){
    	this.client=client;
    }
    public void checkCall(){
    	while (true){

    	}
    }

}