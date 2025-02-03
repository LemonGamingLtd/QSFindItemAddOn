package io.myzticbean.finditemaddon.listeners;

import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.utils.log.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author NahuLD
 */
public class PlayerQuitEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        if (FindItemAddOn.removePlayerMenuUtility(event.getPlayer())) {
            Logger.logDebugInfo("Removed player menu utility for quitting player...");
        }
    }
}
