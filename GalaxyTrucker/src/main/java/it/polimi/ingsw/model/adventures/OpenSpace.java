    package it.polimi.ingsw.model.adventures;

    import it.polimi.ingsw.model.Board;
    import it.polimi.ingsw.model.Player;
    import it.polimi.ingsw.model.components.CardComponent;
    import it.polimi.ingsw.model.enumerates.CardAdventureType;

    import java.util.List;
    import java.util.HashMap;
    import java.util.Map;

    /**
     * This class is a sublass of CardAdventure, from which it inherits attributes and methods
     */
    public class OpenSpace extends CardAdventure{ //every player in the lobby will move

        /**
         *
         * @param level must be level 1 or 2
         * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
         * @param type
         * @param board
         */
        public OpenSpace(int level, int cost_of_days, CardAdventureType type) {
            super(level, cost_of_days,type);
        }
        public void execute(){
            System.out.println("metodo cuscinetto");
        }

        /**
         * This method allows the player to decide whether to use batteries to incrise his engigne power.
         * Then it calculates the power using the 'calculateEngignePower' function of ship.
         * At the end it moves the player forward by power positions through the 'movePlayer' function of board.
         *
         * @param players
         * @param batteryUsageMap
         */
        public void execute(List<Player> players, Map<Player, Map<CardComponent, Boolean>> batteryUsageMap) {


            for (Player player : players) {
                Map<CardComponent, Boolean> playerBatteryUsage = batteryUsageMap.getOrDefault(player, new HashMap<>());

                int power = (int) player.getShip().calculateEnginePower(playerBatteryUsage); // Passiamo la mappa con l'uso delle batterie
                board.movePlayer(player,power);

            }


        }



    }
