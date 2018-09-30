package org.example.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.BiConsumer;

public class Params {

    public static BiConsumer<Integer, PreparedStatement> string(String string) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setString(index, string);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiConsumer<Integer, PreparedStatement> timestamp(Timestamp timestamp) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setTimestamp(index, timestamp);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiConsumer<Integer, PreparedStatement> object(Object object) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setObject(index, object);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiConsumer<Integer, PreparedStatement> booleanTrue() {
        return bool(true);
    }

    public static BiConsumer<Integer, PreparedStatement> booleanFalse() {
        return bool(false);
    }

    private static BiConsumer<Integer, PreparedStatement> bool(boolean boolValue) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setBoolean(index, boolValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
