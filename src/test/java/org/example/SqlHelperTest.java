package org.example;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.example.sql.SqlHelper.selectList;
import static org.example.sql.SqlHelper.selectOne;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlHelperTest {

    private Function<ResultSet, Timestamp> rsToTimestamp = rs -> {
        try {
            return rs.getTimestamp("timestamp");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @Test
    void shouldReadOneRowFromTheDb() {
        Optional<Timestamp> maybeTs = selectOne("select timestamp from event", rsToTimestamp);
        assertTrue(maybeTs.isPresent());
    }

    @Test
    void shouldReadAListFromTheDb() {
        List<Timestamp> timestamps = selectList("select timestamp from event", rsToTimestamp);
        assertTrue(timestamps.size() > 0);
    }

}
