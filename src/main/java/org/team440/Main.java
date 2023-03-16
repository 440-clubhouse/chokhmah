package org.team440;

import org.team440.models.Db;
import org.team440.ui.AdminMain;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Db.init();
        var login = new AdminMain();
        login.showUI();
    }
}
