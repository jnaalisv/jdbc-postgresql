package org.example.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class SqlUtil {

    private final String connectionUrl;

    public SqlUtil(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    private <T> T prepareStatement(String sql, Function<PreparedStatement, T> useStmt) {
        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return useStmt.apply(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T execQuery(String query, Function<ResultSet, T> rsMapper) {
        return prepareStatement(query, stmt -> {
            try (ResultSet resultSet = stmt.executeQuery()) {
                return rsMapper.apply(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Consumer<PreparedStatement> objectParam(int index, Object object) {
        return preparedStatement -> {
            try {
                preparedStatement.setObject(index, object);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Consumer<PreparedStatement> stringParam(int index, String string) {
        return preparedStatement -> {
            try {
                preparedStatement.setString(index, string);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Consumer<PreparedStatement> timestampParam(int index, Timestamp timestamp) {
        return preparedStatement -> {
            try {
                preparedStatement.setTimestamp(index, timestamp);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public int updateOrInsert(String updateOrInsert) {
        return prepareStatement(updateOrInsert, stmt -> {
            try {
                return stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SafeVarargs
    public final int updateOrInsert(String updateOrInsert, Consumer<PreparedStatement>...preparedStatementConsumers) {
        return prepareStatement(updateOrInsert, stmt -> {
            try {
                for (Consumer<PreparedStatement> consumer : preparedStatementConsumers) {
                    consumer.accept(stmt);
                }
                return stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> Optional<T> selectOne(String query, Function<ResultSet, T> rsMapper) {
        return execQuery(query, resultSet -> {
            try {
                final T returnValue = resultSet.next() ? rsMapper.apply(resultSet) : null;
                return Optional.ofNullable(returnValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T, A> Optional<T> selectOne(String q, Function<A, T> ctor, Function<ResultSet, A> mapA) {
        return selectOne(q, rs -> {
            A a = mapA.apply(rs);
            return ctor.apply(a);
        });
    }

    public <T, A, B> Optional<T> selectOne(String query, BiFunction<A, B, T> ctor, Function<ResultSet, A> mapA, Function<ResultSet, B> mapB) {
        return selectOne(query, rs -> {
            A a = mapA.apply(rs);
            B b = mapB.apply(rs);
            return ctor.apply(a, b);
        });
    }

    @FunctionalInterface
    public interface Fun3<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    public <T, A, B, C> Optional<T> selectOne(String query, Fun3<A, B, C, T> ctor, Function<ResultSet, A> mapA, Function<ResultSet, B> mapB, Function<ResultSet, C> mapC) {
        return selectOne(query, resultSet -> {
            A a = mapA.apply(resultSet);
            B b = mapB.apply(resultSet);
            C c = mapC.apply(resultSet);
            return ctor.apply(a, b, c);
        });
    }

    public <T> List<T> selectList(final String query, Function<ResultSet, T> rsMapper) {
        return execQuery(query, resultSet -> {
            final List<T> resultList = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    resultList.add(rsMapper.apply(resultSet));
                }
                return resultList;
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
