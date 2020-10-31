package org.openjfx.dataBase;


import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.controllers.mainSceneController;
import org.openjfx.controllers.sideSceneController;

import java.io.IOException;

/**
 * The scenemanger class in charge of all the data-transfer between scenes in the app.
 */
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

    public void switchScene(Scene to,int index) throws IOException {
        primaryStage.setScene(to);
        primaryStage.show();
        if(index==1) sideSceneController.init();
    }
}
