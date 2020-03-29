package org.example.time;

import java.sql.Timestamp;

public record Event(String description, Timestamp timestamp) { }

