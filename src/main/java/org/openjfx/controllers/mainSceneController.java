package org.openjfx.controllers;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openjfx.App;
import org.openjfx.fetchCourseHandler;
import org.openjfx.dataBase.myCourses;

public class mainSceneController {

    @FXML
    private TextField textField;

    @FXML
    private Button addButton;

    @FXML
    private VBox menu;

    @FXML
    private AnchorPane errorMsg;


    @FXML
    private Button createBtn;


    /**
     * Adding course (if not existed) to the courses database and to the course menu.
     * @throws IOException
     */
    public void addCourse() throws IOException {
        String courseId = textField.getText();
        if (!myCourses.getInstance().contains(courseId)) {
            addButton.getScene().setCursor(Cursor.WAIT); //Change cursor to wait

            Task task = new Task() {
                @Override
                public Void call() {
                    fetchCourseHandler.getInstance().addCourse(courseId); //fetch course from university database.
                    return null;
                }
            };
            task.setOnSucceeded(e -> {// adding course to the display menu.
                addButton.getScene().setCursor(Cursor.DEFAULT);
                try {
                    Node root = FXMLLoader.load(App.class.getResource("course.fxml"));
                    TextFlow t = (TextFlow) ((AnchorPane) root).getChildren().get(0);
                    Text text=new Text(myCourses.getInstance().getLastAdded());
                    text.setFill(Paint.valueOf("#da9816"));
                    text.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                    t.getChildren().add(text);
                    ((AnchorPane) root).getChildren().get(1).setOnMouseClicked(event -> {
                        String toRemove=((Text)t.getChildren().get(0)).getText();
                        myCourses.getInstance().removeCourse(toRemove);
                        menu.getChildren().removeAll(root);
                    });
                    menu.getChildren().add(root);
                } catch (Exception f) {}
            });
            task.setOnFailed(e -> {//display error msg if something went wrong.
                addButton.getScene().setCursor(Cursor.DEFAULT);
                errorMsg.setVisible(true);
                errorMsg.toFront();
            });
            new Thread(task).start();
        }
    }

    /**
     * Exit from the application.
     */
    public void close(){
        App.sceneManager.getPrimaryStage().close();
    }

    /**
     * creates cariculum. Apply when the user press on create button.
     * @throws IOException
     */
    public void createCariculum() throws IOException {
        createBtn.getScene().setCursor(Cursor.WAIT);
        Task task = new Task() {
            @Override
            public Void call() {
                myCourses.getInstance().createCariculum();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            App.sceneManager.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
            Scene to=App.sceneManager.getSideScene();
            App.sceneManager.getSideSceneController().setDisplayIndex();
            try {
                App.sceneManager.switchScene(to,1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        task.setOnFailed(e ->{
            App.sceneManager.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
        });
        new Thread(task).start();
    }

    /**
     * Closes the error msg.
     */
    public void closeErrorMsg(){
        errorMsg.setVisible(false);
    }
}