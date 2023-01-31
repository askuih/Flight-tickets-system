package org.example;

import java.sql.*;

public class Authorisation {
    private final Connection connection;
    private final String username;
    private final String password;

    public Authorisation(Connection connection, String username, String password) {
        this.connection = connection;
        this.username = username;
        this.password = password;
    }

    protected boolean isUserAdmin() {
        ResultSet resultSet;
        String query = """
                SELECT count(1)
                  FROM tb_user_roles ur, tb_users u, tb_roles r
                 WHERE ur.user_id = u.id
                   AND ur.role_id = r.id
                   AND upper(u.username) = ?
                   AND r.name = 'Администратор'""";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.toUpperCase());
            resultSet  = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    protected boolean authorizeUser() {
        String query = "SELECT count(1) FROM tb_users WHERE username = ? AND password = ?";
        ResultSet resultSet;
        int records = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.toUpperCase());
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                records = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return records == 1;
    }

    protected boolean registerUser() {

        String query = "INSERT INTO tb_users (id, username, password) VALUES " +
                "((SELECT COALESCE(MAX(id) + 1, 1) FROM tb_users), ?, ?)";
        int records = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.toUpperCase());
            preparedStatement.setString(2, password);
            records = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        query = "INSERT INTO tb_user_roles (id, user_id, role_id) VALUES " +
                "((SELECT COALESCE(MAX(id) + 1, 1) FROM tb_user_roles), (SELECT MAX(id) FROM tb_users), 2)";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return records == 1;
    }
}
