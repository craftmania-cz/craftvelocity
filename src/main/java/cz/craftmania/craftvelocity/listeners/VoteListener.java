package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.vexsoftware.votifier.velocity.event.VotifierEvent;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Logger;
import cz.craftmania.craftvelocity.utils.PluginMessageUtils;

import java.util.Random;

public class VoteListener {

    private static final Random random = new Random();

    @Subscribe
    public void onVote(VotifierEvent event) {
        Player player = Main.getInstance().getServer().getPlayer(event.getVote().getUsername()).orElse(null); // Pokud null -> hráč je offline

        int coins = getChanceCoins(randRange(1, 100));
        int voteTokens = Main.getInstance().getConfig().getVote().getVoteTokens().getAmount();

        if (player != null) {
            String username = player.getUsername();

            Logger.vote("Zpracování hlasu pro: " + username);

            if (!(System.currentTimeMillis() > Main.getInstance().getSqlManager().getLastVote(username))) {
                Logger.vote("Hrac " + username + " hlasoval driv nez za 2h.");
                return;
            }

            // Server na kterem je hrac
            ServerConnection serverConnection = player.getCurrentServer().orElse(null);

            if (serverConnection == null) {
                Logger.vote("Hrac se nenachazí na zadnem serveru. Hlas bude zaslan jakokdyby byl offline.");
                addOfflineVotesAndCoins(username, voteTokens, coins);
                return;
            }

            String server = serverConnection.getServerInfo().getName();

            boolean voteAdded = false;
            for (String configServer : Main.getInstance().getConfig().getVote().getVoteServers()) {
                if (configServer.equalsIgnoreCase(server)) {
                    Logger.vote("Online zpracování hlasu pro hráče " + username + " (coins: " + coins + ", votetokens: " + voteTokens + ")");
                    PluginMessageUtils.sendVoteMessage(serverConnection, username, String.valueOf(coins), String.valueOf(voteTokens));
                    voteAdded = true;
                    break;
                }
            }
            if (!voteAdded) {
                Logger.vote("Hráč " + username + " se nenacházel na žádném vote serveru. Hlas bude zpracován jako offline hlas. (coins: " + coins + ", votetokens: " + voteTokens + ")");
                addOfflineVotesAndCoins(username, voteTokens, coins);
            }
        } else {
            String voteUsername = event.getVote().getUsername();

            if (!(System.currentTimeMillis() > Main.getInstance().getSqlManager().getLastVote(voteUsername))) {
                Logger.vote("Hrac " + voteUsername + " hlasoval driv nez za 2h.");
                return;
            }

            // Kdyz je offline force to DB (obejit CraftEconomy)
            Logger.vote("Offline zpracování hlasu pro hráče " + voteUsername + " (coins: " + coins + ", votetokens: " + voteTokens + ")");
            this.addOfflineVotesAndCoins(voteUsername, voteTokens, coins);
        }
    }

    /**
     * Gets random coins determinate by chance
     * @param chance Chance 1 to Integer.MAX_VALUE technically
     * @return Number of coins
     */
    private int getChanceCoins(int chance) {
        if (chance == 1) { //1% sance
            return 100;
        } else if (chance <= 5 && chance >= 2) { //5% sance
            return 50;
        } else if (chance <= 25 && chance >= 6) { //25% sance
            return 25;
        } else {
            return 10;
        }
    }

    /**
     * Generates random number
     * @param min Minimal number (inclusive)
     * @param max Maximal number (inclusive)
     * @return Random number
     */
    private static int randRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Adds VoteTokens and coins to player's nick in database
     * @param nick Player's nick
     * @param voteTokens Number of VoteTokens:tm:
     * @param coins Number of coins
     */
    private void addOfflineVotesAndCoins(String nick, int voteTokens, int coins) {
        Main.getInstance().getSqlManager().addPlayerVote(nick);
        Main.getInstance().getSqlManager().addVoteToken(nick, voteTokens); // FIXME: Odebrat deprecated metody?
        Main.getInstance().getSqlManager().addVoteToken2(nick, voteTokens);
        Main.getInstance().getSqlManager().addVoteToken3(nick, voteTokens);
        Main.getInstance().getSqlManager().addCraftCoins(nick, coins);
    }
}
