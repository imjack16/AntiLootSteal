package me.imjack.loot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class MobDeathListener implements Listener {
	Main plugin;

	public MobDeathListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() instanceof Player && !(event.getEntity() instanceof Player)) {
			final Player player = event.getEntity().getKiller();
			if (plugin.worldNames.contains(player.getWorld().getName())) {
				return;
			}
			if (plugin.getPlayerData().containsKey(player.getUniqueId())) {
				if (!plugin.getPlayerData().get(player.getUniqueId()).isToggle()) {
					return;
				}
			}
			for (ItemStack stack : event.getDrops()) {
				if (stack != null) {
					event.getEntity()
							.getWorld()
							.dropItemNaturally(event.getEntity().getLocation(), stack)
							.setMetadata(
									"AntiLoot",
									new FixedMetadataValue(plugin, player.getUniqueId() + " "
											+ String.valueOf(System.currentTimeMillis())));
				}
			}
			event.getDrops().clear();
			if (plugin.warnMessageEnabled) {
				player.sendMessage(plugin.warnMessage.replaceAll("%time", String.valueOf(plugin.pickupTime)));
			}
			if (plugin.warnFreeLootMessageEnabled) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						player.sendMessage(plugin.freeLootMessage);
					}
				}, 20 * plugin.pickupTime);
			}
		}
	}
}
