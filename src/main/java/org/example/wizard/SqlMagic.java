package org.example.wizard;

import org.example.rdb.RdbUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SqlMagic {

    private final RdbUtil rdbUtil;
    private final String query;
    private final BiConsumer<Integer, PreparedStatement>[] sqlParameters;

    @SafeVarargs
    public SqlMagic(RdbUtil rdbUtil, String query, BiConsumer<Integer, PreparedStatement>... sqlParameters) {
        this.rdbUtil = rdbUtil;
        this.query = query;
        this.sqlParameters = sqlParameters;
    }

    public <T> List<T> asList(Function<ResultSet, T> rsMapper) {
        return rdbUtil.selectList(query, rsMapper, sqlParameters);
    }

    public <T> List<T> asList(BiFunction<ResultSet, Integer, T> rsMapper) {
        return rdbUtil.selectList(query, rsMapper, sqlParameters);
    }

    public final <T, A, B> List<T> asList(BiFunction<A, B, T> ctor, BiFunction<ResultSet, Integer, A> mapA, BiFunction<ResultSet, Integer, B> mapB) {
        return rdbUtil.selectList(query, ctor, mapA, mapB, sqlParameters);
    }
}
