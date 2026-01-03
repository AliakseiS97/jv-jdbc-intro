package mate.academy.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Book;

@Dao
public class BookDaoImpl implements BookDao {
    private static final String URL =
            "jdbc:mysql://localhost:3306/books_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Dnl547smykv3di";

    @Override
    public Book create(Book book) {
        String sql = "INSERT INTO books (title, price) VALUES (?,?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getTitle());
            stmt.setBigDecimal(2, book.getPrice());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Object idObj = rs.getObject(1); // Получаем как Object
                    if (idObj != null && idObj instanceof Number) {
                        book.setId(((Number) idObj).longValue()); // Преобразуем безопасно в Long
                    }
                }
            }
            return book;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't insert book into database: " + book.getTitle(), e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT id, title, price FROM books WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't find book by id: " + id, e);
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT id, title, price FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }

            return books;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't get all books from database", e);
        }
    }

    @Override
    public Book update(Book book) {
        String sql = "UPDATE books SET title = ?, price = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL,USER,PASSWORD);
                PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setBigDecimal(2, book.getPrice());
            stmt.setLong(3, book.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataProcessingException(
                        "No book found with id: " + book.getId(), null);
            }
            return book;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't update book in database: " + book.getId(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL,USER,PASSWORD);
                PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete book from database: " + id, e);
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();

        Object idObj = rs.getObject("id");
        if (idObj != null && idObj instanceof Number) {
            book.setId(((Number) idObj).longValue());
        }

        book.setTitle(rs.getString("title"));
        book.setPrice(rs.getBigDecimal("price"));

        return book;
    }

}
