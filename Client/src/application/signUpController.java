package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.util.ArrayList;

import Client.Client;

//import java.awt.event.ActionEvent;

import application.Main;
//import application.Person;
import javafx.collections.*;

public class signUpController<T> {

	private Main mApp;
	private Client client;
	@FXML
    private TextField name;
	@FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Label error;

	@FXML
    private Button signUpPage;

    @FXML
    private void initialize() {
    	System.out.println("Hlelo");
    }
    @FXML
    private void authentication(ActionEvent event){
    	//mApp.getClient().SendSuicideNote();
    	try{
    		//authentication
    		boolean isFieldEmpty=false;
    		ArrayList<String> SignUpCredentials= new ArrayList<String>();
    		if((email.getText().equals(""))|| (password.getText().equals("")) || (name.getText().equals(""))){
    			System.out.println("Empty fields in signup page");
    			isFieldEmpty=true;
    		}
    		if(isFieldEmpty==false){//not empty field
    			SignUpCredentials.add(name.getText());
        		SignUpCredentials.add(email.getText());
        		SignUpCredentials.add(password.getText());
        		int  response=client.SignUp(SignUpCredentials);
        		if (response==1){//success
        			System.out.println("Email and password both are correct");
        			FXMLLoader loader=new FXMLLoader();
            		loader.setLocation(Main.class.getResource("MainScreen.fxml"));
            		//MainScreenController msc=new MainScreenController();
            		universalController<T> msc=new universalController<>();
            		msc.setMain(mApp);
            		msc.setClient(mApp.getClient());
            		loader.setController(msc);
            		AnchorPane signUp=(AnchorPane) loader.load();
        			msc.setClient(client);
        			msc.setName();
        			mApp.root.setCenter(signUp);

        		}else{//failure
        			System.out.println("Email is already in use or there is some other error");
        			error.setText("Email is already in use by some other user");
        			name.setText("");
        			password.setText("");
        			email.setText("");

        			//display error screen
        		}
    		}else{//empty field
    			System.out.println("Please enter the required credentials");
    			error.setText("Please enter required credentials");
    			name.setText("");
    			password.setText("");
    			email.setText("");

    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}


    }
    public void setClient(Client client){
    	this.client=client;
    	System.out.println("SinguP "+ client.socket);
    }
    public void setMain(Main app){
    	this.mApp=app;
    }

}


