package org.example.rdb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sql.JdbcUtil;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RdbUtil {
    private static ObjectMapper objectMapper = buildDefaultOM();

    private static ObjectMapper buildDefaultOM() {
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }

    private final JdbcUtil jdbcUtil;

    public RdbUtil(JdbcUtil jdbcUtil) {
        this.jdbcUtil = jdbcUtil;
    }

    public RdbUtil(JdbcUtil jdbcUtil, ObjectMapper om) {
        this.jdbcUtil = jdbcUtil;
        objectMapper = om;
    }

    @SafeVarargs
    public final <T> Optional<T> selectOne(String query, Function<ResultSet, T> rsMapper, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return jdbcUtil.execQuery(
                query,
                stmt -> {
                    int paramIndex = 0;
                    for (BiConsumer<Integer, PreparedStatement> consumer : preparedStatementConsumers) {
                        consumer.accept(++paramIndex, stmt);
                    }
                },
                resultSet -> {
                    try {
                        final T returnValue = resultSet.next() ? rsMapper.apply(resultSet) : null;
                        return Optional.ofNullable(returnValue);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                );
    }

    @SafeVarargs
    public final <T> List<T> selectList(String query, BiFunction<ResultSet, Integer, T> rsMapper, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return selectList(
                query,
                resultSet -> rsMapper.apply(resultSet, 1),
                preparedStatementConsumers
        );
    }

    @SafeVarargs
    public final <T> List<T> selectList(String query, Function<ResultSet, T> rsMapper, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return jdbcUtil.execQuery(
                query,
                stmt -> {
                    int paramIndex = 0;
                    for (BiConsumer<Integer, PreparedStatement> consumer : preparedStatementConsumers) {
                        consumer.accept(++paramIndex, stmt);
                    }
                },
                resultSet -> {
                    final List<T> resultList = new ArrayList<>();
                    try {
                        while (resultSet.next()) {
                            resultList.add(rsMapper.apply(resultSet));
                        }
                        return resultList;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @SafeVarargs
    public final <T> Optional<T> selectOne(String query, int columnIndex, Class<T> columnClassT, BiConsumer<Integer, PreparedStatement>... preparedStatementConsumers) {
        return selectOne(query, resultSet -> {
            try {
                final String columnValue = resultSet.getString(columnIndex);
                return objectMapper.readValue(columnValue, columnClassT);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, preparedStatementConsumers);
    }

    @SafeVarargs
    public final <T, A> Optional<T> selectOne(String q, Function<A, T> ctor, Function<ResultSet, A> mapA, BiConsumer<Integer, PreparedStatement>... preparedStatementConsumers) {
        return selectOne(q, rs -> {
            A a = mapA.apply(rs);
            return ctor.apply(a);
        }, preparedStatementConsumers);
    }

    @SafeVarargs
    public final <T, A, B> Optional<T> selectOne(String query, BiFunction<A, B, T> ctor, Function<ResultSet, A> mapA, Function<ResultSet, B> mapB, BiConsumer<Integer, PreparedStatement>... preparedStatementConsumers) {
        return selectOne(query, rs -> {
            A a = mapA.apply(rs);
            B b = mapB.apply(rs);
            return ctor.apply(a, b);
        }, preparedStatementConsumers);
    }

    @SafeVarargs
    public final int updateOrInsert(String updateOrInsert, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return jdbcUtil.executeUpdate(updateOrInsert, stmt -> {
            int paramIndex = 0;
            for (BiConsumer<Integer, PreparedStatement> consumer : preparedStatementConsumers) {
                consumer.accept(++paramIndex, stmt);
            }
        });
    }

    public <T, A> List<T> selectList(String query, Function<A, T> ctor, Function<ResultSet, A> mapA) {
        return selectList(query, rs -> {
            A a = mapA.apply(rs);
            return ctor.apply(a);
        });
    }

    public <T, A, B> List<T> selectList(String query, BiFunction<A, B, T> ctor, Function<ResultSet, A> mapA, Function<ResultSet, B> mapB) {
        return selectList(query, rs -> {
            A a = mapA.apply(rs);
            B b = mapB.apply(rs);
            return ctor.apply(a, b);
        });
    }

}
