    package it.polimi.ingsw.model.adventures;

    import it.polimi.ingsw.model.Board;
    import it.polimi.ingsw.model.Player;
    import it.polimi.ingsw.model.components.CardComponent;
    import it.polimi.ingsw.model.enumerates.CardAdventureType;

    import java.util.List;
    import java.util.HashMap;
    import java.util.Map;

    public class OpenSpace extends CardAdventure{ //every player in the lobby will move

        public OpenSpace(int level, int cost_of_days, CardAdventureType type , Board board) {
            super(level, cost_of_days,type ,board);
        }
        public void execute(){
            System.out.println("metodo cuscinetto");
        }
        public void execute(List<Player> players, Map<Player, Map<CardComponent, Boolean>> batteryUsageMap) {


            for (Player player : players) {
                Map<CardComponent, Boolean> playerBatteryUsage = batteryUsageMap.getOrDefault(player, new HashMap<>());

                int power = (int) player.getShip().calculateEnginePower(playerBatteryUsage); // Passiamo la mappa con l'uso delle batterie
                board.movePlayer(player,power);

            }


        }



    }
