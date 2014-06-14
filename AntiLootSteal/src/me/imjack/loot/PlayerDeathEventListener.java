package me.imjack.loot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerDeathEventListener implements Listener {

	static Main plugin;

	public PlayerDeathEventListener(Main instance) {
		plugin = instance;
	}

	@EventHandler
	public void NoDrop(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			int configtime = plugin.getConfig().getInt("ItemPickUpDelayInSeconds");
			final Player killer = event.getEntity().getPlayer().getKiller();
			final String name = plugin.getConfig().getString("Name");
			Player player = event.getEntity().getPlayer();
			List<ItemStack> itemList = new ArrayList<ItemStack>();
			for (ItemStack stack : event.getDrops()) {
				itemList.add(stack);
				Entity entity = player.getWorld().dropItemNaturally(player.getLocation(), stack);
				String time = String.valueOf(System.currentTimeMillis());
				entity.setMetadata("AntiLoot", new FixedMetadataValue(plugin, killer.getUniqueId() + " " + time));
			}
			event.getDrops().clear();
			killer.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + name + ChatColor.GRAY + "] " + plugin.getConfig().getString("Warning").replaceAll("%time", "" + configtime));
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					killer.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + name + ChatColor.GRAY + "] " + plugin.getConfig().getString("FreeLoot"));
				}
			}, 20 * configtime);
		}

	}
}
