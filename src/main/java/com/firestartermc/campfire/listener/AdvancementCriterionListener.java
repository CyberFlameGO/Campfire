package com.firestartermc.campfire.listener;

import org.bukkit.event.Listener;

/**
 * Listener class for all custom advancement criteria
 * that requires integration with other plugin APIs.
 */
public class AdvancementCriterionListener implements Listener {

    /*@EventHandler()
    public void onAuctionBuy(AuctionBuyEvent event) { // TODO doesn't work right, might have to modify the API
        AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-sold");
    }

    @EventHandler()
    public void onAuctionList(AuctionListEvent event) {
        AdvancementUtil.grantAdvancement(event.getPlayer(), "ah-sell");
    }*/ // TODO Auctions events

    /*@EventHandler()
    public void onShopCreated(@NotNull ShopCreatedEvent event) {
        Advancement.grantAdvancement(event.getPlayer(), "chestshop-create");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(@NotNull PreTransactionEvent event) {
        Advancement.grantAdvancement(event.getClient(), "chestshop-buy");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAccrueClaimBlocks(@NotNull AccrueClaimBlocksEvent event) {
        Advancement.grantAdvancement(event.getPlayer(), "claim-blocks");
    }

    @EventHandler(ignoreCancelled = true)
    public void onClaimCreated(@NotNull ClaimCreatedEvent event) {
        Advancement.grantAdvancement((Player) event.getCreator(), "claim");
    }

    @EventHandler
    public void onTrustChanged(TrustChangedEvent event) {
        AdvancementUtil.grantAdvancement(event.getChanger(), "claim-trust");
    }

    @EventHandler()
    public void onCrateReward(@NotNull CrateRewardEvent event) {
        Advancement.grantAdvancement(event.getPlayer(), "crates");
    }

    @EventHandler()
    public void onJobJoin(@NotNull JobsJoinEvent event) {
        Advancement.grantAdvancement(event.getPlayer().getPlayer(), "jobs-join");
    }

    @EventHandler()
    public void onPetActiveSkill(@NotNull MyPetActivatedEvent event) {
        Advancement.grantAdvancement(event.getOwner().getPlayer(), "pets-skilltree");
    }

    @EventHandler()
    public void onPetCreated(@NotNull MyPetCreateEvent event) {
        Advancement.grantAdvancement(event.getOwner().getPlayer(), "pets-tame");
    }

    @EventHandler()
    public void onVote(@NotNull VotifierEvent event) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getVote().getUsername());
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            AdvancementUtils.grant(player.getPlayer(), "vote");
        }
    }*/
}
