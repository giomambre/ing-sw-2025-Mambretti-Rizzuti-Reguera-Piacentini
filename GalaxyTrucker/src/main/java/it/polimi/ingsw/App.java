package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;

import java.io.IOException;
import java.util.List;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        CardComponentLoader  cardComponentLoader = new CardComponentLoader();
        //prova di avvio
        try {
            // Specifica il percorso del tuo file JSON
            String jsonFilePath = "src/main/java/it/polimi/ingsw/jsons/cards/cards.json";
            List<CardComponent> components = cardComponentLoader.loadCardComponents(jsonFilePath);

            // Fai qualcosa con i componenti caricati
            for (CardComponent component : components) {
                System.out.println(component);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Gestisci l'errore
        }


    }
}
