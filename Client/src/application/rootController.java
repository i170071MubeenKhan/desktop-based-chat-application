package application;
import javafx.application.Platform;
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

public class rootController {

	public Main mApp;
	@FXML
	private Button exitButton;
	@FXML
	public void exitApplication(ActionEvent event) {
	    //((Stage)rootPane.getScene().getWindow()).close();
	    //(mApp.getPrimaryStage().getScene().getWindow()).onCloseRequestProperty();
	   // System.out.println("Hello closing the file");
	   // Platform.exit();


	}
	@FXML
	public Stage close=mApp.getPrimaryStage();
   // mApp.setOnCloseRequest( event -> {System.out.println("Closing Stage");});

    public void setMain(Main ma){
	    	this.mApp=ma;
	    }


}
