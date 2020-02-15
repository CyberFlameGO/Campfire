package xyz.nkomarn.Campfire.listener.pets;


import de.Keyle.MyPet.api.event.MyPetActivatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Campfire.util.Advancements;

public class PetActiveSkillListener implements Listener {
    @EventHandler
    public void onPetActiveSkill(MyPetActivatedEvent event) {
        if (Advancements.isComplete(event.getOwner().getPlayer(), "pets-skilltree")) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("advancement grant %s only firestarter:pets-skilltree",
                event.getOwner().getName()));
    }
}
