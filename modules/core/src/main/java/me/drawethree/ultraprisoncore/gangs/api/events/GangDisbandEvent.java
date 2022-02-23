package me.drawethree.ultraprisoncore.gangs.api.events;

import lombok.Getter;
import me.drawethree.ultraprisoncore.gangs.model.Gang;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GangDisbandEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	@Getter
	private final Gang gang;
	private boolean cancelled;

	/**
	 * Called when gang is disbanded
	 *
	 * @param gang Gang
	 */
	public GangDisbandEvent(Gang gang) {
		this.gang = gang;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
}