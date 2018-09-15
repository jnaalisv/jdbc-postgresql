package org.example;

import java.sql.Timestamp;

public class Event {
    String description;
    Timestamp timestamp;

    Event(String description, Timestamp timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }
}

