package org.team440.models;

import com.zaxxer.hikari.HikariDataSource;

public class Db {
    static HikariDataSource pool;

    public static void init() {
        pool = new HikariDataSource();
        pool.setJdbcUrl("jdbc:mariadb://localhost:3306/chokhmah");
        pool.setUsername("root");
        pool.setPassword("zhongbai666");
    }
}
