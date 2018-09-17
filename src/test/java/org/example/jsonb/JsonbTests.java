package org.example.jsonb;

import org.example.sql.SqlDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.example.sql.SqlDb.getString;
import static org.example.sql.SqlDb.getTimestamp;
import static org.example.sql.SqlDb.objectParam;
import static org.example.sql.SqlDb.stringParam;
import static org.example.sql.SqlDb.timestampParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonbTests {
    private static final SqlDb sqlDb = new SqlDb("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password");

    @BeforeEach
    void clearDb() {
        sqlDb.updateOrInsert("delete from books");
    }

    @Test
    void shouldSelectAlistOfTitles() {
        givenSomeTestData();
        List<String> titles = sqlDb.selectList("select data ->> 'title' as title from books", getString("title"));

        assertEquals(Arrays.asList(
                "Sleeping Beauties",
                "Influence",
                "The Dictator's Handbook",
                "Deep Work",
                "Siddhartha"
        ), titles);
    }

    void givenSomeTestData() {
        sqlDb.updateOrInsert(
                "insert into books values(?::jsonb), (?::jsonb), (?::jsonb), (?::jsonb), (?::jsonb)",
                objectParam(1, "{\"title\": \"Sleeping Beauties\", \"genres\": [\"Fiction\", \"Thriller\", \"Horror\"], \"published\": false}"),
                objectParam(2, "{\"title\": \"Influence\", \"genres\": [\"Marketing & Sales\", \"Self-Help \", \"Psychology\"], \"published\": true}"),
                objectParam(3, "{\"title\": \"The Dictator's Handbook\", \"genres\": [\"Law\", \"Politics\"], \"authors\": [\"Bruce Bueno de Mesquita\", \"Alastair Smith\"], \"published\": true}"),
                objectParam(4, "{\"title\": \"Deep Work\", \"genres\": [\"Productivity\", \"Reference\"], \"published\": true}"),
                objectParam(5, "{\"title\": \"Siddhartha\", \"genres\": [\"Fiction\", \"Spirituality\"], \"published\": true}")
        );

    }
}
