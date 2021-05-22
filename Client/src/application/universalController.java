package application;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Client.Client;
import Shared.Chat;
import Shared.GroupChat_Client;
import Shared.Message_Client;
import Shared.PersonalChat_Client;
import application.Main;
import javafx.application.Platform;
import javafx.collections.*;

public class universalController<T> implements Initializable{

	private Main mApp;
	public Client client;
	@FXML
	private Label clientName;
	@FXML
	private Label clientStatus;
	@FXML
	private TextField enterFriendsEmail;
	@FXML
	private Label errorWhileAddingContact;
	@FXML
	private Button showChats;
	@FXML
	private Button showContacts;
	@FXML
	private Button addContact;
	@FXML
	private Button sendAddContactRequest;
	@FXML
	private Label receiverName;
	@FXML
	private Label receiverStatus;
	@FXML
	//messages
	private ListView messageView;
	@FXML
	private ObservableList<Message_Client> messageList;
	@FXML
	//chats list
	private ListView infoView;
	@FXML
	private ObservableList<Chat> infoList;//chats list
	//Contact list
	@FXML
	private ListView<T> Chats;
	@FXML
	private ObservableList<PersonalChat_Client.ChatParticipant> contactList;
	@FXML
	private Button sendMessage;
	@FXML
	private TextArea enterMessage;
	@FXML
	private ImageView makeCall;
	//groupchats list+messages
	@FXML
	private Label errorWhileAddingGroupMembers;
	@FXML
	private Button addNewGroup;
	@FXML
	private Button showGroupChats;
	@FXML
	private Button doneAddingMembers;
	@FXML
	private ArrayList<String> membersList;
	@FXML
	private TextField groupName;
	@FXML
	private TextField member1;
	@FXML
	private TextField member2;
	@FXML
	private MenuItem lan1;
	@FXML
	private MenuItem lan2;
	@FXML
	private MenuItem lan3;
	@FXML
	private MenuItem lan4;
	@FXML
	private MenuItem lan5;
	@FXML
	private ImageView sendAttachment;
	@FXML
	private ImageView image1;
	@FXML
	private ImageView image2;
	@FXML
	private ImageView image3;
	@FXML
	private ImageView image4;

	@FXML
	public void pressImage1(MouseEvent event){

	}
	@FXML
	public void pressImage2(MouseEvent event){

	}
	@FXML
	public void pressImage3(MouseEvent event){

	}
	@FXML
	public void pressImage4(MouseEvent event){

	}
	@FXML
	public void pressImage5(MouseEvent event){

	}


	public int receiverChatIndex = -1;// WILL BE USED TO SAVE THE INDEX OF
										// SELECTED CHAT


	public universalController() {
		// TODO Auto-generated constructor stub
	}

	public universalController(Client c) {
		// TODO Auto-generated constructor stub
		this.client = c;
	}
/*
	public void setReceiverID(int a) {
		this.receiverIndex = a;
	}*/

	@FXML
	public void setLanguage1(ActionEvent event){//english
		System.out.println("language 1 is selected");
		client.ChangeGroupChatLanguage((GroupChat_Client)client.user.allChats.get(receiverChatIndex), "en");
		showMessage(receiverChatIndex);
	}
	@FXML
	public void setLanguage2(ActionEvent event){//spansih
		System.out.println("language 2 is selected");
		client.ChangeGroupChatLanguage((GroupChat_Client)client.user.allChats.get(receiverChatIndex), "es");
		showMessage(receiverChatIndex);
	}
	@FXML
	public void setLanguage3(ActionEvent event){//dutch
		System.out.println("language 3 is selected");
		client.ChangeGroupChatLanguage((GroupChat_Client)client.user.allChats.get(receiverChatIndex), "nl");
		showMessage(receiverChatIndex);
	}
	@FXML
	public void setLanguage4(ActionEvent event){//french
		System.out.println("language 4 is selected");
		client.ChangeGroupChatLanguage((GroupChat_Client)client.user.allChats.get(receiverChatIndex), "fr");
		showMessage(receiverChatIndex);

	}
	@FXML
	public void setLanguage5(ActionEvent event){//swedish
		System.out.println("language 5 is selected");
		client.ChangeGroupChatLanguage((GroupChat_Client)client.user.allChats.get(receiverChatIndex), "sv");
		showMessage(receiverChatIndex);

	}

	@FXML
	public void addNewGroup(ActionEvent event){
		System.out.println("Adding new group");
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("MainScreenAddGroupChat.fxml"));
			universalController uc = new universalController();
			uc.setMain(mApp);
			uc.setClient(mApp.getClient());
			loader.setController(uc);
			AnchorPane mainPage = (AnchorPane) loader.load();
			uc.setClient(client);
			uc.setName();
			mApp.root.setCenter(mainPage);
			System.out.println("Add new group");

		}catch(Exception e){
			e.printStackTrace();
		}


	}
	@FXML
	public void sendAttachment(MouseEvent event){
		System.out.println("Sending attachement");
		try{
			FileChooser fileChooser = new FileChooser();
		    File selectedFile = fileChooser.showOpenDialog(mApp.getPrimaryStage());
		    System.out.println("The name of the file is " + selectedFile.toString());
		    if (selectedFile!=null){
		    	client.SendAttachmentMessage(client.user.allChats.get(receiverChatIndex), selectedFile);
		    }

		}catch(Exception e){
			e.printStackTrace();
		}
		showMessage(receiverChatIndex);

	}

	public void displayPopUp() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("popUpIncomingAudioCall.fxml"));
			popUpInComingController up = new popUpInComingController();
			loader.setController(up);
			up.client=client;
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(mApp.getPrimaryStage());
			AnchorPane popUp = (AnchorPane) loader.load();
			Scene scene = new Scene(popUp);
			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void call(MouseEvent event){
		System.out.println("The reciver id is" + receiverChatIndex);
		String name=((PersonalChat_Client)(client.user.allChats.get(receiverChatIndex))).getChatName();
		System.out.println("the name is " + name);
		popUpCallingController up	= new popUpCallingController();


		try {
			FXMLLoader loader = new FXMLLoader();
			if (client.user.allChats.get(receiverChatIndex).getOnlineStatus()==true){
				loader.setLocation(Main.class.getResource("popUpCalling.fxml"));
			}else{
				loader.setLocation(Main.class.getResource("popUpCallingError.fxml"));
			}


			System.out.println("Status of client is " +client.user.allChats.get(receiverChatIndex).getOnlineStatus());

			up.name=name;
			loader.setController(up);
			up.setClient(client);
			up.receiverChatIndex=receiverChatIndex;
			//up.receiverName.setText(name);
			//up.setName(name);
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(mApp.getPrimaryStage());
			AnchorPane popUp = (AnchorPane) loader.load();
			Scene scene = new Scene(popUp);
			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		@FXML
	public void handleMouseClick(MouseEvent arg0) {
		System.out.println("Infoview is clicked");
		if (infoView.getSelectionModel() == null) {
			System.out.println("InfoView selection model is null");
		}
		if (infoView.getSelectionModel().getSelectedItem() == null) {
			System.out.println("seleted item in infoView is empty");

		} else {
			System.out.println("inside handleMouseClick of infoView" + infoView.getSelectionModel().getSelectedItem());
			System.out.println("inside handleMouseClick of infoView" + infoView.getSelectionModel().getSelectedIndex());
			if (client.user.allChats.get(infoView.getSelectionModel().getSelectedIndex()).chatType==0){//personal
				System.out.println("Personal chat");
				System.out.println("name of the chat is" + ((PersonalChat_Client) (client.user.allChats
						.get(infoView.getSelectionModel().getSelectedIndex()))).getChatName());

			}else{
				System.out.println("name of the chat is" + ((GroupChat_Client) (client.user.allChats
						.get(infoView.getSelectionModel().getSelectedIndex()))).getChatName());

			}
			if (client.user.allChats.get(infoView.getSelectionModel().getSelectedIndex()).chatType==0){//personal
				openChat(infoView.getSelectionModel().getSelectedIndex());
			}else{
				openGroupChat(infoView.getSelectionModel().getSelectedIndex());
			}


		}
		infoView.getSelectionModel().clearSelection();
	}

	public void openChat(int t) {
		System.out.println("The index of the selected chat is" + t);
		if (t > -1) {
			try {
				System.out.println("The id of the receiver is" + t);

				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("chat.fxml"));
				universalController cc = new universalController();
				cc.setMain(mApp);
				cc.setClient(mApp.getClient());
				// cc.setReceiverID(t);
				cc.receiverChatIndex = t;
				loader.setController(cc);
				AnchorPane showChat = (AnchorPane) loader.load();
				mApp.root.setCenter(showChat);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("error");
		}
		setReceiverName();
		showMessage(receiverChatIndex);
	}
	public void openGroupChat(int t) {
		System.out.println("The index of the selected group chat is" + t);
		if (t > -1) {
			try {
				System.out.println("The id of the receiver is" + t);

				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("groupchat.fxml"));
				universalController cc = new universalController();
				cc.setMain(mApp);
				cc.setClient(mApp.getClient());
				// cc.setReceiverID(t);
				cc.receiverChatIndex = t;
				loader.setController(cc);
				AnchorPane showChat = (AnchorPane) loader.load();
				mApp.root.setCenter(showChat);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("error");
		}

		setReceiverName();
		showMessage(receiverChatIndex);
	}


	@Override
    public void initialize(URL location, ResourceBundle resources) {
		System.out.println("hello insid intialize");

		infoList = (FXCollections.observableArrayList(client.user.allChats));
		infoView.setItems(infoList);
		infoView.setCellFactory(infoListView -> new listCellController());
    	setName();
    }

	@FXML
	public void DisplayPersonalChatList() {
		if (client.user.allChats.size() != 0) {
			System.out.println("inside listview");

			infoList = (FXCollections.observableArrayList(client.user.allChats));
			infoView.setItems(infoList);
			infoView.setCellFactory(infoListView -> new listCellController());
		} else {
			System.out.println("No Chat exist in infoView");
		}
	}
	@SuppressWarnings("unchecked")
	@FXML
	public void DisplayGroupChatList() {
		if (client.user.allChats.size() != 0) {
			ObservableList<Chat> groupChat=FXCollections.observableArrayList();

			for (int i=0 ; i<client.user.allChats.size() ; i++){
				if (client.user.allChats.get(i).chatType==1){ //adding group chats into Observable list
					groupChat.add(client.user.allChats.get(i));
				}
			}
			System.out.println("inside group chat listview");
			infoList = groupChat;
			infoView.setItems(infoList);
			infoView.setCellFactory(infoListView -> new listCellController());
			for (int i = 0; i < infoList.size(); i++) {
				System.out.println("Displaying chat list in infoView");
				System.out.println(infoList.get(i));
			}
		} else {
			System.out.println("No Chat exist in infoView");
		}
	}

	@FXML
	public void DisplayContactList() {
		ArrayList<Chat> allContacts = new ArrayList<Chat>();
		String tmp;
		if (client.user.allChats.size() != 0) {
			for (int i = 0; i < client.user.allChats.size(); i++) {
				if (client.user.allChats.get(i).chatType == 0) {
					allContacts.add(client.user.allChats.get(i));
				}
			}
		} else {
			System.out.println("No contacts exist in infoView");
		}
		infoList = FXCollections.observableArrayList(allContacts);
		infoView.setItems(infoList);
		infoView.setCellFactory(contactListView -> new listCellController());
	}
	@FXML
	public void addGroupMembers(ActionEvent event){ //adding new group
		System.out.println("adding new group");
		String cname=groupName.getText();
		String m1=member1.getText();
		String m2=member2.getText();
		System.out.println(cname + m1+m2);
		ArrayList<String> tmp=new ArrayList<String>();
		if (cname.equals("") || m1.equals("") || m2.equals("") || m1.equals(client.getClientName()) || m2.equals(client.getClientName()) ){
			System.out.println("Please enter required/valid credentials");
			errorWhileAddingGroupMembers.setText("Please enter required/Valid credentials");
		}else{
			tmp.add(m1);
			tmp.add(m2);
			int tmp1=client.SendGroupChatRequest(cname, tmp);
			if(tmp1==1){
				DisplayGroupChatList();
			}else{
				errorWhileAddingGroupMembers.setText("Invalid Information");
			}
		}
	}

	@FXML
	public void exitApplication(ActionEvent event) {
		mApp.getClient().SendSuicideNote();
		System.out.println("Closing the application using Universal Controller");
	}

	@FXML
	public void addNewContact(MouseEvent event) {
		System.out.println("Add new contact using universalController");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("MainScreenAddContact.fxml"));
			universalController ucc = new universalController();
			ucc.setMain(mApp);
			ucc.setClient(mApp.getClient());
			loader.setController(ucc);
			AnchorPane AddContact = (AnchorPane) loader.load();
			ucc.setClient(client);
			ucc.setName();
			mApp.root.setCenter(AddContact);
			// System.out.println("Add new contact");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void sendMessage() {
		System.out.println("Sending message");
		System.out.println("The value of receiverChatIndex is " + receiverChatIndex);
		if (enterMessage.getText().equals("")) {
			System.out.println("message text area is empty");
		} else {
			client.SendTextMessage(client.user.allChats.get(receiverChatIndex), enterMessage.getText());
			enterMessage.setText("");
			showMessage(receiverChatIndex);
		}

	}

	@SuppressWarnings("unchecked")
	public void showMessage(int chatIndex) {//
		if (chatIndex == -1) {
			System.out.println("index is -1");
		} else {
			if (client.user.allChats.get(chatIndex).chatType==0){
				if (((PersonalChat_Client)client.user.allChats.get(chatIndex)).allMessages.size() != 0) {
					System.out.println("inside messageview");
					messageList = (FXCollections
							.observableArrayList(((PersonalChat_Client)client.user.allChats.get(receiverChatIndex)).allMessages));
					System.out.println("------------------------The size of the message list is" + messageList.size());
					messageView.setItems(messageList);
					messageView.setCellFactory(messageListView -> new messageCellController(client.user,receiverChatIndex));
					messageView.scrollTo(1000);

				} else {
					System.out.println("The index of the client chat is " + receiverChatIndex);
					System.out.println("Message list is empty");
				}
			}
			else{
				if (((GroupChat_Client)client.user.allChats.get(chatIndex)).allMessages.size() != 0) {
					System.out.println("inside messageview of group chat ");
					messageList = (FXCollections
							.observableArrayList(((GroupChat_Client)client.user.allChats.get(receiverChatIndex)).allMessages));
					messageView.setItems(messageList);

					messageView.setCellFactory(messageListView -> new messageCellController(client.user,receiverChatIndex));
					for (int i = 0; i < messageList.size(); i++) {
						System.out.println("getting messages in showMessgae");
						System.out.println(messageList.get(i).getContent());
					}
				} else {
					System.out.println("The index of the client chat is " + receiverChatIndex);
					System.out.println("Message list is empty");
				}
			}
		}

	}

	@FXML
	public void displayChats(ActionEvent event){

	}
	@FXML
	public void showContacts(ActionEvent event) {
		System.out.println("show contacts");
		DisplayContactList();
	}
	@FXML
	public void showGroupChats(ActionEvent event){
		System.out.println("showing group chat list ");
		DisplayGroupChatList();
	}

	public void setClient(Client c) {
		// Add observable list data to the table
		this.client = c;
		if (c == null) {
			System.out.println("Empty");
		}
		System.out.println("msc" + client.socket);
		client.setController(this);
		// name=client.getClientName();
	}

	public void setMain(Main app) {
		this.mApp = app;
	}

	public void setName() {
		clientName.setText(client.getClientName());
		System.out.println(client.getClientName());
		setReceiverName();
	}

	public void setReceiverName() {
		System.out.println("inside setReceiverName");
		if (receiverChatIndex != -1) {

			if (client.user.allChats.get(receiverChatIndex).chatType==0){
				System.out.println("the index of the chat is " + ((PersonalChat_Client)client.user.allChats.get(receiverChatIndex)).getChatName());
				receiverName.setText(((PersonalChat_Client)client.user.allChats.get(receiverChatIndex)).getChatName());
			}else{
				System.out.println("the index of the chat is " + ((GroupChat_Client)client.user.allChats.get(receiverChatIndex)).getChatName());
				receiverName.setText(((GroupChat_Client)client.user.allChats.get(receiverChatIndex)).getChatName());
			}

			showMessage(receiverChatIndex);
		}
	}

	@FXML
	public void sendAddContactRequest(ActionEvent event) {
		int A = client.SendAddContactRequest(enterFriendsEmail.getText());
		System.out.println(A);
		if (enterFriendsEmail.getText().equals("")) {
			System.out.println("Please enter required credentials");
			enterFriendsEmail.setText("Please enter required credentials");
		}else if (enterFriendsEmail.getText().equals(client.getClientName())){
			enterFriendsEmail.setText("Please enter valid credentials");
		}
		else {
			if (A == 0) {
				System.out.println("Unexpected Error!!");
				enterFriendsEmail.setText("Unexpected Error!!");
			} else if (A == 1) {
				System.out.println("Successfully added new contact");
				enterFriendsEmail.setText("Successfully added new contact");
			} else if (A == 2) {
				System.out.println("Error! no account is registered against this email!");
				enterFriendsEmail.setText("Error! no account is registered against this email!");
			} else if (A == 3) {
				System.out.println("Error! Already a contact");
				enterFriendsEmail.setText("Error! Already a contact");
			}
		}
		System.out.println("Sending new contact request completes using infoView");
		DisplayPersonalChatList();
		// Chats.refresh();
		enterFriendsEmail.setText("");
	}

	@FXML
	public void showChats(ActionEvent event) {
		System.out.println("showing chat using infoView having body ");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("MainScreen.fxml"));
			universalController uc = new universalController();
			uc.setMain(mApp);
			uc.setClient(mApp.getClient());
			loader.setController(uc);
			AnchorPane mainPage = (AnchorPane) loader.load();
			uc.setClient(client);
			uc.setName();
			mApp.root.setCenter(mainPage);
			System.out.println("Add new contact");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void detectChange(int a) {
		if (a==7){
			//contact incoming
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
				//	DisplayContactList();
				//	DisplayGroupChatList();
					DisplayPersonalChatList();

					if (receiverChatIndex != -1) {
						showMessage(receiverChatIndex);
						System.out.println("not -1");
					} else {
						System.out.println("chatindex is zero");
					}

				}
			});

		}else if (a==8){
			//message incoming
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					DisplayPersonalChatList();
					DisplayContactList();
					if (receiverChatIndex != -1) {
						showMessage(receiverChatIndex);
						System.out.println("not -1");
					} else {
						System.out.println("chatindex is zero");
					}

				}
			});

		}else if (a==9){
			System.out.println("flip online");
			DisplayPersonalChatList();
			DisplayContactList();
		}else if (a==10){
			System.out.println("audiocallreaction");

		}else if (a==11){
			System.out.println("handle incoming call");
			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					displayPopUp();
				}

				});

		}else if (a==15){

		}


	}



}