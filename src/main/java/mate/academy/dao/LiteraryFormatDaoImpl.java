package mate.academy.dao;

import mate.academy.model.LiteraryFormat;
import mate.academy.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiteraryFormatDaoImpl implements LiteraryFormatDao{

    @Override
    public List<LiteraryFormat> getAll() {
        List<LiteraryFormat> allFormats = new ArrayList<>();
        //Connection connection = ConnectionUtil.getConnection(); -if without TWR
        //Statement getAllFormatsStatment = null; -if without TWR
        try (Connection connection = ConnectionUtil.getConnection();  //TWR
             Statement getAllFormatsStatment = connection.createStatement()) {
            //getAllFormatsStatment = connection.createStatement() - without TWR
//            ResultSet resultSet = getAllFormatsStatment.executeQuery("SELECT * FROM literary_formats");
            ResultSet resultSet = getAllFormatsStatment.executeQuery(
                    "SELECT * FROM literary_formats WHERE is_deleted = false");
            while (resultSet.next()) {
                String format = resultSet.getString("format");
                Long id = resultSet.getObject("id", Long.class);
                //Long id = resultSet.getLong(2);  //можно по номеру колонки
                LiteraryFormat literaryFormat = new LiteraryFormat();
                literaryFormat.setId(id);
                literaryFormat.setFormat(format);
                allFormats.add(literaryFormat);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Can't get all formats from DB", e);
        }
        return allFormats;
    }

    @Override
    public LiteraryFormat create(LiteraryFormat format) {
        String insertFormatRequest = "INSERT INTO literary_formats(format) values(?)";
        try (Connection connection = ConnectionUtil.getConnection();  //TWR с connection и PrepearedStatement
             PreparedStatement createFormatStatement =
                     connection.prepareStatement(insertFormatRequest, Statement.RETURN_GENERATED_KEYS )) {
             //запрос в DB и указ. что хотим получиь сгенерированный ключ
            createFormatStatement.setString(1, format.getFormat()); //cохраняем в первую колонку значение format.getFormat()
            createFormatStatement.executeUpdate(); //делаем операцию Update с запросом в DB
            ResultSet generatedKeys = createFormatStatement.getGeneratedKeys(); //получим сгенерированный ключ и запишем
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                format.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert format to DB", e);
        }

      return  format;
    }

    @Override
    public Optional<LiteraryFormat> get(Long id) {
        String getByIdRequest = "SELECT * FROM literary_formats WHERE id = ? AND is_deleted = FALSE;";
        LiteraryFormat literaryFormat = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getLiteraryFormatByIdStatement =
                     connection.prepareStatement(getByIdRequest)) {
            getLiteraryFormatByIdStatement.setLong(1, id);
            ResultSet resultSet = getLiteraryFormatByIdStatement.executeQuery();
            if (resultSet.next()) {
                literaryFormat = new LiteraryFormat();
                literaryFormat.setId(resultSet.getObject("id", Long.class));
                literaryFormat.setFormat(resultSet.getString("format"));
            }
            return Optional.ofNullable(literaryFormat);
        } catch (SQLException e) {
            throw new RuntimeException("Can't get from DB format by id=" + id, e);
        }
    };

    @Override // При delete мы физически не удаляем записи(softdelete), а ставим отметки в поле is_deleted
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE literary_formats SET is_deleted = true where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();  //TWR с connection и PrepearedStatement
             PreparedStatement createFormatStatement =
                     connection.prepareStatement(deleteRequest, Statement.RETURN_GENERATED_KEYS )) {
            //запрос в DB и указ. что хотим получиь сгенерированный ключ
            createFormatStatement.setLong(1, id); //cохраняем id в первый параметр запроса(where id = ?)
            return createFormatStatement.executeUpdate() >=1; //возвратим true если createFormatStatement.executeUpdate() >=1
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete format from DB", e);
        }
    }
}
