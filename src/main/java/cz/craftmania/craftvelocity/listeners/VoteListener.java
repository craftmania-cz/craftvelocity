package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.vexsoftware.votifier.velocity.event.VotifierEvent;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.Logger;

import java.util.Random;

public class VoteListener {

    @Subscribe
    public void onVote(VotifierEvent event) {
        Player player = Main.getInstance().getServer().getPlayer(event.getVote().getUsername()).orElse(null); // Pokud null -> hráč je offline
        String username = player.getUsername();

        int coins = getChanceCoins(randRange(1, 100));
        int votetokens = Main.getInstance().getConfig().getVoteTokens().getAmount();

        if (player != null) {
            Logger.vote("Zpracování hlasu pro: " + username);

            if (!(System.currentTimeMillis() > Main.getInstance().getSQLManager().getLastVote(player.getName()))) {
                Logger.vote("Hrac " + username + " hlasoval driv nez za 2h.");
                return;
            }

            // Server na kterem je hrac
            ServerConnection serverConnection = player.getCurrentServer().orElse(null);

            if (serverConnection == null) {
                Logger.vote("Hrac se nenachazi na zadnem serveru. Hlas bude zaslan jakokdyby byl offline.");
                // TODO: Offline hlas
                return;
            }

            // TODO: Přepsat tento zbytek
            boolean voteAdded = false;
            for (String configServer : Main.getVoteServers()) {
                if (configServer.equalsIgnoreCase(server)) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "[Hlasovani]: Online zpracovani hlasu pro: " + player.getName() + ", coins: " + coins + ", votetokens: " + votetokens);
                    BungeeUtils.sendMessageToBukkit("vote", player.getName(), String.valueOf(coins), String.valueOf(votetokens), player.getServer().getInfo());
                    voteAdded = true;
                    break;
                }
            }
            if (!voteAdded) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "[Hlasovani]: Offline (online na serveru) zpracovani hlasu: " + player.getName() + ", coins: " + coins + ", votetokens: " + votetokens);
                this.addOfflineVotes(e.getVote().getUsername(), votetokens, coins);
            }
        } else {
            if (!(System.currentTimeMillis() > Main.getInstance().getSQLManager().getLastVote(e.getVote().getUsername()))) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "[Hlasovani]: Hrac " + e.getVote().getUsername() + " hlasoval driv nez za 2h.");
                return;
            }

            // Kdyz je offline force to DB (obejit CraftEconomy)
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "[Hlasovani]: Offline zpracovani hlasu: " + e.getVote().getUsername() + ", coins: " + coins + ", votetokens: " + votetokens);
            this.addOfflineVotes(e.getVote().getUsername(), votetokens, coins);
        }



    }

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

    private static int randRange(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    private void addOfflineVotes(String nick, int voteTokens, int coins) {
        Main.getInstance().getSQLManager().addPlayerVote(nick);
        Main.getInstance().getSQLManager().addVoteToken(nick, voteTokens);
        Main.getInstance().getSQLManager().addVoteToken2(nick, voteTokens);
        Main.getInstance().getSQLManager().addVoteToken3(nick, voteTokens);
        Main.getInstance().getSQLManager().addCraftCoins(nick, coins);
    }

}
