package org.example.jsonb;

/**
 * Corresponds to table 'books'
 */
public class BookEntity {

    private Long id;
    private BookData data;

    public BookEntity(Long id, BookData data) {
        this.id = id;
        this.data = data;
    }

    public BookData getBookData() {
        return data;
    }
}
