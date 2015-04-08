package me.imjack.loot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.imjack.loot.factions.FactionsItemPickUpListener;
import me.imjack.loot.factions.UuidFactionsItemPickUpListener;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public List<String> worldNames = getConfig().getStringList("DisabledWorlds");
	public int pickupTime = getConfig().getInt("ItemPickUpDelayInSeconds");
	public int maxFriends = getConfig().getInt("MaxFriends");
	public boolean warnMessageEnabled = getConfig().getBoolean("ShowWarnMessages");
	public boolean warnFreeLootMessageEnabled = getConfig().getBoolean("ShowFreeLootMessage");
	public boolean protectPlayers = getConfig().getBoolean("ProtectPlayerLoot");
	public boolean protectMobs = getConfig().getBoolean("ProtectMobLoot");

	public String warnMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Warning"));
	public String freeLootMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("FreeLoot"));
	public String toggleMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("ToggleMessage"));
	public String friendMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("FriendMessage"));
	public String friendAdded = ChatColor.translateAlternateColorCodes('&', getConfig().getString("FriendAdded"));
	public String friendDeleted = ChatColor.translateAlternateColorCodes('&', getConfig().getString("FriendDeleted"));
	public String noFriends = ChatColor.translateAlternateColorCodes('&', getConfig().getString("NoFriends"));
	public String friendNotOnline = ChatColor.translateAlternateColorCodes('&', getConfig()
			.getString("FriendNotOnline"));
	public String friendExisting = ChatColor.translateAlternateColorCodes('&', getConfig().getString("FriendExisting"));
	public String friendDupe = ChatColor.translateAlternateColorCodes('&', getConfig().getString("FriendDupe"));
	public String maxFriendsMessage = ChatColor.translateAlternateColorCodes('&',
			getConfig().getString("MaxFriendMessage"));

	private final File folder = new File(getDataFolder() + "/", "Player Data");
	private final HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();

	public void onEnable() {
		PluginManager manager = this.getServer().getPluginManager();
		loadConfigs();
		getCommand("looter").setExecutor(new LooterCommand(this));
		if (getConfig().getBoolean("HookNewFactions")) {
			if (protectPlayers) {
				manager.registerEvents(new FactionsItemPickUpListener(this), this);
			}
		} else if (getConfig().getBoolean("HookFactionsUUID")) {
			if (protectPlayers) {
				manager.registerEvents(new UuidFactionsItemPickUpListener(this), this);
			}
		} else {
			if (protectPlayers) {
				manager.registerEvents(new ItemPickUpListener(this), this);
			}
		}
		if (protectMobs) {
			manager.registerEvents(new MobDeathListener(this), this);
		}
		manager.registerEvents(new PlayerDeathListener(this), this);
	}

	public void onDisable() {
		for (Entry<UUID, PlayerData> data : getPlayerData().entrySet()) {
			File dataFile = new File(folder, data.getKey().toString() + ".yml");
			if (dataFile.exists()) {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
				config.set("enabled", data.getValue().isToggle());
				config.set("friends", data.getValue().getFriendsString());
				try {
					config.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
				try {
					dataFile.createNewFile();
					config.set("enabled", data.getValue().isToggle());
					config.set("friends", data.getValue().getFriends());
					config.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public File getFolder() {
		return folder;
	}

	public HashMap<UUID, PlayerData> getPlayerData() {
		return playerData;
	}

	public void loadConfigs() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		if (!folder.exists()) {
			folder.mkdir();
		}
		File[] playerData = folder.listFiles();
		for (int i = 0; i < playerData.length; i++) {
			if (playerData[i].isFile()) {
				final String[] playerUUID = playerData[i].getName().split("\\.");
				YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(playerData[i]);
				PlayerData data = new PlayerData(UUID.fromString(playerUUID[0]));
				data.setToggle(dataConfig.getBoolean("enabled"));
				for (String uuid : dataConfig.getStringList("friends")) {
					data.getFriends().add(UUID.fromString(uuid));
				}
				getPlayerData().put(UUID.fromString(playerUUID[0]), data);
			}
		}
	}

	public PlayerData createFile(UUID uuid) {
		File dataFile = new File(folder, uuid.toString() + ".yml");
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
				PlayerData data = new PlayerData(uuid);
				getPlayerData().put(uuid, data);
				return data;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}