package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.utils.LazyUtils;
import cz.craftmania.craftvelocity.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class EventNotifyListener {

    @Subscribe(order = PostOrder.FIRST)
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().getId().equals(Main.CRAFTEVENTS_CHANNEL)) {
            return;
        }

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
            DataInputStream data = new DataInputStream(stream);
            String type = data.readUTF();
            switch (type) {
                case "announce":
                    String eventType = data.readUTF();
                    String reward = data.readUTF();
                    String eventer = data.readUTF();
                    announceMessage(eventType, Integer.parseInt(reward), eventer);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void announceMessage(final String eventType, final int reward, final String eventer) {
        Logger.info("Announcing event (" + eventType + "), reward: " + reward + ", eventer: " + eventer);
        for (Player p : Main.getInstance().getServer().getAllPlayers()) {
            LazyUtils.sendMessageToPlayer(p,"");
            LazyUtils.sendMessageToPlayer(p,"§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            LazyUtils.sendMessageToPlayer(p,"");
            LazyUtils.sendMessageToPlayer(p,"§c§lEvent brzy začne!");
            //LazyUtils.sendMessageToPlayer(p,"§fPozor! Na Event serveru brzo začne celoserverový event!");
            LazyUtils.sendMessageToPlayer(p,"§7Typ: §f" + eventType + "§7, odměna: §f" + reward + " EP");
            LazyUtils.sendMessageToPlayer(p,"§7Eventer: §f" + eventer);
            LazyUtils.sendMessageToPlayer(p,"");
            TextComponent textComponent = Component.text("§eKliknutím zde se připojíš na server!")
                    .hoverEvent(HoverEvent.showText(Component.text("§7Klikni pro připojení!")))
                    .clickEvent(ClickEvent.runCommand("/eventserver-tp"));
            p.sendMessage(textComponent);
            LazyUtils.sendMessageToPlayer(p,"");
            LazyUtils.sendMessageToPlayer(p,"§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            LazyUtils.sendMessageToPlayer(p,"");
        }
    }


}
