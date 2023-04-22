package utils;

import com.mindsmiths.infobipAdapter.models.InfobipReceivedMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReceivedMessage implements Serializable {
    protected String id;
    protected String chatId;
    protected String adapter;
    protected String text;
    protected LocalDateTime receivedAt;
    protected String firstName;
    protected String lastName;

    public ReceivedMessage(InfobipReceivedMessage message) {
        this.id = message.getMessageId();
        this.chatId = message.getFrom();
        this.adapter = "whatsapp";
        this.text = message.getMessage().getText();
        this.receivedAt = message.getReceivedAt();
        String[] name = message.getContact().getName().split(" ", 2);
        this.firstName = name[0];
        this.lastName = name.length > 1 ? name[1] : "";
    }
}
