package it.polimi.ingsw.network;



import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {
    private final String subject;
    private final String content;

    public Message(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!Objects.equals(subject, message.subject)) return false;
        return Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
