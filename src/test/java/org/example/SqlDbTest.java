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
        final LocalDate eventDate = LocalDate.of(2018, 8, 9);
        final LocalTime eventTime = LocalTime.of(14, 22);
        final ZonedDateTime dateTimeInStockholm = LocalDateTime
                .of(eventDate, eventTime)
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
        final LocalDate eventDate = LocalDate.of(2018, 8, 9);
        final LocalTime eventTimeInStockholm = LocalTime.of(14, 22);
        final ZonedDateTime zonedDateTime = LocalDateTime
                .of(eventDate, eventTimeInStockholm)
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
}
