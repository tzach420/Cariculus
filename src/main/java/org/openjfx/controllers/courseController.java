package org.openjfx.controllers;

import javafx.scene.layout.AnchorPane;


public class courseController {
    public AnchorPane anc;

    /**
     * Animations.
     */
    public void itemMouseEntered(){
        anc.setStyle("-fx-background-color:#dae7f3;");
    }
    public void itemMouseExit(){
        anc.setStyle("-fx-background-color:transparent;");
    }
}



