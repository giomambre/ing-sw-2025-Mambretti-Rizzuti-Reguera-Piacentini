package it.polimi.ingsw;

import static it.polimi.ingsw.model.Color.*;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        //prova di avvio
        Game game = new Game();
        game.startGame();
        Player p = new Player("raffa", Green);
        game.addPlayer(p);
        game.addPlayer(new Player("Isa", Red));
        game.addPlayer(new Player("Cice", Blue));
        game.addPlayer(new Player("Gio", Yellow));

        game.startAssembly();
        game.startFlight();
        game.getBoard().printBoard();
        System.out.println("");
        game.getBoard().MovePlayer(p,3);
        game.getBoard().printBoard();


    }
}
