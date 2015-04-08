package me.imjack.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

	private List<UUID> friends = new ArrayList<UUID>();
	private boolean enabled = true;
	private final UUID uuid;

	public PlayerData(UUID uuid) {
		this.uuid = uuid;
	}

	public List<UUID> getFriends() {
		return friends;
	}

	public List<String> getFriendsString() {
		List<String> friends = new ArrayList<String>();
		for (UUID uuid : this.friends) {
			friends.add(String.valueOf(uuid));
		}
		return friends;
	}

	public void setFriends(List<UUID> friends) {
		this.friends = friends;
	}

	public boolean isToggle() {
		return enabled;
	}

	public void setToggle(boolean toggle) {
		this.enabled = toggle;
	}

	public UUID getUuid() {
		return uuid;
	}

}
