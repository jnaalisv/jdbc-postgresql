package org.example.jsonb;

import org.example.AppContext;
import org.example.sql.RdbUtil;
import org.example.sql.SqlUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.sql.ResultSetUtil.getString;
import static org.example.sql.SqlUtil.objectParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonbTests {
    private static final RdbUtil rdbUtil = AppContext.rdbUtil;
    private static final SqlUtil sqlUtil = AppContext.sqlUtil;

    @BeforeEach
    void clearDb() {
        sqlUtil.updateOrInsert("delete from books");
    }

    @Test
    void shouldSelectAlistOfTitles() {
        givenSomeTestData();
        List<String> titles = sqlUtil.selectList("select data ->> 'title' as title from books", getString("title"));

        assertEquals(Arrays.asList(
                "Sleeping Beauties",
                "Influence",
                "The Dictator's Handbook",
                "Deep Work",
                "Siddhartha"
        ), titles);
    }

    @Test
    void jsonbCanBeDeserializedIntoAnObject() {
        givenSomeTestData();

        List<BookData> books = rdbUtil.selectList("select data from books", 1, BookData.class);

        assertEquals(5, books.size());
    }

    @Test
    void oneJsonbRowCanBeDeserializedCompletely() {
        givenSomeTestData();

        Optional<BookData> maybeSiddhartha = rdbUtil.selectOne(
                "select data from books where data ->> 'title' = 'Siddhartha'",
                1,
                BookData.class);

        assertTrue(maybeSiddhartha.isPresent());

        BookData siddhartha = maybeSiddhartha.get();

        assertEquals("Siddhartha", siddhartha.title);
        assertEquals(Arrays.asList(
                "Fiction", "Spirituality"
        ), siddhartha.genres);
        assertTrue(siddhartha.published);
    }

    @Test
    void letsReadToAnEntity() {
        givenSomeTestData();

        Optional<BookEntity> maybeSiddhartha = rdbUtil.selectOne(
                "select id, data from books where data ->> 'title' = 'Siddhartha'",
                BookEntity::new,
                RdbUtil.readLong(1),
                rdbUtil.readType(2, BookData.class)
        );

        assertTrue(maybeSiddhartha.isPresent());

        BookEntity siddhartha = maybeSiddhartha.get();

        assertEquals("Siddhartha", siddhartha.getBookData().title);
        assertEquals(Arrays.asList(
                "Fiction", "Spirituality"
        ), siddhartha.getBookData().genres);
        assertTrue(siddhartha.getBookData().published);
    }

    void givenSomeTestData() {
        sqlUtil.updateOrInsert(
                "insert into books values" +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb)",
                objectParam(1, "{\"title\": \"Sleeping Beauties\", \"genres\": [\"Fiction\", \"Thriller\", \"Horror\"], \"published\": false}"),
                objectParam(2, "{\"title\": \"Influence\", \"genres\": [\"Marketing & Sales\", \"Self-Help \", \"Psychology\"], \"published\": true}"),
                objectParam(3, "{\"title\": \"The Dictator's Handbook\", \"genres\": [\"Law\", \"Politics\"], \"authors\": [\"Bruce Bueno de Mesquita\", \"Alastair Smith\"], \"published\": true}"),
                objectParam(4, "{\"title\": \"Deep Work\", \"genres\": [\"Productivity\", \"Reference\"], \"published\": true}"),
                objectParam(5, "{\"title\": \"Siddhartha\", \"genres\": [\"Fiction\", \"Spirituality\"], \"published\": true}")
        );

    }
}
