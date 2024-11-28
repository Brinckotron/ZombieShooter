package com.Zombie.shooter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserManager {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load MySQL JDBC Driver
            // Establish Connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zombie", "root", "");
        } catch (SQLException e) {
            System.out.println("Connection failed! Check output console");
            e.printStackTrace();
        }
        return conn;
    }

    public static void AddUserScore(String username, int score){
        try (Connection connection = getConnection()) {
            String update = "INSERT INTO userscores(username, score) values(?, ?) ";
            PreparedStatement statement = getConnection().prepareStatement(update);
            statement.setString(1, username);
            statement.setInt(2, score);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
}
