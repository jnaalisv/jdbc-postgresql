package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.sql.SqlHelper.getTimestamp;
import static org.example.sql.SqlHelper.selectList;
import static org.example.sql.SqlHelper.selectOne;
import static org.example.sql.SqlHelper.setString;
import static org.example.sql.SqlHelper.setTimestamp;
import static org.example.sql.SqlHelper.updateOrInsert;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlHelperTest {

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
    void shouldInsertARow() {
        final ZonedDateTime zonedDateTime = LocalDateTime
                .of(2018, 8, 9, 14, 22)
                .atZone(ZoneId.of("Europe/Stockholm"));
        final Timestamp tsInStockholmTimeZone = Timestamp.from(zonedDateTime.toInstant());

        int count = updateOrInsert("insert into event values(?,?)",
                setString(1, "Conference"),
                setTimestamp(2, tsInStockholmTimeZone)
        );
        assertEquals(1, count);

        Optional<Timestamp> maybeTs = selectOne("select timestamp from event where description = 'Conference'", getTimestamp("timestamp"));
        assertTrue(maybeTs.isPresent());
        Timestamp tsFromDb = maybeTs.get();

        assertEquals(tsInStockholmTimeZone, tsFromDb);
    }
}
