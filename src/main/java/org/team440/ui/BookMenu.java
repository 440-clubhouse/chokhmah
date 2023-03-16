package org.team440.ui;

import org.team440.models.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class BookMenu extends JFrame {
    private final JPopupMenu popupMenu;
    private final JTable table;
    private DefaultTableModel model;

    public BookMenu() {
        setTitle("Book Management");
        setPreferredSize(new Dimension(600, 400));

        table = new JTable();
        init();

        // Add right-click menu
        popupMenu = new JPopupMenu();
        JMenuItem editMenuItem = new JMenuItem("Edit the book");
        JMenuItem createMenuItem = new JMenuItem("Create a new book");
        JMenuItem deleteMenuItem = new JMenuItem("Remove the book");
        editMenuItem.addActionListener(this::editBook);
        createMenuItem.addActionListener(this::createBook);
        deleteMenuItem.addActionListener(this::deleteBook);
        popupMenu.add(editMenuItem);
        popupMenu.add(createMenuItem);
        popupMenu.add(deleteMenuItem);

        // Add right-click listener to table
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

        // Add table to scroll pane and set as content pane
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {
        // Initialize table model and sorter
        model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Quantity", "Price"}, 0);
        table.setModel(model);

        // Populate table with data from database
        try {
            List<Book> books = Book.find();
            for (Book book : books) {
                model.addRow(new Object[]{book.id(), book.title(), book.author(), book.quantity(), book.price()});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editBook(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0 && row < model.getRowCount()) {
            int id = (int) model.getValueAt(row, 0);
            String title = (String) model.getValueAt(row, 1);
            String author = (String) model.getValueAt(row, 2);
            int quantity = (int) model.getValueAt(row, 3);
            double price = (double) model.getValueAt(row, 4);
            Book book = new Book(id, title, author, quantity, price);
            new BookDialog(this,book,1).setVisible(true);
        }
    }

    private void createBook(ActionEvent e) {
        new BookDialog(this, new Book(),0).setVisible(true);
    }

    private void deleteBook(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row >= 0 && row < model.getRowCount()) {
            int id = (int) model.getValueAt(row, 0);
            try {
                Book.delete(id);
                model.removeRow(row);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}


class BookDialog extends JDialog {
    private final JTextField titleField;
    private final JTextField authorField;
    private final JSpinner quantitySpinner;
    private final JSpinner priceSpinner;
    private Book book;

    public BookDialog(Frame owner,Book book,int flag) {
        super(owner, "Book Creation", true);

        this.book = book;
        // Create UI components
        JLabel titleLabel = new JLabel("Title:");
        JLabel authorLabel = new JLabel("Author:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JLabel priceLabel = new JLabel("Price:");
        titleField = new JTextField(book.title());
        authorField = new JTextField(book.author());
        SpinnerNumberModel quantityModel = new SpinnerNumberModel(book.quantity().intValue(), 0, Integer.MAX_VALUE, 1);
        quantitySpinner = new JSpinner(quantityModel);
        SpinnerNumberModel priceModel = new SpinnerNumberModel(book.price().doubleValue(), 0.0, Double.MAX_VALUE, 0.01);
        priceSpinner = new JSpinner(priceModel);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Layout UI components
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(authorLabel);
        inputPanel.add(authorField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantitySpinner);
        inputPanel.add(priceLabel);
        inputPanel.add(priceSpinner);

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

        // Add action listeners
        if (flag == 0) {
            saveButton.addActionListener(this::saveBook);
        }else {
            saveButton.addActionListener(this::updateBook);
        }

        cancelButton.addActionListener(e -> dispose());
    }

        private void saveBook(ActionEvent e) {
        book = new Book(
                titleField.getText(),
                authorField.getText(),
                (int) quantitySpinner.getValue(),
                (double) priceSpinner.getValue()
        );
        try {
            book.insert();  /**/
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save book: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateBook(ActionEvent e) {
        book = new Book(
                this.book.id(),
                titleField.getText(),
                authorField.getText(),
                (int) quantitySpinner.getValue(),
                (double) priceSpinner.getValue()
        );
        try {
            book.update();
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save book: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
//    private void controlBook(ActionEvent e) {
//        book = new Book(
//                this.book.id(),
//                titleField.getText(),
//                authorField.getText(),
//                (int) quantitySpinner.getValue(),
//                (double) priceSpinner.getValue()
//        );
//        try {
//            book.update();    /**/
//            book.insert();    /**/
//            dispose();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Failed to save book: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }
//    }
//}


//    private void saveBook(ActionEvent e) {
//        book = new Book(
//                titleField.getText(),
//                authorField.getText(),
//                (int) quantitySpinner.getValue(),
//                (double) priceSpinner.getValue()
//        );
//        try {
//            book.insert();  /**/
//            dispose();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Failed to save book: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }
//    }
//
//    private void updateBook(ActionEvent e) {
//        book = new Book(
//                this.book.id(),
//                titleField.getText(),
//                authorField.getText(),
//                (int) quantitySpinner.getValue(),
//                (double) priceSpinner.getValue()
//        );
//        try {
//            book.update();
//            dispose();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Failed to save book: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }
//    }
//}
//class CreateBookDialog extends JDialog {
//    private final JTextField titleField;
//    private final JTextField authorField;
//    private final JSpinner quantitySpinner;
//    private final JSpinner priceSpinner;
//    private Book book;
//
//    public CreateBookDialog(Frame owner) {
//        super(owner, "Book Creation", true);
//        book = new Book("", "", 0, 0);
//
//        // Create UI components
//        JLabel titleLabel = new JLabel("Title:");
//        JLabel authorLabel = new JLabel("Author:");
//        JLabel quantityLabel = new JLabel("Quantity:");
//        JLabel priceLabel = new JLabel("Price:");
//        titleField = new JTextField(book.title());
//        authorField = new JTextField(book.author());
//        SpinnerNumberModel quantityModel = new SpinnerNumberModel(book.quantity(), 0, Integer.MAX_VALUE, 1);
//        quantitySpinner = new JSpinner(quantityModel);
//        SpinnerNumberModel priceModel = new SpinnerNumberModel(book.price(), 0.0, Double.MAX_VALUE, 0.01);
//        priceSpinner = new JSpinner(priceModel);
//        JButton saveButton = new JButton("Save");
//        JButton cancelButton = new JButton("Cancel");
//
//        // Layout UI components
//        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
//        inputPanel.add(titleLabel);
//        inputPanel.add(titleField);
//        inputPanel.add(authorLabel);
//        inputPanel.add(authorField);
//        inputPanel.add(quantityLabel);
//        inputPanel.add(quantitySpinner);
//        inputPanel.add(priceLabel);
//        inputPanel.add(priceSpinner);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        buttonPanel.add(saveButton);
//        buttonPanel.add(cancelButton);
//
//        JPanel panel = new JPanel(new BorderLayout(5, 5));
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        panel.add(inputPanel, BorderLayout.CENTER);
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        setContentPane(panel);
//        pack();
//        setLocationRelativeTo(null);
//
//        // Add action listeners
//        saveButton.addActionListener(this::saveBook);
//        cancelButton.addActionListener(e -> dispose());
//
//
//
//
//
//    }
//
//    private void saveBook(ActionEvent e) {
//        book = new Book(
//                titleField.getText(),
//                authorField.getText(),
//                (int) quantitySpinner.getValue(),
//                (double) priceSpinner.getValue()
//        );
//        try {
//            book.insert();
//            dispose();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Failed to save book: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }
//    }
//}
//
//
//class EditBookDialog extends JDialog {
//    private final JTextField titleField;
//    private final JTextField authorField;
//    private final JSpinner quantitySpinner;
//    private final JSpinner priceSpinner;
//    private Book book;
//
//    public EditBookDialog(Frame owner, Book book) {
//        super(owner, "Book Edit", true);
//        this.book = book;
//
//        // Create UI components
//        JLabel titleLabel = new JLabel("Title:");
//        JLabel authorLabel = new JLabel("Author:");
//        JLabel quantityLabel = new JLabel("Quantity:");
//        JLabel priceLabel = new JLabel("Price:");
//        titleField = new JTextField(book.title());
//        authorField = new JTextField(book.author());
//        SpinnerNumberModel quantityModel = new SpinnerNumberModel(book.quantity(), 0, Integer.MAX_VALUE, 1);
//        quantitySpinner = new JSpinner(quantityModel);
//        SpinnerNumberModel priceModel = new SpinnerNumberModel(book.price(), 0.0, Double.MAX_VALUE, 0.01);
//        priceSpinner = new JSpinner(priceModel);
//        JButton saveButton = new JButton("Save");
//        JButton cancelButton = new JButton("Cancel");
//
//        // Layout UI components
//        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
//        inputPanel.add(titleLabel);
//        inputPanel.add(titleField);
//        inputPanel.add(authorLabel);
//        inputPanel.add(authorField);
//        inputPanel.add(quantityLabel);
//        inputPanel.add(quantitySpinner);
//        inputPanel.add(priceLabel);
//        inputPanel.add(priceSpinner);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        buttonPanel.add(saveButton);
//        buttonPanel.add(cancelButton);
//
//        JPanel panel = new JPanel(new BorderLayout(5, 5));
//        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        panel.add(inputPanel, BorderLayout.CENTER);
//        panel.add(buttonPanel, BorderLayout.SOUTH);
//
//        setContentPane(panel);
//        pack();
//        setLocationRelativeTo(null);
//
//        // Add action listeners
//        saveButton.addActionListener(this::updateBook);
//        cancelButton.addActionListener(e -> dispose());
//    }
//
//
//    private void updateBook(ActionEvent e) {
//        book = new Book(
//                this.book.id(),
//                titleField.getText(),
//                authorField.getText(),
//                (int) quantitySpinner.getValue(),
//                (double) priceSpinner.getValue()
//        );
//        try {
//            book.update();
//            dispose();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Failed to save book: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//        }
//    }
//}
//
//
