package xyz.nkomarn.Wildfire.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {
    private Inventory inv;
    private GUIType type;
    private String tag;

    public GUIHolder(GUIType type) {
        this.type = type;
    }

    public GUIHolder(GUIType type, String tag) {
        this.type = type;
        this.tag = tag;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public GUIType getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

}
