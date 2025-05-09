package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Board;

public class BoardMessage extends Message {
    Board board;

    public BoardMessage(MessageType type, String content, Board board) {
        super(type, content);
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

}
