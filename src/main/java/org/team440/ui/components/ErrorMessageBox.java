package org.team440.ui.components;

import javax.swing.*;

public class ErrorMessageBox {
    public static void of(Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
    }
}
