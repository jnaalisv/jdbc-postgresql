package org.example.wizard;

import org.example.rdb.RdbUtil;

import java.sql.PreparedStatement;
import java.util.function.BiConsumer;

public class SqlWizard {
    private final RdbUtil rdbUtil;

    public SqlWizard(RdbUtil rdbUtil) {
        this.rdbUtil = rdbUtil;
    }

    public SqlMagic select(String query, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        return new SqlMagic(rdbUtil, query, preparedStatementConsumers);
    }

}
