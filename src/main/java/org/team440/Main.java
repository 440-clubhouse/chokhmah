package org.team440;

import org.team440.models.Db;
import org.team440.ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        Db.init();
        new MainMenu();
    }
}
