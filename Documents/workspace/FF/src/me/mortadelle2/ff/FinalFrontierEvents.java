package me.mortadelle2.ff;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FinalFrontierEvents implements Listener{
	
	FinalFrontier get;
	FinalFrontierUserCmds ffU;
	FinalFrontierAdminCmds ffA;
	

	
	public FinalFrontierEvents(FinalFrontier plugin, FinalFrontierUserCmds uCMD, FinalFrontierAdminCmds aCMD){	
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		get = plugin;
		ffU = uCMD;
		ffA = aCMD;
	}
	
	@EventHandler
	public void playerJoins(PlayerJoinEvent e){
		Player p = e.getPlayer();
		
		if (!get.pSettings.contains(p.getName())){	
			get.pSettings.set(p.getName() + ".hasJoined", false);
		}
		
		if (!get.pData.contains(p.getName())){
			get.pData.set(p.getName() + ".kills", 0.0);
			get.pData.set(p.getName() + ".kills", 0.0);
		}
	}
	
	@EventHandler
	public void playerLeaves(PlayerQuitEvent e){
		Player p = e.getPlayer();
		get.pSettings.set(p.getName(), false);
		
	}
	
	@EventHandler
	public void playerDamagesTeam(EntityDamageByEntityEvent e){
		Player victim = (Player) e.getEntity();
		Player damager = (Player) e.getDamager();

		if (get.attackingPlayers.contains(victim.getName()) && get.attackingPlayers.contains(damager.getName())
				|| get.defendingPlayers.contains(victim.getName()) && get.defendingPlayers.contains(damager.getName())){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerChatsToTeam(AsyncPlayerChatEvent e){
		
		Player p = e.getPlayer();

	
	}
	
	@EventHandler
	public void castleCorePlacment(PlayerInteractEvent e){
		
		Player p = e.getPlayer();
		Location block = e.getClickedBlock().getLocation();
		
			p.sendMessage(get.ffMsg + "Core placed! You can edit the core lives by typing /ffa core [map] [number]");
			get.settingsFile.set("Maps." + ffU.currentMap.get(p.getName()) + ".objectifBlockLoc.x", block.getBlockX());
			get.settingsFile.set("Maps." + ffU.currentMap.get(p.getName()) + ".objectifBlockLoc.y", block.getBlockY());
			get.settingsFile.set("Maps." + ffU.currentMap.get(p.getName()) + ".objectifBlockLoc.z", block.getBlockZ());
			
		    Bukkit.getScheduler().cancelTask(ffA.cancelCorePlacement);
		
	}

}
