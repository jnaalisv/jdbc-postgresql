package org.example.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ResultSetUtil {

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
