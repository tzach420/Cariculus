package Controllers;
import Fxmls.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import dataBase.fetchCourseHandler;
import dataBase.myCourses;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


public class mainSceneController {

    @FXML
    private JFXTextField textField;

    @FXML
    private JFXButton addButton;

    @FXML
    private VBox menu;

    @FXML
    private AnchorPane loading;


    @FXML
    private JFXButton createBtn;

    private int numOfCourses=0;

    public void createCariculum() throws IOException {
        createBtn.getScene().setCursor(Cursor.WAIT); //Change cursor to hand

        Task task = new Task() {
            @Override
            public Void call() {
                myCourses.getInstance().createCariculum();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            Main.sceneManager.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
            Scene to=Main.sceneManager.getSideScene();
            Main.sceneManager.getSideSceneController().setDisplayIndex();
            try {
                Main.sceneManager.switchScene(to,1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        task.setOnFailed(e -> Main.sceneManager.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT));
        new Thread(task).start();
    }

    public  void removeCourse(String name){
        for(Node e: menu.getChildren()){
            Text courseName=(Text)((AnchorPane)e).getChildren().get(0);
            if(name.equals(courseName.getText())){
                menu.getChildren().remove(e);
                myCourses.getInstance().removeCourse(name);
                break;
            }
        }
    }
    public void addCourse() throws IOException {
        loading.setVisible(true);
        String courseId=textField.getText();
        addButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait

        Task task = new Task() {
            @Override
            public Void call() {
               fetchCourseHandler.getInstance().addCourse(courseId);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            addButton.getScene().setCursor(Cursor.DEFAULT);
        try{
            loading.setVisible(false);
            Node root = FXMLLoader.load(getClass().getResource("../Fxmls/test.fxml"));
            Text t=(Text)((AnchorPane)root).getChildren().get(0);
            t.setText(myCourses.getInstance().getLastAdded());
            menu.getChildren().add(root);


        }catch (Exception f){


            ///error msg.
        }
        });
        task.setOnFailed(e -> addButton.getScene().setCursor(Cursor.DEFAULT));
        new Thread(task).start();

    }

    public void close(){
        Main.sceneManager.getPrimaryStage().close();
    }

}
