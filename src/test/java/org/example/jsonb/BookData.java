package org.example.jsonb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BookData {

    private final String title;
    private final List<String> genres;
    private final Boolean published;

    public BookData(String title, List<String> genres, Boolean published) {
        this.title = title;
        this.genres = genres;
        this.published = published;
    }

    public String title() {
        return title;
    }

    public List<String> genres() {
        return genres;
    }

    public Boolean published() {
        return published;
    }

    @JsonCreator
    public static BookData deserializeFromJSON(
            @JsonProperty("title") String title,
            @JsonProperty("genres") List<String> genres,
            @JsonProperty("published") Boolean published
    ) {
        return new BookData(title, genres, published);
    }
}
