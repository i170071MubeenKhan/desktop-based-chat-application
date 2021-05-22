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

public class loginController<T> {

	private Main mApp;

	private Client client;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Label error;
	@FXML
    private Button nextPage;
	@FXML
    private Button signUpPage;

    @FXML
    private void initialize() {
    	System.out.println("Hlelo");
    	
    }
    @FXML
    private void authentication(ActionEvent event){
    	try{
    		//authentication
    		System.out.println("emial is: "+ email.getText() + "password is" + password.getText());
    		ArrayList<String> loginCredentials= new ArrayList<String>();
    		boolean isFieldEmpty=false;
    		if((email.getText().equals(""))|| (password.getText().equals(""))){
    			System.out.println("Empty fields in login page");
    			isFieldEmpty=true;
    		}
    		if (isFieldEmpty==false){//if field is not empty
				loginCredentials.add(email.getText());
				loginCredentials.add(password.getText());
				int response = client.Login(loginCredentials);

				if (response == 1) {// success
					System.out.println("Email and password both are correct");
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(Main.class.getResource("MainScreen.fxml"));
					universalController<T>msc =new universalController<>();
				//	MainScreenController msc = new MainScreenController();
					msc.setMain(mApp);
					msc.setClient(mApp.getClient());
					loader.setController(msc);
					AnchorPane Login = (AnchorPane) loader.load();
					msc.setClient(client);
					msc.setName();
					mApp.root.setCenter(Login);
				} else {// failure
					System.out.println("Email or password is incorrect");
					// display error screen
					email.setText("");
					password.setText("");
					error.setText("Email or password is incorrect");
					System.out.println(client.getClientName());
				}
    		}else{//field is empty
    			System.out.println("Please enter values into the email and password field");
				// display error screen
				error.setText("Please enter required credentials");
				email.setText("");
				password.setText("");
    		}

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void signUpPage(ActionEvent event){
    	try{

       	//	FXMLLoader loader=
    		FXMLLoader loader=new FXMLLoader();
    		loader.setLocation(Main.class.getResource("SignUp.fxml"));
    		signUpController suc=new signUpController();
    		//universalController<T> suc=new universalController<>();
    		suc.setMain(mApp);
    		suc.setClient(mApp.getClient());
			loader.setController(suc);
			AnchorPane signUp=(AnchorPane) loader.load();
			suc.setClient(client);
			mApp.root.setCenter(signUp);

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void setClient(Client client){
    	this.client=client;
    	System.out.println("Login page " + client.socket);
    }

    public void setMain(Main ma){
    	this.mApp=ma;
    }

}


