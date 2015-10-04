package me.mortadelle2.ff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("deprecation")

public class FinalFrontierEvents implements Listener{
	
	FinalFrontier get;
	FinalFrontierUserCmds ffU;
    FinalFrontierAdminCmds ffA;
	
	ArrayList<String> leftOnQuit = new ArrayList<String>();
    
	public FinalFrontierEvents(FinalFrontier plugin, FinalFrontierUserCmds uCMD, FinalFrontierAdminCmds aCMD){	
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		get = plugin;
		ffU = uCMD;
		ffA = aCMD;
	}

	public Player returnOnlinePlayers(){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			return p;
		}
		return null;
	}
	
	@EventHandler
	public void playerJoins(PlayerJoinEvent e){
		Player p = e.getPlayer();
		
		if (!get.pSettings.contains(p.getName())){	
			get.pSettings.set(p.getName() + ".hasJoined", false);
		}
		
		if (!get.pData.contains(p.getName())){
			get.pData.set(p.getName() + ".kills", 0.0);
			get.pData.set(p.getName() + ".deaths", 0.0);
		}
		
		if (leftOnQuit.contains(p.getName())){
			p.sendMessage(get.ffMsg + ChatColor.RED + "You left the game so you were kicked from your previous Final Frontier session!");
			leftOnQuit.remove(p.getName());
		}
		
		get.globalChat.add(p.getName());
	}
	
	@EventHandler
	public void playerLeaves(PlayerQuitEvent e){
		
		Player p = e.getPlayer();
 
		if (get.pSettings.getBoolean(e.getPlayer().getName() + ".hasJoined") == true){
			
			ffU.givePreviousItems(p);
			get.pSettings.set(p.getName() + ".hasJoined", false);
			leftOnQuit.add(p.getName());
		}

		get.globalChat.remove(p.getName());
		
	}
	
	@EventHandler
	public void playerDamagesTeam(EntityDamageByEntityEvent e){
		Player victim = (Player) e.getEntity();
		Player damager = (Player) e.getDamager();
		
		if (get.attackingPlayers.contains(victim.getName()) && get.attackingPlayers.contains(damager.getName())
				|| get.defendingPlayers.contains(victim.getName()) && get.defendingPlayers.contains(damager.getName()) && e.getCause() == EntityDamageByEntityEvent.DamageCause.PROJECTILE){
			e.setCancelled(true);
			
		}else if (get.attackingPlayers.contains(victim.getName()) && get.attackingPlayers.contains(damager.getName())
				|| get.defendingPlayers.contains(victim.getName()) && get.defendingPlayers.contains(damager.getName())){
			e.setCancelled(true);
		}
		else{
			if (get.pSettings.getBoolean(victim.getName() + ".hasJoined") == true && get.pSettings.getBoolean(damager.getName() + ".hasJoined")){
				get.pData.set(damager.getName() + ".kills", get.pData.getInt(damager.getName() + ".kills") + 1);
			}
		}
	}

	@EventHandler
	public void playerChatsToMapPlayers(AsyncPlayerChatEvent e){

		Player sender = e.getPlayer();
		

		for (Player allP : Bukkit.getOnlinePlayers()){
			e.getRecipients().remove(allP);
		}
		
		if (get.globalChat.contains(sender.getName())){
			for (String addedPname : get.globalChat){
				Player addedP = Bukkit.getServer().getPlayer(addedPname);
				e.getRecipients().add(addedP);
			}
		}
		else if (get.mapChats.get(get.currentMap.get(sender.getName())).contains(sender.getName())){
			for (String addedPname : get.mapChats.get(get.currentMap.get(sender.getName()))){
				Player addedP = Bukkit.getServer().getPlayer(addedPname);
				e.getRecipients().add(addedP);
			}
		}

		
	}
	
	@EventHandler
	public void castleCorePlacment(PlayerInteractEvent e){
		
		Player p = e.getPlayer();
		Location block = e.getClickedBlock().getLocation();
		
		if (get.waitingForCorePlacement.contains(p.getName())){
		
			p.sendMessage(get.ffMsg + "Core placed! You can edit the core lives by typing /ffa core [map] [number]");
			get.settingsFile.set("Maps." + get.currentMap.get(p.getName()) + ".objectifBlockLoc.x", block.getBlockX());
			get.settingsFile.set("Maps." + get.currentMap.get(p.getName()) + ".objectifBlockLoc.y", block.getBlockY());
			get.settingsFile.set("Maps." + get.currentMap.get(p.getName()) + ".objectifBlockLoc.z", block.getBlockZ());
			
			get.waitingForCorePlacement.remove(p.getName());
		    Bukkit.getScheduler().cancelTask(get.cancelCorePlacement);
		    
		}
	}
	

	@EventHandler
	public void playerBreaksCastleCore(BlockBreakEvent e){
	
		Player p = e.getPlayer();
		Location coreLocation = e.getBlock().getLocation();
		
		
		if (coreLocation.getBlockX() == get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".objectifBlockLoc.x")){
			get.lives.put(get.currentMap.get(p.getName()), get.lives.get(get.currentMap.get(p.getName())));
			
 
			if (get.pSettings.getBoolean(p.getName() + ".hasJoined") == true){
				for (String s : get.mapChats.get(get.currentMap.get(p.getName()))){
					Player receiver = Bukkit.getPlayer(s);
					receiver.sendMessage(get.ffMsg + "The core was attacked! " + ChatColor.GREEN + get.currentMap.get(get.currentMap.get(p.getName())) + ChatColor.YELLOW + " lives remaining!");
				}
			}
			
		}
		
	}

}
