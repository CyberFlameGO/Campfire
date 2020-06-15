package xyz.nkomarn.Campfire.listener.world;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import xyz.nkomarn.Campfire.util.Border;

public class ChunkLoadListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) {
            Block block = event.getChunk().getBlock(8, 0, 8);
            if (Border.isLocationOutsideBorder(event.getWorld(), block.getX(), block.getY())) {
                event.getChunk().unload(false);
            }
        }
    }
}
