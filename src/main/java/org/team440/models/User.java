package org.team440.models;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public record User(Integer id, String name) {
    public User(String name) {
        this(0, name);
    }

    public static List<User> find() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.createStatement()) {
                var users = new ArrayList<User>();
                try (var resultSet = statement.executeQuery("SELECT * FROM users")) {
                    while (resultSet.next()) {
                        users.add(new User(resultSet.getInt("id"), resultSet.getString("name")));
                    }
                }
                return users;
            }
        }
    }

    public static User findOne(Integer id) throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT * FROM users where id = ?")) {
                statement.setInt(1, id);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.first();
                    return new User(resultSet.getInt("id"), resultSet.getString("name"));
                }
            }
        }
    }

    public static void delete(Integer id) throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
    }

    public Integer insert() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement("INSERT INTO users (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )
            ) {
                statement.setString(1, this.name);
                statement.executeUpdate();
                var resultSet = statement.getGeneratedKeys();
                resultSet.first();
                return resultSet.getInt(1);
            }
        }
    }
}
