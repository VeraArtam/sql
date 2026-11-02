package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    // 1. Объявляем QUERY_RUNNER (из-за отсутствия этой строки он и был красным)
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() throws SQLException {
        // Подключаемся к базе данных, используя пароль из docker-compose
        return DriverManager.getConnection(System.getProperty("db.url"), "app", "pass");
    }

    @SneakyThrows
    public static DataHelper.VerificationCode getVerificationCode() {
        var codeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, codeSQL, new BeanHandler<>(DataHelper.VerificationCode.class));
        }
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
            QUERY_RUNNER.execute(conn, "DELETE FROM card_transactions");
            QUERY_RUNNER.execute(conn, "DELETE FROM cards");
            QUERY_RUNNER.execute(conn, "DELETE FROM users");
        }
    }

    @SneakyThrows
    public static void cleanAuthCodes() {
        try (var conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
        }
    }
}