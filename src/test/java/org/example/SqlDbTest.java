package org.example;

import org.example.sql.SqlDb;
import org.junit.jupiter.api.BeforeEach;
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

import static org.example.sql.SqlDb.getString;
import static org.example.sql.SqlDb.getTimestamp;
import static org.example.sql.SqlDb.stringParam;
import static org.example.sql.SqlDb.timestampParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlDbTest {
    private static final SqlDb sqlDb = new SqlDb("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password");

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId EU_STOCKHOLM = ZoneId.of("Europe/Stockholm");
    private static final ZoneId EU_HELSINKI = ZoneId.of("Europe/Helsinki");

    private static final LocalDate date2018_09_15 = LocalDate.of(2018, 9, 15);

    @BeforeEach
    void clearDb() {
        sqlDb.updateOrInsert("delete from event;");
    }

    @Test
    void shouldReadIntoSimpleDTO() {
        givenOneRowInEventTable();

        Optional<TimeDTO> maybeTs = sqlDb.selectOne("select timestamp from event", TimeDTO::new, getTimestamp("timestamp"));
        assertTrue(maybeTs.isPresent());
    }

    private void givenOneRowInEventTable() {
        final LocalTime eventTime = LocalTime.of(14, 22);
        final ZonedDateTime dateTimeInStockholm = LocalDateTime
                .of(date2018_09_15, eventTime)
                .atZone(EU_STOCKHOLM);
        final Timestamp stockholmEventTs = Timestamp.from(dateTimeInStockholm.toInstant());
        final Event stockholmConference = new Event("Stockholm Conference", stockholmEventTs);

        insertEvent(stockholmConference);
    }

    private void insertEvent(Event event) {
        sqlDb.updateOrInsert("insert into event values(?,?)",
                stringParam(1, event.description),
                timestampParam(2, event.timestamp)
        );
    }

    @Test
    void shouldReadIntoEvent() {
        givenOneRowInEventTable();

        Optional<Event> maybeTs = sqlDb.selectOne(
                "select description, timestamp from event",
                Event::new,
                getString("description"),
                getTimestamp("timestamp")
        );
        assertTrue(maybeTs.isPresent());
    }

    @Test
    void shouldReadOneRowFromTheDb() {
        givenOneRowInEventTable();

        Optional<Timestamp> maybeTs = sqlDb.selectOne("select timestamp from event", getTimestamp("timestamp"));
        assertTrue(maybeTs.isPresent());
    }

    @Test
    void shouldReadAListFromTheDb() {
        givenOneRowInEventTable();

        List<Timestamp> timestamps = sqlDb.selectList("select timestamp from event", getTimestamp("timestamp"));
        assertTrue(timestamps.size() > 0);
    }

    @Test
    void shouldReadAListOfEvents() {
        givenOneRowInEventTable();

        List<Event> events = sqlDb.selectList("select description, timestamp from event", Event::new, getString("description"), getTimestamp("timestamp"));
        assertTrue(events.size() > 0);
    }

    @Test
    void shouldInsertARow() {
        final LocalTime eventTimeInStockholm = LocalTime.of(14, 22);
        final ZonedDateTime zonedDateTime = LocalDateTime
                .of(date2018_09_15, eventTimeInStockholm)
                .atZone(EU_STOCKHOLM);

        final Timestamp eventTimestamp = Timestamp.from(zonedDateTime.toInstant());

        int count = sqlDb.updateOrInsert("insert into event values(?,?)",
                stringParam(1, "Conference"),
                timestampParam(2, eventTimestamp)
        );
        assertEquals(1, count);

        Optional<Timestamp> maybeEventTimestamp = sqlDb.selectOne(
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

    @Test
    void insertingEventWithoutTimezoneResultsInLocalTz() {
        final LocalTime eventTime = LocalTime.of(22, 11);
        final LocalDateTime localDateTime = LocalDateTime.of(date2018_09_15, eventTime);
        final Timestamp eventTimestamp = Timestamp.valueOf(localDateTime);

        int count = sqlDb.updateOrInsert("insert into event values(?,?)",
                stringParam(1, "Local Conference"),
                timestampParam(2, eventTimestamp)
        );
        assertEquals(1, count);

        Optional<Timestamp> maybeEventTimestamp = sqlDb.selectOne(
                "select timestamp from event where description = 'Local Conference'",
                getTimestamp("timestamp")
        );
        assertTrue(maybeEventTimestamp.isPresent());
        final Instant persistedInstant = maybeEventTimestamp.get().toInstant();

        final ZonedDateTime persistedUtcDateTime = ZonedDateTime.ofInstant(persistedInstant, UTC);
        final ZonedDateTime helsinkiDateTime = ZonedDateTime.of(localDateTime, EU_HELSINKI);

        assertEquals(helsinkiDateTime.withZoneSameInstant(UTC), persistedUtcDateTime);

        final ZonedDateTime persistedHelsinkiDateTime = ZonedDateTime.ofInstant(persistedInstant, EU_HELSINKI);
        assertEquals(helsinkiDateTime, persistedHelsinkiDateTime);
        assertEquals(localDateTime, persistedHelsinkiDateTime.toLocalDateTime());
    }
}
