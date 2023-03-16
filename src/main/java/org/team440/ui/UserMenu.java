package org.team440.ui;

import org.team440.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class UserMenu extends JFrame {
    private final JTable table;
    private final DefaultTableModel tableModel;

    public UserMenu() throws SQLException {
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0);
        for (var user : User.find()) {
            Object[] row = {user.id(), user.name()};
            tableModel.addRow(row);
        }
        table = new JTable(tableModel);
        table.setComponentPopupMenu(createPopupMenu());
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);
        setTitle("User Management");
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem createMenuItem = new JMenuItem("Create a new user");
        createMenuItem.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Your name:");
            int id;
            try {
                id = new User(name).insert();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            Object[] row = {id, name};
            tableModel.addRow(row);
        });
        popupMenu.add(createMenuItem);

        JMenuItem deleteMenuItem = new JMenuItem("Remove the user");
        deleteMenuItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    User.delete(id);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                tableModel.removeRow(selectedRow);
            }
        });
        popupMenu.add(deleteMenuItem);
        return popupMenu;
    }
}
