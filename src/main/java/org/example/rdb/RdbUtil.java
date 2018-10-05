package org.example.rdb;

import org.example.sql.JdbcUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RdbUtil {

    private final JdbcUtil jdbcUtil;

    public RdbUtil(JdbcUtil jdbcUtil) {
        this.jdbcUtil = jdbcUtil;
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
    public final <T> List<T> selectList(String query, BiFunction<ResultSet, Integer, T> rsMapper, BiConsumer<Integer, PreparedStatement>...sqlParameters) {
        return selectList(
                query,
                resultSet -> rsMapper.apply(resultSet, 1),
                sqlParameters
        );
    }

    @SafeVarargs
    public final <T, A> List<T> selectList(String query, Function<A, T> ctor, Function<ResultSet, A> mapA, BiConsumer<Integer, PreparedStatement>...sqlParameters) {
        return selectList(query, resultSet -> {
            A a = mapA.apply(resultSet);
            return ctor.apply(a);
        }, sqlParameters);
    }

    @SafeVarargs
    public final <T, A, B> List<T> selectList(String query, BiFunction<A, B, T> ctor, Function<ResultSet, A> mapA, Function<ResultSet, B> mapB, BiConsumer<Integer, PreparedStatement>...sqlParameters) {
        return selectList(query, resultSet -> {
            A a = mapA.apply(resultSet);
            B b = mapB.apply(resultSet);
            return ctor.apply(a, b);
        }, sqlParameters);
    }

    @SafeVarargs
    public final <T, A, B> List<T> selectList(String query, BiFunction<A, B, T> ctor, BiFunction<ResultSet, Integer, A> mapA, BiFunction<ResultSet, Integer, B> mapB, BiConsumer<Integer, PreparedStatement>...sqlParameters) {
        return selectList(
                query,
                ctor,
                rs -> mapA.apply(rs, 1),
                rs -> mapB.apply(rs, 2),
                sqlParameters);
    }

    @SafeVarargs
    public final int updateOrInsert(String updateOrInsert, BiConsumer<Integer, PreparedStatement>...sqlParameters) {
        return jdbcUtil.executeUpdate(updateOrInsert, stmt -> {
            int paramIndex = 0;
            for (BiConsumer<Integer, PreparedStatement> sqlParam : sqlParameters) {
                sqlParam.accept(++paramIndex, stmt);
            }
        });
    }
}
