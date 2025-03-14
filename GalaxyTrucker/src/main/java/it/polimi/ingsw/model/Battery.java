package it.polimi.ingsw.model;


import java.util.Map;
//se anche voi ritenete che queste cose abbiano senso bisogna MODIFICARE UML (aggiunta stored, togliere add battery)
public class Battery extends CardComponent  {
    //capacity
    private int size;
    //effectively stored
    private int stored;

    public Battery(ComponentType component_type, Map<Direction, ConnectorType> connectors, int size) {
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


}
