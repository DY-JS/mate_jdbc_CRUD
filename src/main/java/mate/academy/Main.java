package mate.academy;

import mate.academy.dao.LiteraryFormatDao;
import mate.academy.dao.LiteraryFormatDaoImpl;
import mate.academy.models.LiteraryFormat;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        LiteraryFormat format = new LiteraryFormat();
        format.setFormat("Proza");
        LiteraryFormatDao literaryFormatDao = new LiteraryFormatDaoImpl();
        LiteraryFormat sf = literaryFormatDao.create(format);
        System.out.println(sf);
        System.out.println(literaryFormatDao.delete(sf.getId()));
        //List<LiteraryFormat> allFormats = literaryFormatDao.getAll();
        literaryFormatDao.getAll().forEach(System.out::println);
    }
}
