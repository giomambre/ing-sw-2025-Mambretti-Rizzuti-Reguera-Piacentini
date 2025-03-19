package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OpenSpace extends  CardAdventure{ //every player in the lobby will move



    public OpenSpace(int level, int cost_of_days) {
        super(level, cost_of_days);
    }

    public List<Integer>  calculatePlayersPower(List<Player> players, Map<Player, Map<CardComponent, Boolean>> batteryUsageMap) {
        List<Integer> powers = new ArrayList<>();


        for (Player player : players) {
            Map<CardComponent, Boolean> playerBatteryUsage = batteryUsageMap.getOrDefault(player, new HashMap<>());

            int power = (int) player.getShip().calculateEnginePower(playerBatteryUsage); // Passiamo la mappa con l'uso delle batterie
            powers.add(power);
        }

        return powers;
    }



}
