package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.sql.SqlHelper.getString;
import static org.example.sql.SqlHelper.getTimestamp;
import static org.example.sql.SqlHelper.selectList;
import static org.example.sql.SqlHelper.selectOne;
import static org.example.sql.SqlHelper.stringParam;
import static org.example.sql.SqlHelper.timestampParam;
import static org.example.sql.SqlHelper.updateOrInsert;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlHelperTest {

    public static class TimeDTO {
        Timestamp timestamp;

        TimeDTO(Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }

    @Test
    void shouldReadIntoSimpleDTO() {
        Optional<TimeDTO> maybeTs = selectOne("select timestamp from event", TimeDTO::new, getTimestamp("timestamp"));
        assertTrue(maybeTs.isPresent());
    }

    public static class Event {
        String description;
        Timestamp timestamp;

        Event(String description, Timestamp timestamp) {
            this.description = description;
            this.timestamp = timestamp;
        }
    }

    @Test
    void shouldReadIntoEvent() {
        Optional<Event> maybeTs = selectOne("select description, timestamp from event", Event::new, getString("description"), getTimestamp("timestamp"));
        assertTrue(maybeTs.isPresent());
    }

    @Test
    void shouldReadOneRowFromTheDb() {
        Optional<Timestamp> maybeTs = selectOne("select timestamp from event", getTimestamp("timestamp"));
        assertTrue(maybeTs.isPresent());
    }

    @Test
    void shouldReadAListFromTheDb() {
        List<Timestamp> timestamps = selectList("select timestamp from event", getTimestamp("timestamp"));
        assertTrue(timestamps.size() > 0);
    }

    @Test
    void shouldReadAListOfEvents() {
        List<Event> events = selectList("select description, timestamp from event", Event::new, getString("description"), getTimestamp("timestamp"));
        assertTrue(events.size() > 0);
    }

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId EU_STOCKHOLM = ZoneId.of("Europe/Stockholm");
    private static final ZoneId EU_HELSINKI = ZoneId.of("Europe/Helsinki");

    @Test
    void shouldInsertARow() {
        final LocalDate eventDate = LocalDate.of(2018, 8, 9);
        final LocalTime eventTimeInStockholm = LocalTime.of(14, 22);
        final ZonedDateTime zonedDateTime = LocalDateTime
                .of(eventDate, eventTimeInStockholm)
                .atZone(EU_STOCKHOLM);

        final Timestamp eventTimestamp = Timestamp.from(zonedDateTime.toInstant());

        int count = updateOrInsert("insert into event values(?,?)",
                stringParam(1, "Conference"),
                timestampParam(2, eventTimestamp)
        );
        assertEquals(1, count);

        Optional<Timestamp> maybeEventTimestamp = selectOne(
                "select timestamp from event where description = 'Conference'",
                getTimestamp("timestamp")
        );
        assertTrue(maybeEventTimestamp.isPresent());
        final Instant persistedEventInstant = maybeEventTimestamp.get().toInstant();

        ZonedDateTime eventDateTimeUTC = ZonedDateTime.ofInstant(persistedEventInstant, UTC);
        ZonedDateTime eventDateTimeStockholm = ZonedDateTime.ofInstant(persistedEventInstant, EU_STOCKHOLM);
        ZonedDateTime eventDateTimeHelsinki = ZonedDateTime.ofInstant(persistedEventInstant, EU_HELSINKI);

        assertEquals(eventTimeInStockholm, eventDateTimeStockholm.toLocalTime());
        assertEquals(eventTimeInStockholm, eventDateTimeHelsinki.toLocalTime().minusHours(1));
        assertEquals(eventTimeInStockholm, eventDateTimeUTC.toLocalTime().plusHours(2));
    }
}
