package org.example.jsonb;

import java.util.List;

public record BookData(
        String title
        , List<String> genres
        , Boolean published) {
}
