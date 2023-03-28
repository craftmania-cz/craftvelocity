package cz.craftmania.craftvelocity.managers;

import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.objects.GHelpData;
import cz.craftmania.craftvelocity.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GHelpManager {

    private final @Getter Map<UUID, Long> playerCooldowns = Collections.synchronizedMap(new HashMap<>());
    private final @Getter List<GHelpData> GHelpDataList = Collections.synchronizedList(new LinkedList<>());

    /**
     * Adds GHelp message to runtime list
     * @param player Player
     * @param message Player's message
     * @return Just created {@link GHelpData}
     */
    public GHelpData addMessage(Player player, String message) {
        playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        GHelpData ghelpData = new GHelpData(player.getUsername(), player.getUniqueId(), UUID.randomUUID(), Utils.getPlayerServerName(player), message, System.currentTimeMillis());
        GHelpDataList.add(ghelpData);
        return ghelpData;
    }

    /**
     * Deletes GHelp message from runtime list by message's UUID
     * @param messageUUID Message's UUID
     * @return True if removed, false otherwise
     */
    public boolean deleteMessageByUUID(UUID messageUUID) {
        return GHelpDataList.removeIf(GHelpData -> GHelpData.getMessageUUID().equals(messageUUID));
    }

    /**
     * Gets {@link GHelpData} by message's UUID
     * @param messageUUID Message's UUID
     * @return Nullable {@link GHelpData}
     */
    public @Nullable GHelpData getGhelpDataByMessageUUID(UUID messageUUID) {
        synchronized (GHelpDataList) {
            return GHelpDataList.stream().filter(GHelpData -> GHelpData.getMessageUUID().equals(messageUUID)).findFirst().orElse(null);
        }
    }

    /**
     * Checks if player is under a cooldown
     * @param playerUUID Player's UUID
     * @return True if player is under a cooldown, false otherwise
     */
    public boolean isPlayerUnderCooldown(UUID playerUUID) {
        Long time = playerCooldowns.get(playerUUID);

        if (time == null) {
            return false;
        }

        return System.currentTimeMillis() - time > Main.getInstance().getConfig().getGHelp().getCooldownMillis();
    }

    /**
     * Notifies online admins about {@link GHelpData}
     * @param gHelpData GHelpData
     */
    public void notifyAdminTeam(GHelpData gHelpData) {
        Component message = Component.text("§6§lGHELP §7| §r").append(gHelpData.generateChatMessage());

        for (Player player : Main.getInstance().getServer().getAllPlayers()) {
            if (player.hasPermission("craftvelocity.at-ghelp")) {
                player.sendMessage(message);
            }
        }
    }
}
