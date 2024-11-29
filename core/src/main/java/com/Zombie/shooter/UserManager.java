package com.Zombie.shooter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserManager {

    public static Connection getConnection() {
        Connection conn = null;
        try {

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/zombie", "root", "");

        } catch (SQLException e) {
            System.out.println("Connection failed! Check output console");
            e.printStackTrace();
        }
        return conn;
    }

    public static void AddUserScore(String username, int score){
        try (Connection connection = getConnection()) {
            String update = "INSERT INTO userscores(username, score) values(?, ?) ";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, username);
            statement.setInt(2, score);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]>DisplayUserScore() {
        List<String[]> scoreList = new ArrayList<>();

        try (Connection connection = getConnection()) {
            String query = "SELECT username, score FROM userscores ORDER BY id DESC LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int score = resultSet.getInt("score");
                scoreList.add(new String[]{username, String.valueOf(score)});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scoreList;
    }
}
