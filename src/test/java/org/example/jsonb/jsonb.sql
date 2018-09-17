create table books (
  data jsonb
);

select * from books;

INSERT INTO books VALUES ('{"title": "Sleeping Beauties", "genres": ["Fiction", "Thriller", "Horror"], "published": false}');
INSERT INTO books VALUES ('{"title": "Influence", "genres": ["Marketing & Sales", "Self-Help ", "Psychology"], "published": true}');
INSERT INTO books VALUES ('{"title": "The Dictator''s Handbook", "genres": ["Law", "Politics"], "authors": ["Bruce Bueno de Mesquita", "Alastair Smith"], "published": true}');
INSERT INTO books VALUES ('{"title": "Deep Work", "genres": ["Productivity", "Reference"], "published": true}');
INSERT INTO books VALUES ('{"title": "Siddhartha", "genres": ["Fiction", "Spirituality"], "published": true}');

select data -> 'title' as title
from books;

select jsonb_array_elements_text(data -> 'genres')
from books;

SELECT data->'title'
FROM books
WHERE data->'genres' @> '["Fiction"]'::jsonb;