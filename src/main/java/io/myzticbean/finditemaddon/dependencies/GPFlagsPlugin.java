package io.myzticbean.finditemaddon.dependencies;

import io.myzticbean.finditemaddon.utils.log.Logger;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GPFlagsPlugin {

    private static GPFlags gpFlags;
    private static boolean isPluginEnabled = false;

    public static void setup() {
        if(Bukkit.getPluginManager().isPluginEnabled("GPFlags")) {
            gpFlags = (GPFlags) Bukkit.getServer().getPluginManager().getPlugin("GPFlags");
            if(gpFlags != null) {
                isPluginEnabled = true;
                Logger.logInfo("Found GPFlags");
            }
        }
    }

    public static boolean isEnabled() {
        return isPluginEnabled && gpFlags != null;
    }

    public static GPFlags getAPI() {
        return gpFlags;
    }

    /**
     * Checks if a player is banned from entering the claim at the given location.
     *
     * @param player   The player to check
     * @param location The location to check (shop location)
     * @return true if the player is banned from the claim, false otherwise
     */
    @SuppressWarnings("deprecation")
    public static boolean isPlayerBannedFromClaim(Player player, Location location) {
        if (!isEnabled()) {
            return false;
        }

        try {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
            if (claim == null) {
                return false;
            }

            if (claim.getOwnerID() != null && claim.getOwnerID().equals(player.getUniqueId())) {
                return false;
            }

            FlagManager flagManager = gpFlags.getFlagManager();
            Flag noEnterPlayerFlag = flagManager.getEffectiveFlag(location, "noenterplayer", claim);

            if (noEnterPlayerFlag != null) {
                if (noEnterPlayerFlag.getSet()) {
                    String parameters = noEnterPlayerFlag.getParameters();
                    if (parameters != null && !parameters.isEmpty()) {
                        String[] bannedPlayers = parameters.split(" ");
                        String playerUUID = player.getUniqueId().toString();
                        String playerName = player.getName();

                        for (String banned : bannedPlayers) {
                            banned = banned.trim();
                            if (banned.isEmpty()) continue;
                            if (banned.equalsIgnoreCase(playerUUID) || banned.equalsIgnoreCase(playerName)) {
                                return true;
                            }
                        }
                    }
                }
            }

            Flag noEnterFlag = flagManager.getEffectiveFlag(location, "noenter", claim);

            if (noEnterFlag != null) {
                if (noEnterFlag.getSet()) {
                    String accessDenied = claim.allowAccess(player);
                    if (accessDenied != null) {
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
