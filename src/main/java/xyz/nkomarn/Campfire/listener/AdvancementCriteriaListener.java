package xyz.nkomarn.Campfire.listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.Keyle.MyPet.api.event.MyPetActivatedEvent;
import de.Keyle.MyPet.api.event.MyPetCreateEvent;
import me.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import me.badbones69.crazyauctions.api.events.AuctionListEvent;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.ryanhamshire.GriefPrevention.events.AccrueClaimBlocksEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.Kerosene.util.AdvancementUtil;

/**
 * Listener class for all custom advancement criteria
 * that requires integration with other plugin APIs.
 */
public class AdvancementCriteriaListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onAuctionBuy(AuctionBuyEvent event) { // TODO doesn't work right, might have to modify the API
        AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-sold");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAuctionList(AuctionListEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-sell");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopCreated(ShopCreatedEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "chestshop-create");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(PreTransactionEvent event) {
        AdvancementUtil.grantAdvancement(event.getClient(), "chestshop-buy");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAccrueClaimBlocks(AccrueClaimBlocksEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "claim-blocks");
    }

    @EventHandler(ignoreCancelled = true)
    public void onClaimCreated(ClaimCreatedEvent event) {
        AdvancementUtil.grantAdvancement((Player) event.getCreator(), "claim");
    }

    /*@EventHandler
    public void onTrustChanged(TrustChangedEvent event) {
        AdvancementUtil.grantAdvancement(event.getChanger(), "claim-trust");
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPrize(PlayerPrizeEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "crates");
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobJoin(JobsJoinEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer().getPlayer(), "jobs-join");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPetActiveSkill(MyPetActivatedEvent event) {
        AdvancementUtil.grantAdvancement(event.getOwner().getPlayer(), "pets-skilltree");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPetCreated(MyPetCreateEvent event) {
        AdvancementUtil.grantAdvancement(event.getOwner().getPlayer(), "pets-tame");
    }

    @EventHandler(ignoreCancelled = true)
    public void onVote(VotifierEvent event) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getVote().getUsername());
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            AdvancementUtil.grantAdvancement(player.getPlayer(), "vote");
        }
    }
}
