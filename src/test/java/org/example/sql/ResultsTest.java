package org.example.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResultsTest {

    @Test
    void getString_returnsStringByColumn(@Mock ResultSet resultSet) throws SQLException {
        var columnLabel = "columnA";

        Results.stringFrom(columnLabel).apply(resultSet);

        verify(resultSet).getString(columnLabel);
    }
}
