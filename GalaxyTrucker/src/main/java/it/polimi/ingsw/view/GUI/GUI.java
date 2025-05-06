package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;


public class GUI implements View {

    NicknameController nicknameController;
    Stage primaryStage;
    private boolean nicknamesettato=false;
    private String nicknamescelto;


    public GUI() {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void setNicknamescelto(String nickname) {
        System.out.println(nickname);
        this.nicknamescelto = nickname;
        nicknamesettato=true;
    }
    public boolean getnicknamesettato() {
        return nicknamesettato;
    }

    @Override
    public String askNickname()  {
        /*while(!nicknamesettato) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }*/
        System.out.println("HO scelto"+nicknamescelto);
        return nicknamescelto;

    }

    @Override
    public void showMessage(String message) {

    }



    @Override
    public String chooseConnection() {
        return "";
    }

    @Override
    public String getInput() {
        return "";
    }

    @Override
    public void showGenericError(String error) {

    }

    @Override
    public int askCreateOrJoin() {
        return 0;
    }

    @Override
    public int askNumPlayers() {
        return 0;
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        return 0;
    }

    @Override
    public Color askColor(List<Color> colors) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> askCoordsCrewmate(Ship ship) {
        return null;
    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public void showShip(String nickname) {

    }

    @Override
    public void printShip(CardComponent[][] ship) {

    }

    @Override
    public void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {

    }

    @Override
    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        return 0;
    }

    @Override
    public int selectDeck() {
        return 0;
    }

    @Override
    public int crewmateAction(Pair<Integer,Integer> component) {
        return 0;
    }



    @Override
    public int askFacedUpCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public int askSecuredCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public int showCard(CardComponent card) {
        return 0;
    }

    @Override
    public Pair<Integer, Integer> askCoords(Ship ship) {
        return null;
    }

    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        return null;
    }



}