package mate.academy.dao;

import mate.academy.model.LiteraryFormat;
import java.util.List;
import java.util.Optional;

public interface LiteraryFormatDao {
    List<LiteraryFormat> getAll();

    LiteraryFormat create(LiteraryFormat format);

    Optional<LiteraryFormat> get(Long id);

    boolean delete (Long id);
}
