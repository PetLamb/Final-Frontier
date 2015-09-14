package me.mortadelle2.ff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FinalFrontierEvents implements Listener{
	
	FinalFrontier get;
	FinalFrontierUserCmds ffU;
	
	public FinalFrontierEvents(FinalFrontier plugin){	
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		get = plugin;
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
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerChatsToTeam(AsyncPlayerChatEvent e){
		
		Player p = e.getPlayer();
		String msg = e.getMessage();
		
		String[] playersInChatRoom = get.settingsFile.getString("Maps." + ffU.currentMap.get(p.getName()) + ".defenseChat").split(",");
		
			for (String s : playersInChatRoom) {
				Player receiver = Bukkit.getServer().getPlayer(s);
				
				if (get.settingsFile.contains("Maps." + ffU.currentMap.get(receiver.getName()) + ".defenseChat." + receiver.getName())){
					receiver.sendMessage(msg);
				}else{
					e.getRecipients().remove(receiver);
			
			}
		}
	}

}
