package org.example.sql;

import org.example.AppContext;
import org.example.rdb.RdbUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RdbUtilTest {
    private static final RdbUtil rdbUtil = AppContext.rdbUtil;

    @Test
    void selectOneConstant_returnsConstantValue() {
        Optional<String> maybeResult = rdbUtil.selectOne("select 'value' as value", Results.readString("value"));

        assertTrue(maybeResult.isPresent());
        assertEquals(maybeResult.get(), "value");
    }
}
