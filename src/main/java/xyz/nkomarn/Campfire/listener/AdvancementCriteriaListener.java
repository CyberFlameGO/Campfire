package xyz.nkomarn.Campfire.listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.songoda.ultimatetimber.events.TreeFallEvent;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.Keyle.MyPet.api.event.MyPetActivatedEvent;
import de.Keyle.MyPet.api.event.MyPetCreateEvent;
import me.badbones69.crazyauctions.api.events.AuctionBuyEvent;
import me.badbones69.crazyauctions.api.events.AuctionListEvent;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.ryanhamshire.GriefPrevention.events.AccrueClaimBlocksEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.TrustChangedEvent;
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
    @EventHandler
    public void onAuctionBuy(AuctionBuyEvent event) { // TODO doesn't work right, might have to modify the API
        AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-sold");
    }

    @EventHandler
    public void onAuctionList(AuctionListEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-sell");
    }

    @EventHandler
    public void onShopCreated(ShopCreatedEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "chestshop-create");
    }

    @EventHandler
    public void onTransaction(PreTransactionEvent event) {
        AdvancementUtil.grantAdvancement(event.getClient(), "chestshop-buy");
    }

    @EventHandler
    public void onAccrueClaimBlocks(AccrueClaimBlocksEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "claim-blocks");
    }

    @EventHandler
    public void onClaimCreated(ClaimCreatedEvent event) {
        AdvancementUtil.grantAdvancement((Player) event.getCreator(), "claim");
    }

    /*@EventHandler
    public void onTrustChanged(TrustChangedEvent event) {
        AdvancementUtil.grantAdvancement(event.getChanger(), "claim-trust");
    }*/

    @EventHandler
    public void onPlayerPrize(PlayerPrizeEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "crates");
    }

    @EventHandler
    public void onJobJoin(JobsJoinEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer().getPlayer(), "jobs-join");
    }

    @EventHandler
    public void onPetActiveSkill(MyPetActivatedEvent event) {
        AdvancementUtil.grantAdvancement(event.getOwner().getPlayer(), "pets-skilltree");
    }

    @EventHandler
    public void onPetCreated(MyPetCreateEvent event) {
        AdvancementUtil.grantAdvancement(event.getOwner().getPlayer(), "pets-tame");
    }

    @EventHandler
    public void onTreeFall(TreeFallEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "tree");
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getVote().getUsername());
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            AdvancementUtil.grantAdvancement(player.getPlayer(), "vote");
        }
    }
}
