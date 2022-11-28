package mate.academy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    static {  //сделали статический блок инициализации чтобы Class.forName с драйвером зарегистрировать один раз при вызове
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//загрузили драйвер(из библиотеки com.mysql - в dependencies)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't load JDBC driver from MySQL", e);
        }
    }

    public static Connection getConnection() {
        try {
            Properties dbProperties = new Properties(); // в dbProperties устанавливаем кредлы для входа в DB
            dbProperties.put("user", "root");
            dbProperties.put("password", "mysql2022");
//            System.out.println("Connected");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", dbProperties); //создали connection
        } catch (SQLException throwables) {
            throw new RuntimeException("Can't create connection to DB", throwables);
        }
    }
}
