package xyz.nkomarn.Campfire.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiHolder implements InventoryHolder {
    private GuiType type;
    private int page;
    private String data;

    public GuiHolder(final GuiType type, final int page) {
        this.type = type;
        this.page = page;
    }

    public GuiHolder(final GuiType type, final int page,
                     final String data) {
        this.type = type;
        this.page = page;
        this.data = data;
    }

    public Inventory getInventory() {
        return null; // Unused
    }

    public int getPage() {
        return this.page;
    }

    public GuiType getType() {
        return this.type;
    }

    public String getData() {
        return this.data;
    }
}
