package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import net.kyori.adventure.text.Component;

public class TheEndListener {

    String text = "§e§lThe End\n\n§7Byla to dlouhá cesta, cesta na kterou se nedá zapomenout.\n§7A stejně tak jsi nezapomněl(a) ani ty na tento server.\n§7Děkujeme za všechny ty krásné chvíle, které jsme spolu prožili.\n\n§b§lCraftMania.cz\n§82014 - 2024";

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        event.getPlayer().disconnect(Component.text(text));
    }
}
