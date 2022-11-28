package mate.academy.dao;

import mate.academy.model.Book;

import java.sql.SQLException;

public interface BookDao {
    Book create(Book book);

    Book get(Long id);

    boolean delete(Long bookId);

    Book update(Book book);
}
