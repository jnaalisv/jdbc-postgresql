package org.example;

import org.example.rdb.RdbUtil;
import org.example.sql.JdbcUtil;

public class AppContext {

    /**
     * see postgres.yml
     */
    public static final String postgresConnUrl = "jdbc:postgresql://localhost:5433/postgres?user=postgres&password=password";

    public static final RdbUtil rdbUtil;
    public static final JdbcUtil jdbcUtil;

    static {
        jdbcUtil = new JdbcUtil(postgresConnUrl);
        rdbUtil = new RdbUtil(jdbcUtil);
    }
}
