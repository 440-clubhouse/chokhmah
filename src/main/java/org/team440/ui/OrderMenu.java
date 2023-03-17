package org.team440.ui;

import org.team440.models.Book;
import org.team440.models.BookOrder;
import org.team440.models.Order;
import org.team440.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OrderMenu extends JFrame {
    private final JPopupMenu popupMenu;
    private final JTable table;
    private DefaultTableModel model;

    public OrderMenu() {
        setTitle("Order Management");
        setPreferredSize(new Dimension(700, 400));

        table = new JTable();
        init();

        popupMenu = new JPopupMenu();
        var createMenuItem = new JMenuItem("Create a new order");
        var deleteMenuItem = new JMenuItem("Remove the order");
        createMenuItem.addActionListener(e -> {
            try {
                createOrder();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteMenuItem.addActionListener(this::deleteOrder);
        popupMenu.add(createMenuItem);
        popupMenu.add(deleteMenuItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                    } else {
                        table.clearSelection();
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                init();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {
        model = new DefaultTableModel(new String[]{"ID", "User name", "Books", "Amount", "Date"},
                0
        );
        table.setModel(model);

        try {
            var orders = Order.find();
            for (var order : orders) {
                model.addRow(new Object[]{order.id(), order.user().name(), order.bookOrdersToString(), order.amount(), order.date().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createOrder() throws SQLException {
        new CreateOrderDialog(this).setVisible(true);
    }

    private void deleteOrder(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0 && row < model.getRowCount()) {
            int id = (int) model.getValueAt(row, 0);
            try {
                Order.delete(id);
                model.removeRow(row);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class CreateOrderDialog extends JDialog {
    private final JComboBox<Object> userMenu;
    private final JComboBox<Object> bookMenu;
    private final JSpinner quantitySpinner;
    private final JSpinner dateSpinner;
    private final List<User> users;
    private final List<Book> books;

    public CreateOrderDialog(Frame owner) throws SQLException {
        super(owner, "Order Creation", true);
        users = User.find();
        books = Book.find();
        var userLabel = new JLabel("User:");
        var bookLabel = new JLabel("Book:");
        var quantityLabel = new JLabel("Quantity:");
        var dateLabel = new JLabel("Date:");
        userMenu = new JComboBox<>(users.stream().map(User::name).toArray());
        bookMenu = new JComboBox<>(books.stream().map(Book::title).toArray());
        SpinnerNumberModel quantityModel = new SpinnerNumberModel(0,
                0,
                Integer.MAX_VALUE,
                1
        );
        quantitySpinner = new JSpinner(quantityModel);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        var saveButton = new JButton("Save");
        var cancelButton = new JButton("Cancel");

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(userLabel);
        inputPanel.add(userMenu);
        inputPanel.add(bookLabel);
        inputPanel.add(bookMenu);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantitySpinner);
        inputPanel.add(dateLabel);
        inputPanel.add(dateSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);

        saveButton.addActionListener(this::saveOrder);
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveOrder(ActionEvent e) {
        var order = new Order(users.get(userMenu.getSelectedIndex()),
                Collections.singletonList(new BookOrder(books.get(bookMenu.getSelectedIndex()),
                        (int) quantitySpinner.getValue()
                )),
                LocalDateTime.ofInstant(((Date) dateSpinner.getValue()).toInstant(),
                        ZoneId.systemDefault()
                )
        );
        try {
            order.insert();
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to save order: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
