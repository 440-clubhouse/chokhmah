package org.team440.ui;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.team440.models.Book;
import org.team440.ui.components.ErrorMessageBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class BookMenu extends JFrame {
    private final JTable table = new JTable();
    private DefaultTableModel model;

    public BookMenu() {
        this.setTitle("Book Management");
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refreshModel();
            }
        });

        var addMenuItem = new JMenuItem("Add a book");
        var removeMenuItem = new JMenuItem("Remove the book");
        var editMenuItem = new JMenuItem("Edit the book");
        addMenuItem.addActionListener(e -> new BookDialog(this));
        removeMenuItem.addActionListener(this::removeBook);
        editMenuItem.addActionListener(this::editBook);
        var popupMenu = new JPopupMenu();
        popupMenu.add(addMenuItem);
        popupMenu.add(removeMenuItem);
        popupMenu.add(editMenuItem);
        this.table.setComponentPopupMenu(popupMenu);

        this.setContentPane(new JScrollPane(this.table));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void refreshModel() {
        try {
            this.model = new DefaultTableModel(Book.find()
                    .stream()
                    .map(book -> new Object[]{book.id(), book.title(), book.author(), book.quantity(), book.price()})
                    .toArray(Object[][]::new), new String[]{"ID", "Title", "Author", "Quantity", "Price"});
            this.table.setModel(this.model);
        } catch (SQLException e) {
            ErrorMessageBox.of(e);
        }
    }

    private void removeBook(ActionEvent e) {
        var selectedRow = this.table.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        try {
            Book.delete((Integer) this.model.getValueAt(selectedRow, 0));
            this.model.removeRow(selectedRow);
        } catch (SQLException exception) {
            ErrorMessageBox.of(exception);
        }
    }

    private void editBook(ActionEvent e) {
        var selectedRow = this.table.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        new BookDialog(this, new Book((Integer) this.model.getValueAt(selectedRow, 0),
                (String) this.model.getValueAt(selectedRow, 1),
                (String) this.model.getValueAt(selectedRow, 2),
                (Integer) this.model.getValueAt(selectedRow, 3),
                (Double) this.model.getValueAt(selectedRow, 4)
        ));
    }
}

class BookDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField authorField;
    private final JSpinner quantitySpinner;
    private final JSpinner priceSpinner;
    private final Book book;

    BookDialog(JFrame owner, @Nullable Book book) {
        super(owner, true);
        var isToInsert = book == null;

        this.setTitle(isToInsert ? "Add a book" : "Edit the book");
        this.book = isToInsert ? new Book() : book;
        this.titleField = new JTextField(this.book.title());
        this.titleField.setPreferredSize(new Dimension(200, this.titleField.getPreferredSize().height));
        this.authorField = new JTextField(this.book.author());
        this.authorField.setPreferredSize(new Dimension(200, this.authorField.getPreferredSize().height));
        this.quantitySpinner = new JSpinner(new SpinnerNumberModel(this.book.quantity().intValue(),
                1,
                Integer.MAX_VALUE,
                1
        ));
        this.quantitySpinner.setPreferredSize(new Dimension(100, quantitySpinner.getPreferredSize().height));
        this.priceSpinner = new JSpinner(new SpinnerNumberModel(this.book.price().doubleValue(),
                0.00,
                Double.MAX_VALUE,
                0.01
        ));
        this.priceSpinner.setPreferredSize(new Dimension(100, priceSpinner.getPreferredSize().height));

        var centerPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        centerPanel.add(new JLabel("Title:"));
        centerPanel.add(titleField);
        centerPanel.add(new JLabel("Author:"));
        centerPanel.add(authorField);
        centerPanel.add(new JLabel("Quantity:"));
        centerPanel.add(quantitySpinner);
        centerPanel.add(new JLabel("Price:"));
        centerPanel.add(priceSpinner);

        var saveButton = new JButton("Save");
        var cancelButton = new JButton("Cancel");
        saveButton.addActionListener(isToInsert ? this::insertBook : this::updateBook);
        cancelButton.addActionListener(e -> dispose());

        var bottomPanel = new JPanel();
        bottomPanel.add(cancelButton);
        bottomPanel.add(saveButton);

        var mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    BookDialog(JFrame owner) {
        this(owner, null);
    }

    private void insertBook(ActionEvent e) {
        try {
            new Book(this.titleField.getText(),
                    this.authorField.getText(),
                    (Integer) this.quantitySpinner.getValue(),
                    (Double) this.priceSpinner.getValue()
            ).insert();
            dispose();
        } catch (SQLException exception) {
            ErrorMessageBox.of(exception);
        }
    }

    private void updateBook(ActionEvent e) {
        try {
            new Book(this.book.id(),
                    this.titleField.getText(),
                    this.authorField.getText(),
                    (Integer) this.quantitySpinner.getValue(),
                    (Double) this.priceSpinner.getValue()
            ).update();
            dispose();
        } catch (SQLException exception) {
            ErrorMessageBox.of(exception);
        }
    }
}
