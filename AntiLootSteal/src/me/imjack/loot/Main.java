package me.imjack.loot;

import java.io.IOException;
import java.util.logging.Logger;

import me.imjack.loot.mcstats.Metrics;
import me.imjack.loot.mcstats.Metrics.Graph;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Main plugin;

	public void onEnable() {
		PluginManager manager = this.getServer().getPluginManager();
		getConfig().options().copyDefaults(true);
		saveConfig();
		Logger log = Bukkit.getLogger();
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		try {
			Metrics metrics = new Metrics(this);

			Graph weaponsUsedGraph = metrics.createGraph("Percent of item pick up wait");

			weaponsUsedGraph.addPlotter(new Metrics.Plotter("Item pick up wait") {

				@Override
				public int getValue() {
					return getConfig().getInt("ItemPickUpDelayInSeconds");
				}

			});

			metrics.start();
		} catch (IOException e) {
			this.getServer().getConsoleSender().sendMessage(e.getMessage());
		}
		if (getConfig().getBoolean("AlowFactionMemersLooting")) {
			if (getServer().getPluginManager().getPlugin("mcore") != null && getServer().getPluginManager().getPlugin("Factions") != null) {
				log.info(getDescription().getName() + " Factions mode enabled");
				manager.registerEvents(new FactionsItemPickUpEventListener(this), this);
			} else {
				log.info(getDescription().getName() + " Factions or Mcore could not be found");
				manager.registerEvents(new ItemPickUpEventListener(this), this);
			}
		} else {
			manager.registerEvents(new ItemPickUpEventListener(this), this);
			log.info(getDescription().getName() + " Normal mode running");
		}
		manager.registerEvents(new PlayerDeathEventListener(this), this);
	}

	public void onDisable() {

	}
}