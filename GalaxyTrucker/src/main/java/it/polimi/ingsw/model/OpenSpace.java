    package it.polimi.ingsw.model;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.HashMap;
    import java.util.Map;

    public class OpenSpace extends CardAdventure{ //every player in the lobby will move



        public OpenSpace(int level, int cost_of_days, CardAdventureType type , Board board) {
            super(level, cost_of_days,type ,board);
        }

        public void executeAdventureEffects(List<Player> players, Map<Player, Map<CardComponent, Boolean>> batteryUsageMap) {


            for (Player player : players) {
                Map<CardComponent, Boolean> playerBatteryUsage = batteryUsageMap.getOrDefault(player, new HashMap<>());

                int power = (int) player.getShip().calculateEnginePower(playerBatteryUsage); // Passiamo la mappa con l'uso delle batterie
                board.MovePlayer(player,power);

            }


        }



    }
