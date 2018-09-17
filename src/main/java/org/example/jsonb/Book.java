package org.example.jsonb;

import java.util.List;

public class Book {

    public String title;
    public List<String> genres;
    public Boolean published;

    public Book() {}

    public Book(String title, List<String> genres, Boolean published) {
        this.title = title;
        this.genres = genres;
        this.published = published;
    }
}
