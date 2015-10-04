package me.mortadelle2.ff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FinalFrontierUserCmds implements CommandExecutor{


	FinalFrontier get;
	
	FinalFrontierAdminCmds adminGet = new FinalFrontierAdminCmds(get);
	
	HashMap<String, Integer> mapTimer = new HashMap<String, Integer>();

	
	public FinalFrontierUserCmds(FinalFrontier finalFrontier) {
		get = finalFrontier;
	}

	
	public void sendErrorMessage(Player p){
		p.sendMessage(get.ffMsg + "AN ERROR OCCURED! Please try again!");
	}
	
	public void givePreviousItems(Player p){
		
		if (get.pSettings.getBoolean(p.getName() + ".hasJoined")){
			try {
				
				List<ItemStack> previousItems = get.previousInv.get(p.getName());
				List<ItemStack> previousArmour = get.previousArmour.get(p.getName());
				
				for (ItemStack i : previousItems){
					p.getInventory().addItem(i);
				}
				for (ItemStack i : previousArmour){
					p.getInventory().addItem(i);
				}
				get.previousInv.remove(p.getName());
				get.previousArmour.remove(p.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		
		final Player p = (Player) sender;
		
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
						
						get.previousLocation.put(p.getName(), p.getLocation());
						p.teleport(new Location(Bukkit.getServer().getWorld(get.settingsFile.getString("Maps." + args[1] + ".world")), warpX, warpY, warpZ));
						
						p.sendMessage(get.ffMsg + "You have joined the battle of " + ChatColor.GREEN + "Final Frontier" + ChatColor.YELLOW + "! Choose a side! (You will receive your "
								+ "previous items after the game!)");
						
						get.pSettings.set(p.getName() + ".hasJoined", true);
						get.currentMap.put(p.getName(), args[1]);

						ArrayList<ItemStack> pItems = new ArrayList<ItemStack>();
						ArrayList<ItemStack> pArmour = new ArrayList<ItemStack>();
						
						ItemStack[] allItems = p.getInventory().getContents();
						ItemStack[] armourContents = p.getInventory().getArmorContents();
						
						for (int i = 0; i < allItems.length; i++){
							pItems.add(allItems[i]);
							get.previousInv.put(p.getName(), pItems);
						}
						for (int i = 0; i < armourContents.length; i++){
							pArmour.add(armourContents[i]);
							get.previousArmour.put(p.getName(), pArmour);
							armourContents[i].setType(Material.AIR);
						}
				
						if(get.mapChats.get(args[1]) == null){
							get.mapChats.put(args[1], new ArrayList<String>());
						}
						if (mapTimer.get(args[1]) == null){
							mapTimer.put(args[1], 6);
						}
						if (get.lives.get(args[1]) == null){
							get.lives.put(args[1], new Integer(get.settingsFile.getInt("Maps.currentLives")));
						}
						
						get.mapChats.get(args[1]).add(p.getName());
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(get, new Runnable() {
							
							@Override
							public void run() {
                        
								if (get.pSettings.getBoolean(p.getName() + ".hasJoind")){
									
									for (String s : get.mapChats.get(args[1])){
										
										Player receiver = Bukkit.getServer().getPlayer(s);
										receiver.sendMessage(get.ffMsg + "Game " + ChatColor.GREEN +  ChatColor.YELLOW + "\"" + ChatColor.GREEN + args[1] + ChatColor.YELLOW + "\"" + " has ended!");
										if (get.pSettings.getBoolean(receiver.getName() + ".hasJoined") == true){
											Bukkit.getServer().dispatchCommand(receiver, "ff leave");
										}
										
									}
								}
								
							}
						}, get.mapSettings.getInt("Maps." + args[1] + ".time-limit") * 1200);
						
						Bukkit.getScheduler().scheduleAsyncRepeatingTask(get, new Runnable() {
							
							@Override
							public void run() {
								
								for (String s : get.mapChats.get(args[1])){
									Player receiver = Bukkit.getServer().getPlayer(s);
									receiver.sendMessage(get.ffMsg + ChatColor.YELLOW + "Game ending...");
									break;
								}
							
								
							}
						}, (get.mapSettings.getInt("Maps." + args[1] + ".timeLimit") * 1200) - 100 , 20);
		
						get.globalChat.remove(p.getName());
						
						
						p.getInventory().clear();
						
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
					int warpX = get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".attackLoc.x");
                    int warpY = get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".attackLoc.y");
                    int warpZ = get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".attackLoc.z");
                    
                    p.teleport(new Location(Bukkit.getServer().getWorld(get.settingsFile.getString("Maps." + get.currentMap.get(p.getName()) + ".world")), warpX, warpY, warpZ));	
                    
					get.attackingPlayers.add(p.getName());
					p.sendMessage(get.ffMsg + "You are now attacking! Better watch out, eh?");
					return true;
					
					}catch (Exception e){
						sendErrorMessage(p);
						get.pSettings.set(p.getName(), false);
					}
					
					
					
					
				}else if (args[0].equalsIgnoreCase("defend") && get.pSettings.getBoolean(p.getName() + ".hasJoined")){
					
					try{
                    int warpX = get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".defenseLoc.x");
                    int warpY = get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".defenseLoc.y");
                    int warpZ = get.settingsFile.getInt("Maps." + get.currentMap.get(p.getName()) + ".defenseLoc.z");
                    
                    get.defendingPlayers.add(p.getName());
                    
                    
                    p.teleport(new Location(Bukkit.getServer().getWorld(get.settingsFile.getString("Maps." + get.currentMap.get(p.getName()) + ".world")), warpX, warpY, warpZ));				
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

						if (get.pSettings.getBoolean(p.getName() + ".hasJoined") == true){
							if (get.attackingPlayers.contains(p.getName())){
								get.attackingPlayers.remove(p.getName());
							}else if (get.defendingPlayers.contains(p.getName())){
								get.defendingPlayers.remove(p.getName());
							}
							
							if (get.pSettings.getBoolean(p.getName() + ".hasJoined") == true && get.previousInv.containsKey(p.getName())){
								
								givePreviousItems(p);
								get.mapChats.get(get.currentMap.get(p.getName())).remove(p.getName());
								
							}
							
							
							get.pSettings.set(p.getName() + ".hasJoined", false);
							p.teleport(get.previousLocation.get(p.getName()));
							get.previousLocation.remove(p.getName());
							
							p.sendMessage(get.ffMsg + ChatColor.YELLOW + "You have left the battle of " + ChatColor.GREEN + "Final Frontier" + ChatColor.YELLOW + "!");
						}else{
							p.sendMessage(get.ffMsg + ChatColor.RED + "You aren't in a game!");
						}
						
					
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
				}else if(args[0].equalsIgnoreCase("ratio")){
					StringBuilder sb = new StringBuilder();
					sb.append(get.pData.getInt(p.getName() + ".kills"));
					p.sendMessage(sb.toString());
					return true;
				}
				
			}
			
			return true;
		}
		
		return false;
	}

}
