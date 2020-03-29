package org.example.jsonb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BookData(
        @JsonProperty("title") String title
        , @JsonProperty("genres") List<String> genres
        , @JsonProperty("published") Boolean published) {
}
