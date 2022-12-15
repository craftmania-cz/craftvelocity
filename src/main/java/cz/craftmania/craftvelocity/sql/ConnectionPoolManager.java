package cz.craftmania.craftvelocity.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Config;
import cz.craftmania.craftvelocity.utils.Logger;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private @Getter HikariDataSource dataSource;
    private String host;
    private String database;
    private String username;
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    public ConnectionPoolManager() {
        try {
            init();
            setupPool();
            Main.getInstance().getLogger().info("Database loaded!");
        } catch (Exception e) {
            Main.getInstance().getLogger().error("Could not load database.", e);
        }
    }

    private void init() {
        Config config = Main.getInstance().getConfig();
        Config.SQL sql = config.getSql();

        host = sql.getHostname();
        database = sql.getDatabase();
        username = sql.getUsername();
        password = sql.getPassword();
        minimumConnections = (int) sql.getSettings().getMinimumConnections();
        maximumConnections = (int) sql.getSettings().getMaximumConnections();
        connectionTimeout = sql.getSettings().getTimeout();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":3306" + "/" + database + "?characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
            Logger.sql("Connection was manually closed from class " + Thread.currentThread().getStackTrace()[2].getClassName());
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }
}
