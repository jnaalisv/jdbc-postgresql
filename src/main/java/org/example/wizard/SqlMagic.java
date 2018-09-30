package org.example.wizard;

import org.example.rdb.RdbUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SqlMagic {

    private final RdbUtil rdbUtil;
    private final String query;
    private final BiConsumer<Integer, PreparedStatement>[] preparedStatementConsumers;

    @SafeVarargs
    public SqlMagic(RdbUtil rdbUtil, String query, BiConsumer<Integer, PreparedStatement>...preparedStatementConsumers) {
        this.rdbUtil = rdbUtil;
        this.query = query;
        this.preparedStatementConsumers = preparedStatementConsumers;
    }

    public <T> Optional<T> as(Function<ResultSet, T> rsMapper) {

        return null;
    }

    public final <T, A, B> Optional<T> as(BiFunction<A, B, T> ctor, BiFunction<ResultSet, Integer, A> mapA, BiFunction<ResultSet, Integer, B> mapB) {
        return rdbUtil.selectOne(query, rs -> {
            A a = mapA.apply(rs, 1);
            B b = mapB.apply(rs, 2);
            return ctor.apply(a, b);
        }, preparedStatementConsumers);
    }

    public <T> List<T> asList(Function<ResultSet, T> rsMapper) {
        return rdbUtil.selectList(query, rsMapper, preparedStatementConsumers);
    }

    /**
     *
     * */
    public <T> List<T> asList(Class<T> columnClassT) {

        return rdbUtil.selectList(
                query,
                rdbUtil.readJsonToObject(1, columnClassT),
                preparedStatementConsumers
        );
    }
}
