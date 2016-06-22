package com.github.maxopoly.finale.misc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.maxopoly.finale.Finale;

public class SaturationHealthRegenHandler implements Runnable {
	private List<LinkedList<UUID>> ticks;
	private Map <UUID, Integer> tickMapping;
	private int currentTick;
	private double healthPerCycle;
	private int minimumFood;
	private float exhaustionPerHeal;
	private int interval;
	private int PID;
	private boolean blockPassiveHealthRegen;
	private boolean blockFoodHealthRegen;

	public SaturationHealthRegenHandler(int interval, double healthPerCycle,
			int minimumFood, float exhaustionPerHeal, boolean blockPassiveHealthRegen, boolean blockFoodHealthRegen) {
		this.currentTick = 0;
		this.ticks = new ArrayList<LinkedList<UUID>>(interval);
		for(int i = 0; i < interval; i++) {
			ticks.add(null);
		}
		tickMapping = new TreeMap<UUID, Integer>();
		this.interval = interval;
		this.healthPerCycle = healthPerCycle;
		this.minimumFood = minimumFood;
		this.exhaustionPerHeal = exhaustionPerHeal;
		this.PID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Finale.getPlugin(), this, 0L, 1L);
		this.blockPassiveHealthRegen = blockPassiveHealthRegen;
		this.blockFoodHealthRegen = blockFoodHealthRegen;
	}

	public void registerPlayer(UUID uuid) {
		LinkedList<UUID> players = ticks.get(currentTick);
		if (players == null) {
			players = new LinkedList<UUID>();
			ticks.set(currentTick, players);
		}
		Integer exisTick = tickMapping.get(uuid);
		if (exisTick != null) {
			LinkedList<UUID> exis = ticks.get(exisTick);
			exis.remove(uuid);
		}
		tickMapping.put(uuid, currentTick);
		players.add(uuid);
	}

	public void run() {
		LinkedList<UUID> players = ticks.get(currentTick);
		if (players != null) {
			ListIterator<UUID> iter = players.listIterator();
			while (iter.hasNext()) {
				UUID player = iter.next();
				Player p = Bukkit.getPlayer(player);
				if (p == null) {
					// player is offline?
					iter.remove();
					continue;
				}
				if (p.isDead() || p.getHealth() <= 0.0) {
					continue;
				}
				if (p.getFoodLevel() >= minimumFood && p.getHealth() < p.getMaxHealth()) {
					double newHealth = p.getHealth() + healthPerCycle;
					newHealth = Math.min(newHealth, p.getMaxHealth());
					p.setExhaustion(p.getExhaustion() + exhaustionPerHeal);
					p.setHealth(newHealth);
				}
			}
		}
		currentTick++;
		if (currentTick >= interval) {
			currentTick = 0;
		}
	}
	
	public int getMinimumFood() {
		return minimumFood;
	}
	
	public float getExhaustionPerHeal() {
		return exhaustionPerHeal;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public double getHealthPerHeal() {
		return healthPerCycle;
	}
	
	public int getPID() {
		return PID;
	}
	
	public boolean blockPassiveHealthRegen() {
		return blockPassiveHealthRegen;
	}
	
	public boolean blockFoodHealthRegen() {
		return blockFoodHealthRegen;
	}

}
