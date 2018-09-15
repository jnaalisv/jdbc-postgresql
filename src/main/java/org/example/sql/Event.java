package org.example.sql;

import java.sql.Timestamp;

public class Event {
    public String description;
    public Timestamp timestamp;

    public Event(String description, Timestamp timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }
}

