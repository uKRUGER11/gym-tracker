module gym.gymtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens gym.gymtracker to javafx.fxml;
    exports gym.gymtracker;
    exports gym.gymtracker.db;
    opens gym.gymtracker.db to javafx.fxml;
}