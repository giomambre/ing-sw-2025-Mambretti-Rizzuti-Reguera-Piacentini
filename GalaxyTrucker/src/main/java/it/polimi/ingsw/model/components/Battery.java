package it.polimi.ingsw.model.components;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.util.Map;
public class Battery extends CardComponent {
    //capacity
    private int size;
    //effectively stored
    private int stored;

    @JsonCreator
    public Battery(

            @JsonProperty("component_type") ComponentType component_type,
            @JsonProperty("connectors") Map<Direction, ConnectorType> connectors, @JsonProperty("size") int size) {
        super(component_type, connectors);
        this.size = size;
        this.stored = 0;
    }

    //hp: le batterie si aggiungono solo quando si fanno i rifornimenti iniziali (mi sembra di ricordare dal regolamento)
    //se così non fosse vanno messe delle condizioni aggiuntive di verifica es: stored+num_batteries<size ...

    //altra cosa: non so se imporre direttamente che num batteries sia uguale alla size (ha più senso perchè penso tutti i giocatori riempiano al massimo lo spazio) però per attenermi all'uml ho messo così
    //se così fosse ha senso farlo direttamente nel costruttore ed eliminare questa funzione
    public void addBattery(int num_batteries){
       if(num_batteries>size ){
           System.out.println("Battery overflow non so se sia un errore o una exception");
       }
       stored += num_batteries;
    }

     public void removeBattery(){
        if(stored==0){
            System.out.println("anche qui non so se sia un errore o una exception");
        }
        if(stored>0){
            stored--;
        }
     }

     public int getStored(){
        return stored;
     }


}
