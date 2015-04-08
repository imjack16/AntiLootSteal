package me.imjack.loot;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LooterCommand implements CommandExecutor {
	Main plugin;

	public LooterCommand(Main instance) {
		this.plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is for players only");
			return true;
		}
		Player player = (Player) sender;
		PlayerData data = plugin.getPlayerData().get(player.getUniqueId());
		if (args.length == 0) {
			sendHelp(cmd.getName(), player);
			return true;
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				if (player.hasPermission("anti.loot.list")) {
					if (data != null) {
						StringBuilder builder = new StringBuilder();
						for (UUID uuid : data.getFriends()) {
							builder.append(", ").append(Bukkit.getOfflinePlayer(uuid).getName());
						}
						if (builder.length() > 0) {
							builder.deleteCharAt(0);
						} else {
							builder.append("Nobody");
						}
						player.sendMessage(plugin.friendMessage.replaceAll("%friends", builder.toString()));
						return true;
					} else {
						plugin.createFile(player.getUniqueId());
						player.sendMessage(plugin.friendMessage.replaceAll("%friends", "Nobody"));
						return true;
					}
				}
				player.sendMessage(cmd.getPermissionMessage());
				return true;
			} else if (args[0].equalsIgnoreCase("toggle")) {
				if (player.hasPermission("anti.loot.toggle")) {
					if (data != null) {
						if (data.isToggle()) {
							data.setToggle(false);
							player.sendMessage(plugin.toggleMessage.replaceAll("%status", "disabled"));
							return true;
						} else {
							data.setToggle(true);
							player.sendMessage(plugin.toggleMessage.replaceAll("%status", "enabled"));
							return true;
						}
					} else {
						plugin.createFile(player.getUniqueId()).setToggle(false);
						player.sendMessage(plugin.toggleMessage.replaceAll("%status", "disabled"));
						return true;
					}
				}
				player.sendMessage(cmd.getPermissionMessage());
				return true;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("add")) {
				if (player.hasPermission("anti.loot.add")) {
					if (data != null) {
						if (data.getFriends().size() < plugin.maxFriends) {
							if (Bukkit.getPlayer(args[1]) != null) {
								Player adding = Bukkit.getPlayer(args[1]);
								if (Bukkit.getPlayer(args[1]).isOnline()) {
									if (!data.getFriends().contains(adding.getUniqueId())) {
										data.getFriends().add(Bukkit.getPlayer(args[1]).getUniqueId());
										player.sendMessage(plugin.friendAdded);
										return true;
									}
									player.sendMessage(plugin.friendDupe);
									return true;
								}
							}
							player.sendMessage(plugin.friendNotOnline);
							return true;
						}
						player.sendMessage(plugin.maxFriendsMessage);
						return true;
					} else {
						if (Bukkit.getPlayer(args[1]).isOnline()) {
							plugin.createFile(player.getUniqueId()).getFriends()
									.add(Bukkit.getPlayer(args[1]).getUniqueId());
							return true;
						}
						player.sendMessage(plugin.friendNotOnline);
						return true;
					}
				}
				player.sendMessage(cmd.getPermissionMessage());
				return true;
			} else if (args[0].equalsIgnoreCase("delete")) {
				if (player.hasPermission("anti.loot.delete")) {
					if (data != null) {
						if (data.getFriends().contains(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
							data.getFriends().remove(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
							player.sendMessage(plugin.friendDeleted);
							return true;
						} else {
							player.sendMessage(plugin.friendExisting);
							return true;
						}
					} else {
						plugin.createFile(player.getUniqueId());
						player.sendMessage(plugin.noFriends);
						return true;
					}
				}
				player.sendMessage(cmd.getPermissionMessage());
				return true;
			}
		}
		sendHelp(cmd.getName(), player);
		return true;
	}

	public void sendHelp(String cmd, Player player) {
		player.sendMessage(ChatColor.GOLD + "--- AntiLooter Help ---");
		player.sendMessage(ChatColor.GOLD + "/" + cmd + " list - Shows added friends");
		player.sendMessage(ChatColor.GOLD + "/" + cmd + " add <name> - Allow friends to take your loot");
		player.sendMessage(ChatColor.GOLD + "/" + cmd + " delete <name> - Removes ability of friends taking your loot");
		player.sendMessage(ChatColor.GOLD + "/" + cmd + " toggle - toggles if you want AntiLooter enabled for you.");
		player.sendMessage(ChatColor.GOLD + "----------------------------");
	}

}
