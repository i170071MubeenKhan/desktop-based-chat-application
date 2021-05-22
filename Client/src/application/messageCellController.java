package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Shared.Chat;
import Shared.PersonalChat_Client;
import Shared.User_Client;
import Shared.Message_Client;

public class messageCellController extends ListCell<Message_Client> {

    public User_Client client;
    public int index;

	@FXML
    private Text message;
    @FXML
    private Text date;
  /*  @FXML
    private FontAwesomeIconView fxIconGender;*/

    @FXML
    private GridPane gridPane;

    private FXMLLoader mLLoader;

    public messageCellController(User_Client user , int a ) {
    	client=user;
    	index=a;
		// TODO Auto-generated constructor stub
    	System.out.println("This is message constructor");
    }
    @Override
    protected void updateItem(Message_Client oc, boolean empty) {


        super.updateItem( oc, empty);
        System.out.println("inside custom list cell");
        if(empty || oc == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("messageCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            message.setText(oc.getContent());

           /* Date adate = new Date();
            adate.setTime(oc.timeStamp.getTime());
            String formattedDate = new SimpleDateFormat("yyyyMMdd").format(adate);
            date.setText(formattedDate);*/

            date.setText(oc.timeStamp.toString());

            if (oc.senderEmail!=client.email){
            	message.setTranslateX(220);
            	date.setTranslateX(250);
            	//message.setTextAlignment();

            }


            // if (oc.hashCode())
      //      System.out.println("The status of the client is "+ oc.chatType);
          //  System.out.println("The data is " + oc.toString());


            setText(null);
            setGraphic(gridPane);
        }

    }
}
