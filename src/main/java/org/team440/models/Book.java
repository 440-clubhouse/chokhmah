package org.team440.models;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public record Book(int id, String title, String author, int quantity, double price) {
    public Book(String title, String author, int quantity, double price) {
        this(0, title, author, quantity, price);
    }

    public static List<Book> find() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.createStatement()) {
                var books = new ArrayList<Book>();
                try (var resultSet = statement.executeQuery("SELECT * FROM books")) {
                    while (resultSet.next()) {
                        books.add(new Book(
                                resultSet.getInt("id"),
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getInt("quantity"),
                                resultSet.getDouble("price")
                        ));
                    }
                }
                return books;
            }
        }
    }

    public static Book findOne(int id) throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT * FROM books where id = ?")) {
                statement.setInt(1, id);
                try (var resultSet = statement.executeQuery()) {
                    resultSet.first();
                    return new Book(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("price")
                    );
                }
            }
        }
    }

    public static void delete(int id) throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement("DELETE FROM books WHERE id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
    }

    public int insert() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "INSERT INTO books (title, author, quantity, price) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, this.title);
                statement.setString(2, this.author);
                statement.setInt(3, this.quantity);
                statement.setDouble(4, this.price);
                statement.executeUpdate();
                var resultSet = statement.getGeneratedKeys();
                resultSet.first();
                return resultSet.getInt(1);
            }
        }
    }

    public void update() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "UPDATE books SET title = ?, author = ?, quantity = ?, price = ? WHERE id = ?")) {
                statement.setString(1, this.title);
                statement.setString(2, this.author);
                statement.setInt(3, this.quantity);
                statement.setDouble(4, this.price);
                statement.setInt(5, this.id);
                statement.executeUpdate();
            }
        }
    }
}
