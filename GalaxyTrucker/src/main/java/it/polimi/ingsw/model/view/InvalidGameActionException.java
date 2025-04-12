package it.polimi.ingsw.model.view;

public class InvalidGameActionException extends RuntimeException {
    public InvalidGameActionException(String message) {
        super(message);
    }
}
