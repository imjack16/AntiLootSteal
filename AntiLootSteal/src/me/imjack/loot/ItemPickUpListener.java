package me.imjack.loot;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemPickUpListener implements Listener {

	private Main plugin = Main.plugin;

	public ItemPickUpListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Boolean hasData = event.getItem().hasMetadata("AntiLoot");
		List<String> worldNames = plugin.getConfig().getStringList("DisabledWorlds");
		if(worldNames.contains(player.getWorld().getName())){
			return;
		}
		if (hasData == true) {
			int configtime = plugin.getConfig().getInt("ItemPickUpDelayInSeconds") * 1000;
			if(!player.hasPermission("anti.loot.bypass")){
			String getvalue = event.getItem().getMetadata("AntiLoot").get(0)//Item on the floor
					.asString();
			String[] theValue = getvalue.split(" ");//Spliting metadata
			String killersUUID = theValue[0];//Gets killers name
			UUID realUUID = UUID.fromString(killersUUID);

			Long oldTime = Long.valueOf(theValue[1]);//Gets time of death
			long TimeNow = System.currentTimeMillis();//Gets current time

			if (player.getUniqueId().equals(realUUID)) {
				return;
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