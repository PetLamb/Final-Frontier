package me.mortadelle2.ff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FinalFrontier extends JavaPlugin{

	//ALL class object:
	FinalFrontierAdminCmds ffA;
	FinalFrontierUserCmds ffU;
	FinalFrontierKits ffK;
	
	File f;
	
	String ffMsg = ChatColor.GREEN + "[" + ChatColor.YELLOW + "Final Frontier" + ChatColor.GREEN + "]" + ChatColor.YELLOW + ": ";
	
	ArrayList<String> attackingPlayers = new ArrayList<String>();
	ArrayList<String> defendingPlayers = new ArrayList<String>();
	
	
	//Files that may be touched by user
	private File playerData = new File(getDataFolder() + "/Data/PlayerInfo.yml");
	public FileConfiguration pData = YamlConfiguration.loadConfiguration(playerData);
	private File kits = new File(getDataFolder() + "/Data/kits.yml");
	public FileConfiguration ffKits = YamlConfiguration.loadConfiguration(kits);
	
	//Files that must not be touched by user
	private File config = new File(getDataFolder() + "/DO NOT TOUCH/settings.yml");
	public FileConfiguration settingsFile = YamlConfiguration.loadConfiguration(config);
	private File playerSettings = new File(getDataFolder() + "/DO NOT TOUCH/playerSettings");
	public FileConfiguration pSettings = YamlConfiguration.loadConfiguration(playerSettings);

	public void loadPlayers(){
		ffU = new FinalFrontierUserCmds(this);
		for (Player p : Bukkit.getServer().getOnlinePlayers()){
			if (!pData.contains(p.getName())){
				pData.set(p.getName() + ".kills", 0.0);
				pData.set(p.getName() + ".deaths", 0.0);
				try {
					pData.save(playerData);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			pSettings.set(p.getName() + ".hasJoined", false);
			attackingPlayers.remove(p.getName());
			defendingPlayers.remove(p.getName());
		}
		
	}
	
	public void loadKits(){
		ffK = new FinalFrontierKits(this);
		if (!ffKits.contains("Kits.archer")){
			ffKits.set("Kits.archer.items", "300-1,301-1");
			ffKits.set("Kits.archer.enchants", "34:2,34:1");
			ffKits.set("Kits.archer.names", "Final Frontier Archer Leggings,default");
			
			ffKits.set("Kits.archer.perm" , "ffKits.archer");
			try {
				ffKits.save(kits);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void laodConfig(){
		settingsFile.addDefault("Maps", "");
		settingsFile.options().copyDefaults(true);
	}
	
	public void loadFiles(){
		if(kits.exists() && playerData.exists() && config.exists() && playerSettings.exists()){
				try {
					settingsFile.load(config);
					pSettings.load(playerSettings);
					ffKits.load(kits);
					pData.load(playerData);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
		}
		else{
			try {
				settingsFile.save(config);
				pSettings.save(playerSettings);
				ffKits.save(kits);
				ffKits.save(kits);
				loadPlayers();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveFiles(){
		try {
			settingsFile.save(config);
			pSettings.save(playerSettings);
			pData.save(playerData);
			ffKits.save(kits);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	
	
	
	
	
	public void onEnable(){
		
		loadPlayers();
		laodConfig();
		loadKits();
		System.out.println(getDataFolder());
		
		new FinalFrontierAdminCmds(this);
		new FinalFrontierEvents(this);
		new FinalFrontierKits(this);
		
		//Registering commands from other classes
		this.getCommand("ffa").setExecutor(new FinalFrontierAdminCmds(this));
		this.getCommand("ff").setExecutor(new FinalFrontierUserCmds(this));
		this.getCommand("ffk").setExecutor(new FinalFrontierKits(this));
		loadFiles();
		
	}
	
	public void onDisable(){
		saveFiles();
		saveConfig();
	}
	
}
