package mate.academy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.dao.BookDao;
import mate.academy.lib.Injector;
import mate.academy.model.Book;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.academy");

    public static void main(String[] args) {
        BookDao bookDao = (BookDao) injector.getInstance(BookDao.class);
        Book newBook = new Book();
        newBook.setTitle("Mate");
        newBook.setPrice(BigDecimal.valueOf(12.34));
        newBook = bookDao.create(newBook);
        System.out.println("Created book: " + newBook.getId() + " | " + newBook.getTitle());

        Optional<Book> foundBook = bookDao.findById(newBook.getId());
        foundBook.ifPresent(book ->
                System.out.println("Found book: " + book.getId() + " | " + book.getTitle()));

        List<Book> books = bookDao.findAll();
        System.out.println("All books in database:");
        for (Book book : books) {
            System.out.println(book.getId() + " | " + book.getTitle() + " | " + book.getPrice());
        }

        newBook.setTitle("New Book");
        newBook.setPrice(BigDecimal.valueOf(43.33));
        newBook = bookDao.update(newBook);
        System.out.println("Updated book: " + newBook.getId() + " | " + newBook.getTitle());

        boolean deleted = bookDao.deleteById(newBook.getId());
        System.out.println("Deleted book: " + deleted);
    }
}
