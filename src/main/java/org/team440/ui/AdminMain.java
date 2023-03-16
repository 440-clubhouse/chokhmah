package org.team440.ui;

import javax.swing.*;
import java.awt.*;

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
            new UserMenu();
        });
        toBookMenu.addActionListener((event) -> {
            new BookMenu();
        });
        toOrderMenu.addActionListener((e) -> {
            new OrderMenu();
        });

        this.setBounds(300, 200, 300, 250);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
