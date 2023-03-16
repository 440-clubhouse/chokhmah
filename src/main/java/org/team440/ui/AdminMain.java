package org.team440.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AdminMain extends JFrame {
    public AdminMain() {
        this.setTitle("Online Book Store");
        this.setLayout(new GridLayout(5, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var toUserMenuPanel = new JPanel();
        var toBookMenuPanel = new JPanel();
        var toOrderMenuPanel = new JPanel();
        var toUserMenu = new JButton("Users");
        var toBookMenu = new JButton("Books");
        var toOrderMenu = new JButton("Orders");
        var buttonSize = new Dimension(120, 40);
        toUserMenu.setPreferredSize(buttonSize);
        toBookMenu.setPreferredSize(buttonSize);
        toOrderMenu.setPreferredSize(buttonSize);
        toUserMenuPanel.add(toUserMenu);
        toBookMenuPanel.add(toBookMenu);
        toOrderMenuPanel.add(toOrderMenu);
        this.add(toUserMenuPanel);
        this.add(toBookMenuPanel);
        this.add(toOrderMenuPanel);
        toUserMenu.addActionListener((event) -> {
            UserMenu menu;
            try {
                menu = new UserMenu();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            menu.setVisible(true);
        });
        toBookMenu.addActionListener((event) -> {
            var menu = new BookMenu();
            menu.setVisible(true);
        });
        toOrderMenu.addActionListener((e) -> {
            var menu = new OrderMenu();
            menu.setVisible(true);
        });

        this.setBounds(300, 200, 300, 250);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    public void showUI() {
        this.setVisible(true);
    }
}
