package xyz.nkomarn.Campfire.listener.jobs;

import com.gamingmesh.jobs.api.JobsJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class JobsJoinListener implements Listener {
    @EventHandler
    public void onJobJoin(JobsJoinEvent event) {
        if (Advancements.isComplete(event.getPlayer().getPlayer(), "jobs-join")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:jobs-join",
                event.getPlayer().getName()));
    }
}
