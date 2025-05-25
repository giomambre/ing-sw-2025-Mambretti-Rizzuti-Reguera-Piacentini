package it.polimi.ingsw.network.messages;

public class NotificationMessage extends Message {
    String nickname;
    public NotificationMessage(MessageType type, String content, String nickname) {
        super(type, content);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

}
