package org.team440.models;

import com.zaxxer.hikari.HikariDataSource;

public class Db {
    static HikariDataSource pool;

    public static void init() {
        pool = new HikariDataSource();
        pool.setJdbcUrl(System.getenv("URL"));
        pool.setUsername(System.getenv("USERNAME"));
        pool.setPassword(System.getenv("PASSWORD"));
    }
}
