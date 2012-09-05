package me.G4meM0ment.Chaintrain.Bounty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.G4meM0ment.Chaintrain.Chaintrain;
import me.G4meM0ment.Chaintrain.Messages.Messages;
import me.G4meM0ment.Chaintrain.Messages.Messenger;

public class Bounty {
	
	private Chaintrain plugin;
	private Messenger messenger;
	private FileConfiguration customConfig = null;
	private File bountyFile;
	
	public Bounty(Chaintrain plugin, Messenger messenger) {
		this.plugin = plugin;
		this.messenger = messenger;
	}
	
	public void reloadCustomConfig() {
	    if (bountyFile == null) {
	    	bountyFile = new File(plugin.getDataFolder(), "data_bounty");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(bountyFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("data_bounty");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || bountyFile == null) {
	    	return;
	    }
	    try {
	        customConfig.save(bountyFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + bountyFile, ex);
	    }
	}
	
	public void newBounty(Player player, Double amount, String type) {
		if(player == null) return;
		if(!checkAmount(player, amount)) {
			messenger.sendMoney(player, Messages.minMoney, String.valueOf(amount));
			return;
		}
		
		Chaintrain.econ.withdrawPlayer(player.getName(), amount);
		storeBounty(player, amount, type);
	}
	
	private void storeBounty(Player player, double amount, String type) {
		if(!existsCfgsel(player)) {
			getCustomConfig().set("bountys." + player.getName() + ".amount.kill", 0);
			getCustomConfig().set("bountys." + player.getName() + ".amount.chain", 0);
		}
		
		if(existsBounty(player)) {
			double oldAmount = getCustomConfig().getInt("bountys." + player.getName() + ".amount." + type);
			amount = oldAmount + amount;
			getCustomConfig().set("bountys." + player.getName() + ".amount." + type, amount);
			saveCustomConfig();
		}
		else {
			getCustomConfig().set("bountys." + player.getName() + ".bounty", true);
			getCustomConfig().set("bountys." + player.getName() + ".amount." + type, amount);
			saveCustomConfig();
		}
	}
	
	public boolean existsCfgsel(Player player) {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("bountys");
		if (cfgsel == null) return false;
		Set<String> names = cfgsel.getKeys(false);
		String pname = player.getName();
		
		try {
			for (String name : names) {
				if(name.equalsIgnoreCase(pname) && getCustomConfig().getBoolean("bountys." + player.getName() + ".bounty")) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;
	}

	public boolean existsBounty(Player player) {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("bountys");
		if (cfgsel == null) return false;
		Set<String> names = cfgsel.getKeys(false);
		String pname = player.getName();
		
		try {
			for (String name : names) {
				if(name.equalsIgnoreCase(pname) && getCustomConfig().getBoolean("bountys." + player.getName() + ".bounty")) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public boolean acceptedBounty(Player p, String hname) {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("hunters");
		if (cfgsel == null) return false;
		Set<String> names = cfgsel.getKeys(false);
		String pname = p.getName();
		
		try {
			for (String name : names) {
				if(name.equalsIgnoreCase(pname) && getCustomConfig().getBoolean("hunters." + p.getName() + "." + hname)) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public List<String> getAllBountys() {
		Player[] players = plugin.getServer().getOnlinePlayers();
		List<String> bountys = new ArrayList<String>();
		
		for(Player player: players) {
			
			if(!existsBounty(player)) break;
			
			int kmoney = getCustomConfig().getInt("bountys." + player.getName() + ".amount.kill");
			String kamount = ""+getCustomConfig().getInt("bountys." + player.getName() + ".amount.kill");
			int cmoney = getCustomConfig().getInt("bountys." + player.getName() + ".amount.chain");
			String camount = ""+getCustomConfig().getInt("bountys." + player.getName() + ".amount.chain");
			int i = 1;
			
			if(bountys.size() != 0) {
				for(String s:bountys){
					if(Integer.parseInt(s.split("|")[2]) <= kmoney && kmoney > cmoney) {
						bountys.add(i, player.getName() + " | " + "kill" + " | " + kamount);
						if(cmoney > 0) bountys.add(i, player.getName() + " | " + "chain" + " | " + camount);
						break;
					}
					else {
						if(cmoney > 0) bountys.add(i, player.getName() + " | " + "chain" + " | " + camount);
						if(kmoney > 0) bountys.add(i, player.getName() + " | " + "kill" + " | " + kamount);
					}
					i++;
				}
			}
			else {
				if(kmoney > 0 && kmoney > cmoney) {
	                bountys.add(player.getName() + " | " + "kill" + " | " + kamount);
	                if(cmoney > 0) bountys.add(player.getName() + " | " + "chain" + " | " + camount);	
				}
		
				if(cmoney > 0 && cmoney > kmoney) {
	                bountys.add(player.getName() + " | " + "chain" + " | " + camount);
	                if(kmoney > 0) bountys.add(player.getName() + " | " + "kill" + " | " + kamount);	
				}

				else
	                bountys.add(player.getName() + " | " + "kill" + " | " + kamount);			
			}
		}
		return bountys;
	}
	
	public List<String> getHuntersBountys(Player p) {
		Player[] players = plugin.getServer().getOnlinePlayers();
		List<String> bountys = new ArrayList<String>();
		
		for(Player player: players) {
			
			if(!existsBounty(player)) break;
			if(!acceptedBounty(p, player.getName())) break;
			
			int kmoney = getCustomConfig().getInt("bountys." + player.getName() + ".amount.kill");
			String kamount = ""+getCustomConfig().getInt("bountys." + player.getName() + ".amount.kill");
			int cmoney = getCustomConfig().getInt("bountys." + player.getName() + ".amount.chain");
			String camount = ""+getCustomConfig().getInt("bountys." + player.getName() + ".amount.chain");
			int i = 1;
			
			if(bountys.size() != 0) {
				for(String s:bountys){
					if(Integer.parseInt(s.split("|")[2]) <= kmoney && kmoney > cmoney) {
						bountys.add(i, player.getName() + " | " + "kill" + " | " + kamount);
						if(cmoney > 0) bountys.add(i, player.getName() + " | " + "chain" + " | " + camount);
						break;
					}
					else {
						if(cmoney > 0) bountys.add(i, player.getName() + " | " + "chain" + " | " + camount);
						if(kmoney > 0) bountys.add(i, player.getName() + " | " + "kill" + " | " + kamount);
					}
					i++;
				}
			}
			else {
				if(kmoney > 0 && kmoney > cmoney) {
	                bountys.add(player.getName() + " | " + "kill" + " | " + kamount);
	                if(cmoney > 0) bountys.add(player.getName() + " | " + "chain" + " | " + camount);	
				}
		
				if(cmoney > 0 && cmoney > kmoney) {
	                bountys.add(player.getName() + " | " + "chain" + " | " + camount);
	                if(kmoney > 0) bountys.add(player.getName() + " | " + "kill" + " | " + kamount);	
				}

				else
	                bountys.add(player.getName() + " | " + "kill" + " | " + kamount);			
			}
		}
		return bountys;
	}

	public void finishedBounty(Player p, Player k, String type) {
		Double m = getCustomConfig().getDouble("bountys." + p.getName() + ".amount");
		Chaintrain.econ.depositPlayer(p.getName(), m);
		messenger.sendMoney(p, Messages.bountyFinished, String.valueOf(m));
		getCustomConfig().set("bountys." + p.getName() + ".bounty", false);
		getCustomConfig().set("bountys." + p.getName() + ".amount." + type, 0);
		getCustomConfig().set("hunters." + p.getName() + "." + k.getName(), false);
		saveCustomConfig();
	}
	
	public boolean checkAmount(Player p, Double a) {
		if(Chaintrain.econ.has(p.getName(), a))
			return true;
		else
			return false;
	}
	
	public boolean checkMinAmount(Player p, Double a) {
		if(plugin.getConfig().getDouble("minBountyAmount") <= a)
			return true;
		else
			return false;
	}
}
