package org.example.sql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ResultSetUtil {

    private static ObjectMapper objectMapper = buildDefaultOM();

    private static ObjectMapper buildDefaultOM() {
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }

    public static <T> BiFunction<ResultSet, Integer, T> readJsonAs(Class<T> columnClassT) {
        return (resultSet, columnIndex) -> {
            try {
                final String columnValue = resultSet.getString(columnIndex);
                return objectMapper.readValue(columnValue, columnClassT);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiFunction<ResultSet, Integer, Long> readLong() {
        return (var resultSet, var columnIndex) -> {
            try {
                return resultSet.getLong(columnIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Function<ResultSet, Timestamp> readTimestamp(String columnLabel) {
        return resultSet -> {
            try {
                return resultSet.getTimestamp(columnLabel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Function<ResultSet, String> readString(String columnLabel) {
        return resultSet -> {
            try {
                return resultSet.getString(columnLabel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
