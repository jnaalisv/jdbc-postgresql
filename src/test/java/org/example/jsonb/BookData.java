package org.example.jsonb;

import java.beans.ConstructorProperties;
import java.util.List;

public class BookData {

    private final String title;
    private final List<String> genres;
    private final Boolean published;

    @ConstructorProperties({"title", "genres", "published"})
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

}
