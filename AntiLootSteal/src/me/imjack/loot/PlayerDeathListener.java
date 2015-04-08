package me.imjack.loot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerDeathListener implements Listener {
	Main plugin;

	public PlayerDeathListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player) {
			Player player = event.getEntity().getPlayer();
			if (plugin.worldNames.contains(player.getWorld().getName())) {
				return;
			}
			final Player killer = event.getEntity().getPlayer().getKiller();
			if(plugin.getPlayerData().containsKey(killer.getUniqueId())){
				if(!plugin.getPlayerData().get(killer.getUniqueId()).isToggle()){
					return;
				}
			}
			List<ItemStack> itemList = new ArrayList<ItemStack>();
			for (ItemStack stack : event.getDrops()) {
				itemList.add(stack);
				player.getWorld()
						.dropItemNaturally(player.getLocation(), stack)
						.setMetadata(
								"AntiLoot",
								new FixedMetadataValue(plugin, killer.getUniqueId() + " "
										+ String.valueOf(System.currentTimeMillis())));
			}
			event.getDrops().clear();
			if (plugin.warnMessageEnabled) {
				System.out.println("pass");
				killer.sendMessage(plugin.warnMessage.replaceAll("%time", String.valueOf(plugin.pickupTime)));
			}
			if (plugin.warnFreeLootMessageEnabled) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						killer.sendMessage(plugin.freeLootMessage);
					}
				}, 20 * plugin.pickupTime);
			}
		}
	}
}