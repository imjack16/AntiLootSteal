package me.imjack.loot;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.massivecraft.factions.entity.UPlayer;

public class FactionsItemPickUpEventListener implements Listener {

	static Main plugin;

	public FactionsItemPickUpEventListener(Main instance) {
		plugin = instance;
	}

	@EventHandler
	public void NoDrop(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Boolean hasData = event.getItem().hasMetadata("AntiLoot");
		if (hasData == true) {
			int configtime = plugin.getConfig().getInt("ItemPickUpDelayInSeconds") * 1000;
			if (!player.hasPermission("anti.loot.bypass")) {
				String getvalue = event.getItem().getMetadata("AntiLoot").get(0).asString();// Item on the floor
				String[] theValue = getvalue.split(" ");// Spliting metadata
				String killersUUID = theValue[0];// Gets killers name
				UUID realUUID = UUID.fromString(killersUUID);

				Long oldTime = Long.valueOf(theValue[1]);// Gets time of death
				long TimeNow = System.currentTimeMillis();// Gets current time
				Player TheKiller = Bukkit.getPlayer(realUUID);

				if (player.getUniqueId().equals(realUUID)) {
					return;
				}
				
				UPlayer uPlayer = UPlayer.get(player);
				if (uPlayer.hasFaction()) {
					if (uPlayer.getFaction().getOnlinePlayers().contains(TheKiller)) {
						return;
					}
				}
				
				if ((TimeNow - oldTime) >= configtime) {
					return;
				} else {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}