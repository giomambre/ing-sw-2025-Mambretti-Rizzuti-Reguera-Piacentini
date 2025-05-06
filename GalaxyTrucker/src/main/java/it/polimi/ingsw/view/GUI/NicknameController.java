package it.polimi.ingsw.view.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;


public class NicknameController {
    private GUI gui;
    @FXML
    private TextField nickname;

    public NicknameController(GUI gui) {
        this.gui = gui;
        System.out.println("sono nel costruttore di nicknamecontroller");
    }






}