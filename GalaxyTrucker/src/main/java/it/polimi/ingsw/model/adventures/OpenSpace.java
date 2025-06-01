    package it.polimi.ingsw.model.adventures;

    import it.polimi.ingsw.model.Board;
    import it.polimi.ingsw.model.Player;
    import it.polimi.ingsw.model.components.CardComponent;
    import it.polimi.ingsw.model.enumerates.CardAdventureType;

    import java.io.Serializable;
    import java.util.List;
    import java.util.HashMap;
    import java.util.Map;

    /**
     * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
     */
    public class OpenSpace extends CardAdventure implements Serializable { //every player in the lobby will move

        /**
         *
         * @param level must be level 1 or 2
         * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
         * @param type
         */
        public OpenSpace(int level, int cost_of_days, CardAdventureType type,String path) {
            super(level, cost_of_days,type,path);
        }
        /**
         * @param players the list of players whose engine power is evaluated
         * @param batteryUsageMap a map specifying, for each player, which engine components use batteries
         */
        /**
         * This method allows the player to decide whether to use batteries to incrise his engigne power.
         * For each player, this method uses the specified battery usage map
         * to determine which components should consume batteries.
         * Then it calculates the total engine power using {@code calculateEnginePower} from the player's ship,
         * at the end it moves the player forward by that amount using {@code movePlayer} from the board.
         *
         * @param players the list of players whose engine power is evaluated
         * @param batteryUsageMap  map specifying, for each player, which components use batteries
         */
 /*       public void execute(List<Player> players, Map<Player, Map<CardComponent, Boolean>> batteryUsageMap) {


            for (Player player : players) {
                Map<CardComponent, Boolean> playerBatteryUsage = batteryUsageMap.getOrDefault(player, new HashMap<>());

                int power = (int) player.getShip().calculateEnginePower(playerBatteryUsage); // Passiamo la mappa con l'uso delle batterie
                board.movePlayer(player,power);

            }


        }*/



    }
