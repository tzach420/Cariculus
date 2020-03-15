package dataBase;

import Controllers.mainSceneController;
import Controllers.sideSceneController;
import Fxmls.Main;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class sceneManager {
    private Scene mainScene;
    private Scene sideScene;
    private Stage primaryStage;
    private mainSceneController mainSceneController;
    private sideSceneController sideSceneController;

    public sceneManager(Scene mainScene, Scene sideScene, Stage primaryStage,mainSceneController mainSceneController, sideSceneController sideSceneController) {
        this.mainScene = mainScene;
        this.sideScene = sideScene;
        this.primaryStage = primaryStage;
        this.mainSceneController = mainSceneController;
        this.sideSceneController = sideSceneController;
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public Scene getSideScene() {
        return sideScene;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public mainSceneController getMainSceneController() {
        return mainSceneController;
    }

    public sideSceneController getSideSceneController() {
        return sideSceneController;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void setSideScene(Scene sideScene) {
        this.sideScene = sideScene;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMainSceneController(Controllers.mainSceneController mainSceneController) {
        this.mainSceneController = mainSceneController;
    }

    public void setSideSceneController(Controllers.sideSceneController sideSceneController) {
        this.sideSceneController = sideSceneController;
    }
    public void switchScene(Scene to,int index) throws IOException {
        primaryStage.setScene(to);
        primaryStage.show();
        if(index==1) sideSceneController.init();
    }
}
