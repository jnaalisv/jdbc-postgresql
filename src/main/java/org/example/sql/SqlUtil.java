package org.example.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public <T> T execQuery(String query, Function<ResultSet, T> rsMapper) {
        return prepareStatement(query, stmt -> {
            try (ResultSet resultSet = stmt.executeQuery()) {
                return rsMapper.apply(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int executeUpdate(String updateOrInsert, Consumer<PreparedStatement> statementConsumer) {
        return prepareStatement(updateOrInsert, stmt -> {
            try {
                statementConsumer.accept(stmt);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
