package me.mortadelle2.ff;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FinalFrontierAdminCmds implements CommandExecutor{

	FinalFrontier get;
	
	ArrayList<String> waitingForReset = new ArrayList<String>();
	ArrayList<String> waitingForCorePlacement = new ArrayList<String>();
	
	int confirmReset;
	int cancelCorePlacement;
	
	public FinalFrontierAdminCmds(FinalFrontier finalFrontier) {
		get = finalFrontier;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel,
			String[] args) {
	
		final Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("ffa")){
	
			
			// Check if only /ff is typed
			if (args.length == 0){
				p.sendMessage("This will display help menu");
			    return true;	
			}
			
			if (args.length == 1){
				if (args[0].equalsIgnoreCase("reset")){
					
					if (!waitingForReset.contains(p.getName())){
					waitingForReset.add(p.getName());
					p.sendMessage(get.ffMsg + "Type " + ChatColor.GREEN + "/ffa confirm" + ChatColor.YELLOW
							+ " to reset. The task will be cancelled in 15 seconds otherwise. " +
					ChatColor.RED + "NOTE: ALL MAPS WILL BE DELETED IN FILE SETTINGS.YML IN FOLDER \"DO NOT TOUCH\"");
					
					confirmReset = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(get, new Runnable() {
						
						@Override
						public void run() {
							
							p.sendMessage(get.ffMsg + ChatColor.RED + "Config reset cancelled!");
							waitingForReset.remove(p.getName());
							
						}
					}, 300L);
					
					}
					
					else{
						p.sendMessage(get.ffMsg + ChatColor.RED + "You are already in the process of resetting maps file.");
					}
					
				}else if (args[0].equalsIgnoreCase("confirm")){
					if (waitingForReset.contains(p.getName())){
						waitingForReset.remove(p.getName());
						get.settingsFile.set("Maps", null);
						
						Bukkit.getScheduler().cancelTask(confirmReset);
						
						p.sendMessage(get.ffMsg + "Settings.YML successfully reset!");
					}
				}
			}
			
			if (args.length == 2){
				
				if (args[0].equalsIgnoreCase("create")){
					if(args[1] != null && !get.settingsFile.contains("Maps." + args[1])){
						
						get.settingsFile.set("Maps." + args[1] + ".world", p.getLocation().getWorld().getName());
						get.settingsFile.set("Maps." + args[1] + ".x", p.getLocation().getBlockX());
						get.settingsFile.set("Maps." + args[1] + ".y", p.getLocation().getBlockY());
						get.settingsFile.set("Maps." + args[1] + ".z", p.getLocation().getBlockZ());
						get.settingsFile.set("Maps." + args[1] + ".defenseLoc", "");
						get.settingsFile.set("Maps." + args[1] + ".attackLoc", "");
						get.settingsFile.set("Maps." + args[1] + ".defenseLoc.x", null);
						get.settingsFile.set("Maps." + args[1] + ".defenseLoc.y", null);
						get.settingsFile.set("Maps." + args[1] + ".defenseLoc.z", null);
						get.settingsFile.set("Maps." + args[1] + ".attackLoc.x", null);
						get.settingsFile.set("Maps." + args[1] + ".attackLoc.y", null);
						get.settingsFile.set("Maps." + args[1] + ".attackLoc.z", null);
		                get.settingsFile.set("Maps." + args[1] + ".defenseChat", "");
		                get.settingsFile.set("Maps." + args[1] + ".attackChat", "");
		                get.settingsFile.set("Maps." + args[1] + ".objectifBlockLoc", "");
		                get.settingsFile.set("Maps." + args[1] + ".objectifBlockLoc.x", "");
		                get.settingsFile.set("Maps." + args[1] + ".objectifBlockLoc.y", "");
		                get.settingsFile.set("Maps." + args[1] + ".objectifBlockLoc.z", "");
		                
						p.sendMessage(get.ffMsg + "You have succesfully created map " + ChatColor.GREEN + args[1] + ChatColor.YELLOW + "!");
						return true;
						
					}else{
						p.sendMessage(get.ffMsg + ChatColor.RED + "That map already exists!");
						return true;
					}
					
				}
				else if (args[0].equalsIgnoreCase("del")){
					if (args[1] != null){
						
						if (get.settingsFile.contains("Maps." + args[1])){
							get.settingsFile.set("Maps." + args[1], null);
							p.sendMessage(get.ffMsg + "You have successfully deleted " + ChatColor.GREEN + args[1] + ChatColor.YELLOW + "!");
							return true;
						}
						else {
							p.sendMessage(get.ffMsg + ChatColor.RED + "Map not found! You typed the following: /ff del" + args[1]);
							return true;
						}
						
					}
				}
				
				else if (args[0].equalsIgnoreCase("obj")){
					if (args[1] != null && get.settingsFile.contains("Maps." + args[1])){
						
						p.sendMessage(get.ffMsg + "Click on any block and that will become the defenders' castle core!");
						
						waitingForCorePlacement.add(p.getName());
						
						cancelCorePlacement = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(get, new Runnable() {
							
							@Override
							public void run() {

								p.sendMessage(get.ffMsg + ChatColor.GREEN + "Core " + ChatColor.YELLOW + "placement cancelled!");
										
								waitingForCorePlacement.remove(p.getName());
		
							}
						}, 300L);
						
						
					}else{
						p.sendMessage(get.ffMsg + ChatColor.RED + "Not found!");
					}
				}
				
				else{
					p.sendMessage(get.ffMsg + ChatColor.RED + "That Final Frontier command doesn't exist!");
					return true;
				}
			}
			if (args.length == 3)
				 if (args[0].equalsIgnoreCase("setloc")){
					if (args[1] != null){
						if (args[2].equalsIgnoreCase("attack")){
							get.settingsFile.set("Maps." + args[1] + ".attackLoc.x", p.getLocation().getBlockX());
							get.settingsFile.set("Maps." + args[1] + ".attackLoc.y", p.getLocation().getBlockY());
							get.settingsFile.set("Maps." + args[1] + ".attackLoc.z", p.getLocation().getBlockZ());
							
							p.sendMessage(get.ffMsg + ChatColor.RED + "Attackers' " + ChatColor.YELLOW + "spawn location set for map " + ChatColor.GREEN
									+ args[1] + ChatColor.YELLOW + "!");
							
							return true;
						}
						else if(args[2].equalsIgnoreCase("defense")){
							get.settingsFile.set("Maps." + args[1] + ".defenseLoc.x", p.getLocation().getBlockX());
							get.settingsFile.set("Maps." + args[1] + ".defenseLoc.y", p.getLocation().getBlockY());
							get.settingsFile.set("Maps." + args[1] + ".defenseLoc.z", p.getLocation().getBlockZ());
							
							p.sendMessage(get.ffMsg + ChatColor.AQUA + "Defenders' " + ChatColor.YELLOW + "spawn location set for map " + ChatColor.GREEN
									+ args[1] + ChatColor.YELLOW + "!");
							
							return  true;
						}
					}
				}
		
			
			
			return true;
		}
		
		return false;
	}

}
