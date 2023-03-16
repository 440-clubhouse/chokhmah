package org.team440.ui;

import org.team440.models.Book;
import org.team440.models.BookOrder;
import org.team440.models.Order;
import org.team440.models.User;
import org.team440.ui.components.ErrorMessageBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderMenu extends JFrame {
    private final JTable table = new JTable();
    private DefaultTableModel model;

    public OrderMenu() {
        this.setTitle("Order Management");
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refreshModel();
            }
        });

        var addMenuItem = new JMenuItem("Add an order");
        var removeMenuItem = new JMenuItem("Remove the order");
        addMenuItem.addActionListener(e -> new AddOrderDialog(this));
        removeMenuItem.addActionListener(e -> {
            var selectedRow = this.table.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            try {
                Order.delete((Integer) this.model.getValueAt(selectedRow, 0));
                this.model.removeRow(selectedRow);
            } catch (SQLException exception) {
                ErrorMessageBox.of(exception);
            }
        });
        var popupMenu = new JPopupMenu();
        popupMenu.add(addMenuItem);
        popupMenu.add(removeMenuItem);
        this.table.setComponentPopupMenu(popupMenu);

        this.setContentPane(new JScrollPane(this.table));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void refreshModel() {
        try {
            this.model = new DefaultTableModel(Order.find()
                    .stream()
                    .map(order -> new Object[]{order.id(), order.user().name(), order.bookOrdersToString(), order.amount(), order.date()})
                    .toArray(Object[][]::new), new String[]{"ID", "User name", "Books", "Amount", "Date"});
            this.table.setModel(this.model);
        } catch (SQLException e) {
            ErrorMessageBox.of(e);
        }
    }
}

class AddOrderDialog extends JDialog {
    private Integer boxCounter = 1;

    AddOrderDialog(Frame owner) {
        super(owner, "Add an order", true);
        try {
            var users = User.find();
            var books = Book.find();

            var userMenu = new JComboBox<>(users.stream().map(User::name).toArray(String[]::new));
            var dateSpinner = new JSpinner(new SpinnerDateModel());
            var verticalBox = Box.createVerticalBox();

            var addButton = new JButton("Add");
            var removeButton = new JButton("Remove");
            var saveButton = new JButton("Save");
            var cancelButton = new JButton("Cancel");
            addButton.addActionListener(e -> {
                verticalBox.add(new BookOrderPanel(books));
                this.boxCounter++;
                verticalBox.revalidate();
                verticalBox.repaint();
                this.pack();
                this.setLocationRelativeTo(null);
            });
            removeButton.addActionListener(e -> {
                if (this.boxCounter <= 1) {
                    return;
                }
                verticalBox.remove(--this.boxCounter);
                verticalBox.revalidate();
                verticalBox.repaint();
                this.pack();
                this.setLocationRelativeTo(null);
            });
            saveButton.addActionListener(e -> {
                try {
                    new Order(
                            users.get(userMenu.getSelectedIndex()),
                            Arrays.stream(verticalBox.getComponents())
                                    .map(component -> ((BookOrderPanel) component).toBookOrder())
                                    .toList(),
                            LocalDateTime.ofInstant(((Date) dateSpinner.getValue()).toInstant(), ZoneId.systemDefault())
                    ).insert();
                } catch (SQLException exception) {
                    ErrorMessageBox.of(exception);
                }
                this.dispose();
            });
            cancelButton.addActionListener(e -> this.dispose());

            var topPanel = new JPanel();
            topPanel.add(new JLabel("User:"));
            topPanel.add(userMenu);
            topPanel.add(new JLabel("Date:"));
            topPanel.add(dateSpinner);

            verticalBox.add(new BookOrderPanel(books));

            var bottomPanel = new JPanel();
            bottomPanel.add(addButton);
            bottomPanel.add(removeButton);
            bottomPanel.add(saveButton);
            bottomPanel.add(cancelButton);

            var mainPanel = new JPanel(new BorderLayout(5, 5));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(verticalBox, BorderLayout.CENTER);
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);

            this.setContentPane(mainPanel);
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        } catch (SQLException e) {
            ErrorMessageBox.of(e);
        }
    }
}

class BookOrderPanel extends JPanel {
    private final JComboBox<String> bookMenu;
    private final JSpinner quantitySpinner;
    private final List<Book> books;

    BookOrderPanel(List<Book> books) {
        this.bookMenu = new JComboBox<>(books.stream().map(Book::title).toArray(String[]::new));
        this.quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        this.books = books;

        this.add(new JLabel("Book:"));
        this.add(this.bookMenu);
        this.add(new JLabel("Quantity:"));
        this.add(this.quantitySpinner);
    }

    BookOrder toBookOrder() {
        return new BookOrder(this.books.get(this.bookMenu.getSelectedIndex()), (int) this.quantitySpinner.getValue());
    }
}
