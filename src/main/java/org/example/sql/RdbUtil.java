package org.example.sql;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RdbUtil {

    private final ObjectMapper objectMapper;
    private final SqlUtil sqlUtil;

    public RdbUtil(SqlUtil sqlUtil, ObjectMapper objectMapper) {
        this.sqlUtil = sqlUtil;
        this.objectMapper = objectMapper;
    }

    public <T> List<T> selectList(String query, int columnIndex, Class<T> columnClassT) {
        return sqlUtil.selectList(
                query,
                readType(columnIndex, columnClassT)
        );
    }

    public static Function<ResultSet, Long> readLong(int columnIndex) {
        return resultSet -> {
            try {
                return resultSet.getLong(columnIndex);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }


    public <T> Function<ResultSet, T> readType(int columnIndex, Class<T> columnClassT) {
        return resultSet -> {
            try {
                final String columnValue = resultSet.getString(columnIndex);
                return objectMapper.readValue(columnValue, columnClassT);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public <T> Optional<T> selectOne(String query, int columnIndex, Class<T> columnClassT) {
        return sqlUtil.selectOne(query, resultSet -> {
            try {
                final String columnValue = resultSet.getString(columnIndex);
                return objectMapper.readValue(columnValue, columnClassT);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T, A, B> Optional<T> selectOne(String query, BiFunction<A, B, T> ctor, Function<ResultSet, A> mapA, Function<ResultSet, B> mapB) {
        return sqlUtil.selectOne(query, rs -> {
            A a = mapA.apply(rs);
            B b = mapB.apply(rs);
            return ctor.apply(a, b);
        });
    }
}
