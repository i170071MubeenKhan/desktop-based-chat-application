package application;

import java.io.IOException;
import javafx.event.*;
import javafx.event.EventHandler;
import Client.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

//753
//530

public class Main extends Application {

	private Client client;
	private Stage primaryStage;
	public static BorderPane root;

	public Main() {
		System.out.println("Const");
		client = new Client();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			primaryStage.setTitle("Messenger");
			mainBorder(primaryStage);
			login();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				public void handle(WindowEvent we) {
					System.out.println("Stage is closing");
					System.out.println(client.sendingBuffer.messageType);
					client.SendSuicideNote();
					// primaryStage.hide();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mainBorder(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("root.fxml"));
			this.root = new BorderPane();
			root = (BorderPane) loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Login.fxml"));
			loginController lc = new loginController();
			lc.setMain(this);
			loader.setController(lc);
			AnchorPane Login = (AnchorPane) loader.load();
			// AnchorPane.setStyle()

			lc.setClient(client);
			root.setCenter(Login);
			System.out.println(client.getClientName());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void mainScreen() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("MainScreen.fxml"));
			AnchorPane Login = (AnchorPane) loader.load();
			root.setCenter(Login);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chat() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("test.fxml"));
			AnchorPane Login = (AnchorPane) loader.load();
			root.setCenter(Login);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static BorderPane getRoot() {
		return root;
	}

	public Client getClient() {
		return client;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
