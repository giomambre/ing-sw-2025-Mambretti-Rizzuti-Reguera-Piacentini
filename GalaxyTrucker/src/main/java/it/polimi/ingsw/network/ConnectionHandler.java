package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.util.UUID;

public interface ConnectionHandler extends Runnable {
    /**
     * Sends a message to the client.
     * @param msg The message to be sent.
     * @throws IOException if a communication error occurs.
     */
    void sendMessage(Message msg) throws IOException;
    /**
     * Closes the connection with the client.
     * @throws IOException if an error occurs while closing the connection.
     */
    void close() throws IOException;
    /**
     * Gets the unique identifier of the client.
     * @return The client's UUID.
     */
    UUID getClientId();
    /**
     * Gets the nickname of the client.
     * @return The client's nickname.
     */
    String getNickname();
    /**
     * Sets the nickname for the client.
     * @param nickname The nickname to set.
     */
    void setNickname(String nickname);
}
