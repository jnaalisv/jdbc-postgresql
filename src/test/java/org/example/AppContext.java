package org.example;

import org.example.rdb.RdbUtil;
import org.example.sql.JdbcUtil;

public class AppContext {

    public static final RdbUtil rdbUtil;

    static {
        var postgresConnUrl = "jdbc:postgresql://localhost:5433/postgres?user=postgres&password=password";
        var jdbcUtil = new JdbcUtil(postgresConnUrl);
        rdbUtil = new RdbUtil(jdbcUtil);
    }
}
