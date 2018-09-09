package org.example;

import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        final String url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password";

        try (
            final Connection conn = DriverManager.getConnection(url);
            final PreparedStatement stmt = conn.prepareStatement("select * from datetimeentity");
            final ResultSet resultSet = stmt.executeQuery()
        ) {
            while (resultSet.next()) {
                final String firstColumn = resultSet.getString(1);
                final String secondColumn = resultSet.getString(2);
                final String thirdColumn = resultSet.getString(3);
                final String fourthColumn = resultSet.getString(4);
                System.out.println("firstColumn " + firstColumn);
                System.out.println("secondColumn " + secondColumn);
                System.out.println("thirdColumn " + thirdColumn);
                System.out.println("fourthColumn " + fourthColumn);
            }
        } catch (PSQLException psqlException) {
            System.err.println(psqlException.getServerErrorMessage());
        }
    }
}
