package org.openjfx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.controllers.mainSceneController;
import org.openjfx.controllers.sideSceneController;
import org.openjfx.dataBase.sceneManager;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private double xOffset;
    private double yOffset;
    public static sceneManager sceneManager;

    private static Scene scene;
    @Override
    public void start(Stage primaryStage) throws IOException {
        //init selenium driver.
        fetchCourseHandler.getInstance();

        //init main-scene.
        FXMLLoader loader=new FXMLLoader(getClass().getResource("mainScene.fxml"));
        Parent root = loader.load();
        mainSceneController mainSceneController=loader.getController();
        Scene scene=new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        //init side-scene.
        loader=new FXMLLoader(getClass().getResource("sideScene.fxml"));
        Parent root2=loader.load();
        sideSceneController sideSceneConroller=loader.getController();
        Scene sideScene=new Scene(root2);
        sideScene.setFill(Color.TRANSPARENT);
        root2.requestFocus();

        //init the scenemanager.
        sceneManager =new sceneManager(scene,sideScene,primaryStage,mainSceneController,sideSceneConroller);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        //starts the app.
        primaryStage.setScene(scene);
        primaryStage.show();
        root.requestFocus();


        //adding drag options to the app.
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset= mouseEvent.getSceneX();
                yOffset=mouseEvent.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                primaryStage.setX(mouseEvent.getScreenX()-xOffset);
                primaryStage.setY(mouseEvent.getScreenY()-yOffset);
            }
        });
        root2.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset= mouseEvent.getSceneX();
                yOffset=mouseEvent.getSceneY();
            }
        });
        root2.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                primaryStage.setX(mouseEvent.getScreenX()-xOffset);
                primaryStage.setY(mouseEvent.getScreenY()-yOffset);
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }

}