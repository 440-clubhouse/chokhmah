package org.team440.models;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record Order(Integer id, User user, List<BookOrder> bookOrders,
                    LocalDateTime date) {
    public Order(
            User user, List<BookOrder> bookOrders, LocalDateTime date
    ) {
        this(null, user, bookOrders, date);
    }

    public static List<Order> find() throws SQLException {
        var innerOrders = InnerOrder.find();
        return innerOrders.stream()
                .map(InnerOrder::id)
                .distinct()
                .map(id -> innerOrders.stream()
                        .filter(innerOrder -> Objects.equals(innerOrder.id(),
                                id
                        ))
                        .toList())
                .map(idOrder -> {
                    var bookOrders = idOrder.stream().map(innerOrder -> {
                        try {
                            return new BookOrder(
                                    Book.findOne(innerOrder.bookId()),
                                    innerOrder.quantity()
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Order order;
                    try {
                        order = new Order(idOrder.get(0).id(),
                                User.findOne(idOrder.get(0).userId()),
                                bookOrders.toList(),
                                idOrder.get(0).date()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return order;
                })
                .toList();
    }

    public static void delete(int id) throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "DELETE FROM orders WHERE id = ?")
            ) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
    }

    public void insert() throws SQLException {
        bookOrders.stream().map(bookOrder -> new InnerOrder(this.user.id(),
                bookOrder.book().id(),
                bookOrder.quantity(),
                this.date
        )).forEach(innerOrder -> {
            try {
                innerOrder.insert();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String bookOrdersToString() {
        return this.bookOrders.stream()
                .map(bookOrder -> bookOrder.book()
                        .title() + ": " + bookOrder.quantity())
                .collect(Collectors.joining("\n"));
    }

    public double amount() {
        return this.bookOrders.stream()
                .map(bookOrder -> bookOrder.book()
                        .price() * bookOrder.quantity())
                .reduce(0.0, Double::sum);
    }
}

record InnerOrder(Integer id, int userId, int bookId, int quantity,
                  LocalDateTime date) {
    InnerOrder(
            int userId, int bookId, int quantity, LocalDateTime date
    ) {
        this(null, userId, bookId, quantity, date);
    }

    static List<InnerOrder> find() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var statement = connection.createStatement()) {
                var innerOrders = new ArrayList<InnerOrder>();
                try (var resultSet = statement.executeQuery(
                        "SELECT * FROM orders")
                ) {
                    while (resultSet.next()) {
                        innerOrders.add(new InnerOrder(resultSet.getInt("id"),
                                resultSet.getInt("user_id"),
                                resultSet.getInt("book_id"),
                                resultSet.getInt("quantity"),
                                resultSet.getObject("date", LocalDateTime.class)
                        ));
                    }
                }
                return innerOrders;
            }
        }
    }

    public void insert() throws SQLException {
        try (var connection = Db.pool.getConnection()) {
            try (var queryStatement = connection.createStatement()) {
                try (var resultSet = queryStatement.executeQuery(
                        "SELECT MAX(id) as max_id FROM orders")
                ) {
                    resultSet.first();
                    var maxId = resultSet.getInt("max_id");
                    try (var statement = connection.prepareStatement(
                            "INSERT INTO orders VALUES (?, ?, ?, ?, ?)")
                    ) {
                        statement.setInt(1, maxId + 1);
                        statement.setInt(2, this.userId);
                        statement.setInt(3, this.bookId);
                        statement.setInt(4, this.quantity);
                        statement.setTimestamp(5, Timestamp.valueOf(this.date));
                        statement.executeUpdate();
                    }
                }
            }
        }
    }
}
