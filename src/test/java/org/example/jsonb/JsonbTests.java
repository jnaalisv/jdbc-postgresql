package org.example.jsonb;

import org.example.AppContext;
import org.example.rdb.RdbUtil;
import org.example.sql.ResultSetUtil;
import org.example.wizard.SqlWizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.rdb.RdbUtil.booleanParam;
import static org.example.rdb.RdbUtil.objectParam;
import static org.example.rdb.RdbUtil.stringParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonbTests {
    private static final RdbUtil rdbUtil = AppContext.rdbUtil;
    private static final SqlWizard sqlWizard = new SqlWizard(rdbUtil);

    @BeforeEach
    void clearDb() {
        rdbUtil.updateOrInsert("delete from books");
    }

    @Test
    void shouldSelectAlistOfTitles() {
        givenSomeTestData();

        List<String> titles = sqlWizard
                .select("select data ->> 'title' as title from books where ? = ?", booleanParam(true), booleanParam(true))
                .asList(ResultSetUtil.readString("title"));

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

        var books = sqlWizard
                .select("select data from books where (data ->> 'published')::boolean = ?", booleanParam(true))
                .asList( BookData.class);


        assertEquals(4, books.size());
    }

    @Test
    void selectWithParameters() {
        givenSomeTestData();

        String expectedTitle = "Siddhartha";

        Optional<String> maybeTitle = rdbUtil.selectOne(
                "select data ->> 'title' as title from books where data ->> 'title' = ?",
                ResultSetUtil.readString("title"),
                stringParam(expectedTitle)
        );

        assertTrue(maybeTitle.isPresent());
        assertEquals(expectedTitle, maybeTitle.get());
    }

    @Test
    void oneJsonbRowCanBeDeserializedCompletely() {
        givenSomeTestData();

        String expectedTitle = "Siddhartha";

        Optional<BookData> maybeSiddhartha = rdbUtil.selectOne(
                "select data from books where data ->> 'title' = ?",
                1,
                BookData.class,
                stringParam(expectedTitle));

        assertTrue(maybeSiddhartha.isPresent());

        BookData siddhartha = maybeSiddhartha.get();

        assertEquals(expectedTitle, siddhartha.title);
        assertEquals(Arrays.asList(
                "Fiction", "Spirituality"
        ), siddhartha.genres);
        assertTrue(siddhartha.published);
    }

    @Test
    void letsReadToAnEntity() {
        givenSomeTestData();

        var maybeSiddhartha = sqlWizard
                .select("select id, data from books where data ->> 'title' = 'Siddhartha'")
                .as(
                    BookEntity::new,
                    RdbUtil.readLong(),
                    rdbUtil.readJsonToObject( BookData.class)
                );

        assertTrue(maybeSiddhartha.isPresent());

        var siddhartha = maybeSiddhartha.get();

        assertEquals("Siddhartha", siddhartha.getBookData().title);
        assertEquals(Arrays.asList(
                "Fiction", "Spirituality"
        ), siddhartha.getBookData().genres);
        assertTrue(siddhartha.getBookData().published);
    }

    private void givenSomeTestData() {
        rdbUtil.updateOrInsert(
                "insert into books values" +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb), " +
                        "(nextval('serial'), ?::jsonb)",
                objectParam("{\"title\": \"Sleeping Beauties\", \"genres\": [\"Fiction\", \"Thriller\", \"Horror\"], \"published\": false}"),
                objectParam("{\"title\": \"Influence\", \"genres\": [\"Marketing & Sales\", \"Self-Help \", \"Psychology\"], \"published\": true}"),
                objectParam("{\"title\": \"The Dictator's Handbook\", \"genres\": [\"Law\", \"Politics\"], \"authors\": [\"Bruce Bueno de Mesquita\", \"Alastair Smith\"], \"published\": true}"),
                objectParam("{\"title\": \"Deep Work\", \"genres\": [\"Productivity\", \"Reference\"], \"published\": true}"),
                objectParam("{\"title\": \"Siddhartha\", \"genres\": [\"Fiction\", \"Spirituality\"], \"published\": true}")
        );

    }
}
