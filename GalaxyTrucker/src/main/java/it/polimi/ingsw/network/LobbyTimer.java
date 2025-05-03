package it.polimi.ingsw.network;

import java.util.*;
// LobbyTimer.java
import java.util.Timer;
import java.util.TimerTask;

public class LobbyTimer {
    private final Timer timer = new Timer();
    private boolean someoneFinished = false;
    private boolean thirtyStarted   = false;
    private boolean build120Ended = false;

    private final List<UUID> finishOrder = new ArrayList<>();
    private final int totalPlayers;
    /**
     * Avvia il countdown di 120s.
     * @param on120End callback invocata allo scadere dei 120s
     */
    public void start120(Runnable on120End) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                on120End.run();
            }
        }, 120 * 1000);
    }

    public LobbyTimer(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }



    public void start30IfNeeded(Runnable on30End) {
        if (thirtyStarted) return;
        thirtyStarted = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                build120Ended = true;
                on30End.run();
            }
        }, 30 * 1000);
    }

    /**
     * Chiamato quando i 120s sono finiti.
     * Se già qualcuno ha finito, parte subito il 30s;
     * altrimenti si aspetta che arrivi notifySomeoneFinished().
     */
    public void handle120End(Runnable on30End, Runnable onWaitStart) {
        build120Ended = true;
        if (someoneFinished) {
            start30IfNeeded(on30End);
        } else {
            onWaitStart.run();

        }
    }

    public void notifyFinished(UUID playerId,
                               Runnable onAllFinished,
                               Runnable on30StartNeeded,
                               Runnable on30End) {
        someoneFinished = true;

        if (!finishOrder.contains(playerId)) {
            finishOrder.add(playerId);
        }

        if (finishOrder.size() == totalPlayers) {
            timer.cancel();
            onAllFinished.run();
            return;
        }
        // Se 120s già conclusi e 30s non ancora partiti -> avvia 30s
        if (build120Ended && !thirtyStarted) {

            on30StartNeeded.run();
            start30IfNeeded(on30End);
            thirtyStarted = true;
        }
    }

    public String  getPositionByPlayer(UUID playerId) {
        return String.valueOf(finishOrder.indexOf(playerId));
    }

    public List<UUID> getFinishOrder() {
        return Collections.unmodifiableList(finishOrder);
    }


    public void cancel() {
        timer.cancel();
    }
}
