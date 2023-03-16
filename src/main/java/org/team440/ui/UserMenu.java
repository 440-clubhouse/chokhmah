package org.team440.ui;

import org.team440.models.User;
import org.team440.ui.components.ErrorMessageBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class UserMenu extends JFrame {
    public UserMenu() {
        this.setTitle("User Management");
        try {
            var model = new DefaultTableModel(User.find()
                    .stream()
                    .map(user -> new Object[]{user.id(), user.name()})
                    .toArray(Object[][]::new), new Object[]{"ID", "Name"});
            var table = new JTable(model);

            var addMenuItem = new JMenuItem("Add a user");
            var removeMenuItem = new JMenuItem("Remove the user");
            addMenuItem.addActionListener(e -> {
                var name = JOptionPane.showInputDialog(this, "Your name:", "Add a user", JOptionPane.PLAIN_MESSAGE);
                if (name == null) {
                    return;
                }
                try {
                    model.addRow(new Object[]{new User(name).insert(), name});
                } catch (SQLException exception) {
                    ErrorMessageBox.of(exception);
                }
            });
            removeMenuItem.addActionListener(e -> {
                var selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                try {
                    User.delete((Integer) model.getValueAt(selectedRow, 0));
                    model.removeRow(selectedRow);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            var popupMenu = new JPopupMenu();
            popupMenu.add(addMenuItem);
            popupMenu.add(removeMenuItem);
            table.setComponentPopupMenu(popupMenu);

            this.setContentPane(new JScrollPane(table));
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        } catch (SQLException e) {
            ErrorMessageBox.of(e);
        }
    }
}
