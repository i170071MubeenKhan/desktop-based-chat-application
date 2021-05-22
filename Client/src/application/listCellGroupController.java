package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;

import Shared.Chat;
import Shared.PersonalChat_Client;

/**
 * Created by Johannes on 23.05.16.
 * @param <T>
 * @param <T>
 *
 */

public class listCellGroupController extends ListCell<Chat> {

    @FXML
    private Label label1;
//    @FXML
   private Circle isOnline;
  /*  @FXML
    private FontAwesomeIconView fxIconGender;*/

    @FXML
    private GridPane gridPane;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Chat oc, boolean empty) {

        super.updateItem( oc, empty);
        System.out.println("inside custom list cell");
        if(empty || oc == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("ListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (oc.chatType==0){//personal chat
            	 label1.setText(oc.toString());
            }else {
            	 label1.setText(oc.getChatName());
            }




            // if (oc.hashCode())
            System.out.println("The status of the client is "+ oc.chatType);
            if (oc.chatType==0){//personal_chat
            	if (oc.getOnlineStatus()==false){

            		isOnline.setFill((Paint.valueOf("#E80E0E")));
            	}else{

            		isOnline.setFill((Paint.valueOf("#0FCF23")));
            	}
            }else{
            //	isOnline.setFill((Paint.valueOf("#E80E0E")));
            }

            System.out.println("The data is " + oc.toString());


            setText(null);
            setGraphic(gridPane);
        }

    }
}
