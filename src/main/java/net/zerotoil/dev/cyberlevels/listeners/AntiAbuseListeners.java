package net.zerotoil.dev.cyberlevels.listeners;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AntiAbuseListeners implements Listener {

    private final CyberLevels main;
    private final FixedMetadataValue placedMetadata;

    public AntiAbuseListeners(CyberLevels main) {
        this.main = main;
        this.placedMetadata = new FixedMetadataValue(main, true);
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPistonExtend(BlockPistonExtendEvent event) {
        fixPlacedAbuse(event.getBlocks(), event.getDirection());
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPistonRetract(BlockPistonRetractEvent event) {
        fixPlacedAbuse(event.getBlocks(), event.getDirection());
    }

    private void fixPlacedAbuse(List<Block> blocks, BlockFace direction) {
        new BukkitRunnable() {
            @Override
            public void run() {
//                long startTime = System.nanoTime();

                boolean[] hadMetadata = new boolean[blocks.size()];
                for (int i = 0; i < blocks.size(); i++) {
                    hadMetadata[i] = blocks.get(i).hasMetadata("CLV_PLACED");
                }
                for (int i = 0; i < blocks.size(); i++) {
                    if (hadMetadata[i]) {
                        blocks.get(i).removeMetadata("CLV_PLACED", main);
                    }
                }
                for (int i = 0; i < blocks.size(); i++) {
                    if (hadMetadata[i]) {
                        Block newBlock = blocks.get(i).getRelative(direction);
                        newBlock.setMetadata("CLV_PLACED", placedMetadata);
                    }
                }

//                long endTime = System.nanoTime();
//                double durationMs = (endTime - startTime) / 1_000_000.0;

//                Bukkit.getLogger().info("[fixPlacedAbuse] Metadata fix took " + durationMs + " ms for " + blocks.size() + " blocks.");
            }
        }.runTaskLater(main, 1L);
    }

}
