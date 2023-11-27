package gym.gymtracker.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

    public static Connection connectDb() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/gymtracker", "root", "12345");

            return connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}