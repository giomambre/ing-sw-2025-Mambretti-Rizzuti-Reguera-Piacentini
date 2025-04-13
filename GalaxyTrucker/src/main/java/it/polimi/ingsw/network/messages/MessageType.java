package it.polimi.ingsw.network.messages;

public enum MessageType {
    SENDED_NAME,
    STANDARD,
    REQUEST_NAME,
    NAME_ACCEPTED,
    NAME_REJECTED,

    CREATE_LOBBY,
    SEE_LOBBIES,
    SELECT_LOBBY,
    ASSIGN_UUID,
    GAME_STARTED,
    COLOR_SELECTED,
    BUILD_START,
    ASK_CARD,
    FACED_UP_CARD_ADDED,
    CARD_UNAVAILABLE,
    REJECTED_CARD

}

