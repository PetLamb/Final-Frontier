package me.mortadelle2.ff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FinalFrontierKits implements CommandExecutor{

	FinalFrontier get;
	
	public FinalFrontierKits(FinalFrontier plugin){
		get = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("ffk")){
			
			if (args.length == 0){
				try{
					StringBuilder kitsList = new StringBuilder();
					ArrayList<String> sortedKits = new ArrayList<String>();
					
					for (String s : get.ffKits.getConfigurationSection("Kits").getKeys(false)){
						sortedKits.add(s);
						Collections.sort(sortedKits);
					}
					for (String s : sortedKits){
						kitsList.append(s + ", ");
					}
					
					p.sendMessage(get.ffMsg + kitsList.toString());
					
				}catch(Exception e){
					p.sendMessage(get.ffMsg + ChatColor.RED + "No kits found!");
				}
								
				return true;
			}
			
			else if (args.length == 1){
				

				if (get.pSettings.getBoolean(p.getName() + ".hasJoined") == true){
				
					
				for (String s : get.ffKits.getConfigurationSection("Kits").getKeys(false)){
					
					if (args[0].equalsIgnoreCase(s) && get.ffKits.contains("Kits." + args[0])){
							
					p.sendMessage(get.ffMsg + "You acquire the " + ChatColor.GREEN + args[0] + ChatColor.YELLOW + " kit!");
							
					
					try{
					
					String getKits = get.ffKits.getString("Kits." + args[0] + ".items");
					String[] kits = getKits.split(",");
					String[] enchants = get.ffKits.getString("Kits." + args[0] + ".enchants").split(",");
					String[] names = get.ffKits.getString("Kits." + args[0] + ".names").split(",");
					
					for (int i = 0; i < kits.length; i++){
						
						String[] singleKits = kits[i].split("-");
						String[] singleEnchant = enchants[i].split(":");
   
						
						ItemStack kit = new ItemStack(Integer.valueOf(singleKits[0]), Integer.valueOf(singleKits[1]));
						ItemMeta kitMeta = kit.getItemMeta();
						kitMeta.setDisplayName(names[i]);
						
						kitMeta.addEnchant(Enchantment.getById(Integer.valueOf(singleEnchant[0])), Integer.valueOf(singleEnchant[1]), false);
						kit.setItemMeta(kitMeta);
						
						p.getInventory().addItem(kit);
						
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				
						
						}else if (!args[0].equalsIgnoreCase(s) && !get.ffKits.contains("Kits." + args[0])){
							p.sendMessage(get.ffMsg + ChatColor.RED + "Kit not found!");
							break;
						}
					}
				}else{
					p.sendMessage(get.ffMsg + ChatColor.RED + "Nice try! Please join a game!");
				}
			}

				return true;
		}
		
		return false;
	}

	
	
}
