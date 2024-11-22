package com.example.inventory_control_system.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CustomEmailMessage {

    private String from;
    private String to;
    private Date sentDate;
    private String subject;
    private String text;

    public CustomEmailMessage(String from, String to, Date sentDate, String subject, String text) {
        this.from = from;
        this.to = to;
        this.sentDate = sentDate;
        this.subject = subject;
        this.text = text;
    }
}
