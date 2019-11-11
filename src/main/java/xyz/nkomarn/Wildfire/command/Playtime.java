package xyz.nkomarn.Wildfire.command;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Kerosene.database.subscribers.BasicSubscriber;
import xyz.nkomarn.Wildfire.Wildfire;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Playtime implements CommandExecutor {

    private DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            final Player player = (Player) sender;

            Wildfire.playerData.async().find(Filters.eq("uuid", player.getUniqueId().toString()))
                    .subscribe(new BasicSubscriber<Document>() {

                        @Override
                        public void onNext(Document document) {
                            Date joinDate = new Date(document.getLong("joined"));

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&r&m&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&7Playtime: &e" + intToTimeString(document.getInteger("playtime")) + "."));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&7Join date: &e" + dateFormat.format(joinDate) + "."));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&r&m&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"));
                        }

                    }
            );
        } else {
            sender.sendMessage("Playtime can only be used by players.");
        }
        return true;
    }

    private String intToTimeString(int time) {
        return time/24/60 + " days, " + time/60%24 + " hours, and " + time%60 + " minutes";
    }

}
