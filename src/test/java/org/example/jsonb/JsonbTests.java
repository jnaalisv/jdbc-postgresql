package org.example.jsonb;

import org.example.AppContext;
import org.example.rdb.RdbUtil;
import org.example.sql.Params;
import org.example.sql.Results;
import org.example.wizard.SqlWizard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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

        var selectSQL = """
                select data ->> 'title' as title
                from books
                where ? = ?
                """;
        var titles = sqlWizard
                .select(selectSQL, Params.booleanTrue(), Params.booleanTrue())
                .asList(Results.stringFrom("title"));

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

        var selectSQL = """
                select data
                from books
                where (data ->> 'published')::boolean = ?
                """;

        var books = sqlWizard
                .select(selectSQL, Params.booleanTrue())
                .asList(Results.jsonValueAs(BookData.class));

        assertEquals(4, books.size());
    }

    @Test
    void selectWithParameters() {
        givenSomeTestData();
        var expectedTitle = "Siddhartha";

        var selectSQL = """
                select data ->> 'title' as title
                from books
                where data ->> 'title' = ?
                """;
        var selectedTitles = sqlWizard
                .select(selectSQL, Params.string(expectedTitle))
                .asList(Results.stringFrom("title"));

        assertTrue(selectedTitles.size() > 0);
        assertEquals(expectedTitle, selectedTitles.get(0));
    }

    @Test
    void oneJsonbRowCanBeDeserializedCompletely() {
        givenSomeTestData();

        var expectedTitle = "Siddhartha";

        var selectSQL = """
                select data
                from books
                where data ->> 'title' = ?
                """;
        var maybeSiddhartha = sqlWizard
                .select(selectSQL, Params.string(expectedTitle))
                .asList(Results.jsonValueAs(BookData.class));

        assertTrue(maybeSiddhartha.size() > 0);

        var siddhartha = maybeSiddhartha.get(0);

        assertEquals(expectedTitle, siddhartha.title());
        assertEquals(Arrays.asList(
                "Fiction", "Spirituality"
        ), siddhartha.genres());
        assertTrue(siddhartha.published());
    }

    @Test
    void letsReadToAnEntity() {
        givenSomeTestData();

        var selectSQL = """
                select id, data
                from books
                where data ->> 'title' = 'Siddhartha'
                """;
        var maybeSiddhartha = sqlWizard
                .select(selectSQL)
                .asList(
                    BookEntity::new,
                    Results.longValue(),
                    Results.jsonValueAs(BookData.class)
                );

        assertTrue(maybeSiddhartha.size()  > 0);

        var siddhartha = maybeSiddhartha.get(0);

        assertEquals("Siddhartha", siddhartha.getBookData().title());
        assertEquals(Arrays.asList(
                "Fiction", "Spirituality"
        ), siddhartha.getBookData().genres());
        assertTrue(siddhartha.getBookData().published());
    }

    @Test
    void readAListOfEntities() {
        givenSomeTestData();

        var books = sqlWizard
                .select("select id, data from books")
                .asList(
                        BookEntity::new,
                        Results.longValue(),
                        Results.jsonValueAs(BookData.class)
                );

        assertEquals(5, books.size());
        BookData firstBook = books.get(0).getBookData();
        assertEquals(firstBook.title(), "Sleeping Beauties");
        assertEquals(firstBook.genres(), Arrays.asList("Fiction", "Thriller", "Horror"));

        BookData lastBook = books.get(4).getBookData();
        assertEquals(lastBook.title(), "Siddhartha");
        assertEquals(lastBook.genres(), Arrays.asList("Fiction", "Spirituality"));
    }

    private void givenSomeTestData() {
        var insertBooksSQL = """
        insert into books
        values
            (nextval('serial'), ?::jsonb),
            (nextval('serial'), ?::jsonb),
            (nextval('serial'), ?::jsonb),
            (nextval('serial'), ?::jsonb),
            (nextval('serial'), ?::jsonb)
        """;
        rdbUtil.updateOrInsert(
                insertBooksSQL,
                Params.object(
                        """
                        {
                            "title": "Sleeping Beauties",
                            "genres": ["Fiction", "Thriller", "Horror"],
                            "published": false
                        }
                        """),
                Params.object(
                        """
                        {
                            "title": "Influence",
                            "genres": ["Marketing & Sales", "Self-Help ", "Psychology"],
                            "published": true
                        }
                        """),
                Params.object(
                        """
                        {
                            "title": "The Dictator's Handbook",
                            "genres": ["Law", "Politics"],
                            "authors": ["Bruce Bueno de Mesquita", "Alastair Smith"],
                            "published": true
                        }
                        """),
                Params.object(
                        """
                        {
                            "title": "Deep Work",
                            "genres": ["Productivity", "Reference"],
                            "published": true
                        }
                        """),
                Params.object("""
                        {
                            "title": "Siddhartha",
                            "genres": ["Fiction", "Spirituality"],
                            "published": true
                        }
                        """)
        );

    }
}
