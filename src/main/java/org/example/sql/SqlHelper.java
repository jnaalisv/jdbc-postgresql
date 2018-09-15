package org.example.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SqlHelper {

    private static final String connectionUrl = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password";

    private static <T> T prepareStatement(String sql, Function<PreparedStatement, T> useStmt) {
        try (Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return useStmt.apply(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T execQuery(String query, Function<ResultSet, T> rsMapper) {
        return prepareStatement(query, stmt -> {
            try (ResultSet resultSet = stmt.executeQuery()) {
                return rsMapper.apply(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static int updateOrInsert(String updateOrInsert) {
        return prepareStatement(updateOrInsert, stmt -> {
            try {
                return stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> Optional<T> selectOne(final String query, Function<ResultSet, T> rsMapper) {
        return execQuery(query, resultSet -> {
            try {
                final T returnValue = resultSet.next() ? rsMapper.apply(resultSet) : null;
                return Optional.ofNullable(returnValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T> List<T> selectList(final String query, Function<ResultSet, T> rsMapper) {
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
}
