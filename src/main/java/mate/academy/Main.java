package mate.academy;

import mate.academy.dao.BookDao;
import mate.academy.dao.BookDaoImpl;
import mate.academy.dao.LiteraryFormatDao;
import mate.academy.dao.LiteraryFormatDaoImpl;
import mate.academy.model.Book;
import mate.academy.model.LiteraryFormat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws SQLException {
        LiteraryFormatDao literaryFormatDao = new LiteraryFormatDaoImpl();
        LiteraryFormat format = new LiteraryFormat();
        BookDao bookDao = new BookDaoImpl();
//        format.setFormat("Tragedy");
//        Book book = new Book();
//        book.setTitle("Romeo with Guille");
//        book.setPrice(BigDecimal.valueOf(200));
        //LiteraryFormat f = literaryFormatDao.get(3L).get();
        //book.setFormat(f);
        //book.setFormat();
       //bookDao.create(book);
        Book n = bookDao.get(2L);
        System.out.println(n);
//        LiteraryFormat sf = literaryFormatDao.create(format);
//        System.out.println(sf);
        //System.out.println(literaryFormatDao.delete(sf.getId()));
        //List<LiteraryFormat> allFormats = literaryFormatDao.getAll();
        //literaryFormatDao.getAll().forEach(System.out::println);
    }
}
