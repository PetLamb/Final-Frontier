package me.mortadelle2.ff;

import java.util.ArrayList;
import java.util.Collections;

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
				
				if (args[0].equalsIgnoreCase("reset")){
					try{
						get.ffKits.set("Kits", null);
						p.sendMessage("config reset");
					}catch(Exception e){
						
					}
				}

				for (String s : get.ffKits.getConfigurationSection("Kits").getKeys(false)){
					
					if (args[0].equalsIgnoreCase(s)) {
						if (p.hasPermission(get.ffKits.getString("Kits." + args[0] + ".perm"))) {
							
							try {
					
					String getKits = get.ffKits.getString("Kits." + args[0] + ".items");
					String[] kits = getKits.split(",");
					String[] enchants = get.ffKits.getString("Kits." + args[0] + ".enchants").split(",");
					String[] nameMeta = get.ffKits.getString("Kits." + args[0] + ".names").split(",");
					
					for (int i = 0; i < kits.length; i++){
						
						String[] singleKits = kits[i].split("-");
						String[] singleEnchant = enchants[i].split(":");

						ItemStack kit = new ItemStack(Integer.valueOf(singleKits[0]), Integer.valueOf(singleKits[1]));
						ItemMeta kitMeta = kit.getItemMeta();
						
						kitMeta.addEnchant(Enchantment.getById(Integer.valueOf(singleEnchant[0])), Integer.valueOf(singleEnchant[1]), false);
						if (!nameMeta[i].equals("default")){
							kitMeta.setDisplayName(nameMeta[i]);
						}
						kit.setItemMeta(kitMeta);
						
						p.getInventory().addItem(kit);
						
					}
					}catch(Exception e){
						e.printStackTrace();
						
						
							}
						}else{
							p.sendMessage(get.ffMsg + "You can't always get what you want, buddy! Don't worry man, I know the feels ;(");
						}
					}
					
				}
			}
				return true;

		}
		
		return false;
	}

	
	
}
