package org.example.sql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Functions;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Results {

    private static final ObjectMapper objectMapper = buildDefaultOM();

    private static ObjectMapper buildDefaultOM() {
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }

    public static <T> BiFunction<ResultSet, Integer, T> jsonValueAs(Class<T> columnClassT) {
        return Functions.unchecked((var resultSet, var columnIndex) -> objectMapper.readValue(resultSet.getString(columnIndex), columnClassT));
    }

    public static BiFunction<ResultSet, Integer, Long> longValue() {
        return Functions.unchecked(ResultSet::getLong);
    }

    public static Function<ResultSet, Timestamp> timeStampFrom(String columnLabel) {
        return Functions.unchecked(resultSet -> resultSet.getTimestamp(columnLabel));
    }

    public static Function<ResultSet, String> stringFrom(String columnLabel) {
        return Functions.unchecked(resultSet -> resultSet.getString(columnLabel));
    }

}
