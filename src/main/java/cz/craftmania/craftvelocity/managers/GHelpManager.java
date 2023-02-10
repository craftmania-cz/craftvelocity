package cz.craftmania.craftvelocity.managers;

import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.objects.GHelpData;
import cz.craftmania.craftvelocity.utils.Utils;
import lombok.Getter;

import java.util.*;

public class GHelpManager {

    private final @Getter Map<UUID, Long> playerCooldowns = Collections.synchronizedMap(new HashMap<>());
    private final @Getter List<GHelpData> GHelpDataList = Collections.synchronizedList(new LinkedList<>());

    public GHelpData addMessage(Player player, String message) {
        playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        GHelpData ghelpData = new GHelpData(player.getUsername(), player.getUniqueId(), UUID.randomUUID(), Utils.getPlayerServerName(player), message, System.currentTimeMillis());
        GHelpDataList.add(ghelpData);
        return ghelpData;
    }

    public boolean deleteMessageByUUID(UUID messageUUID) {
        return GHelpDataList.removeIf(GHelpData -> GHelpData.getMessageUUID().equals(messageUUID));
    }

    public GHelpData getGhelpDataByMessageUUID(UUID messageUUID) {
        synchronized (GHelpDataList) {
            return GHelpDataList.stream().filter(GHelpData -> GHelpData.getMessageUUID().equals(messageUUID)).findFirst().orElse(null);
        }
    }

    public boolean isPlayerUnderCooldown(UUID playerUUID) {
        Long time = playerCooldowns.get(playerUUID);

        if (time == null) {
            return false;
        }

        return System.currentTimeMillis() - time > Main.getInstance().getConfig().getGHelp().getCooldownMillis();
    }
}
