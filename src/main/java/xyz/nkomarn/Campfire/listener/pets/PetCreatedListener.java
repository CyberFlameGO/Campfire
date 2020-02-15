package xyz.nkomarn.Campfire.listener.pets;

import de.Keyle.MyPet.api.event.MyPetCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class PetCreatedListener implements Listener {
    @EventHandler
    public void onPetCreated(MyPetCreateEvent event) {
        if (Advancements.isComplete(event.getOwner().getPlayer(), "pets-tame")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:pets-tame",
                event.getOwner().getName()));
    }
}
