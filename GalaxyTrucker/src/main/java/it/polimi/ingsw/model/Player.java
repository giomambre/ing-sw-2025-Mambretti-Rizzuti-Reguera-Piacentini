package it.polimi.ingsw.model;
import java.util.List;
import java.util.Random;

import static  it.polimi.ingsw.model.Color.*;

public class Player {
    private String nickname;
    private Color color;
    private Ship ship = new Ship();
    private int exposed_connectors = 0;


    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;

        ship.PrintShipPlance();
    }


    public void SecureComponent(CardComponent component) {

        List<CardComponent> extra_components = ship.getExtra_components();

        if (extra_components.size()>1)
            System.out.println("You have more than one extra component");
        else
            extra_components.add(component);

        ship.setExtra_components(extra_components);
    }

    public void AddToShip(CardComponent component) {
        int row=0;
        int col=0;

        // the player select the box of the ship where he wants to drop the card, controller???

        ship.AddComponent(component, row, col);
    }

    public void UseExtraComponent(CardComponent component) {

        List<CardComponent> extra_components = ship.getExtra_components();

        extra_components.remove(component);
        ship.setExtra_components(extra_components);
    }

    public int ThrowDice(){
        Random dice1 = new Random();
        Random dice2 = new Random();

        return (dice1.nextInt(6)+1)+(dice2.nextInt(6)+1);
    }
}
