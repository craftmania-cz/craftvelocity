package cz.craftmania.craftvelocity.sql;

import com.google.common.util.concurrent.Futures;
import cz.craftmania.craftvelocity.objects.AutologinPlayer;
import cz.craftmania.craftvelocity.utils.Logger;
import cz.craftmania.craftvelocity.utils.ReflectionUtils;
import cz.craftmania.craftvelocity.utils.Utils;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLManager {

    private final @Getter ConnectionPoolManager pool;

    public SQLManager() {
        pool = new ConnectionPoolManager();
    }

    public void shutdown() {
        pool.closePool();
    }

    public void createAutologinPlayersTable() {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: N/A");

        try (Connection conn = pool.getConnection()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS autologin_players (
                        id int auto_increment,
                        uuid varchar(64) not null,
                        nick varchar(32) not null,
                        last_online datetime null,
                        constraint autologin_players_pk primary key (id)
                    );
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();

                Logger.debugSQL("Tabulka autologin_players byla vytvořena pokud neexistovala");
            }
        } catch (SQLException exception) {
            Logger.sql("Nastala chyba při vytváření SQL tabulky autologin_players", exception);
        }
    }

    public CompletableFuture<AutologinPlayer> fetchAutologinPlayer(UUID uuid) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + uuid);

        CompletableFuture<AutologinPlayer> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT nick, last_online FROM autologin_players WHERE uuid = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String nick = rs.getString("nick");
                            Date lastOnline = rs.getDate("last_online");

                            completableFuture.complete(new AutologinPlayer(uuid, nick, lastOnline));
                        } else {
                            completableFuture.complete(null);
                        }
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání AutologinPlayer pomocí UUID " + uuid, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<AutologinPlayer> fetchAutologinPlayer(String nick) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick);

        CompletableFuture<AutologinPlayer> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT * FROM autologin_players WHERE LOWER(?) LIKE LOWER(nick)
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nick);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            UUID uuid = UUID.fromString(rs.getString("uuid"));
                            String databaseNick = rs.getString("nick");
                            Date lastOnline = rs.getDate("last_online");

                            completableFuture.complete(new AutologinPlayer(uuid, databaseNick, lastOnline));
                        } else {
                            completableFuture.complete(null);
                        }
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání AutologinPlayer pomocí nicku " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> insertOrUpdateAutologinPlayer(AutologinPlayer autologinPlayer) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + autologinPlayer);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            fetchAutologinPlayer(autologinPlayer.getUuid()).whenComplete((autologinPlayerOnDatabase, throwable) -> {
                if (throwable != null) {
                    completableFuture.completeExceptionally(throwable);
                    return;
                }

                CompletableFuture<Void> updateOrInsertCompletableFuture = null;

                if (autologinPlayerOnDatabase == null) {
                    updateOrInsertCompletableFuture = insertAutologinPlayer(autologinPlayer);
                } else {
                    updateOrInsertCompletableFuture = updateAutologinPlayer(autologinPlayer);
                }

                updateOrInsertCompletableFuture.whenComplete((aVoid, insertThrowable) -> {
                    if (insertThrowable != null) {
                        completableFuture.completeExceptionally(insertThrowable);
                        return;
                    }

                    completableFuture.complete(null);
                });
            });
        });

        return completableFuture;
    }

    public CompletableFuture<Void> insertAutologinPlayer(AutologinPlayer autologinPlayer) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + autologinPlayer);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        INSERT INTO autologin_players (uuid, nick, last_online) VALUES (?, ?, ?)
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, autologinPlayer.getUuid().toString());
                    ps.setString(2, autologinPlayer.getNick());
                    ps.setDate(3, new java.sql.Date(autologinPlayer.getLastOnline().getTime()));

                    ps.execute();

                    completableFuture.complete(null);
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při vkládání AutologinPlayer objektu do tabulky autologin_players " + autologinPlayer, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> updateAutologinPlayer(AutologinPlayer autologinPlayer) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + autologinPlayer);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        UPDATE autologin_players SET nick = ?, last_online = ? WHERE uuid = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, autologinPlayer.getNick());
                    ps.setDate(2, new java.sql.Date(autologinPlayer.getLastOnline().getTime()));
                    ps.setString(3, autologinPlayer.getUuid().toString());

                    ps.execute();

                    completableFuture.complete(null);
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při aktualizaci AutologinPlayer objektu v tabulce autologin_players " + autologinPlayer, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> removeAutologinPlayer(String nick) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        DELETE FROM autologin_players WHERE nick = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nick);

                    ps.execute();

                    completableFuture.complete(null);
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při mazání AutologinPlayeru v tabulce autologin_players podle nick " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> removeAutologinPlayer(UUID uuid) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + uuid);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        DELETE FROM autologin_players WHERE uuid = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());

                    ps.execute();

                    completableFuture.complete(null);
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při mazání AutologinPlayeru v tabulce autologin_players podle UUID " + uuid, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }
}
