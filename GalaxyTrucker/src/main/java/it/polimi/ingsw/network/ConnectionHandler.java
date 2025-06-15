package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.util.UUID;

public interface ConnectionHandler extends Runnable {
    void sendMessage(Message msg) throws IOException;
    void close() throws IOException;
    UUID getClientId();
    String getNickname();
    void setNickname(String nickname);
}
