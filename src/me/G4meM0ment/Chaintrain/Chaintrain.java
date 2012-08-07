package me.G4meM0ment.Chaintrain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.commons.ChatColor;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

public class Chaintrain extends JavaPlugin {
			
	Chaintrain chaintrain;
	public EventListener listener;
	public Messages messages;
	private FileConfiguration customConfig = null;
	private File dataFile;
	public SpoutManager sm;
	public PluginManager pm;
		
	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		saveConfig();
		saveCustomConfig();
	}
	
	@Override
	public void onEnable() {
		
		pm = getServer().getPluginManager();
		
		chaintrain = this;
		messages = new Messages(this);
		listener = new EventListener(this, messages);
		pm.registerEvents(listener, this);
		
		//creates the config
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		reloadCustomConfig();
		saveCustomConfig();
		
		listener.checkForPlugins();
		
		if(this.getConfig().getBoolean("useSpout")) {
			this.getLogger().info("Spout found, features enabled.");		
		}
		this.getLogger().info("Successfully enabled!");
		this.getLogger().info("By G4meM0ment (Originaly by T4sk)");
	}
	
	public void reloadCustomConfig() {
	    if (dataFile == null) {
	    	dataFile = new File(this.getDataFolder(), "data");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(dataFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("data");
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
	    if (customConfig == null || dataFile == null) {
	    	return;
	    }
	    try {
	        customConfig.save(dataFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + dataFile, ex);
	    }
	}
	
	public void newPlayer(Player player) {
		if(existsPlayer(player)) {
			return;
		}
		String pname = player.getName();
				
		getCustomConfig().set("players." +pname +".chained", false);
		getCustomConfig().set("players." +pname +".oldTime", 0);
		getCustomConfig().set("players." +pname +".cooldown", 0);
		
		saveCustomConfig();
	}
	
	/**
	 * Checks if the player is already listed	
	 * @param player to be checked
	 * @return boolean
	 */
	public boolean existsPlayer(Player player) {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("players");
		if (cfgsel == null) return false;
		Set<String> names = cfgsel.getKeys(false);
		String pname = player.getName();
		
		try {
			for (String name : names) {
				if(name.equalsIgnoreCase(pname)) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public final HashMap<String,String> data = new HashMap<String,String>();
	
	public boolean isChained(String chained)
	{
		if(chained == null) return false;
	    return data.containsKey(chained);
	}
	public boolean isChained(Player chained)
	{
		if(chained == null) return false;
		
		if(this.getCustomConfig().getLong("players." + chained.getName() +".oldTime") > 0 && this.getCustomConfig().getLong("players." + chained.getName() +".cooldown") > 0)
		{
			long oldTime = this.getCustomConfig().getLong("players." + chained.getName() +".oldTime");
			int cooldown = this.getCustomConfig().getInt("players." + chained.getName() +".cooldown")*60;
			long diff = (System.currentTimeMillis() - oldTime)/1000;
			if (diff > cooldown) {
	    	unchain(chained.getName());
	    	messages.timeOver(chained);
	    	return false;
			}
		}
	    else{
	    	return isChained(chained.getName());
	    }
		return isChained(chained.getName());
	}
	
	public void chainAll(Player[] all, Player chainer)
	{
		//for every player online which isn't filtered by permissions, chained will be set
		for(Player chained:all)
		{
			if(!(chained.hasPermission("chaintrain.ignore") || chained.hasPermission("chaintrain.admin") || chained.isOp()))
			chain(chained, chainer);
		}
	}
	public void unchainAll(Player[] all)
	{		
		for(Player chained:all)
	    {
		   String player = chained.getName();
		   unchain(player);
	    }
	}
	public void chain(Player chained,Player chainer)
	{
		chain(chained.getName(),chainer.getName());
	}
	public void chain(String player, String chainer)
	{
		data.put(player, chainer);
		this.getCustomConfig().set("players." + player +".chained", true);
		this.saveCustomConfig();
	}
	public void unchain(Player player)
	{
		unchain(player.getName());
	}
	public void unchain(String player)
	{
		if(!isChained(player))
			return;
		data.remove(player);
		this.getCustomConfig().set("players." + player +".chained", false);
		this.getCustomConfig().set("players." + player +".oldTime", 0);
		this.getCustomConfig().set("players." + player +".cooldown", 0);
		this.saveCustomConfig();
	}
	
	public void playSpoutSound(Player player)
	{
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(!sp.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Chaintrain");
	
		// *** Sound effect ***
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playCustomSoundEffect(spoutPlugin, sp, chaintrain.getConfig().getString("chainSound"), false);
	}

//The command block
    HashMap<String,String> allBoolean = new HashMap<String,String>();
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		String p = sender.getName();
		Player chainer = (Player) sender;
		
		if(command.getName().equalsIgnoreCase("chaintrain") && args.length > 0 && args[0].equals("reload") && (chainer.hasPermission("chaintrain.command.reload") || chainer.hasPermission("chaintrain.admin") || chainer.isOp()))
		{
		 chaintrain.reloadConfig();
		 chaintrain.saveConfig();
		 reloadCustomConfig();
		 saveCustomConfig();
		 chainer.sendMessage("Chaintrain reloaded!");
		 getLogger().info("Reloaded config!");
		 return true;
		}
		
		if(command.getName().equalsIgnoreCase("chaintrain") && args.length > 0 && args[0].equals("help") && (chainer.hasPermission("chaintrain.command.reload") || chainer.hasPermission("chaintrain.admin") || chainer.isOp()))
		{
		 PluginDescriptionFile pdf = getDescription();
		 chainer.sendMessage(ChatColor.GOLD + "*** Chaintrain Help Menu ***");
		 chainer.sendMessage(ChatColor.GREEN + "/chaintrain reload" + ChatColor.GOLD + " Reloads the Configs.");
		 chainer.sendMessage(ChatColor.GREEN + "/chaintrain help" + ChatColor.GOLD + " Displays the help menu.");
		 chainer.sendMessage(ChatColor.GREEN + "/chain <player> (<time>)" + ChatColor.GOLD + " Chain a player, arg time is to chain the player over time (minutes).");
		 chainer.sendMessage(ChatColor.GREEN + "/unchain <player>" + ChatColor.GOLD + " Unchain the player.");
		 chainer.sendMessage(ChatColor.GREEN + "/chainall" + ChatColor.GOLD + " Chain all players online and joining.");
		 chainer.sendMessage(ChatColor.GREEN + "/unchainall" + ChatColor.GOLD + " Removes the effect of '/chainall'.");
		 chainer.sendMessage(ChatColor.GREEN + "/chaintrain time" + ChatColor.GOLD + " To check how many minutes left, you're chained.");
		 chainer.sendMessage(ChatColor.GREEN + "/chain " + ChatColor.GOLD + "AND " + ChatColor.GREEN + "/unchain" + ChatColor.GOLD + " alone will chain/unchain you.");
		 chainer.sendMessage(ChatColor.GOLD + "Information: Devs: G4meM0ment Version: " + pdf.getVersion());
		 chainer.sendMessage(ChatColor.GOLD + "Thanks to ferrybig for helping me! Plugin originally made by T4sk!");
		 return true;
		}
		
		if(command.getName().equalsIgnoreCase("chain") && args.length > 1 && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chain") || chainer.isOp()))
		{
			Player chained = getPlayer(args[0]);
			if(chained != null)
			{
				if(!chaintrain.isChained(chained))
				{
					int time = Integer.parseInt(args[1]);
					long currentTime = System.currentTimeMillis();
					getCustomConfig().set("players." + chained.getName() +".oldTime", currentTime);
					getCustomConfig().set("players." + chained.getName() +".cooldown", time);
					saveCustomConfig();
					chain(chained.getName(), p);
					messages.timeChained(chained, chainer, args[1]);
					return true;
				}
				else {
					messages.playerAlreadyChained(chainer);
					return true;
				}


			}
			else {
				messages.cantFindPlayer(chainer);
				return true;
			}
		}
		
		if(command.getName().equalsIgnoreCase("chain") && args.length > 0 && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chain") || chainer.isOp()))
		{			
			Player chained = getPlayer(args[0]);
			if(chained != null)
			{
				if(!chaintrain.isChained(chained))
				{
					chaintrain.chain(chained.getName(), p);
					messages.chained(chained, chainer);
					return true;
				}
				else {
					messages.playerAlreadyChained(chainer);
					return true;
				}


			}
			else {
				messages.cantFindPlayer(chainer);
				return true;
			}
		}
		
		if(command.getName().equalsIgnoreCase("unchain") && args.length > 0 && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chain") || chainer.isOp()))
		{
			Player chained = getPlayer(args[0]);
			if(chained != null)
			{
				if(chaintrain.isChained(chained))
				{
					chaintrain.unchain(chained.getName());
					messages.unchained(chained, chainer);
					return true;
				}
				else {
					messages.playerNotChained(chainer);
					return true;
				}


			}
			else {
				messages.cantFindPlayer(chainer);
				return true;
			}

		}
		
		if(command.getName().equalsIgnoreCase("chain") && args.length == 0  && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chain") || chainer.isOp()))
		{
			chaintrain.chain(p, p);
			messages.chained(chainer, chainer);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("unchain") && args.length == 0 && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chain") || chainer.isOp()))
		{
				chaintrain.unchain(p);
				messages.unchained(chainer, chainer);
				return true;
		}
		
		if(command.getName().equalsIgnoreCase("chainall") && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chainall") || chainer.isOp()))
		{
			//sets chainall to active so if a player joins he will be chained too
			allBoolean.put("chainAll", "true");
			Player[] all = Bukkit.getServer().getOnlinePlayers();
			chaintrain.chainAll(all, chainer);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("unchainall") && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chainall") || chainer.isOp()))
		{
			allBoolean.put("chainAll", "false");
			Player[] all = Bukkit.getServer().getOnlinePlayers();
			chaintrain.unchainAll(all);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("chaintrain") && args[0].equalsIgnoreCase("time") && (chainer.hasPermission("chaintrain.admin") || chainer.hasPermission("chaintrain.command.chaintime") || chainer.isOp()))
		{
			messages.timeOverIn(chainer);
			return true;
		}		
		return false;
	}
	
    public Player getPlayer(final String name) {
        Player[] players = Bukkit.getOnlinePlayers();
 
        Player found = null;
        String lowerName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : players) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
                int curDelta = player.getName().length() - lowerName.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                    break;
                }
                if (curDelta == 0) break;
            }

        }
        return found;
    }
}
