package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sql.Env;
import org.example.sql.RdbUtil;
import org.example.sql.SqlUtil;

public class AppContext {
    public static final RdbUtil rdbUtil;
    public static final SqlUtil sqlUtil;

    static {
        sqlUtil = new SqlUtil(Env.postgresConnUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        rdbUtil = new RdbUtil(sqlUtil, objectMapper);
    }
}
