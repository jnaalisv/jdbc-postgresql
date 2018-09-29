package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rdb.RdbUtil;
import org.example.sql.SqlUtil;

public class AppContext {

    /**
     * see postgres.yml
     */
    public static final String postgresConnUrl = "jdbc:postgresql://localhost:5433/postgres?user=postgres&password=password";

    public static final RdbUtil rdbUtil;
    public static final SqlUtil sqlUtil;

    static {
        sqlUtil = new SqlUtil(postgresConnUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        rdbUtil = new RdbUtil(sqlUtil, objectMapper);
    }
}
