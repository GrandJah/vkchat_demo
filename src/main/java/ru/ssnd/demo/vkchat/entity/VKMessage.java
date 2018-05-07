package ru.ssnd.demo.vkchat.entity;

import javax.persistence.Entity;
import java.time.Instant;

@Entity
public class VKMessage {
    private Long id;
    private String text;
    private Instant sentAt;
    private Sender sender;

    public VKMessage(Long id, String message, Instant time, Sender sender){
        this.id = id;
        this.text = message;
        this.sentAt = time;
        this.sender = sender;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Sender getSender() {
        return sender;
    }
}


