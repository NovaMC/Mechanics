package xyz.novaserver.mechanics.features.chairs.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.novaserver.mechanics.features.chairs.Chair;

public class ChairUnsitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Chair chair;
    private boolean cancelled = false;

    public ChairUnsitEvent(Chair chair) {
        this.chair = chair;
    }

    public Player getPlayer() {
        return chair.getPlayer();
    }

    public Chair getChair() {
        return chair;
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static ChairUnsitEvent callEvent(Chair chair) {
        ChairUnsitEvent event = new ChairUnsitEvent(chair);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}
