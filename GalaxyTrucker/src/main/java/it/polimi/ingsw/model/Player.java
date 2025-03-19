package it.polimi.ingsw.model;
import java.util.List;
import java.util.Random;

public class Player {
    private String nickname;
    private Color color;
    private Ship ship = new Ship(this);
    private int exposed_connectors = 0;
    private Game game = new Game();

    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;

        ship.PrintShipPlance();
    }

    public void EndBuild(){
        
    }

    public void SecureComponent(CardComponent component) {

        List<CardComponent> extra_components = ship.getExtra_components();

        if (extra_components.size()>1)
            System.out.println("You have more than one extra component");
        else
            extra_components.add(component);

        ship.setExtra_components(extra_components);
    }

    public void AddToShip(CardComponent component, int row, int col) {

        ship.AddComponent(component, row, col);
    }

    public void DismissComponent(CardComponent component) {

        component.changefaceshowed();

        List<CardComponent> deck_components = game.getDeck_components();
        deck_components.add(component);

        game.setDeck_components(deck_components);

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

    public Ship getShip() {
        return ship;
    }

    public Color getColor() {
        return color;
    }

    public String getNickname() {
        return nickname;
    }
}
