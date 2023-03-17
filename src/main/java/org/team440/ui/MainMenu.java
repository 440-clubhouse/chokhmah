package org.team440.ui;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        this.setTitle("Online Book Store");
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        var toUserMenu = new JButton("Users");
        var toBookMenu = new JButton("Books");
        var toOrderMenu = new JButton("Orders");
        var buttonSize = new Dimension(120, 40);
        toUserMenu.setPreferredSize(buttonSize);
        toBookMenu.setPreferredSize(buttonSize);
        toOrderMenu.setPreferredSize(buttonSize);
        toUserMenu.addActionListener((e) -> new UserMenu());
        toBookMenu.addActionListener((e) -> new BookMenu());
        toOrderMenu.addActionListener((e) -> new OrderMenu());
        this.add(toUserMenu);
        this.add(toBookMenu);
        this.add(toOrderMenu);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
