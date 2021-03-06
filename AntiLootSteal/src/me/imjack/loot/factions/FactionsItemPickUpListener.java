package me.imjack.loot.factions;

import java.util.UUID;

import me.imjack.loot.Main;
import me.imjack.loot.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.massivecraft.factions.entity.MPlayer;

public class FactionsItemPickUpListener implements Listener {

	Main plugin;

	public FactionsItemPickUpListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onFactionLoot(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (plugin.worldNames.contains(player.getWorld().getName())) {
			return;
		}
		if (event.getItem().hasMetadata("AntiLoot")) {
			if (!player.hasPermission("anti.loot.bypass")) {
				String getvalue = event.getItem().getMetadata("AntiLoot").get(0).asString();
				String[] theValue = getvalue.split(" ");// Splitting metadata
				String killersUUID = theValue[0];// Gets killers name
				UUID realUUID = UUID.fromString(killersUUID);

				Long oldTime = Long.valueOf(theValue[1]);// Gets time of death
				long TimeNow = System.currentTimeMillis();// Gets current time

				if (player.getUniqueId().equals(realUUID)) {
					return;
				} else if (plugin.getPlayerData().get(realUUID) != null) {
					PlayerData data = plugin.getPlayerData().get(realUUID);
					if (data.getFriends().contains(player.getUniqueId())) {
						return;
					}
				}
				if (MPlayer.get(player) != null) {
					MPlayer mPlayer = MPlayer.get(player);
					if (MPlayer.get(player).hasFaction()) {
						for (Player factionMembers : mPlayer.getFaction().getOnlinePlayers()) {
							if (factionMembers.getUniqueId().equals(realUUID)) {
								return;
							}
						}
					}
				}
				if ((TimeNow - oldTime) >= plugin.pickupTime * 1000) {
					return;
				} else {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
