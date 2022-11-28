package mate.academy.dao;

import mate.academy.model.Author;
import mate.academy.model.Book;
import mate.academy.model.LiteraryFormat;
import mate.academy.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    @Override
    public Book create(Book book) {
        String insertRequest = "INSERT INTO books(title, price, literary_format_id) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createBookStatement =
                     connection.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createBookStatement.setString(1, book.getTitle());
            createBookStatement.setBigDecimal(2, book.getPrice());
            createBookStatement.setLong(3, book.getFormat().getId()); //так получаем foreign key - book.getFormat().getId()
            createBookStatement.executeUpdate();
            ResultSet generatedKeys = createBookStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                book.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cant't insert book to DB", e);
        }
        insertAuthors(book); //добавили авторов обязательно после 1го connectionа
        return book;
    }

    private void insertAuthors(Book book) {
        String insertAuthorQuery = "INSERT INTO books_authors (book_id, author_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addAuthorToBookStatement = connection.prepareStatement(insertAuthorQuery)) {
             addAuthorToBookStatement.setLong(1, book.getId());
             for (Author author : book.getAuthors()) {
                 addAuthorToBookStatement.setLong(2, author.getId());
                 addAuthorToBookStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert authors to book", e);
        }
    }


    @Override
    public boolean delete(Long bookId) { //softDelete
        String deleteBookQuery = "UPDATE books SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteBookStatement = connection.prepareStatement(deleteBookQuery)) {
             int numberOfDeletedRows = deleteBookStatement.executeUpdate();
             return numberOfDeletedRows != 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    //update books fields
    // delete all relations in book_authors table where bookId = book.getId()
    // add new relations to book_authors table
    @Override
    public Book update(Book book) {
        String query = "UPDATE books SET title = ?, price = ?, format = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateStatement
                     = connection.prepareStatement(query)) {
//            updateStatement.setString(1, book.getTitle());
//            updateStatement.setBigDecimal(2, book.getPrice());
//            updateStatement.setObject(3, book.getFormat());
//            updateStatement.setArray(4, (Array) book.getAuthors());
//            updateStatement.setLong(5, book.getId());
            ResultSet resultSet = updateStatement.executeQuery();
            if (resultSet.next()) { //если будет хоть одно значение
                book = parseBookWithLiteraryFormat(resultSet);  //получили книгу с LiteraryFormat
            }

            deleteRelationsBookToAuthor(book.getId());
            insertAuthors(book);

            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't update a book " + book, e);
        }
    }

    private void deleteRelationsBookToAuthor(Long id) {   //delete from books_authors
        String query = "DELETE FROM books_authors WHERE book_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't delete note in books_authors relations by book id "
                    + id, e);
        }
    }

    @Override
    public Book get(Long id) {
        String selectRequest = "SELECT b.id as book_id, title, price, " +
                "lf.id as literary_format_id, lf.format FROM books b JOIN literary_formats lf " +
                "ON b.literary_format_id = lf.id WHERE b.id = ? AND b.is_deleted=FALSE;";
        Book book = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getBookStatement =
                     connection.prepareStatement(selectRequest, Statement.RETURN_GENERATED_KEYS)) {
            getBookStatement.setLong(1, id);
            ResultSet resultSet = getBookStatement.executeQuery();
            if (resultSet.next()) { //если будет хоть одно значение
                book = parseBookWithLiteraryFormat(resultSet);  //получили книгу с LiteraryFormat
            }

        } catch (SQLException e) {
            throw new RuntimeException("Cant't get book from DB", e);
        }
        if (book != null) { //обязательно после 1го connectionа
            try {
                book.setAuthors(getAllAuthorsForBook(id));//получили список авторов и записали в book
            } catch (SQLException e) {
                throw new RuntimeException("Can't get authors from DB", e);
            }
        }
        return book;
    }

    //для get(id) получить книгу и жанр
    private Book parseBookWithLiteraryFormat(ResultSet resultSet) throws SQLException {
        Book book = new Book(); //для полей из БД books
        book.setTitle(resultSet.getString("title"));
        book.setPrice(resultSet.getBigDecimal("price"));
        LiteraryFormat literaryFormat = new LiteraryFormat(); //для полей из БД literary_formats
        literaryFormat.setId(resultSet.getObject("literary_format_id", Long.class));
        literaryFormat.setFormat(resultSet.getString("format"));
        book.setFormat(literaryFormat);
        book.setId(resultSet.getObject("book_id", Long.class));
        return book;
    }
    //книги-автор - many to many
    private List<Author> getAllAuthorsForBook(Long bookId) throws SQLException {  //доп. табл. books_authors
        String getAllAuthorsRequest = "SELECT id, name, lastname FROM authors JOIN books_authors " +
                "ON authors.id=books_authors.author_id WHERE books_authors.book_id=?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllAuthorsStatement = connection.prepareStatement(getAllAuthorsRequest)) {
            getAllAuthorsStatement.setLong(1, bookId);
            ResultSet resultSet = getAllAuthorsStatement.executeQuery();
            List<Author> authors = new ArrayList<>();
            while (resultSet.next()) {
                authors.add(parseBookWithAuthors(resultSet));
            }
            return authors;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find authors in DB by book id " + bookId, e);
        }
    }

    //для get(id) получить aвторов книги
    private Author parseBookWithAuthors(ResultSet resultSet) throws SQLException {
        Author author = new Author();
        author.setId(resultSet.getObject("id", Long.class));
        author.setName(resultSet.getString("name"));
        author.setLastname(resultSet.getString("lastname"));
        return author;
    }
}

