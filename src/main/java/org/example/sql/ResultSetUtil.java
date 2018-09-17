package org.example.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.Function;

public class ResultSetUtil {

    public static Function<ResultSet, Timestamp> getTimestamp(String columnLabel) {
        return resultSet -> {
            try {
                return resultSet.getTimestamp(columnLabel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Function<ResultSet, String> getString(String columnLabel) {
        return resultSet -> {
            try {
                return resultSet.getString(columnLabel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Function<ResultSet, String> getString(int columnIndex) {
        return resultSet -> {
            try {
                return resultSet.getString(columnIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
