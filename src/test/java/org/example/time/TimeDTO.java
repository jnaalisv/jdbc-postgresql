package org.example.time;

import java.sql.Timestamp;

public class TimeDTO {
    private Timestamp timestamp;

    public TimeDTO(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}