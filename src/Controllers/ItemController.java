package Controllers;

import Fxmls.Main;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;


public class ItemController {
   public AnchorPane anc;
   public ImageView image;
   public void removeCourse(){
        Text t=(Text)(anc.getChildren().get(0));
        Main.sceneManager.getMainSceneController().removeCourse(t.getText());
    }
    public void itemMouseEntered(){
        anc.setStyle("-fx-background-color:#dae7f3;");
    }
    public void itemMouseExit(){
        anc.setStyle("-fx-background-color:transparent;");
    }
}
