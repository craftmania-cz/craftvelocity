package cz.craftmania.craftvelocity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.UncheckedExecutionException;
import cz.craftmania.craftvelocity.Main;
import cz.craftmania.craftvelocity.api.minetools.MineToolsAPI;
import cz.craftmania.craftvelocity.api.minetools.objects.MineToolsPlayer;
import cz.craftmania.craftvelocity.cache.objects.MineToolsCacheObject;
import cz.craftmania.craftvelocity.exceptions.AutologinNotEnabledException;
import cz.craftmania.craftvelocity.objects.AutologinPlayer;
import cz.craftmania.craftvelocity.utils.Logger;
import cz.craftmania.craftvelocity.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Holds Autologin Caches: {@link MineToolsCacheObject}, {@link AutologinPlayer} cache, and disabled autologin player cache
 */
public class AutologinCache {

    // Nick : AutologinCacheObject
    private @Getter LoadingCache<String, MineToolsCacheObject> resolvedMineToolsPlayersCache;
    private @Getter LoadingCache<String, AutologinPlayer> resolvedAutologinPlayerCache;
    private @Getter LoadingCache<String, Boolean> disabledAutologinPlayerCache;

    /**
     * Initializes Autologin Cache. Must be called at the start of plugin
     */
    public void init() {
        Logger.info("Inicializuji AutologinCache...");

        initMineToolsCache();
        initAutologinCache();
        initDisabledAutologinCache();
    }

    private void initMineToolsCache() {
        long mineToolsExpireAfter = Main.getInstance().getConfig().getAutologin().getCache().getInvalidateMineToolsCacheAfter();

        Logger.debug("[AUTOLOGIN-CACHE] Objekty v resolvedMineToolsPlayersCache budou invalidovány po " + mineToolsExpireAfter + " milisekundách (" + TimeUnit.MILLISECONDS.toMinutes(mineToolsExpireAfter) + " minut)");

        resolvedMineToolsPlayersCache = CacheBuilder.newBuilder()
                                                    .expireAfterWrite(mineToolsExpireAfter, TimeUnit.MILLISECONDS)
                                                    .expireAfterAccess(mineToolsExpireAfter, TimeUnit.MILLISECONDS)
                                                    .removalListener((RemovalListener<String, MineToolsCacheObject>) notification -> {
                                                        Logger.debug("[AUTOLOGIN-CACHE] Hráč " + notification.getKey() + " byl odstraněn z MineTools cache z důvodu: " + notification.getCause());
                                                    })
                                                    .build(new CacheLoader<>() {
                                                        @Override
                                                        public MineToolsCacheObject load(String nick) throws Exception {
                                                            try {
                                                                MineToolsPlayer player = MineToolsAPI.getInstance()
                                                                                                     .getMineToolsPlayer(nick)
                                                                                                     .sendAsync()
                                                                                                     .join();

                                                                Logger.debug("[AUTOLOGIN-CACHE] Hráč " + nick + " byl přidán do MineTools cache - je originální: " + player.isOriginalNick());

                                                                return new MineToolsCacheObject(player);
                                                            } catch (Exception exception) {
                                                                throw new RuntimeException("Nastala chyba při získávání informací z MineTools pro hráče s nickem " + nick + "!", exception);
                                                            }
                                                        }
                                                    });
    }

    private void initAutologinCache() {
        long autologinExpireAfter = Main.getInstance().getConfig().getAutologin().getCache().getInvalidateAutologinCacheAfter();

        Logger.debug("[AUTOLOGIN-CACHE] Objekty v resolvedAutologinPlayerCache budou invalidovány po " + autologinExpireAfter + " milisekundách (" + TimeUnit.MILLISECONDS.toMinutes(autologinExpireAfter) + " minut)");

        resolvedAutologinPlayerCache = CacheBuilder.newBuilder()
                                                   .expireAfterWrite(autologinExpireAfter, TimeUnit.MILLISECONDS)
                                                   .expireAfterAccess(autologinExpireAfter, TimeUnit.MILLISECONDS)
                                                   .removalListener((RemovalListener<String, AutologinPlayer>) notification -> {
                                                       Logger.debug("[AUTOLOGIN-CACHE] Hráč " + notification.getValue()
                                                                                                            .getNick() + "(" + notification.getValue()
                                                                                                                                           .getUuid() + ") byl odstraněn z Autologin cache z důvodu: " + notification.getCause());
                                                   })
                                                   .build(new CacheLoader<>() {
                                                       @Override
                                                       public AutologinPlayer load(String nick) throws Exception {
                                                           AutologinPlayer autologinPlayer = Main.getInstance()
                                                                                                 .getSqlManager()
                                                                                                 .fetchAutologinPlayer(nick)
                                                                                                 .join();

                                                           if (autologinPlayer == null) {
                                                               disabledAutologinPlayerCache.get(nick); // Přidá jakokdyby hráče na disabled autologin player cache
                                                               throw new AutologinNotEnabledException(nick);
                                                           }

                                                           invalidateDisabledAutologinCacheForNick(nick);

                                                           return autologinPlayer;
                                                       }
                                                   });
    }

    private void initDisabledAutologinCache() {
        long disabledAutologinExpireAfter = Main.getInstance().getConfig().getAutologin().getCache().getInvalidateDisabledAutologinCacheAfter();

        Logger.debug("[AUTOLOGIN-CACHE] Objekty v disabledAutologinPlayerCache budou invalidovány po " + disabledAutologinExpireAfter + " milisekundách (" + TimeUnit.MILLISECONDS.toMinutes(disabledAutologinExpireAfter) + " minut)");

        disabledAutologinPlayerCache = CacheBuilder.newBuilder()
                                                   .expireAfterWrite(disabledAutologinExpireAfter, TimeUnit.MILLISECONDS)
                                                   .expireAfterAccess(disabledAutologinExpireAfter, TimeUnit.MILLISECONDS)
                                                   .removalListener((RemovalListener<String, Boolean>) notification -> {
                                                       Logger.debug("[AUTOLOGIN-CACHE] Hráč " + notification.getKey() + " byl odstraněn z DisabledAutologin cache z důvodu: " + notification.getCause());
                                                   })
                                                   .build(new CacheLoader<>() {
                                                       @Override
                                                       public Boolean load(String nick) throws Exception {
                                                           return true; // Jen nějaká random value... Jelikož LoadingCache je KEY:VALUE, ale my potřebujeme jen key
                                                       }
                                                   });
    }

    /**
     * Fetch or loads {@link MineToolsPlayer} from player's nick.<br>Request to MineTools is only made when there no MineToolsPlayer with specified
     * nick is cached.
     *
     * @param nick Player's nick
     *
     * @return {@link CompletableFuture} with {@link MineToolsCacheObject}
     */
    public CompletableFuture<MineToolsCacheObject> fetchOrLoadMineToolsPlayerFromNick(String nick) {
        CompletableFuture<MineToolsCacheObject> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            try {
                completableFuture.complete(resolvedMineToolsPlayersCache.get(nick));
            } catch (ExecutionException exception) {
                completableFuture.completeExceptionally(exception);
            }
        });

        return completableFuture;
    }

    /**
     * Fetch or loads {@link AutologinPlayer} from player's nick.<br>Request to SQL is only made when there no AutologinPlayer with specified nick is
     * cached.
     *
     * @param nick Player's nick
     *
     * @return {@link CompletableFuture} with {@link MineToolsCacheObject}
     */
    public CompletableFuture<AutologinPlayer> fetchOrLoadAutologinPlayerFromNick(String nick) {
        CompletableFuture<AutologinPlayer> completableFuture = new CompletableFuture<>();

        Utils.runAsync(() -> {
            // Pokud je v tomto cache, tak víme, že hráč nemá autologin - není třeba dotazovat SQL tabulku
            if (disabledAutologinPlayerCache.getIfPresent(nick) != null) {
                completableFuture.complete(null);
                return;
            }

            try {
                completableFuture.complete(resolvedAutologinPlayerCache.get(nick));
                // Hráč měl zapnutý autologin / byl cachnutý -> poggies
            } catch (UncheckedExecutionException | ExecutionException exception) {
                if (exception.getCause() instanceof AutologinNotEnabledException) {
                    completableFuture.complete(null); // Autologin nebyl zapnutý, tak vrátíme null
                } else {
                    completableFuture.completeExceptionally(exception); // Něco se stalo D:
                }
            }
        });

        return completableFuture;
    }

    /**
     * Invalids disabled Autologin cache for player's nick
     *
     * @param nick Player's nick
     */
    public void invalidateDisabledAutologinCacheForNick(String nick) {
        disabledAutologinPlayerCache.invalidate(nick);
    }

    /**
     * Invalids Autologin cache for player's nick
     *
     * @param nick Player's nick
     */
    public void invalidateAutologinCacheForNick(String nick) {
        resolvedAutologinPlayerCache.invalidate(nick);
    }

    /**
     * Invalids MineTools cache for player's nick
     *
     * @param nick Player's nick
     */
    public void invalidateMineToolsCacheForNick(String nick) {
        resolvedMineToolsPlayersCache.invalidate(nick);
    }

    /**
     * Force adds {@link AutologinPlayer} to cache
     *
     * @param autologinPlayer {@link AutologinPlayer}
     */
    public void forceAddToAutologinPlayerCache(AutologinPlayer autologinPlayer) {
        resolvedAutologinPlayerCache.put(autologinPlayer.getNick(), autologinPlayer);
    }

    /**
     * Force adds player's nick to cache
     *
     * @param nick player's nick
     */
    public void forceAddToDisabledAutologinPlayerCache(String nick) {
        disabledAutologinPlayerCache.put(nick, true);
    }

    /**
     * Gets {@link AutologinPlayer} from cache by player's nick if present
     * @param nick Player's nick
     * @return Nullable {@link AutologinPlayer}
     */
    public @Nullable AutologinPlayer getIfPresentFromAutologinPlayerCache(String nick) {
        return resolvedAutologinPlayerCache.getIfPresent(nick);
    }

    /**
     * Invalidates all caches
     */
    public void invalidateAll() {
        resolvedMineToolsPlayersCache.invalidateAll();
        resolvedAutologinPlayerCache.invalidateAll();
        disabledAutologinPlayerCache.invalidateAll();
    }
}
