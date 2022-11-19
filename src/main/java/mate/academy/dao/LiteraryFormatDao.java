package mate.academy.dao;

import mate.academy.models.LiteraryFormat;
import java.util.List;

public interface LiteraryFormatDao {
    List<LiteraryFormat> getAll();

    LiteraryFormat create(LiteraryFormat format);

    boolean delete (Long id);
}
