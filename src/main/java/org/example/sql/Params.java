package org.example.sql;

import org.example.Consumers;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.function.BiConsumer;

public class Params {

    public static BiConsumer<Integer, PreparedStatement> string(String string) {
        return Consumers.unchecked((index, preparedStatement) -> preparedStatement.setString(index, string));
    }

    public static BiConsumer<Integer, PreparedStatement> timestamp(Timestamp timestamp) {
        return Consumers.unchecked((index, preparedStatement) -> preparedStatement.setTimestamp(index, timestamp));
    }

    public static BiConsumer<Integer, PreparedStatement> object(Object object) {
        return Consumers.unchecked((index, preparedStatement) -> preparedStatement.setObject(index, object));
    }

    public static BiConsumer<Integer, PreparedStatement> booleanTrue() {
        return bool(true);
    }

    public static BiConsumer<Integer, PreparedStatement> booleanFalse() {
        return bool(false);
    }

    private static BiConsumer<Integer, PreparedStatement> bool(boolean boolValue) {
        return Consumers.unchecked((index, preparedStatement) -> preparedStatement.setBoolean(index, boolValue));
    }
}
