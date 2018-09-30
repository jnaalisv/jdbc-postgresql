package org.example.rdb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sql.SqlUtil;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    private final SqlUtil sqlUtil;

    public RdbUtil(SqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }

    public RdbUtil(SqlUtil sqlUtil, ObjectMapper om) {
        this.sqlUtil = sqlUtil;
        objectMapper = om;
    }

    @SafeVarargs
    public final <T> Optional<T> selectOne(String query, Function<ResultSet, T> rsMapper, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return sqlUtil.execQuery(
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
        return sqlUtil.execQuery(
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
    public final <T> List<T> selectList(String query, int columnIndex, Class<T> columnClassT, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return selectList(
                query,
                readJsonAs(columnIndex, columnClassT),
                preparedStatementConsumers
        );
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

    public static <T> Function<ResultSet, T> readJsonAs(int columnIndex, Class<T> columnClassT) {
        return resultSet -> {
            try {
                final String columnValue = resultSet.getString(columnIndex);
                return objectMapper.readValue(columnValue, columnClassT);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        };
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
        return sqlUtil.executeUpdate(updateOrInsert, stmt -> {
            int paramIndex = 0;
            for (BiConsumer<Integer, PreparedStatement> consumer : preparedStatementConsumers) {
                consumer.accept(++paramIndex, stmt);
            }
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

    public static BiConsumer<Integer, PreparedStatement> stringParam(String string) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setString(index, string);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiConsumer<Integer, PreparedStatement> timestampParam(Timestamp timestamp) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setTimestamp(index, timestamp);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiConsumer<Integer, PreparedStatement> objectParam(Object object) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setObject(index, object);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BiConsumer<Integer, PreparedStatement> booleanParam(boolean bool) {
        return (index, preparedStatement) -> {
            try {
                preparedStatement.setBoolean(index, bool);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
