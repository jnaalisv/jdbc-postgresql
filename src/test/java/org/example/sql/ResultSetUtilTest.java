package org.example.sql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResultSetUtilTest {

    @Test
    void getString_returnsStringByColumn(@Mock ResultSet resultSet) throws SQLException {
        String columnLabel = "columnA";

        ResultSetUtil.getString(columnLabel).apply(resultSet);

        verify(resultSet).getString(columnLabel);
    }
}
