package com.github.maxopoly.finale.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.maxopoly.finale.FinaleManager;

public class PlayerListener implements Listener {

	private FinaleManager manager;

	public PlayerListener(FinaleManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void playerLogin(PlayerJoinEvent e) {
		if (manager.isAttackSpeedEnabled()) {;
			// Set attack speed
			AttributeInstance attr = e.getPlayer().getAttribute(
				Attribute.GENERIC_ATTACK_SPEED);
			if (attr != null) {
				attr.setBaseValue(manager.getAttackSpeed());
			}
		}
		if (manager.isRegenHandlerEnabled()) {
			// Register login for custom health regen
			manager.getPassiveRegenHandler().registerPlayer(
					e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void healthRegen(EntityRegainHealthEvent e) {
		if (!manager.isRegenHandlerEnabled()) return;
		if (e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		if (e.getRegainReason() == RegainReason.SATIATED
				&& manager.getPassiveRegenHandler().blockPassiveHealthRegen()) {
			e.setCancelled(true);
			return;
		}
		if (e.getRegainReason() == RegainReason.EATING && manager.getPassiveRegenHandler().blockFoodHealthRegen()) {
			e.setCancelled(true);
		}
	}

}
