package org.example.wizard;

import org.example.sql.Env;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class SqlMagic {

    private final String query;
    private final BiConsumer<Integer, PreparedStatement>[] preparedStatementConsumers;

    public <T> T prepareStatement(String sql, Function<PreparedStatement, T> useStmt) {
        try (Connection conn = DriverManager.getConnection(Env.postgresConnUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return useStmt.apply(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T execQuery(String query, Consumer<PreparedStatement> statementConsumer, Function<ResultSet, T> rsMapper) {
        return prepareStatement(query, stmt -> {
            statementConsumer.accept(stmt);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return rsMapper.apply(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SafeVarargs
    public SqlMagic(String query, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        this.query = query;
        this.preparedStatementConsumers = preparedStatementConsumers;
    }

    public <T> Optional<T> as(Function<ResultSet, T> rsMapper) {

        return null;
    }

    public <T> List<T> asList(Function<ResultSet, T> rsMapper) {
        return execQuery(
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
}
