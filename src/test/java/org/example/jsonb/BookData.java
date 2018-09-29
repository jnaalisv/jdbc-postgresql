package org.example.jsonb;

import java.util.List;

public class BookData {

    public String title;
    public List<String> genres;
    public Boolean published;

    public BookData() {}

    public BookData(String title, List<String> genres, Boolean published) {
        this.title = title;
        this.genres = genres;
        this.published = published;
    }
}
