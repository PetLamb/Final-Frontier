package me.mortadelle2.ff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FinalFrontierUserCmds implements CommandExecutor{


	FinalFrontier get;
	
	FinalFrontierAdminCmds adminGet = new FinalFrontierAdminCmds(get);
	
	public FinalFrontierUserCmds(FinalFrontier finalFrontier) {
		get = finalFrontier;
	}

	
	HashMap<String, String> currentMap = new HashMap<String, String>();
	
	public void sendErrorMessage(Player p){
		p.sendMessage(get.ffMsg + "AN ERROR OCCURED! Please try again!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("ff")){
			
			if (args.length == 2){
				
				if (args[0].equalsIgnoreCase("join")){
				  if (get.settingsFile.contains("Maps." + args[1])){
					if (args[1] != null && get.settingsFile.getString("Maps." + args[1] + ".defenseLoc.x") != null
							&& get.settingsFile.getString("Maps." + args[1] + ".attackLoc.x") != null){
						
						if (get.settingsFile.contains("Maps." + args[1])){
						int warpX = get.settingsFile.getInt("Maps." + args[1] + ".x");
						int warpY = get.settingsFile.getInt("Maps." + args[1] + ".y");
						int warpZ = get.settingsFile.getInt("Maps." + args[1] + ".z");
						
						p.teleport(new Location(Bukkit.getServer().getWorld(get.settingsFile.getString("Maps." + args[1] + ".world")), warpX, warpY, warpZ));
						
						p.sendMessage(get.ffMsg + "You have joined the battle of " + ChatColor.GREEN + "Final Frontier" + ChatColor.YELLOW + "! Choose a side!");
						
						get.pSettings.set(p.getName() + ".hasJoined", true);
						currentMap.put(p.getName(), args[1]);
						
						return true;
						}else{
							p.sendMessage(get.ffMsg + ChatColor.RED + "That map doesn't exist! You typed /ff join " + args[1] + "!");
							return true;
						}
					}
					else{
						p.sendMessage(get.ffMsg + ChatColor.RED + "The spawn locations for both teams have not been set! Please contact a member of staff!");
						return true;
					}
				  }else{
					  p.sendMessage(get.ffMsg + ChatColor.RED + "Map not found!");
					  return true;
				  }
				}
				
			}
			
			
			
			 if (args.length == 1){
				
				if (args[0].equalsIgnoreCase("attack") && get.pSettings.getBoolean(p.getName() + ".hasJoined")){
					
					try{
					int warpX = get.settingsFile.getInt("Maps." + currentMap.get(p.getName()) + ".attackLoc.x");
                    int warpY = get.settingsFile.getInt("Maps." + currentMap.get(p.getName()) + ".attackLoc.y");
                    int warpZ = get.settingsFile.getInt("Maps." + currentMap.get(p.getName()) + ".attackLoc.z");
                    
                    p.teleport(new Location(Bukkit.getServer().getWorld(get.settingsFile.getString("Maps." + currentMap.get(p.getName()) + ".world")), warpX, warpY, warpZ));	
                    
					get.attackingPlayers.add(p.getName());
					p.sendMessage(get.ffMsg + "You are now attacking! Better watch out, eh?");
					return true;
					
					}catch (Exception e){
						sendErrorMessage(p);
						get.pSettings.set(p.getName(), false);
					}
					
					
					
					
				}else if (args[0].equalsIgnoreCase("defend") && get.pSettings.getBoolean(p.getName() + ".hasJoined")){
					
					try{
                    int warpX = get.settingsFile.getInt("Maps." + currentMap.get(p.getName()) + ".defenseLoc.x");
                    int warpY = get.settingsFile.getInt("Maps." + currentMap.get(p.getName()) + ".defenseLoc.y");
                    int warpZ = get.settingsFile.getInt("Maps." + currentMap.get(p.getName()) + ".defenseLoc.z");
                    get.defendingPlayers.add(p.getName());
                    
                    
                    p.teleport(new Location(Bukkit.getServer().getWorld(get.settingsFile.getString("Maps." + currentMap.get(p.getName()) + ".world")), warpX, warpY, warpZ));				
					p.sendMessage(get.ffMsg + "You are now defending! Good luck to you sir!");
					return true;
					
					}catch(Exception e){
						sendErrorMessage(p);
						get.pSettings.set(p.getName(), false);
					}
					
				}else if (args[0].equalsIgnoreCase("defend") || args[0].equalsIgnoreCase("attack") && get.pSettings.getBoolean(p.getName() + ".hasJoined") == false){
					p.sendMessage(get.ffMsg + ChatColor.RED + "Please join a game first! To do so, type /ff join [game].");
					return true;
				}
				
				
				
				
				
				else if(args[0].equalsIgnoreCase("leave")){
					
					if (get.attackingPlayers.contains(p.getName())){
						get.attackingPlayers.remove(p.getName());
					}else if (get.defendingPlayers.contains(p.getName())){
						get.defendingPlayers.remove(p.getName());
					}
					
					get.pSettings.set(p.getName() + ".hasJoined", false);
					p.sendMessage(get.ffMsg + ChatColor.YELLOW + "You have left the battle of " + ChatColor.GREEN + "Final Frontier" + ChatColor.YELLOW + "!");
				}
				
				
				
				
				else if (args[0].equalsIgnoreCase("list")){
					try{
						
						ArrayList<String> mapList = new ArrayList<String>();
						StringBuilder sb = new StringBuilder();
						
						for (String s : get.settingsFile.getConfigurationSection("Maps").getKeys(false)){
							mapList.add(s + ", ");
							Collections.sort(mapList);
						}
						for (String s : mapList){
							sb.append(s);
						}
						
						p.sendMessage(get.ffMsg + sb.toString());
						
					}catch(Exception e){
						p.sendMessage(get.ffMsg + ChatColor.RED + "No maps found!");
					}
				}
				
			}
			
			return true;
		}
		
		return false;
	}

}
