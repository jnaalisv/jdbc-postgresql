package org.example.sql;

import org.example.AppContext;
import org.example.rdb.RdbUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RdbUtilTest {
    private static final RdbUtil rdbUtil = AppContext.rdbUtil;

    @Test
    void selectConstant_returnsConstantValue() {
        var results = rdbUtil.selectList("select 'value' as value", Results.stringFrom("value"));

        assertTrue(results.size() > 0);
        assertEquals(results.get(0), "value");
    }
}
