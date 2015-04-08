package me.imjack.loot;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemPickUpListener implements Listener {

	Main plugin;

	public ItemPickUpListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if(plugin.worldNames.contains(player.getWorld().getName())){
			return;
		}
		if (event.getItem().hasMetadata("AntiLoot")) {
			if(!player.hasPermission("anti.loot.bypass")){
			String getvalue = event.getItem().getMetadata("AntiLoot").get(0).asString();//Item on the floor
				String[] theValue = getvalue.split(" ");// Splitting metadata
				String killersUUID = theValue[0];// Gets killers name
				UUID realUUID = UUID.fromString(killersUUID);

				Long oldTime = Long.valueOf(theValue[1]);// Gets time of death
				long TimeNow = System.currentTimeMillis();// Gets current time

				if (player.getUniqueId().equals(realUUID)) {
					return;
				} else if (plugin.getPlayerData().get(realUUID) != null) {
					if (plugin.getPlayerData().get(realUUID).getFriends().contains(player.getUniqueId())) {
						return;
					}
				}

				if ((TimeNow - oldTime) >= plugin.pickupTime * 1000) {// checks if protection has ended
					return;
				} else {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}