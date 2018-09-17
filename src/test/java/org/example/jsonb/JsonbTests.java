package org.example.jsonb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.sql.Env;
import org.example.sql.SqlUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.example.sql.ResultSetUtil.getString;
import static org.example.sql.SqlUtil.objectParam;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonbTests {
    private static final SqlUtil sqlUtil = new SqlUtil(Env.postgresConnUrl);

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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        List<Book> books = sqlUtil.selectList("select data from books", resultSet -> {
            try {
                return objectMapper.readValue(resultSet.getString(1), Book.class);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(5, books.size());
    }

    void givenSomeTestData() {
        sqlUtil.updateOrInsert(
                "insert into books values(?::jsonb), (?::jsonb), (?::jsonb), (?::jsonb), (?::jsonb)",
                objectParam(1, "{\"title\": \"Sleeping Beauties\", \"genres\": [\"Fiction\", \"Thriller\", \"Horror\"], \"published\": false}"),
                objectParam(2, "{\"title\": \"Influence\", \"genres\": [\"Marketing & Sales\", \"Self-Help \", \"Psychology\"], \"published\": true}"),
                objectParam(3, "{\"title\": \"The Dictator's Handbook\", \"genres\": [\"Law\", \"Politics\"], \"authors\": [\"Bruce Bueno de Mesquita\", \"Alastair Smith\"], \"published\": true}"),
                objectParam(4, "{\"title\": \"Deep Work\", \"genres\": [\"Productivity\", \"Reference\"], \"published\": true}"),
                objectParam(5, "{\"title\": \"Siddhartha\", \"genres\": [\"Fiction\", \"Spirituality\"], \"published\": true}")
        );

    }
}
