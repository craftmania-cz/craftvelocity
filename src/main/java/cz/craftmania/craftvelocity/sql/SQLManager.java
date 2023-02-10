package cz.craftmania.craftvelocity.sql;

import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.objects.AutologinPlayer;
import cz.craftmania.craftvelocity.objects.connectionwhitelist.BlacklistedASN;
import cz.craftmania.craftvelocity.objects.connectionwhitelist.WhitelistedIP;
import cz.craftmania.craftvelocity.objects.connectionwhitelist.WhitelistedName;
import cz.craftmania.craftvelocity.utils.LazyUtils;
import cz.craftmania.craftvelocity.utils.Logger;
import cz.craftmania.craftvelocity.utils.ReflectionUtils;
import cz.craftmania.craftvelocity.utils.Utils;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class SQLManager {

    private final @Getter ConnectionPoolManager pool;

    public SQLManager() {
        pool = new ConnectionPoolManager();
    }

    public void shutdown() {
        pool.closePool();
    }

    ////////////
    // Tables //
    ////////////

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

    ///////////////
    // Autologin //
    ///////////////

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

    //////////
    // Vote //
    //////////

    public final long getLastVote(final String p) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + p);

        try (Connection conn = pool.getConnection()) {
            String sql = """
                    SELECT last_vote FROM minigames.player_profile WHERE nick = ?;
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p);

                ps.executeQuery();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return rs.getLong("last_vote");
                    }
                }
            }
        } catch (SQLException exception) {
            Logger.sql("Nastala chyba při získávání času posledního hlasu pro hráče " + p, exception);
        }

        return 0L;
    }

    public CompletableFuture<Void> addPlayerVote(String nick) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        UPDATE minigames.player_profile SET total_votes = total_votes + 1, week_votes = week_votes + 1, month_votes = month_votes + 1, vote_pass = vote_pass + 1, last_vote = ? WHERE nick = ?;
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, System.currentTimeMillis());
                    ps.setString(2, nick);
                    ps.executeUpdate();

                    completableFuture.complete(null);
                }

            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při připočítávání hlasu pro hráče " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    @Deprecated
    public CompletableFuture<Void> addVoteToken(String nick, int voteTokens) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick + ", " + voteTokens);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE minigames.player_profile SET vote_tokens = vote_tokens + " + voteTokens + " WHERE nick = ?;";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nick);
                    ps.executeUpdate();

                    completableFuture.complete(null);
                }

            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při přidávání " + voteTokens + " votetokenů pro hráče " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;

    }

    @Deprecated
    public CompletableFuture<Void> addVoteToken2(String nick, int voteTokens) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick + ", " + voteTokens);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE minigames.player_profile SET vote_tokens_2 = vote_tokens_2 + " + voteTokens + " WHERE nick = ?;";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nick);
                    ps.executeUpdate();

                    completableFuture.complete(null);
                }

            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při přidávání " + voteTokens + " votetokenů pro hráče " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> addVoteToken3(String nick, int voteTokens) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick + ", " + voteTokens);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE minigames.player_profile SET vote_tokens_3 = vote_tokens_3 + " + voteTokens + " WHERE nick = ?;";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nick);
                    ps.executeUpdate();

                    completableFuture.complete(null);
                }

            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při přidávání " + voteTokens + " votetokenů pro hráče " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> addCraftCoins(String nick, int coins) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + nick + ", " + coins);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE minigames.player_profile SET craft_coins = craft_coins + " + coins + " WHERE nick = ?;";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, nick);
                    ps.executeUpdate();

                    completableFuture.complete(null);
                }

            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při přidávání " + coins + " CraftCoinů pro hráče " + nick, exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<Void> updateStats(Player player, boolean online) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1) + " s argumenty: " + player + ", " + online);

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE minigames.player_profile SET last_server = ?, last_online = ?, is_online = ?, mc_version = ? WHERE nick = ?;";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, Utils.getPlayerServerName(player));
                    ps.setLong(2, System.currentTimeMillis());
                    ps.setString(3, online ? "1" : "0");
                    ps.setString(4, player.getProtocolVersion().getMostRecentSupportedVersion());
                    ps.setString(5, player.getUsername());
                    ps.executeUpdate();

                    completableFuture.complete(null);
                }

            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při aktualizace statů pro hráče " + player.getUsername() + " (online: " + online + ")", exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    //////////////////////////
    // Connection Whitelist //
    //////////////////////////

    public CompletableFuture<List<WhitelistedName>> fetchWhitelistedNames() {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1));

        CompletableFuture<List<WhitelistedName>> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT * FROM minigames.name_whitelist;
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        List<WhitelistedName> whitelistedNames = new LinkedList<>();

                        while (rs.next()) {
                            String nick = rs.getString("nick");
                            String description = rs.getString("description");

                            whitelistedNames.add(new WhitelistedName(nick, description));
                        }

                        completableFuture.complete(whitelistedNames);
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání whitelisted nicků!", exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<List<WhitelistedIP>> fetchWhitelistedIPs() {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1));

        CompletableFuture<List<WhitelistedIP>> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT * FROM minigames.ip_whitelist;
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        List<WhitelistedIP> whitelistedIPs = new LinkedList<>();

                        while (rs.next()) {
                            Pattern address = Pattern.compile(rs.getString("address"));
                            String description = rs.getString("description");

                            whitelistedIPs.add(new WhitelistedIP(address, description));
                        }

                        completableFuture.complete(whitelistedIPs);
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání whitelisted IPs!", exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<List<BlacklistedASN>> fetchBlacklistedASNs() {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1));

        CompletableFuture<List<BlacklistedASN>> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT * FROM minigames.blacklisted_asns;
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        List<BlacklistedASN> blacklistedASNs = new LinkedList<>();

                        while (rs.next()) {
                            String asn = rs.getString("asn");

                            blacklistedASNs.add(new BlacklistedASN(asn));
                        }

                        completableFuture.complete(blacklistedASNs);
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání blacklisted ASNs!", exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<List<String>> fetchAllowedBlacklistedNicks() {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1));

        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT * FROM minigames.allowed_blacklisted_names;
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        List<String> whitelistedNicks = new LinkedList<>();

                        while (rs.next()) {
                            String nick = rs.getString("nick");

                            whitelistedNicks.add(nick);
                        }

                        completableFuture.complete(whitelistedNicks);
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání povolených nicků s zablokováným slovem!", exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<List<String>> fetchBlacklistedWords() {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1));

        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = """
                        SELECT * FROM minigames.blacklisted_name_words;
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        List<String> blacklistedWords = new LinkedList<>();

                        while (rs.next()) {
                            String word = rs.getString("word");

                            blacklistedWords.add(word);
                        }

                        completableFuture.complete(blacklistedWords);
                    }
                }
            } catch (SQLException exception) {
                Logger.sql("Nastala chyba při získávání blacklisted nick slov!", exception);
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    //////////////////////
    // PlayerUpdateTask //
    //////////////////////

    public void updateTime(Player player) {
        Logger.debugSQL("Metoda " + ReflectionUtils.getMethodNameByIndex(2) + " zavolalo metodu " + ReflectionUtils.getMethodNameByIndex(1));

        String playerServer = Utils.getPlayerServerName(player);

        // Nebude to async kvůli tomu ať nespamujeme konzoli x async taskama

        try (Connection conn = pool.getConnection()) {
            String sql = """
                    UPDATE minigames.player_profile SET played_time = played_time + 1, last_server = ? WHERE nick = ?;
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerServer);
                ps.setString(2, player.getUsername());

                ps.executeUpdate();
            }
        } catch (SQLException exception) {
            Logger.sql("Nastala chyba při aktualizace hráče " + player.getUsername() + "!", exception);
        }
    }
}
