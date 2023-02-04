package cz.craftmania.craftvelocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import cz.craftmania.craftvelocity.Main;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

public class JoinRateLimitListener {

    private static final @Getter long joinLimit = Main.getInstance().getConfig().getJoinRateLimit().getJoinLimit();
    private static @Getter @Setter long currentConnectionCount;

    @Subscribe(order = PostOrder.LATE)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        if (currentConnectionCount > joinLimit) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(Main.getInstance().getConfig().getJoinRateLimit().getMessages().getLimitReached())));
            return;
        }

        currentConnectionCount++;
    }
}
