package io.myzticbean.finditemaddon.dependencies;

import io.myzticbean.finditemaddon.utils.LocationUtils;
import io.myzticbean.finditemaddon.utils.log.Logger;
import lombok.experimental.UtilityClass;
import ltd.lemongaming.slimeskyblock.SlimeSkyblock;
import ltd.lemongaming.slimeskyblock.island.enums.Dimension;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class SlimeSkyblockPlugin {

    private static SlimeSkyblock plugin;

    public static void setup() {
        if (!Bukkit.getPluginManager().isPluginEnabled("SlimeSkyblock")) {
            return;
        }

        Logger.logInfo("Found SlimeSkyblock");
        plugin = SlimeSkyblock.getInstance();

        LocationUtils.registerLocationParser((player, locDataList) -> {
            if (locDataList.size() <= 1)
                return null;

            final String worldName = locDataList.get(0);
            final UUID islandId = getIslandIdFromWorldName(worldName);
            if (islandId == null) {
                return null;
            }

            final CompletableFuture<Location> location = new CompletableFuture<>();
            plugin.getIslandDistributedExecutor().executeIslandSynchronized(player, islandId, island -> {
                final World world = Bukkit.getWorld(worldName);
                int locX = Integer.parseInt(locDataList.get(1));
                int locY = Integer.parseInt(locDataList.get(2));
                int locZ = Integer.parseInt(locDataList.get(3));
                location.complete(new Location(world, locX, locY, locZ));
            });
            return location;
        });
    }

    private static UUID getIslandIdFromWorldName(String worldName) {
        worldName = Dimension.getBaseWorldName(worldName);
        if (worldName.length() != 36) {
            return null;
        }
        try {
            return UUID.fromString(worldName);
        } catch (IllegalArgumentException var4) {
            return null;
        }
    }
}
