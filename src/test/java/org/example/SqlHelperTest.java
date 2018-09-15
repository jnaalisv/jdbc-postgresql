package org.example;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.example.sql.SqlHelper.getTimestamp;
import static org.example.sql.SqlHelper.prepareStatement;
import static org.example.sql.SqlHelper.selectList;
import static org.example.sql.SqlHelper.selectOne;
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

}
