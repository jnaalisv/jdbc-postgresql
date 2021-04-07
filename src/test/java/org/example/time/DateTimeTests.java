package org.example.time;

import org.example.AppContext;
import org.example.rdb.RdbUtil;
import org.example.sql.Params;
import org.example.sql.Results;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeTests {
    private static final RdbUtil rdbUtil = AppContext.rdbUtil;

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId EU_STOCKHOLM = ZoneId.of("Europe/Stockholm");
    private static final ZoneId EU_HELSINKI = ZoneId.of("Europe/Helsinki");

    private static final LocalDate date2018_09_15 = LocalDate.of(2018, 9, 15);

    @BeforeEach
    void clearDb() {
        rdbUtil.updateOrInsert("delete from event;");
    }

    @Test
    void shouldReadIntoSimpleDTO() {
        givenOneRowInEventTable();

        var times = rdbUtil.selectList("select timestamp from event", TimeDTO::new, Results.timeStampFrom("timestamp"));
        assertEquals(1, times.size());
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
        rdbUtil.updateOrInsert("insert into event values(?,?)",
                Params.string(event.description()),
                Params.timestamp(event.timestamp())
        );
    }

    @Test
    void shouldReadIntoEvent() {
        givenOneRowInEventTable();

        var events = rdbUtil.selectList(
                "select description, timestamp from event",
                Event::new,
                Results.stringFrom("description"),
                Results.timeStampFrom("timestamp")
        );
        assertEquals(1, events.size());
    }

    @Test
    void shouldReadOneRowFromTheDb() {
        givenOneRowInEventTable();

        var timestamps = rdbUtil.selectList("select timestamp from event", Results.timeStampFrom("timestamp"));
        assertEquals(1, timestamps.size());
    }

    @Test
    void shouldReadAListFromTheDb() {
        givenOneRowInEventTable();

        var timestamps = rdbUtil.selectList("select timestamp from event", Results.timeStampFrom("timestamp"));
        assertTrue(timestamps.size() > 0);
    }

    @Test
    void shouldReadAListOfEvents() {
        givenOneRowInEventTable();

        var events = rdbUtil
                .selectList("select description, timestamp from event",
                        Event::new,
                        Results.stringFrom("description"),
                        Results.timeStampFrom("timestamp"));
        assertTrue(events.size() > 0);
    }

    @Test
    void shouldInsertARow() {
        final LocalTime eventTimeInStockholm = LocalTime.of(14, 22);
        final ZonedDateTime zonedDateTime = LocalDateTime
                .of(date2018_09_15, eventTimeInStockholm)
                .atZone(EU_STOCKHOLM);

        final Timestamp eventTimestamp = Timestamp.from(zonedDateTime.toInstant());

        int count = rdbUtil.updateOrInsert("insert into event values(?,?)",
                Params.string( "Conference"),
                Params.timestamp( eventTimestamp)
        );
        assertEquals(1, count);

        var timestamps = rdbUtil.selectList(
                "select timestamp from event where description = 'Conference'",
                Results.timeStampFrom("timestamp")
        );
        assertTrue(timestamps.size() > 0);
        final Instant persistedEventInstant = timestamps.get(0).toInstant();

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

        int count = rdbUtil.updateOrInsert("insert into event values(?,?)",
                Params.string( "Local Conference"),
                Params.timestamp( eventTimestamp)
        );
        assertEquals(1, count);

        var selectSQL = """
                select timestamp
                from event
                where description = 'Local Conference'
                """;
        var timestamps = rdbUtil.selectList(
                selectSQL,
                Results.timeStampFrom("timestamp")
        );
        assertTrue(timestamps.size() > 0);
        final Instant persistedInstant = timestamps.get(0).toInstant();

        final ZonedDateTime persistedUtcDateTime = ZonedDateTime.ofInstant(persistedInstant, UTC);
        final ZonedDateTime helsinkiDateTime = ZonedDateTime.of(localDateTime, EU_HELSINKI);

        assertEquals(helsinkiDateTime.withZoneSameInstant(UTC), persistedUtcDateTime);

        final ZonedDateTime persistedHelsinkiDateTime = ZonedDateTime.ofInstant(persistedInstant, EU_HELSINKI);
        assertEquals(helsinkiDateTime, persistedHelsinkiDateTime);
        assertEquals(localDateTime, persistedHelsinkiDateTime.toLocalDateTime());
    }
}
