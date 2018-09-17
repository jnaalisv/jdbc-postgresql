package org.example.sql;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.example.sql.ResultSetUtil.getString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlUtilTest {
    private static final SqlUtil sqlUtil = new SqlUtil("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password");

    @Test
    void selectOneConstant_returnsConstantValue() {
        Optional<String> maybeResult = sqlUtil.selectOne("select 'value' as value", getString("value"));

        assertTrue(maybeResult.isPresent());
        assertEquals(maybeResult.get(), "value");
    }
}
